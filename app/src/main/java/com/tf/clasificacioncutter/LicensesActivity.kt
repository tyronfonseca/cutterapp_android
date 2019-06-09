package com.tf.clasificacioncutter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.widget.TextView

class LicensesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licenses)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val kotlin_link = findViewById<TextView>(R.id.kotlin_link)
        val opencsv_link = findViewById<TextView>(R.id.opencsv_link)

        kotlin_link.movementMethod = LinkMovementMethod.getInstance()
        opencsv_link.movementMethod = LinkMovementMethod.getInstance()

    }

}
