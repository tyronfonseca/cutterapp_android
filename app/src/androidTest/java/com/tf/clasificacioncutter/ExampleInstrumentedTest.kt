package com.tf.clasificacioncutter

import android.content.Context
import android.util.Log
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.tf.clasificacioncutter.Utils.CutterGetter

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.tf.clasificacioncutter", appContext.packageName)
    }

    @Test
    fun getFile_works(){
        val context: Context = InstrumentationRegistry.getTargetContext()
        val lista = CutterGetter().getCutterList(context.applicationContext,1)
        Log.d("SIZE",lista.size.toString())
//        for (item in lista){
//            Log.d("ITEM", item[0]+" "+item[1])
//        }

    }
    @Test
    fun getCutter_works(){
        val context: Context = InstrumentationRegistry.getTargetContext()
        val lista = CutterGetter().getCutterList(context.applicationContext, 1)
        val name = "ROGER"
        val lastName = "BACH"


        val result = CutterGetter().search(name,lastName,2,lista)

        Log.e("RESULADO:",result[0]+" "+result[1])
    }
}
