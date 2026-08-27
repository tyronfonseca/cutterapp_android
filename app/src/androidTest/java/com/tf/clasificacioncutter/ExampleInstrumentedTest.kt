package com.tf.clasificacioncutter

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tf.clasificacioncutter.utils.CutterGetter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

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
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.tf.clasificacioncutter", appContext.packageName)
    }

    @Test
    fun getCutterList_isNotNullAndNotEmpty() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val list = CutterGetter().getCutterList(appContext)
        
        assertNotNull("The cutter list should not be null", list)
        assertFalse("The cutter list should not be empty", list.isEmpty())
    }

    @Test
    fun search_withRealData_returnsValidResult() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val getter = CutterGetter()
        val list = getter.getCutterList(appContext)

        // Perform a search with names that likely exist in any Cutter table predecessor
        val result = getter.search("Juan", "Perez", list)

        assertNotNull(result)
        assertEquals(2, result.size)
        assertFalse("The first element of the result should not be empty", result[0].isEmpty())
        assertFalse("The second element of the result should not be empty", result[1].isEmpty())
    }
}
