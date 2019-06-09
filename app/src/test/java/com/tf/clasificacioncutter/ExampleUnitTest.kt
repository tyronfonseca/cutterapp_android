package com.tf.clasificacioncutter


import com.tf.clasificacioncutter.Utils.CutterHelper
import org.junit.Test

import org.junit.Assert.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    val res :ArrayList<Int> = CutterHelper().strToList("FONSECA")
    val expected: List<Int> = listOf(23,28,21,18,16)
    val before:ArrayList<Int> = CutterHelper().strToList("FONN")
    val after:ArrayList<Int> = CutterHelper().strToList("FONS")

    @Test
    fun convert_isTrue(){
        assertEquals(expected,res)
    }

    @Test
    fun compareTo_isTrue(){
        val resultBefore = CutterHelper().compareStrTo(res, before)
        val resultAfter = CutterHelper().compareStrTo(res,after)
        if (resultBefore >= 0 && resultAfter<0){
            print(true)
        }else{
            print(false)
        }


    }

    @Test
    fun firstLetter_works(){
        val result = CutterHelper().firstLetter("Llorente")
        assertEquals("Ll",result)
    }

    @Test
    fun ucrFix_works(){
        val res = CutterHelper().strToList("LLAVES")
        val item = CutterHelper().ucrFix(res)
        val expected: List<Int> = listOf(14,1,25,6,22)
        assertEquals(expected,item)
    }

}
