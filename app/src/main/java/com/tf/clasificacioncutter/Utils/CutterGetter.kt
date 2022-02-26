package com.tf.clasificacioncutter.Utils

import android.content.Context
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import com.tf.clasificacioncutter.R
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

/**
 * Clase encargada de procesar los datos de los archivos .csv
 */
class CutterGetter {

    //Creamos un objeto para tener acceso a las funciones de CutterHelper
    private var cHelper = CutterHelper()

    /**
     *  Funcion para leer y convertir un archivo .csv y devolver un ArrayList de Array<String>
     *  @param ctx Contexto de la activity
     *  @param dbType Version del archivo .csv
     *  @return Array de arrays con los apellidos y numeros de cutter
     */
    fun getCutterList(ctx:Context,dbType:Int): ArrayList<Array<String>> {
        val lista = ArrayList<Array<String>>()

        // Por defecto el archivo sera el de la UCR
        var location = R.raw.cutter_ucr
        if(dbType!=1){
            location = R.raw.cutter_normal
        }

        // Abrimos el archivo .csv
        val file = InputStreamReader(ctx.resources.openRawResource(location))

        // Lector de csv, los archivos usan ';' para separar los datos
        val reader = CSVReaderBuilder(file)
                    .withCSVParser(CSVParserBuilder().withSeparator(';').build())
                    .build()
        val items = reader.readAll()

        // Recorremos cada fila y la agregamos a lista
        for (item in items){
            lista.add(item)
        }

        return lista
    }

    /**
     * Funcion encargada de determinar el orden lexicografico (Orden Diccionario) de una palabra, y retornar el
     * campo que cumpla con las condiciones.
     *
     * @param name Nombre de la persona
     * @param lstName Apellido de la persona
     * @param dbType Version de cutter
     * @param csvList Lista de nombres y apellidos
     *
     * @return Array de strings que contiene el nombre en el indice 0 y el apellido en el indice 1
     */
    fun search(name:String,lstName:String,dbType:Int,csvList:ArrayList<Array<String>>): Array<String> {
        //Normalizar nombre completo
        val nombreCompleto = "$lstName, $name"

        // Convertimos el apellido e inicial concatenados, a valores enteros
        var scnStr:ArrayList<Int> = cHelper.strToList(nombreCompleto)

        var result:Array<String> = arrayOf(String())

        // Variable que almacenara uno de los posibles resultados
        var oldResult:ArrayList<Int> = ArrayList()

        val size:Int = csvList.size-1

        //Indice para navegar en la lista
        var index = 0
        //Ver si se rompe el ciclo o no
        var continuar = true

        // Analizar cada item de la lista (csvList)
        do{
            if(nombreCompleto == csvList[index][0].toUpperCase(Locale.ROOT)){
                result = csvList[index]
                continuar = false
            }else {
                // Convertir las filas a valores enteros
                var row: ArrayList<Int> = cHelper.strToList(csvList[index][0].toUpperCase(Locale.ROOT))
                var secondRow: ArrayList<Int> = cHelper.strToList(csvList[index + 1][0].toUpperCase(Locale.ROOT))

                // Si la version es UCR, se realiza una modificacion a los valores enteros,
                // la UCR utiliza una version de Cutter que tiene las letras 'Ch' y 'Ll', por lo que
                // hay que cambiarlo
                if (dbType == 1) {
                    row = cHelper.ucrFix(row)
                    secondRow = cHelper.ucrFix(secondRow)
                    scnStr = cHelper.ucrFix(scnStr)
                }

                // Orden lexicografico
                val prevRow: Int = cHelper.compareStrTo(scnStr, row)
                val nextRow: Int = cHelper.compareStrTo(scnStr, secondRow)

                // Valor entero que servira para determinar el orden lexicografico de las iniciales de scnStr y row
                val pInicial: Int = scnStr.last() - row.last()
                // Lo mismo que la anterior solo que con secondRow en lugar de row
                val sInicial: Int = scnStr.last() - secondRow.last()

                // Si el valor de prevRow es positivo o 0 y nextRow es negativo,
                // significa que scnStr se encuentra entre row y secondRow, o es igual a row
                if (prevRow >= 0 && nextRow < 0) {
                    // Verificamos el orden lexicografico de old con respecto a row
                    val oldCompared: Int = cHelper.compareStrTo(oldResult, row)

                    // Verificar si la inicial esta entre la inicial de row y secondRow, o es row
                    if (pInicial >= 0 && sInicial < 0) {
                        //El resultado es igual a la fila que se analiza actualmente
                        result = csvList[index]
                    }
                    // Verificamos si el orden lexicografico de row es negativo,
                    // esto significa que el nuevo item (row) no esta despues del viejo (oldResult)
                    else if (oldCompared < 0) {
                        // Ahora oldResult se le asgina los valores de row
                        oldResult = row
                        // Resultado es igual a la fila que se analizo
                        result = csvList[index]
                    }
                }else if(prevRow < 0){
                    continuar = false
                }
                index++
            }
        }while(continuar && size>index)
        //Retornar resultado
        if(result.size == 1){
            result = csvList[index]
        }
        return result
    }
}