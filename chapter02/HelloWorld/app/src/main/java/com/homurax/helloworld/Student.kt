package com.homurax.helloworld

class Student(val sno: String, val grade: Int, name: String, age: Int) : Person(name, age), Study {

    constructor(name: String, age: Int) : this("", 0, name, age) {
    }

    constructor() : this("", 0) {
    }

    override fun readBooks() {
        println("$name is reading.")
    }

    override fun doHomework() {
        println("$name is doing homework.")
    }
}

fun main() {
    val student1 = Student()
    val student2 = Student("Jack", 19)
    val student3 = Student("123", 5, "Tom", 16)
    doStudy(student2)
}

fun doStudy(study: Study) {
    study.readBooks()
    study.doHomework()
}