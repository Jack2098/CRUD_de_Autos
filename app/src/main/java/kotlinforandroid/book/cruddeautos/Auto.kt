package kotlinforandroid.book.cruddeautos

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Auto")
class Auto(
    val nombre:String,
    val imagen: String,
    val descripcion:String,
    val color:String,
    val precio:Double,
    @PrimaryKey(autoGenerate = true)
    var idAuto:Int =0,):Serializable