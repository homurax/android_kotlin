package com.homurax.helloworld

class Student(val sno: String, val grade: Int, name: String, age: Int) : Person(name, age) {

    /*constructor(name: String, age: Int) : super(name, age) {
    }*/

    constructor(name: String, age: Int) : this("", 0, name, age) {
    }

    constructor() : this("", 0) {
    }
}

fun main() {
    val student1 = Student()
    val student2 = Student("Tom", 16)
    val student3 = Student("123", 5, "Tom", 16)
}