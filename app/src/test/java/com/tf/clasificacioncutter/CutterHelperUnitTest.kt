package com.tf.clasificacioncutter


import com.tf.clasificacioncutter.Utils.CutterHelper
import org.junit.Test

import org.junit.Assert.*

/**
 *  Testing core methods of the class CutterHelper
 */
class CutterHelperUnitTest {
    private val res :ArrayList<Int> = CutterHelper().strToList("FONSECA")
    @Test
    fun convert_isTrue(){
        val expected: List<Int> = listOf(7,18,16,22,6,3,1)
        assertEquals(expected,res)
    }

    @Test
    fun compareTo_isTrue(){
        val before:ArrayList<Int> = CutterHelper().strToList("FONN")
        val after:ArrayList<Int> = CutterHelper().strToList("FONZ")

        val resultBefore = CutterHelper().compareStrTo(res, before)
        val resultAfter = CutterHelper().compareStrTo(res,after)
        assertTrue(resultBefore >= 0)
        assertTrue(resultAfter < 0)
    }

    @Test
    fun firstLetter_works(){
        val result = CutterHelper().firstLetter("Friend")
        assertEquals("F",result)
    }

    @Test
    fun firstLetter_LL_works(){
        val result = CutterHelper().firstLetter("Llorente")
        assertEquals("Ll",result)
    }

    @Test
    fun firstLetter_CH_works(){
        val result = CutterHelper().firstLetter("Chavez")
        assertEquals("Ch",result)
    }
    @Test
    fun ucrFix_LL_works(){
        val res = CutterHelper().strToList("LLAVES")
        val item = CutterHelper().ucrFix(res)
        val expected: List<Int> = listOf(14,1,25,6,22)
        assertEquals(expected,item)
    }
    @Test
    fun ucrFix_CH_works(){
        val res = CutterHelper().strToList("CHAVES")
        val item = CutterHelper().ucrFix(res)
        val expected: List<Int> = listOf(4,1,25,6,22)
        assertEquals(expected,item)
    }
}
