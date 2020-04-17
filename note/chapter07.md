## 持久化技术简介

Android 系统主要提供了 3 中方式用于简单地实现数据持久化功能：

- 文件存储
- SharedPreferences 存储
- 数据库存储

## 文件存储

### 将数据存储到文件中

```kotlin
try {
    // FileOutputStream
    val output = openFileOutput("data", Context.MODE_PRIVATE)
    val writer = BufferedWriter(OutputStreamWriter(output))
    writer.use {
        it.write(inputText)
    }
} catch (e: IOException) {
    e.printStackTrace()
}
```

文件名不可以包含路径，所有文件都默认存储到 `/data/date/<package name>/files/` 目录下。

文件的操作模式主要有 `MODE_PRIVATE` 和 `MODE_APPEND`，`MODE_WORLD_READABLE` 和 `MODE_WORLD_WRITEABLE` 在 Android 4.2 版本中被废弃。

use 函数是 Kotlin 提供的一个内置扩展函数，它会保证在 Lambda 表达式中的代码全部执行完之后自动将外层的流关闭，这样就不用写 finally 去手动关闭了。

### 从文件中读取数据

```kotlin
val content = StringBuilder()
try {
    val input = openFileInput("data")
    val reader = BufferedReader(InputStreamReader(input))
    reader.use {
        reader.forEachLine {
            content.append(it)
        }
    }
} catch (e: IOException) {
    e.printStackTrace()
}
```

forEachLine 函数也是 Kotlin 提供的一个内置扩展函数，它会将读到的每行内容都回调到 Lambda 表达式中。

## SharedPreferences 存储

SharedPreferences 是使用键值对的方式来存储数据的，支持多种不同的数据类型存储。

### 将数据存储到 SharedPreferences  中

Android 中主要提供以下两种方法用于得到 SharedPreferences 对象：

1. Context 类中的 `getSharedPreferences()` 方法

   第一个参数：指定 SharedPreferences 文件的名称，不存在则会创建。存放在 `/data/date/<package name>/shared_prefs/` 目录下；

   第二个参数：指定操作模式，目前只有默认的 MODE_PRIVATE 可选，表示只有当前应用程序才可以对这个 SharedPreferences 文件进行读写。其他几种操作模式均已被废弃。

2. Activity 类中的 `getPreferences()` 方法

   只接收一个操作模式参数，此方法自动将当前 Activity 的类名作为 SharedPreferences 的文件名。

```kotlin
val editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
editor.putString("name", "Tom")
editor.putInt("age", 28)
editor.putBoolean("married", false)
// 提交
editor.apply()
```

### 从 SharedPreferences 中读取数据

```kotlin
val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
val name = prefs.getString("name", "")
val age = prefs.getInt("age", 0)
val married = prefs.getBoolean("married", false)
```

很多应用程序中偏好设置功能起始就使用带了 SharedPreferences 技术。

### 实现记住密码功能

## SQLite 数据库存储

































