package ru.prafdin.todolik.utils

import scala.util.Using
import java.io.File
import java.nio.file.Files

class TempFile(prefix: String = "temp", suffix: String = ".tmp") {
    val file: File = Files.createTempFile(prefix, suffix).toFile
    export file.*
    def write(content: String): Unit = {
        Files.write(file.toPath, content.getBytes)
    }

    def read(): String = {
        new String(Files.readAllBytes(file.toPath))
    }
}

object TempFile {
    // Реализация трейта Releasable для TempFile
    implicit val tempFileReleasable: Using.Releasable[TempFile] = (resource: TempFile) => {
        if (resource.file.exists()) {
            resource.file.delete()
        }
    }
}
