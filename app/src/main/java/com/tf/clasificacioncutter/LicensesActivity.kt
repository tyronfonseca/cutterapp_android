package com.tf.clasificacioncutter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem

class LicensesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licenses)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
