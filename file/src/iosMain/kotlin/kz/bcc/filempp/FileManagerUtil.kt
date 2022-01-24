package kz.bcc.filempp

import cocoapods.SSZipArchive.SSZipArchive
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.posix.memcpy

actual object FileManagerUtil {
    actual fun saveFile(name: String, byteArray: ByteArray) {
        val fileManager = NSFileManager.defaultManager()
        val filePath = getCacheDir() + name
        if (fileManager.fileExistsAtPath(filePath)) {
            removeFile(filePath)
        }
        fileManager.createFileAtPath(filePath, byteArray.toNSData(), null)
    }


    actual fun checkFile(name: String): Boolean {
        val fileManager = NSFileManager.defaultManager()
        return fileManager.fileExistsAtPath(name)
    }

    actual fun removeFile(name: String) {
        val fileManager = NSFileManager.defaultManager()
        fileManager.removeItemAtPath(name, null)
    }

    actual fun getFile(name: String): ByteArray? {
        val filePath = getCacheDir() + name
        return NSData.dataWithContentsOfFile(filePath)?.toByteArray()
    }

    actual fun unzipFile(name: String) {
        val path = getCacheDir()
        SSZipArchive.unzipFileAtPath(path + name, path)
    }

    actual fun getJsonAsString(name: String): String {
        val filePath = getCacheDir() + name
        val json = NSString.stringWithContentsOfFile(filePath, NSUTF8StringEncoding, null)
        return json ?: ""
    }


    private fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }

    private fun ByteArray.toNSData(): NSData = memScoped {
        NSData.create(
            bytes = allocArrayOf(this@toNSData),
            length = this@toNSData.size.toULong()
        )
    }

    private fun getCacheDir(): String {
        return (NSFileManager.defaultManager().URLsForDirectory(9.toULong(), 1.toULong())
            .firstOrNull() as? NSURL)?.path + "/"
    }
}