package com.tf.clasificacioncutter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import com.tf.clasificacioncutter.databinding.ActivityLicensesBinding

class LicensesActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLicensesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLicensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Mostrar botón para retroceder
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val kotlinLink = binding.kotlinLink
        val opencsvLink = binding.opencsvLink

        //Estos TextViews tienen links que hay que habilitar
        kotlinLink.movementMethod = LinkMovementMethod.getInstance()
        opencsvLink.movementMethod = LinkMovementMethod.getInstance()

    }

}
