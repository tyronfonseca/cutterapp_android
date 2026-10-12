package com.tf.clasificacioncutter

import com.tf.clasificacioncutter.data.model.CutterSearchOptions
import com.tf.clasificacioncutter.data.model.TextBeforeAfterNum
import com.tf.clasificacioncutter.data.model.cutterdata.CutterData
import com.tf.clasificacioncutter.data.settings.IAppSettings
import com.tf.clasificacioncutter.utils.CutterGetter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CutterGetterUnitTest {

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

    private val defaultOptions = CutterSearchOptions(
        settings = mockSettings,
        dontSeparateName = false,
        ignoreArticles = true,
        useFormatMac = true
    )

    private val data = listOf(
        CutterData(name = "Andrade", code = "254"),
        CutterData(name = "Fons, X", code = "24"),
        CutterData(name = "Zy", code = "99")
    )

    @Test
    fun search_exact() {
        val result = CutterGetter.search("X", "FONS", data, defaultOptions)
        assertNotNull(result)
        assertEquals("Fons, X", result?.name)
        assertEquals("24", result?.code)
    }

    @Test
    fun search_close() {
        val result = CutterGetter.search("X", "FONSECA", data, defaultOptions)
        assertNotNull(result)
        assertEquals("Fons, X", result?.name)
        assertEquals("24", result?.code)
    }

    @Test
    fun search_before() {
        val result = CutterGetter.search("M", "BARRANTES", data, defaultOptions)
        assertNotNull(result)
        assertEquals("Andrade", result?.name)
        assertEquals("254", result?.code)
    }

    @Test
    fun search_after() {
        val result = CutterGetter.search("T", "ZZZZ", data, defaultOptions)
        assertNotNull(result)
        assertEquals("Zy", result?.name)
        assertEquals("99", result?.code)
    }

    @Test
    fun search_emptyList() {
        val result = CutterGetter.search("Juan", "Perez", emptyList(), defaultOptions)
        assertNull(result)
    }

    @Test
    fun search_singleElementList() {
        val singleData = listOf(CutterData(name = "Perez", code = "123"))

        val resultMatch = CutterGetter.search("", "Perez", singleData, defaultOptions)
        assertNotNull(resultMatch)
        assertEquals("Perez", resultMatch?.name)

        val resultBefore = CutterGetter.search("", "Alvarez", singleData, defaultOptions)
        assertNotNull(resultBefore)
        assertEquals("Perez", resultBefore?.name)

        val resultAfter = CutterGetter.search("", "Zapata", singleData, defaultOptions)
        assertNotNull(resultAfter)
        assertEquals("Perez", resultAfter?.name)
    }

    @Test
    fun search_blankName() {
        val result = CutterGetter.search("", "Andrade", data, defaultOptions)
        assertNotNull(result)
        assertEquals("Andrade", result?.name)
    }

    @Test
    fun search_withAccentsNormalization() {
        val result = CutterGetter.search("X", "FÓNS", data, defaultOptions)
        assertNotNull(result)
        assertEquals("Fons, X", result?.name)
    }

    @Test
    fun search_caseInsensitive() {
        val result = CutterGetter.search("x", "fons", data, defaultOptions)
        assertNotNull(result)
        assertEquals("Fons, X", result?.name)
    }

    @Test
    fun search_oldAlphabetChall() {
        val oldData = listOf(
            CutterData(name = "Chal", code = "436"),
            CutterData(name = "Chalm", code = "437"),
            CutterData(name = "Chall", code = "441"),
            CutterData(name = "Cham", code = "442")
        )
        val result = CutterGetter.search("", "Chall", oldData, defaultOptions, isOldAlphabet = true)
        assertNotNull(result)
        assertEquals("Chall", result?.name)
        assertEquals("441", result?.code)
    }

    @Test
    fun search_oldAlphabetLlAndN() {
        val oldData = listOf(
            CutterData(name = "Llaveria", code = "790"),
            CutterData(name = "Nunez", code = "850"),
            CutterData(name = "Ond", code = "870")
        )
        val resultLl = CutterGetter.search("", "Llaveria", oldData, defaultOptions, isOldAlphabet = true)
        assertNotNull(resultLl)
        assertEquals("Llaveria", resultLl?.name)
        assertEquals("790", resultLl?.code)

        val resultN = CutterGetter.search("", "Nuñez", oldData, defaultOptions, isOldAlphabet = true)
        assertNotNull(resultN)
        assertEquals("Nunez", resultN?.name)
        assertEquals("850", resultN?.code)
    }

    @Test
    fun search_dontSeparateNameOption() {
        val customOptions = CutterSearchOptions(
            settings = mockSettings,
            dontSeparateName = true,
            ignoreArticles = false,
            useFormatMac = false
        )
        val customData = listOf(
            CutterData(name = "John Smith", code = "500")
        )
        val result = CutterGetter.search("John", "Smith", customData, customOptions)
        assertNotNull(result)
        assertEquals("John Smith", result?.name)
    }

    @Test
    fun search_ignoreArticlesOption() {
        val customOptions = CutterSearchOptions(
            settings = mockSettings,
            dontSeparateName = false,
            ignoreArticles = true,
            useFormatMac = false
        )
        val customData = listOf(
            CutterData(name = "King", code = "100")
        )
        val result = CutterGetter.search("The", "King", customData, customOptions)
        assertNotNull(result)
        assertEquals("King", result?.name)
    }

    @Test
    fun search_useFormatMacOption() {
        val customOptions = CutterSearchOptions(
            settings = mockSettings,
            dontSeparateName = false,
            ignoreArticles = false,
            useFormatMac = true
        )
        val customData = listOf(
            CutterData(name = "Mac Donald", code = "300")
        )
        val result = CutterGetter.search("", "Mc Donald", customData, customOptions)
        assertNotNull(result)
        assertEquals("Mac Donald", result?.name)
    }

    @Test
    fun splitText_variousFormats() {
        val (name1, surname1) = CutterGetter.splitText("Smith, John")
        assertEquals("John", name1)
        assertEquals("Smith", surname1)

        val (name2, surname2) = CutterGetter.splitText("Garcia Marquez")
        assertEquals("Garcia", name2)
        assertEquals("Marquez", surname2)

        val (name3, surname3) = CutterGetter.splitText("Perez")
        assertEquals("", name3)
        assertEquals("Perez", surname3)
    }
}
