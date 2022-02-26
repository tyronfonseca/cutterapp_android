package com.tf.clasificacioncutter

import com.tf.clasificacioncutter.Utils.CutterGetter
import org.junit.Assert.*
import org.junit.Test
class CutterGetterUnitTest{
    private val data = arrayListOf(arrayOf("Andrade", "254"), arrayOf("Fons, X", "24"),arrayOf("Zamora", "10"))
    @Test
    fun search_exact(){
        val result = CutterGetter().search("X", "FONS", 0, data)
        assertArrayEquals(data[1], result)
    }

    @Test
    fun search_close(){
        val result = CutterGetter().search("X", "FONSECA", 0, data)
        assertArrayEquals(data[1], result)
    }
    @Test
    fun search_before(){
        val result = CutterGetter().search("M", "BARRANTES", 0, data)
        assertArrayEquals(data[0], result)
    }
    @Test
    fun search_after(){
        val result = CutterGetter().search("M", "ZZZZ", 0, data)
        assertArrayEquals(data[2], result)
    }

    @Test
    fun search_close_ucr(){
        val result = CutterGetter().search("X", "FONSECA", 1, data)
        assertArrayEquals(data[1], result)
    }
    @Test
    fun search_before_ucr(){
        val result = CutterGetter().search("M", "BARRANTES", 1, data)
        assertArrayEquals(data[0], result)
    }
    @Test
    fun search_after_ucr(){
        val result = CutterGetter().search("M", "ZZZZ", 1, data)
        assertArrayEquals(data[2], result)
    }
}