package com.tf.clasificacioncutter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView

class LicensesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licenses)

        //Mostrar botón para retroceder
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val kotlinLink = findViewById<TextView>(R.id.kotlin_link)
        val opencsvLink = findViewById<TextView>(R.id.opencsv_link)

        //Estos TextViews tienen links que hay que habilitar
        kotlinLink.movementMethod = LinkMovementMethod.getInstance()
        opencsvLink.movementMethod = LinkMovementMethod.getInstance()

    }

}
