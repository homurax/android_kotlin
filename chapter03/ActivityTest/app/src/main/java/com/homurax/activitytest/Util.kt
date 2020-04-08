package com.homurax.activitytest

class Util {
    fun doAction1(){
        println("doAction1（）")
    }

    companion object {
        @JvmStatic
        fun doAction2(){
            println("doAction2（）")
        }
    }
}