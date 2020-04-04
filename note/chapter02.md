## 变量和函数

### 变量

出色的类型推导机制。

只能在变量前声明两种关键字

- `val` value 的缩写，用来声明一个不可变的变量，初始赋值之后不可重新赋值。对应 Java 中的 final 变量
- `var` variable 的缩写，用来声明一个可变的变量，初始赋值之后仍可重新复制。对应 Java 中的非 final 变量

对一个变量延迟赋值，Kotlin 就无法自动推导它的类型了，可以显式地声明变量类型

```kotlin
fun main() {
    println("Hello World")
    var a: Int = 10
    a *= 10
    println("a = " + a)
    println("a = $a")
}
```



Java 和 Kotlin 数据类型对照

| Java 基本数据类型 | Kotlin 对象数据类型 | 数据类型说明 |
|:---:|:--------------:|:-----------|
|int|Int|整形|
|long|Long|长整形|
|short|Short|短整形|
|float|Float|单精度浮点型|
|double|Double|双精度浮点型|
|boolean|Boolean|布尔型|
|char|Char|字符型|
|byte|Byte|字节型|

Kotlin 完全抛弃了 Java 中的基本数据类型，全部使用了数据对象类型。

`val` 的设计是为了解决 Java 中 `final` 关键字没有被合理使用的问题（大部分人没有主动使用）。永远优先使用 val 来声明一个变量，当 val 无法满足需求时再使用 var。

### 函数

Java 习惯叫做方法（翻译自 method），Kotlin 习惯叫做函数（翻译自 function）。

规则

- **`fun` **是定义函数的关键字
- fun 后面的是**函数名**
- 参数列表，**参数**的声明格式**参数名: 参数类型**
- 声明函数**返回类型**

```kotlin
import kotlin.math.max

fun main() {
    val a = 37
    val b = 40
    val value = largerNumber(a, b)
    println("The larger number is $value")
}

fun largerNumber(a: Int, b: Int): Int {
    return max(a, b)
}
```

函数的语法糖：函数中代码只有一行时，允许不必编写函数体

```kotlin
fun largerNumber(a: Int, b: Int) = max(a, b)
```

## 程序的逻辑控制

顺序语句、条件语句和循环语句

### if 条件语句

Kotlin 中的 if 语句比 Java 有一个额外的功能，可以有返回值。

```kotlin
fun largerNumber(a: Int, b: Int): Int {
    return if (a > b) {
        a
    } else {
        b
    }
}
```

if 语句使用每个条件的最后一行作为返回值。

```kotlin
fun largerNumber(a: Int, b: Int) = if (a > b) a else b
```

### when 条件语句

when 类似，但比 Java 中的 switch 功能强大。和 if 语句一样，可以有返回值，且配合单行代码函数的语法糖使用。

```kotlin
fun getScore(name: String) = when (name) {
    "Tom" -> 86
    "Jim" -> 77
    "Lily" -> 100
    else -> 0
}
```

when 语句允许传入一个任意类型的参数，然后在 when 的结构体中定义一系列的条件，执行逻辑只有一行时，`{}`可以省略。

`匹配值 -> { 执行逻辑 }`

when 语句允许进行类型判断。

```kotlin
fun checkNumber(num: Number) = when (num) {
    is Int -> println("number is Int")
    is Double -> println("number is Double")
    else -> println("number not support")
}
```

`is` 相当于 Java 中的 `instanceof` 关键字。

when 语句还有一种不带参数的用法，不常用但是能发挥出扩展性。

```kotlin
fun getScore(name: String) = when {
    name.startsWith("Tom") -> 86
    name == "Jim" -> 77
    name == "Lily" -> 100
    else -> 0
}
```

注意 Kotlin 中判断字符串或者对象相等可以直接使用 `==` 。

### 循环语句

while 循环与 Java 中没有任何区别，跳过。

Java 中的 `for-i` 循环被舍弃了，`for-each` 循环被增强，变为 Kotlin 中的 `for-in` 循环。

**区间**

Kotlin 中使用 `a .. b` 可以创建一个双端闭区间（[a, b]），使用 `a until b` 可以创建一个单端闭区间（[a, b)）。

```kotlin
fun main() {
    for (i in 0..10) {
        println(i)
    }
    for (i in 0 until 10) {
        println(i)
    }
}
```

`..` 与 `until` 都要求左端小于等于右端，如果需要降序区间，可以使用 `downTo` 来创建。比如 `10 downTo 1` 相当于 `[10, 1]` 的降序区间。

```kotlin
fun main() {
    for (i in 10 downTo 1) {
        println(i)
    }
}
```

