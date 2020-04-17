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



