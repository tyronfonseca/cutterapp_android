package com.tf.clasificacioncutter.Utils

import kotlin.math.min

/**
 * Clase con varias funciones que ayudan a convertir y/o procesar datos de la app.
 */
class CutterHelper{

    // Diccionario utilizado para convertir vocales acentuadas a vocales normales
    private var spaDict = mapOf("Á" to "A","É" to "E","Í" to "I","Ó" to "O","Ú" to "U","Ü" to "U")

    // Diccionario utilizado para convertir caracteres/simbolos en valores enteros
    // Note: que no se asignan los valores 4 ni 14. Refierase a la funcion 'ucrFix' para saber mas.
    private var oldAlph = mapOf("A" to 1,"B" to 2,"C" to 3, "CH" to 4, "D" to 5,"E" to 6,"F" to 7,"G" to 8,"H" to 9,"I" to 10,"J" to 11,
        "K" to 12,"L" to 13, "LL" to 14,"M" to 15,"N" to 16,"Ñ" to 17,"O" to 18,"P" to 19,"Q" to 20,"R" to 21,"S" to 22,"T" to 23,
        "U" to 24,"V" to 25,"W" to 26,"X" to 27,"Y" to 28,"Z" to 29,"@" to 30,"," to 0, " " to 0, "-" to 0)

    /**
     * Funcion encargada de eliminar acentuaciones y convertir letras a valores enteros
     *
     * @param str String a convertir
     * @return Array de enteros que representan los valores numericos de las letras
     */
    fun strToList(str: String): ArrayList<Int>{
        val res : ArrayList<Int> = ArrayList()

        var tmp:String?
        var intVal:Int?

        //Analizamos cada caracter del string
        for(letra in str){

            var letter = letra.toString()

            tmp = spaDict[letter]

            //Si existe se lo asignamos a 'letter'
            if (tmp != null){
                letter = tmp.toString()
            }

            //Conseguir valor entero de la letra 'letter'
            intVal = oldAlph[letter]

            //Si existe agregamos el valor entero de 'letter' a la respuesta
            if (intVal != null){
                res.add(intVal)
            }

        }
        return res
    }

    /**
     * Determinar el valor lexicografico de dos arrays de enteros.
     * Esta funcion esta basada en la funcion [compareTo] de Java
     *
     * @param scnStr Array a verificar si esta antes o despues
     * @param strComp Array con el cual se comparara [scnStr]
     *
     * @return Valor entero, si es negativo significa que [scnStr] esta antes de [strComp],
     * si es positivo o igual a 0 [scnStr] esta despues de [scnStr] o son iguales
     */
    fun compareStrTo(scnStr:ArrayList<Int>,strComp:ArrayList<Int>):Int{
        //Determinar tamaño de los arrays
        val len1:Int = scnStr.size
        val len2:Int = strComp.size
        val lim:Int = min(len1, len2)
        var k = 0

        //Mientras el contador sea menor al limite 'lim'
        while(k<lim){
            //Valores en el indice 'k'
            val c1:Int = scnStr[k]
            val c2:Int = strComp[k]
            //Si son diferentes se retorna el valor de la resta de c1 y c2
            if(c1!=c2){
                return c1-c2
            }

            //Agregamos una unidad al contador
            k += 1
        }
        //Si son iguales retornamos la diferencia de sus tamaños
        return len1-len2
    }

    /**
     * Funcion que determina la primera letra de un string, con casos especiales como Ch y Ll
     * @param word String a analizar
     * @return Primera letra deacuerdo al caso
     */
    fun firstLetter(word:String):String{
        val letter:String = if(word[0] == 'C' && word[1] == 'h'){
            "Ch"
        } else if(word[0] == 'L' && word[1] == 'l'){
            "Ll"
        }else{
            word[0].toString()
        }
        return letter
    }

    /**
     * Funcion encargada de agregar los valores numericos de 'Ch' y 'Ll',
     * la UCR utiliza una version de cutter que toma en cuenta letras del alfabeto español viejo.
     * @param lista Array de enteros a convertir
     * @return Array de enteros procesado con los valores de 'Ch' y/o 'Ll' asignados
     */
    fun ucrFix(lista: ArrayList<Int>): ArrayList<Int> {
        var count = 0
        val lastIndex = lista.size - 1

        //Verificamos cada item del array 'lista'
        for (i in 1..lastIndex) {
            //Primer item
            val prim = lista[i - 1]
            //Item que le sigue a 'prim'
            val segu = lista[i]
            if (prim == oldAlph["C"] && segu == oldAlph["H"]) {
                //Modificamos el valor del indice i-1 con 4. 4 representa 'CH'
                lista[i - 1] = 4
                //Eliminamos el item que le sigue
                lista.removeAt(i)
                //Agregamos un item temporal al final de la lista, para mantener el mismo tamaño
                lista.add(0)
                //Sumamos una unidad al contador
                count += 1
            } else if (prim == oldAlph["L"] && segu == oldAlph["L"]) {
                //Modificamos el valor del indice i-1 con 14. 14 representa 'LL'
                lista[i - 1] = 14
                lista.removeAt(i)
                lista.add(0)
                count += 1
            }
        }

        //Si se agregaron ceros al final de las lista, se eliminan
        while (count > 0) {
            //Eliminamos el ultimo item de la lista
            lista.removeAt(lista.size - 1)
            count -= 1
        }
        return lista
    }
}