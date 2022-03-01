package kotlinforandroid.book.cruddeautos

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_auto.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AutoActivity : AppCompatActivity() {

    private lateinit var dataBase: AppDataBase

    private lateinit var auto: Auto
    private lateinit var autoLiveData: LiveData<Auto>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto)

        dataBase = AppDataBase.getDatabase(this)

        val idAuto = intent.getIntExtra("id",0)

        autoLiveData = dataBase.autos().get(idAuto)
        autoLiveData.observe(this, Observer{
            auto = it
            nombre_auto.text = auto.nombre
            color_auto.text = auto.color
            precio_auto.text = auto.precio.toString()
            descripcion_auto.text = auto.descripcion
            if (auto.imagen!=""){
                imagen_auto.setImageURI(auto.imagen.toUri())
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.auto_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.edit_item->{
                val intent = Intent(this, NuevoAutoActivity::class.java)
                intent.putExtra("auto",auto)
                startActivity(intent)
            }

            R.id.delete_item->{
                autoLiveData.removeObservers(this)
                CoroutineScope(Dispatchers.IO).launch {
                    dataBase.autos().delete(auto)
                    this@AutoActivity.finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}