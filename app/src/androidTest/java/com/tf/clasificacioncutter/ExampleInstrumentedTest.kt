package com.tf.clasificacioncutter

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tf.clasificacioncutter.data.model.CutterSearchOptions
import com.tf.clasificacioncutter.data.model.TextBeforeAfterNum
import com.tf.clasificacioncutter.data.settings.IAppSettings
import com.tf.clasificacioncutter.utils.CutterGetter
import com.tf.clasificacioncutter.utils.csv.CSVHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device or emulator.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private val mockSettings = object : IAppSettings {
        override var dontSeparateName: Boolean = false
        override var ignoreArticles: Boolean = true
        override var textBeforeNum: TextBeforeAfterNum = TextBeforeAfterNum.AUTHOR_SURNAME
        override var charsBeforeNum: Int = 1
        override var textAfterNum: TextBeforeAfterNum = TextBeforeAfterNum.AUTHOR_SURNAME
        override var charsAfterNum: Int = 0
        override var includeExtrasInExport: Boolean = true
        override var useFormatMac: Boolean = true
    }

    private val options = CutterSearchOptions(
        settings = mockSettings,
        dontSeparateName = false,
        ignoreArticles = true,
        useFormatMac = true
    )

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.tf.clasificacioncutter", appContext.packageName)
    }

    @Test
    fun getCSVData_isNotNullAndNotEmpty() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val list = CSVHelper.getCSVData(appContext, R.raw.cutter_normal)

        assertNotNull("The cutter list should not be null", list)
        assertFalse("The cutter list should not be empty", list.isEmpty())
    }

    @Test
    fun search_withRealData_returnsValidResult() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val list = CSVHelper.getCSVData(appContext, R.raw.cutter_normal)

        val result = CutterGetter.search("Juan", "Perez", list, options)

        assertNotNull(result)
        assertFalse("The code should not be empty", result!!.code.isEmpty())
        assertFalse("The name should not be empty", result.name.isEmpty())
    }
}
