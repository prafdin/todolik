package ru.prafdin.todolik.utils

import java.io.File
import java.nio.file.Files
import scala.sys.process.*
import scala.util.Using

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

    def edit(file: TempFile, editor: String): Unit = {
        val command = f"$editor ${file.getAbsolutePath()}"
        val rc = command.run(BasicIO.standard(connectInput = true)).exitValue()
        if (rc != 0) {
            throw new IllegalStateException(f"При попытке вызвать редактор получен return code = $rc")
        }
    }
}
