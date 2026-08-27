package com.tf.clasificacioncutter

import com.tf.clasificacioncutter.utils.CutterGetter
import org.junit.Assert.*
import org.junit.Test
class CutterGetterUnitTest{
    private val data = arrayListOf(arrayOf("Andrade", "254"), arrayOf("Fons, X", "24"), arrayOf("Zy","99"))
    @Test
    fun search_exact(){
        val result = CutterGetter().search("X", "FONS",  data)
        assertArrayEquals(data[1], result)
    }

    @Test
    fun search_close(){
        val result = CutterGetter().search("X", "FONSECA", data)
        assertArrayEquals(data[1], result)
    }
    @Test
    fun search_before(){
        val result = CutterGetter().search("M", "BARRANTES", data)
        assertArrayEquals(data[0], result)
    }
    @Test
    fun search_after(){
        val result = CutterGetter().search("T", "ZZZZ", data)
        assertArrayEquals(data[2], result)
    }

    @Test
    fun search_emptyList() {
        val result = CutterGetter().search("Juan", "Perez", arrayListOf())
        assertArrayEquals(arrayOf("", ""), result)
    }

    @Test
    fun search_singleElementList() {
        val singleData = arrayListOf(arrayOf("Perez", "123"))
        val resultMatch = CutterGetter().search("", "Perez", singleData)
        assertArrayEquals(singleData[0], resultMatch)

        val resultBefore = CutterGetter().search("", "Alvarez", singleData)
        assertArrayEquals(singleData[0], resultBefore)

        val resultAfter = CutterGetter().search("", "Zapata", singleData)
        assertArrayEquals(singleData[0], resultAfter)
    }

    @Test
    fun search_blankName() {
        val result = CutterGetter().search("", "Andrade", data)
        assertArrayEquals(data[0], result)
    }

    @Test
    fun search_withAccentsNormalization() {
        // "Fons, X" is data[1]. "FÓNS" should match "Fons" if it follows the normalization rules correctly in context
        // Actually, formatTarget("FÓNS", "") -> "FONS"
        // formatTargetFromCsv("Fons, X") -> "FONS, X"
        // Let's verify how it behaves with accents.
        val result = CutterGetter().search("X", "FÓNS", data)
        assertArrayEquals(data[1], result)
    }

    @Test
    fun search_caseInsensitive() {
        val result = CutterGetter().search("x", "fons", data)
        assertArrayEquals(data[1], result)
    }
}