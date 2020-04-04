package com.homurax.helloworld

fun main() {
    for (i in 0..10 step 2) {
        println(i)
    }
    for (i in 0 until 10 step 3) {
        println(i)
    }
    for (i in 10 downTo 1 step 4) {
        println(i)
    }
}

fun checkNumber(num: Number) = when (num) {
    is Int -> println("number is Int")
    is Double -> println("number is Double")
    else -> println("number not support")
}

fun getScore(name: String) = when {
    name.startsWith("Tom") -> 86
    name == "Jim" -> 77
    name == "Lily" -> 100
    else -> 0
}

fun largerNumber(a: Int, b: Int) = if (a > b) a else b