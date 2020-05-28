import java.nio.file.Files
import java.nio.file.Paths
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class CFileManager(iFileName : String) {
    //  *** MEMBERS ***
    private val m_FileName = iFileName
    //  *** METHODS ***
    fun SaveDatas(iGame : CGame) {
        ObjectOutputStream(FileOutputStream(m_FileName)).use{ it.writeObject(iGame)}
    }
    fun LoadDatas() : CGame? {
        var retValue : CGame? = null
        if(CheckFile()) {
            ObjectInputStream(FileInputStream(m_FileName)).use {
                val game = it.readObject()
                when(game) {
                    is CGame -> retValue = game
                    else -> println("Deserialization failed")
                }
            }
        }
        return retValue
    }
    private fun CheckFile() : Boolean {
        return Files.exists(Paths.get(m_FileName))
    }
}
