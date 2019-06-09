package com.tf.clasificacioncutter.Utils

class CutterHelper{
    var spaDict = mapOf("Á" to "A","É" to "E","Í" to "I","Ó" to "O","Ú" to "U","Ü" to "U")

    var oldAlph = mapOf("A" to 1,"B" to 2,"C" to 3,"D" to 5,"E" to 6,"F" to 7,"G" to 8,"H" to 9,"I" to 10,"J" to 11,
        "K" to 12,"L" to 13,"M" to 15,"N" to 16,"Ñ" to 17,"O" to 18,"P" to 19,"Q" to 20,"R" to 21,"S" to 22,"T" to 23,
        "U" to 24,"V" to 25,"W" to 26,"X" to 27,"Y" to 28,"Z" to 29,"," to 0)


    fun strToList(str: String): ArrayList<Int>{
        val res : ArrayList<Int> = ArrayList()
        var tmp:String?
        var newLetter:Int?

        for(lt in str){

            var letter = lt.toString()

            tmp = spaDict.get(letter)

            if (tmp != null){
                letter = tmp.toString()
            }

            newLetter = oldAlph.get(letter)

            if (newLetter != null){
                res.add(newLetter)
            }

        }
        return res
    }

    fun compareStrTo(scnStr:ArrayList<Int>,strComp:ArrayList<Int>):Int{
        val len1:Int = scnStr.size
        val len2:Int = strComp.size
        val lim:Int = Math.min(len1, len2)
        var k = 0

        while(k<lim){
            val c1:Int = scnStr[k]
            val c2:Int = strComp[k]
            if(c1!=c2){
                return c1-c2
            }
            k += 1
        }
        return len1-len2
    }

    fun firstLetter(word:String):String{
        val letter:String
        if(word[0].equals('C') && word[1].equals('H')){
            letter = "Ch"
        }else if(word[0].equals('L') && word[1].equals('L')){
            letter = "Ll"
        }else{
            letter = word[0].toString()
        }
        return letter
    }

    fun ucrFix(lista:ArrayList<Int>):ArrayList<Int>{
        var count = 0
        val arr = lista
        val lastIndex = lista.size-1
        for (i in 1..lastIndex){
            val prim = lista[i-1]
            val segu = lista[i]
            if(prim==3 && segu==9){
                arr.set(i-1,4)
                arr.removeAt(i)
                arr.add(0)
                count+=1
            }else if(prim==13 && segu==13){
                arr.set(i-1,14)
                arr.removeAt(i)
                arr.add(0)
                count+=1
            }
        }
        while(count>0){
            arr.removeAt(arr.size-1)
            count-=1
        }
        return arr
    }
}