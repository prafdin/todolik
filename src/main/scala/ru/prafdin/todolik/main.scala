package ru.prafdin.todolik

import com.jcabi.manifests.Manifests
import com.typesafe.config.ConfigFactory
import io.circe.generic.auto.*
import io.circe.parser.decode
import io.circe.syntax.*
import ru.prafdin.todolik.utils.TempFile

import java.io.{BufferedWriter, FileWriter}
import java.nio.file.{Files, Path, Paths}
import scala.io.Source
import scala.util.*

given CommandLineParser.FromString[Option[String]] with
    def fromString(value: String): Option[String] = Some(value)

def parseTaskNum(args: Seq[String]): Int =
    args.headOption.getOrElse(
        throw new IllegalStateException("Необходимо указать номер заметки")
    ).toIntOption.getOrElse(
        throw new IllegalStateException("Номер заметки должен быть числом")
    )

def writeTasksToDb(dbPath: Path, tasks: List[Task]): Try[Unit] = {
    Using(new BufferedWriter(new FileWriter(dbPath.toFile))) { writer =>
        writer.write(tasks.asJson.toString)
    }
}

@main
def main(action: String, args: String*): Unit =
    val configPath = Paths.get(System.getProperty("user.home"), ".config", "todolik", "todolik.conf")

    if (!Files.exists(configPath)) {
        Files.createDirectories(configPath.getParent)
        Option(this.getClass.getClassLoader.getResourceAsStream("todolik.conf")) match
            case Some(inputStream) => Files.copy(
                inputStream,
                configPath
            )
            case None => throw new IllegalStateException("В приложении отсутствует ресурс todolik.conf")
    }
    val config = ConfigFactory.parseFile {
        new java.io.File(configPath.toString)
    }

    val editor = config.getString("editor")
    val dbPath = Paths.get(config.getString("dbPath"))

    lazy val rawJsonFromBd = {
        if (!Files.exists(dbPath)) {
            Files.createFile(dbPath)
        }
        Using(Source.fromFile(dbPath.toFile)) { s =>
            s.getLines().mkString
        }.fold(
            err => throw IllegalStateException("Что-то пошло не так при чтении БД", err),
            identity
        )
    }

    lazy val tasks =
        if (rawJsonFromBd.isEmpty) List.empty
        else decode[List[Task]] {
            rawJsonFromBd
        }.fold(
            err => throw new IllegalStateException("Ошибка при чтении списка задач", err),
            identity
        )

    action match
        case "version" =>
            println(Try(Manifests.read("Todolik-Version")).getOrElse("Unknown version"))
        case "list" =>
            TaskTablePrinter().printTasks(tasks)
        case "create" =>
            val title = args.headOption.getOrElse(
                throw new IllegalStateException("Необходимо передать название новой заметки")
            )
            val editor = Try(config.getString("editor")).getOrElse("vi")

            val taskDescription = Using(TempFile()) { f =>
                TempFile.edit(f, editor)
                f.read()
            }.fold(err =>
                throw new IllegalStateException("Ошибка при попытке создании новой задачи", err),
                identity
            )

            writeTasksToDb(dbPath, Task(title, taskDescription) :: tasks) match
                case Failure(err) => throw new IllegalStateException("Ошибка при записи данных", err)
                case Success(_) => println("Задача успешно создана")

        case "delete" =>
            val taskNum = args.headOption
                .flatMap(_.toIntOption)
                .getOrElse(throw new IllegalStateException("Необходимо передать корректный номер заметки"))

            Try(tasks(taskNum)).fold(
                err => throw new IllegalStateException(f"Заметки по индексу $taskNum не найдено", err),
                t => writeTasksToDb(dbPath, tasks.filterNot(_ == t))
            ) match
                case Failure(err) => throw new IllegalStateException("Ошибка при записи данных", err)
                case Success(_) => println("Задача успешно удалена")

        case "update" =>
            val taskNum = args.headOption
                .flatMap(_.toIntOption)
                .getOrElse(throw new IllegalStateException("Необходимо передать корректный номер заметки"))

            val task = Try(tasks(taskNum)).fold(err =>
                throw new IllegalStateException(f"Заметка по индексу $taskNum не найдена", err),
                identity
            )

            val newDescription = Using(TempFile()) { f =>
                f.write(task.description)
                TempFile.edit(f, editor)
                f.read()
            }.fold(err =>
                throw new IllegalStateException("Ошибка при попытке редактировании задачи", err),
                identity
            )

            writeTasksToDb(
                dbPath,
                tasks.updated(taskNum, task.copy(description = newDescription))
            ) match
                case Failure(err) => throw new IllegalStateException("Ошибка при записи данных", err)
                case Success(_) => println("Задача успешно обновлена")

        case _ =>
            throw new IllegalArgumentException(s"Неизвестная команда: $action")
