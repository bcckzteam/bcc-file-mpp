package kz.bcc.filempp

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipFile

actual object FileManagerUtil {
    actual fun saveFile(name: String, byteArray: ByteArray) {
        removeFile(name)
        val tempFile = File.createTempFile(name, ".temp")
        tempFile.writeBytes(byteArray)
        tempFile.renameTo(File(cacheDir + name))

    }

    actual fun checkFile(name: String): Boolean {
        val list = getCacheDir().listFiles()?.map { it.path }
        return list?.find { it.equals(name) }?.isNotEmpty() == true
    }


    actual fun removeFile(name: String) {
        getCacheDir().listFiles()?.filter { it.path.equals(name) }?.forEach {
            it.deleteOnExit()
        }
    }

    actual fun getFile(name: String): ByteArray? {
        val list = getCacheDir().listFiles()?.map { it.path }
        return list?.find { it.equals(name) }?.toByteArray()
    }

    actual fun unzipFile(name: String) {
        unzip(File(cacheDir + name), cacheDir)
    }

    actual fun getJsonAsString(name: String): String {
        val jsonFile = File(cacheDir + name)
        return if (jsonFile.exists() && jsonFile.isFile && name.endsWith(".json")) {
            jsonFile.readText()
        } else {
            ""
        }
    }

    private const val BUFFER_SIZE = 4096

    private var cacheDir = ""

    private fun getCacheDirName(): String {
        return if (cacheDir.isNotBlank()) cacheDir
        else {
            val tempFile = File.createTempFile("temp", ".temp")
            cacheDir = tempFile.path.substringBefore("temp")
            cacheDir
        }
    }

    private fun getCacheDir(): File {
        return File(getCacheDirName())
    }


    private fun unzip(zipFilePath: File, destDirectory: String) {
        File(destDirectory).run {
            if (!exists()) {
                mkdirs()
            }
        }

        ZipFile(zipFilePath).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    val filePath = destDirectory + File.separator + entry.name
                    if (!entry.isDirectory) {
                        // if the entry is a file, extracts it
                        extractFile(input, filePath)
                    } else {
                        // if the entry is a directory, make the directory
                        val dir = File(filePath)
                        dir.mkdir()
                    }

                }

            }
        }
    }


    private fun extractFile(inputStream: InputStream, destFilePath: String) {
        val bos = BufferedOutputStream(FileOutputStream(destFilePath))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }


}