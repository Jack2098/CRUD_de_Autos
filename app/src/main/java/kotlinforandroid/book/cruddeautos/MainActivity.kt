package kotlinforandroid.book.cruddeautos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var listaAuto = emptyList<Auto>()

        val dataBase = AppDataBase.getDatabase(this)

        dataBase.autos().getAll().observe(this, Observer {
            listaAuto = it
            val adapter = AutoAdapter(mContext = this,listaAuto)
            lista.adapter = adapter
        })

        floatingActionButton.setOnClickListener{
            val intent = Intent(this,NuevoAutoActivity::class.java)
            startActivity(intent)
        }

        lista.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this,AutoActivity::class.java)
            intent.putExtra("id",listaAuto[position].idAuto)
            startActivity(intent)
        }
    }
}