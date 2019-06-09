package com.tf.clasificacioncutter

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.tf.clasificacioncutter.Utils.CutterGetter
import com.tf.clasificacioncutter.Utils.CutterHelper

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val cutterGetter = CutterGetter()
    private val cutterHelper = CutterHelper()
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "sharedPref"
    lateinit var sharedPref:SharedPreferences
    val dbString = "DB"
    var dbType:Int = 0
    lateinit var listaArray:ArrayList<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPref = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        sharedPref.registerOnSharedPreferenceChangeListener(this)

        if(!sharedPref.contains(dbString)){
            val editor = sharedPref.edit()
            editor.putInt(dbString,1)
            editor.apply()
            dbType = 1
        }else{
            dbType = sharedPref.getInt(dbString,0)
        }


        val txvResult = findViewById<TextView>(R.id.txv_cutter_result)
        val txvCutter = findViewById<TextView>(R.id.txv_cutter_used)

        val etxLastName = findViewById<EditText>(R.id.etx_last_name)
        val etxName = findViewById<EditText>(R.id.etx_name)

        val btSearch = findViewById<Button>(R.id.bt_search)
        val btVersion = findViewById<ImageButton>(R.id.bt_db)

        listaArray = cutterGetter.getCutterList(this,dbType)

        val fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out)
        val fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in)

        btSearch.setOnClickListener{

            val lastName = etxLastName.text.toString().toUpperCase()
            val name  = etxName.text.toString().toUpperCase()

            if(name.isNotEmpty() && lastName.isNotEmpty()){
                val result = cutterGetter.search(name,lastName,dbType,listaArray)
                var letter = lastName[0].toString()
                if (dbType==1){
                    letter = cutterHelper.firstLetter(lastName)
                }
                val cutter = letter+result[1]
                val cutterUsed = result[0]+": "+result[1]

                txvResult.startAnimation(fadeOut)

                txvCutter.startAnimation(fadeOut)

                fadeOut.setAnimationListener(object: Animation.AnimationListener{
                    override fun onAnimationStart(p0: Animation?) {
                    }

                    override fun onAnimationRepeat(p0: Animation?) {}

                    override fun onAnimationEnd(p0: Animation?) {
                        txvCutter.startAnimation(fadeIn)
                        txvResult.startAnimation(fadeIn)
                        txvCutter.text = cutterUsed
                        txvResult.text = cutter
                    }
                })
            }else{
                Snackbar.make(findViewById(R.id.container),R.string.msg_error, Snackbar.LENGTH_LONG).show()
            }
        }
        btVersion.setOnClickListener{
            selectVersion()
        }

    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        val get = sharedPref.getInt(dbString,0)
        listaArray = cutterGetter.getCutterList(this,get)
        dbType = get
    }

    private fun selectVersion(){

        val versiones = arrayOf("Normal","UCR")

        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.db_version_select)

        builder.setSingleChoiceItems(versiones,sharedPref.getInt(dbString,0)) { _, which->
            val selection = versiones[which]
            try{
                val editor = sharedPref.edit()
                if (selection=="UCR"){
                    editor.putInt(dbString,1)
                }else{
                    editor.putInt(dbString,0)
                }
                editor.apply()

            }catch(e:IllegalArgumentException){
                Log.e("TF",e.toString())
            }
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

}
