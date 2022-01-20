package kz.bcc.filempp

expect object FileManagerUtil {
    fun saveFile(name: String, byteArray: ByteArray)
    fun checkFile(name: String): Boolean
    fun removeFile(name: String)
    fun getFile(name: String): ByteArray?
    fun unzipFile(name: String)
    fun getJsonAsString(name: String): String
}