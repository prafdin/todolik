package ru.prafdin.todolik


import io.circe._ // Базовые импорты
import io.circe.generic.auto._ // Импорт авто-декодеров
import io.circe.parser._ // Для decode
import com.typesafe.config.ConfigFactory
import io.circe.parser.decode

import scala.io.Source
import scala.util.{CommandLineParser, Failure, Success, Using}

given CommandLineParser.FromString[Option[String]] with
    def fromString(value: String): Option[String] = Some(value)

def parseTaskNum(args: Seq[String]): Int =
    args.headOption.getOrElse(
        throw new IllegalStateException("Необходимо указать номер заметки")
    ).toIntOption.getOrElse(
        throw new IllegalStateException("Номер заметки должен быть числом")
    )

@main
def main(action: String, args: String*): Unit =
    val config = ConfigFactory.load()

    val rawJsonFromBd = Using(Source.fromFile(config.getString("dbPath"))) { s =>
        s.getLines().mkString
    }

    val t = decode[Task](
        rawJsonFromBd.getOrElse(throw IllegalStateException("Что-то пошло не так при чтении БД"))
    )

    println(t)

    action match
        case "list" =>
            println("Will list tasks")

        case "create" =>
            val title = args.headOption.getOrElse(
                throw new IllegalStateException("Необходимо передать название новой заметки")
            )
            println(s"Will create new one with $title")

        case "delete" =>
            println(s"Will delete task number of ${parseTaskNum(args)}")

        case "update" =>
            println(s"Will update task number of ${parseTaskNum(args)}")

        case _ =>
            throw new IllegalArgumentException(s"Неизвестная команда: $action")
