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
import com.tf.clasificacioncutter.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val cutterGetter = CutterGetter()
    private val cutterHelper = CutterHelper()
    private val PRIVATE_MODE = 0
    private val PREF_NAME = "sharedPref"
    private lateinit var sharedPref:SharedPreferences
    private val TYPE_DB = "DB"
    private val NUM_CUTTER = "numeroCutter"
    private val CUTTER_USED = "cutterUsado"

    private var dbType:Int = 0 //Tipo de cutter por defecto
    private lateinit var listaArray:ArrayList<Array<String>>

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val txvResult = binding.txvCutterResult
        val txvCutter = binding.txvCutterUsed

        val etxLastName = binding.etxLastName
        val etxName = binding.etxName

        val btSearch = binding.btSearch

        sharedPref = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        sharedPref.registerOnSharedPreferenceChangeListener(this)

        //Verificar si exite un SharedPreferences con el valor de la version de Cutter
        if(!sharedPref.contains(TYPE_DB)){
            val editor = sharedPref.edit()
            this.selectVersion()
            editor.putString(NUM_CUTTER,getString(R.string.empty_text))
            editor.putString(CUTTER_USED,getString(R.string.empty_text_2))
            editor.apply()
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

        //Animaciones
        val fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out)
        val fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in)

        //Agrega funcionalidad al boton de busqueda
        btSearch.setOnClickListener{
            val lastName = etxLastName.text.toString().toUpperCase(Locale.ROOT)
            val name  = etxName.text.toString().toUpperCase(Locale.ROOT)

            if(name.isNotEmpty() && lastName.isNotEmpty()){
                if(lastName.length>1){
                    val result = cutterGetter.search(name,lastName,dbType,listaArray)
                    //Primera letra del apellido
                    var letter = result[0][0].toString()
                    //Si es UCR se verifica si la primera letra es "CH" o "LL"
                    if (dbType==1){
                        letter = cutterHelper.firstLetter(result[0])
                    }
                    //Resultado de Notacion de Cutter. Ej. F676
                    val cutter = letter+result[1]
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
                            txvCutter.startAnimation(fadeIn)
                            txvResult.startAnimation(fadeIn)

                            txvCutter.text = cutterUsed
                            txvResult.text = cutter
                        }
                    })
                }
                else{
                    Snackbar.make(findViewById(R.id.container),R.string.msg_error_last_name, Snackbar.LENGTH_LONG).show()
                }
            }else{
                Snackbar.make(findViewById(R.id.container),R.string.msg_error, Snackbar.LENGTH_LONG).show()
            }
        }

        //Cambiar de version de Cutter
        binding.btDb.setOnClickListener{
            selectVersion()
        }

        //Abrir Activity de Acerca de
        binding.txvCredit.setOnClickListener {
            startActivity(Intent(this@MainActivity,LicensesActivity::class.java))
        }

    }

    //Cuando haya un cambio en los SharedPreferences se llamara a esta funcion
    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        //Version de cutter
        val get = sharedPref.getInt(TYPE_DB,0)
        listaArray = cutterGetter.getCutterList(this,get)
        dbType = get
    }

    /**
     * Funcion encargada de mostrar un AlertDialog en el cual se seleccionara la version de Cutter a utilizar
     */
    private fun selectVersion(){
        //Versiones disponibles
        val normalString = getString(R.string.abc_normal)
        val ucrString = getString(R.string.abc_ucr)
        val versiones = arrayOf(normalString, ucrString)
        var selected = 0
        //Inicializamos el contructor de AlertDialogs
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.db_version_select)
        builder.setSingleChoiceItems(versiones,sharedPref.getInt(TYPE_DB,0)) { _, which->
            selected = which
            changeSharedPreferences(versiones[selected])
        }
        builder.setNegativeButton(R.string.close){ _, which->
            changeSharedPreferences(versiones[selected])
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun changeSharedPreferences(selection: String){
        val editor = sharedPref.edit()
        try{
            //Selecciono UCR
            if (selection.equals(getString(R.string.abc_ucr))){
                editor.putInt(TYPE_DB,1)
                Toast.makeText(this,R.string.select_ucr,Toast.LENGTH_SHORT).show()
            }else{
                editor.putInt(TYPE_DB,0)
                Toast.makeText(this,R.string.select_normal,Toast.LENGTH_SHORT).show()
            }
            editor.apply()
        }catch(e:IllegalArgumentException){
            Log.e("TF",e.toString())
        }
    }
}