`for-in` 每次循环时都会在区间范围内递增1，类似于 `for-i` 中的 `i++` 。

如果想多跳过其中的一些元素，可以使用 `step` 关键字。

```kotlin
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
```

`for-in` 没有传统的 `for-i` 灵活，但是简单好用，覆盖了绝大部分使用场景。如果一些特殊场景无法实现， 可以改用 while 循环的方式进行。

## 面向对象编程

### 类与对象

File 通常是用于编写 Kotlin 顶层函数和扩展哈桑农户的，Class 表示创建一个类。

Kotlin 中实例化一个类的时候不需要 `new` 关键字。

```kotlin
class Person {

    var name = ""
    var age = 0

    fun eat() {
        println("$name is eating. He is $age years old.")
    }
}

fun main() {
    val p = Person()
    p.name = "Jack"
    p.age = 17
    p.eat()
}
```

### 继承与构造函数

### 继承

Kotlin 中 任何一个非抽象类默认都是不可以继承的，相当于 Java 中给类声明了 final 关键字。

之所以这么设计，与 val 关键字的原因差不多。类和变量一样，最好都是不可变的，如果一个类可以继承，它无法预知子类会如何实现，因此可能会存在一些未知的风险。《*Effective Java*》中明确提到，如果一个类不是专门为继承而设计的，那么就应该主动将它加上 final 声明，禁止它可以被继承。

*Kotlin 中抽象类和 Java 中并无区别。*

**在类前加上 open 关键字，这个类就允许被继承了。继承的关键字是 `:`。**

```kotlin
open class Person {
    ...
}

class Student : Person() {
    var sno = ""
    var grade = 0
}
```

Person 类后面为什么要有一对括号的问题，涉及到 Kotlin 中的构造函数。

Kotlin 将构造函数分为：**主构造函数**和**次构造函数**。

### 主构造函数

每个类都默认会有一个不带参数的主构造函数，也可以显式的指名参数。

**主构造函数**的特点是**没有函数体，直接定义在类的后面**即可。

```kotlin
class Student(val sno: String, val grade: Int) : Person() {
}

fun main() {
    val student = Student("123", 5)
}
```

这就表明在对 Student 类实例化时，必须传入构造函数中的所有参数（由于不用重新赋值，所以声明用了 val）。

主构造函数没有函数体，如果需要在主构造函数中编写逻辑，可以写在 **init 结构体**中。

```kotlin
class Student(val sno: String, val grade: Int) : Person() {
    init {
        println("sno: $sno")
        println("grade: $grade")
    }
}
```

**子类中的构造函数必须调用父类的构造函数。子类的主构造函数调用父类中的哪个构造函数，在继承的时候通过括号来指定。**

Person 类后面的一对空括号表示 Student 类的主构造函数在初始化的时候会调用 Person 类的无参构造函数。即使在无参数的情况下，这对括号爷不能省略。

所以如果修改 Person 类的主构造函数为带有参数的形式，Student 类也要做相应的修改。

```kotlin
open class Person(val name: String, val age: Int) {
    ...
}

class Student(val sno: String, val grade: Int, name: String, age: Int) : Person(name, age) {
}

fun main() {
    val student = Student("123", 5, "Tom", 16)
}
```

Student 类的主构造函数在增加 name 和 age 这两个字段时，不能声明为 val 。

**因为声明成 val 或 var 的参数将会自动成为该类的字段**，这就会导致与父类中同名的 name 和 age 字段造成冲突。这里不加任何关键字，作用域仅限定在主构造函数中即可。

### 次构造函数

**一个类中只能有一个主构造函数（最多一个，可以没有），可以有多个次构造函数，次构造函数具有函数体。**

Kotlin 规定当一个类**既有主构造函数，又有次构造函数时，所有的次构造函数都必须调用主构造函数（包括间接调用）**。

```kotlin
class Student(val sno: String, val grade: Int, name: String, age: Int) : Person(name, age) {
    
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
```

#### 类中只有次构造函数

**当一个类没有显式地定义主构造函数，且定义了次构造函数时，它就是没有主构造函数的。**

```kotlin
class Student : Person {
    constructor(name: String, age: Int) : super(name, age) {
    }
}
```

此时的 Student 类是没有主构造函数的，既然没有主构造函数，继承 Person 类时也就不需要加上括号了。

因为此时不存在 Student 类的主构造函数要指定调用父类哪个构造函数的问题了。

同时由于 Student 类没有主构造函数，次构造函数就必须通过 super 直接调用父构造函数了。