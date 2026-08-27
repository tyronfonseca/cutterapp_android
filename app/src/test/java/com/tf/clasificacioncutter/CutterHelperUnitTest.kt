package com.tf.clasificacioncutter

import com.tf.clasificacioncutter.utils.CutterHelper
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Testing core methods of the class CutterHelper with the new logic.
 */
class CutterHelperUnitTest {

    private val cutterHelper = CutterHelper()

    @Test
    fun removeAccents_works() {
        assertEquals("FONSECA", cutterHelper.removeAccents("FONSECA"))
        assertEquals("AEIOU", cutterHelper.removeAccents("ÁÉÍÓÚ"))
        assertEquals("NINO", cutterHelper.removeAccents("NIÑO"))
    }

    @Test
    fun removeAccents_emptyString() {
        assertEquals("", cutterHelper.removeAccents(""))
    }

    @Test
    fun removeAccents_withNumbersAndSpecialChars() {
        assertEquals("PRUEBA 123!", cutterHelper.removeAccents("Prueba 123!"))
        assertEquals("HOLA-MUNDO", cutterHelper.removeAccents("hóla-múndö"))
    }

    @Test
    fun removeAccents_lowercaseInput() {
        assertEquals("ABC", cutterHelper.removeAccents("abc"))
    }

    @Test
    fun removeAccents_mixedCharacters() {
        assertEquals("CAFE", cutterHelper.removeAccents("café"))
        assertEquals("CANON", cutterHelper.removeAccents("cañón"))
        assertEquals("LEON", cutterHelper.removeAccents("León"))
    }
}
