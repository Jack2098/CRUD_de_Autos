package kotlinforandroid.book.cruddeautos

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_auto.*
import kotlinx.android.synthetic.main.activity_nuevo_auto.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class NuevoAutoActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 101

    //selección de imagen Constants
    private val IMAGE_PICK_CAMERA_CODE = 102
    private val IMAGE_PICK_GALLERY_CODE = 103

    // matrices de permisos
    private val cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE) // cámara y almacenamiento

    private var storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var imageUri: Uri? = Uri.EMPTY
    private var imageAuto:String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_auto)

        var idAuto: Int? = null
        if (intent.hasExtra("auto")){
            val auto = intent.extras?.getSerializable("auto") as Auto
            nuevo_nombre_auto.setText(auto.nombre)
            nuevo_color_auto.setText(auto.color)
            nuevo_precio_auto.setText(auto.precio.toString())
            nuevo_descripcion_auto.setText(auto.descripcion)
            if (auto.imagen==""){

            }else{
                nuevo_imagen_auto.setImageURI(auto.imagen.toUri())
                imageAuto = auto.imagen
            }
            Log.d("imageUri","-----------------")
            Log.d("imageUri",imageUri.toString())
            idAuto = auto.idAuto
        }

        val dataBase = AppDataBase.getDatabase(this)

        nuevo_imagen_auto.setOnClickListener {
            imagePickDialog();
        }

        btn_guardar.setOnClickListener {
            val nombre = nuevo_nombre_auto.text.toString()
            val color = nuevo_color_auto.text.toString()
            val precio = nuevo_precio_auto.text.toString()
            val descripcion = nuevo_descripcion_auto.text.toString()
            var imagen:String = imageAuto
            if(imageUri!= Uri.EMPTY){
                imagen = imageUri.toString()
            }

            val auto = Auto(nombre,imagen,descripcion,color,precio.toDouble())
            if(idAuto != null){
                CoroutineScope(Dispatchers.IO).launch {
                    auto.idAuto = idAuto
                    dataBase.autos().update(auto)

                    this@NuevoAutoActivity.finish()
                }
            }else{
                CoroutineScope(Dispatchers.IO).launch {
                    dataBase.autos().insertAll(auto)

                    this@NuevoAutoActivity.finish()
                }
            }
        }
    }

    private fun imagePickDialog() {

        val options = arrayOf("Cámara","Galería")

        //dialogo
        val builder = AlertDialog.Builder(this)
        //Titulo
        //Titulo
        builder.setTitle("Seleccionar imagen")
        // establecer elementos / opciones
        // establecer elementos / opciones
        builder.setItems(options) { dialog, which ->
            // manejar clicks
            if (which == 0) {
                //click en camara
                if (!checkCameraPermission()) {
                    requestCameraPermission()
                } else {
                    // permiso ya otorgado
                    PickFromCamera()
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    // permiso ya otorgado
                    PickFromGallery()
                }
            }
        }

        // Crear / mostrar diálogo

        // Crear / mostrar diálogo
        builder.create().show()

    }

    private fun checkCameraPermission(): Boolean {
        // verifica si el permiso de la cámara está habilitado o no
        val result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return result && result1
    }

    private fun requestCameraPermission() {
        // solicita el permiso de la cámara
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE)
    }

    private fun PickFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Titulo de la Imagen")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Descripción de la imagen")
        //put image Uri
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)!!

        // Intento de abrir la cámara para la imagen
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE)
    }

    private fun checkStoragePermission(): Boolean {
        //comprobar si el permiso de almacenamiento está habilitado o no
        return ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        // solicita el permiso de almacenamiento
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE)
    }

    private fun PickFromGallery() {
        // intento de elegir la imagen de la galería, la imagen se devolverá en el método onActivityResult
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String?>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.size > 0) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted) {
                        // ambos permisos permitidos
                        PickFromCamera()
                    } else {
                        Toast.makeText(this,"Se requieren permisos de cámara y almacenamiento",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE -> {
                if (grantResults.size > 0) {

                    // si se permite devolver verdadero de lo contrario falso
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted) {
                        // permiso de almacenamiento permitido
                        PickFromGallery()
                    } else {
                        Toast.makeText(this,"Se requiere permiso de almacenamiento",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //image picked from camera or gallery will be received hare
        if (resultCode == RESULT_OK) {
            //Image is picked
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //Picked from gallery

                //crop image
                CropImage.activity(data!!.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //Picked from camera
                //crop Image
                CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this)
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                //Croped image received
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    val resultUri: Uri = result.getUri()
                    imageUri = resultUri
                    //set Image
                    Log.d("imageUri",imageUri.toString())
                    nuevo_imagen_auto.setImageURI(imageUri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    //ERROR
                    val error: Exception = result.getError()
                    Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}