package kotlinforandroid.book.cruddeautos

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.item_auto.view.*

class AutoAdapter(private val mContext: Context,private val listaAuto:List<Auto>): ArrayAdapter<Auto>(mContext,0,listaAuto){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout= LayoutInflater.from(mContext).inflate(R.layout.item_auto, parent, false)
        val auto=listaAuto[position]
        layout.nombre.text = auto.nombre
        layout.color.text = auto.color
        layout.precio.text = "${auto.precio}"
        layout.descripcion.text = auto.descripcion

        if (auto.imagen!=""){
            layout.imagen.setImageURI(auto.imagen.toUri())
        }

        return layout
    }
}