package ru.prafdin.todolik.utils

object TaskNumber {
    def parseTaskNumberFromUser(taskNumber: Option[String]): Option[Int] =
        taskNumber
            .flatMap(_.toIntOption)
            .map(_ - 1)
        
    def prepareTaskNumberForUser(taskNumber: Int): String = (taskNumber + 1).toString
}
