package com.tf.clasificacioncutter.Utils

import android.content.Context
import android.util.Log
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import com.tf.clasificacioncutter.R
import java.io.FileReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader


class CutterGetter {

    val cHelper = CutterHelper()

    fun getCutterList(ctx:Context,dbType:Int): ArrayList<Array<String>> {
        val lista = ArrayList<Array<String>>()
        var location = R.raw.cutter_ucr

        if(dbType!=1){
            location = R.raw.cutter_normal
        }
        val file = InputStreamReader(ctx.resources.openRawResource(location))



        val reader = CSVReaderBuilder(file)
            .withCSVParser(CSVParserBuilder().withSeparator(';').build())
            .build()
        val items = reader.readAll()
        for (item in items){
            lista.add(item)
        }
        return lista
    }

    fun search(name:String,lstName:String,dbType:Int,csvList:ArrayList<Array<String>>): Array<String> {
        val nName = name[0]
        var scnStr:ArrayList<Int> = cHelper.strToList("$lstName, $nName")
        var result:Array<String> = arrayOf(String())
        var old:ArrayList<Int> = ArrayList()

        val size:Int = csvList.size-2

        for (i:Int in 0..size){
            var row: ArrayList<Int> = cHelper.strToList(csvList[i][0].toUpperCase())
            var sRow: ArrayList<Int> = cHelper.strToList(csvList[i+1][0].toUpperCase())

            if (dbType==1){
                row = cHelper.ucrFix(row)
                sRow = cHelper.ucrFix(sRow)
                scnStr = cHelper.ucrFix(scnStr)
            }
            val prevRow:Int = cHelper.compareStrTo(scnStr,row)
            val nextRow:Int = cHelper.compareStrTo(scnStr,sRow)

            val pInicial:Int = scnStr.last()-row.last()
            val sInicial:Int = scnStr.last()-sRow.last()

            if(prevRow>=0 && nextRow<0){
                val com:Int = cHelper.compareStrTo(old,row)
                if(pInicial>=0 && sInicial<0){
                    result = csvList[i]
                }else if(com<0){
                    old = row
                    result = csvList[i]
                }
            }
        }
        return result
    }
}