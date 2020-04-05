package com.homurax.helloworld

fun main() {
    Thread { println("Thread is running.") }.start()
}

fun test() {
    for (i in 0..10 step 2) {
        println(i)
    }
    for (i in 0 until 10 step 3) {
        println(i)
    }
    for (i in 10 downTo 1 step 4) {
        println(i)
    }
    val c1 = CellPhone("abc", 1234.56)
    val c2 = CellPhone("abc", 1234.56)
    println(c1 == c2)
    println(c1)
    println(c2)
    val list1 = listOf("Apple", "Banana", "Orange", "Pear", "Grape")
    for (fruit in list1) {
        println(fruit)
    }
    val list2 = mutableListOf("Apple", "Banana", "Orange", "Pear", "Grape")
    list2.add("Watermelon")
    for (fruit in list2) {
        println(fruit)
    }
    val set1 = setOf("Apple", "Banana", "Orange", "Pear", "Grape")
    for (fruit in set1) {
        println(fruit)
    }
    val set2 = mutableSetOf("Apple", "Banana", "Orange", "Pear", "Grape")
    set2.add("Watermelon")
    for (fruit in set2) {
        println(fruit)
    }
    val map = mapOf("Apple" to 1, "Banana" to 2, "Orange" to 3, "Pear" to 4, "Grape" to 5)
    for ((fruit, number) in map) {
        println("fruit is $fruit, number is $number")
    }
    val maxLengthFruit = list1.maxBy { it.length }
    println(maxLengthFruit)
    val newList = list1.map { it.toUpperCase() }
    println(newList)
    val newList2 = list1.filter { it.length <= 5 }.map { it.toUpperCase() }
    println(newList2)
    val anyResult = list1.any { it.length <= 5 }
    val allResult = list1.all { it.length <= 5 }
    println(anyResult)
    println(allResult)
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