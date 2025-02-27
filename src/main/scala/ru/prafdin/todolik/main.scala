package ru.prafdin.todolik

import com.typesafe.config.ConfigFactory
import io.circe.generic.auto.*
import io.circe.parser.decode
import io.circe.syntax.*
import ru.prafdin.todolik.utils.TempFile

import java.io.{BufferedWriter, FileWriter}
import scala.io.Source
import scala.sys.process.*
import scala.util.*

given CommandLineParser.FromString[Option[String]] with
    def fromString(value: String): Option[String] = Some(value)

def parseTaskNum(args: Seq[String]): Int =
    args.headOption.getOrElse(
        throw new IllegalStateException("Необходимо указать номер заметки")
    ).toIntOption.getOrElse(
        throw new IllegalStateException("Номер заметки должен быть числом")
    )

def writeTasksToDb(dbPath :String, tasks: List[Task]): Try[Unit] = {
    Using(new BufferedWriter(new FileWriter(dbPath))) { writer =>
        writer.write(tasks.asJson.toString)
    }
}

@main
def main(action: String, args: String*): Unit =
    val config = ConfigFactory.load()

    val rawJsonFromBd = Using(Source.fromFile(config.getString("dbPath"))) { s =>
        s.getLines().mkString
    }

    val tasks = decode[List[Task]](
        rawJsonFromBd.getOrElse(throw IllegalStateException("Что-то пошло не так при чтении БД"))
    ).fold(
        err => throw IllegalStateException("Ошибка при чтении списка задач", err),
        t => t
    )

    action match
        case "list" =>
            TaskTablePrinter().printTasks(tasks)
        case "create" =>
            val title = args.headOption.getOrElse(
                throw new IllegalStateException("Необходимо передать название новой заметки")
            )
            val editor = Try(config.getString("editor")).getOrElse("vi")

            val taskDescription = Using(TempFile()) { f =>
                val command = f"$editor ${f.getAbsolutePath()}"
                val rc = command.run(BasicIO.standard(connectInput = true)).exitValue()
                if (rc != 0) {
                    throw new IllegalStateException(f"При попытке вызвать редактор получен return code = $rc")
                }
                f.read()
            }.fold(
                err => throw new IllegalStateException("Ошибка при попытке создании новой задачи", err),
                taskDescription => taskDescription
            )

            writeTasksToDb(config.getString("dbPath"), Task(title, taskDescription) :: tasks) match
                case Failure(err) => throw new IllegalStateException("Ошибка при записи данных", err)
                case Success(_) => println("Задача успешно создана")

        case "delete" =>
            val taskNum = args.headOption
                .flatMap(_.toIntOption)
                .getOrElse(throw new IllegalStateException("Необходимо передать корректный номер заметки"))

            Try(tasks(taskNum)).fold(
                err => throw new IllegalStateException(f"Заметки по индексу $taskNum не найдено", err),
                t => writeTasksToDb(config.getString("dbPath"), tasks.filterNot(_ == t))
            ) match
                case Failure(err) => throw new IllegalStateException("Ошибка при записи данных", err)
                case Success(_) => println("Задача успешно удалена")

        case "update" =>
            println(s"Will update task number of ${parseTaskNum(args)}")

        case _ =>
            throw new IllegalArgumentException(s"Неизвестная команда: $action")
