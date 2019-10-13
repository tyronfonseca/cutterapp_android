package com.tf.clasificacioncutter

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.tf.clasificacioncutter.Utils.CutterGetter
import com.tf.clasificacioncutter.Utils.CutterHelper
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val cutterGetter = CutterGetter()
    private val cutterHelper = CutterHelper()
    private val PRIVATE_MODE = 0
    private val PREF_NAME = "sharedPref"
    lateinit var sharedPref:SharedPreferences
    private val TYPE_DB = "DB"
    private val NUM_CUTTER = "numeroCutter"
    private val CUTTER_USED = "cutterUsado"

    var dbType:Int = 0
    lateinit var listaArray:ArrayList<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txvResult = findViewById<TextView>(R.id.txv_cutter_result)
        val txvCutter = findViewById<TextView>(R.id.txv_cutter_used)
        val txvCredit = findViewById<TextView>(R.id.txv_credit)

        val etxLastName = findViewById<EditText>(R.id.etx_last_name)
        val etxName = findViewById<EditText>(R.id.etx_name)

        val btSearch = findViewById<Button>(R.id.bt_search)
        val btVersion = findViewById<ImageButton>(R.id.bt_db)

        sharedPref = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        sharedPref.registerOnSharedPreferenceChangeListener(this)

        //Verificar si exite un SharedPreferences con el valor de la version de Cutter
        if(!sharedPref.contains(TYPE_DB)){
            val editor = sharedPref.edit()
            editor.putInt(TYPE_DB,1)
            editor.putString(NUM_CUTTER,getString(R.string.empty_text))
            editor.putString(CUTTER_USED,getString(R.string.empty_text_2))
            editor.apply()
            dbType = 1
        }else{
            //Conseguir la version de Cutter
            dbType = sharedPref.getInt(TYPE_DB,0)
            txvResult.text = sharedPref.getString(NUM_CUTTER,null)
            txvCutter.text = sharedPref.getString(CUTTER_USED,null)
        }

        //Cambiar tipo de letra
        val typeFaceBold: Typeface? = ResourcesCompat.getFont(this.applicationContext,R.font.kollektif_bold)
        val typeFaceNormal: Typeface? = ResourcesCompat.getFont(this.applicationContext,R.font.kollektif)

        txvResult.typeface = typeFaceBold
        etxLastName.typeface = typeFaceNormal
        etxName.typeface = typeFaceNormal
        btSearch.typeface = typeFaceBold


        //Lista de nombres y numeros de Cutter
        listaArray = cutterGetter.getCutterList(this,dbType)

        //Animacion de salida
        val fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out)
        //Animacion de entrada
        val fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in)

        //Agrega funcionalidad al boton de busqueda
        btSearch.setOnClickListener{
            //Conseguimos los valores de los EditTexts
            val lastName = etxLastName.text.toString().toUpperCase(Locale.ROOT)
            val name  = etxName.text.toString().toUpperCase(Locale.ROOT)

            //Verificamos que no esten vacios
            if(name.isNotEmpty() && lastName.isNotEmpty()){
                //Array con el resultado de la notacion de Cutter
                val result = cutterGetter.search(name,lastName,dbType,listaArray)

                //Primera letra del apellido
                var letter = result[0][0].toString()
                //Si es UCR se verifica si la primera letra es "CH" o "LL"
                if (dbType==1){
                    letter = cutterHelper.firstLetter(result[0])
                }
                //Resultado de Notacion de Cutter. Ej. F676
                val cutter = letter+result[1]

                //Cutter que se utilizo para dar la respuesta
                val cutterUsed = result[0]+": "+result[1]

                //Guardar valores
                val editor = sharedPref.edit()
                editor.putString(NUM_CUTTER,cutter)
                editor.putString(CUTTER_USED,cutterUsed)
                editor.apply()

                //Iniciamos las animaciones de salida
                txvResult.startAnimation(fadeOut)
                txvCutter.startAnimation(fadeOut)


                fadeOut.setAnimationListener(object: Animation.AnimationListener{
                    override fun onAnimationStart(p0: Animation?) {}
                    override fun onAnimationRepeat(p0: Animation?) {}

                    override fun onAnimationEnd(p0: Animation?) {
                        //Cuando las animaciones terminen, iniciamos las animaciones de entrada
                        txvCutter.startAnimation(fadeIn)
                        txvResult.startAnimation(fadeIn)
                        //Cambiamos los valores por la respuesta (Cotacion de Cutter)
                        txvCutter.text = cutterUsed
                        txvResult.text = cutter
                    }
                })
            }else{
                //Si alguno o ambos de los EditTexts estan vacios se lo hacemos saber al usuario
                Snackbar.make(findViewById(R.id.container),R.string.msg_error, Snackbar.LENGTH_LONG).show()
            }
        }

        //Cambiar de version de Cutter
        btVersion.setOnClickListener{
            selectVersion()
        }

        //Abrir Activity de Acerca de
        txvCredit.setOnClickListener {
            startActivity(Intent(this@MainActivity,LicensesActivity::class.java))
        }

    }

    //Cuando haya un cambio en los SharedPreferences se llamara a esta funcion
    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        //Version de cutter
        val get = sharedPref.getInt(TYPE_DB,0)
        //Actualizar lista con la version correcta
        listaArray = cutterGetter.getCutterList(this,get)
        //Modificar la version
        dbType = get
    }

    /**
     * Funcion encargada de mostrar un AlertDialog en el cual se seleccionara la version de Cutter a utilizar
     */
    private fun selectVersion(){
        //Versiones disponibles
        val versiones = arrayOf("Normal","UCR")

        //Inicializamos el contructor de AlertDialogs
        val builder = AlertDialog.Builder(this)
        //Titulo del AlertDialog
        builder.setTitle(R.string.db_version_select)

        //Creamos una lista de seleccion unica
        builder.setSingleChoiceItems(versiones,sharedPref.getInt(TYPE_DB,0)) { _, which->
            //Que version escogio el usuario
            val selection = versiones[which]
            try{
                //Editor del SharedPreferences
                val editor = sharedPref.edit()
                if (selection=="UCR"){
                    //Si selecciono UCR se cambia la version de cutter
                    editor.putInt(TYPE_DB,1)
                    Toast.makeText(this,"Version UCR seleccionada.",Toast.LENGTH_SHORT).show()
                }else{
                    //Si selecciono NORMAl se cambia la version de cutter
                    editor.putInt(TYPE_DB,0)
                    Toast.makeText(this,"Version Normal seleccionada.",Toast.LENGTH_SHORT).show()
                }
                //Guardamos los cambios
                editor.apply()

            }catch(e:IllegalArgumentException){
                Log.e("TF",e.toString())
            }
        }
        //Creamos el AlertDialog con las caracteristicas de 'builder'
        val alertDialog = builder.create()
        //Mostramos el AlertDialog
        alertDialog.show()
    }

}
