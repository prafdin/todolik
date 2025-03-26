package ru.prafdin.todolik

import ru.prafdin.todolik.utils.TaskNumber

class TaskTablePrinter {
    def printTasks(tasks: List[Task]): Unit = {
        val headers = Seq("№", "Название", "Описание")
        val rows = tasks.zipWithIndex.map { case (task, idx) =>
            Seq(TaskNumber.prepareTaskNumberForUser(idx), task.title, task.description.takeWhile(_ != '\n'))
        }

        val colWidths = (headers +: rows).transpose.map(_.map(_.length).max)

        def formatRow(row: Seq[String]): String = {
            row.zip(colWidths).map { case (value, width) =>
                value.padTo(width, ' ')
            }.mkString(" | ")
        }

        val separator = colWidths.map("-" * _).mkString("-+-")
        println(formatRow(headers))
        println(separator)
        println(rows.collect{ formatRow(_) }.mkString("\n"))
    }
}
