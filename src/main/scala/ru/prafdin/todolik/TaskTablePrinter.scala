package ru.prafdin.todolik

class TaskTablePrinter {
    def printTasks(tasks: List[Task]): Unit = {
        val headers = Seq("№", "Название", "Описание")
        val rows = tasks.zipWithIndex.map { case (task, idx) =>
            Seq(idx.toString, task.title, task.description)
        }

        // Вычисляем максимальную ширину для каждого столбца
        val colWidths = (headers +: rows).transpose.map(_.map(_.length).max)

        // Метод для форматированного вывода строки
        def formatRow(row: Seq[String]): String = {
            row.zip(colWidths).map { case (value, width) =>
                value.padTo(width, ' ') // Дополняем пробелами до нужной ширины
            }.mkString(" | ")
        }

        // Выводим таблицу
        val separator = colWidths.map("-" * _).mkString("-+-")
        println(formatRow(headers)) // Вывод заголовков
        println(separator) // Разделитель
        rows.foreach(row => println(formatRow(row))) // Вывод строк задач
    }
}
