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

很多应用程序中偏好设置功能起始就使用到了 SharedPreferences 技术。

### 实现记住密码功能

## SQLite 数据库存储

### 创建数据库

Android 提供了一个 SQLiteOpenHelper 抽象帮助类。

`getWritableDatabase()` 和 `getReadableDatabase()` 都可以创建或打开一个现有的数据库（没有时创建一个新的数据库），并返回一个可对数据库进行读写操作的对象。

当数据库不可写入时，`getReadableDatabase()` 返回的对象将以只读的方式打开数据库，而 `getWritableDatabase()` 方法则将出现异常。

创建的数据库存放在 ` /data/date/<package name>/databases/` 目录下 。

使用 `Database Navigator` 插件。

### 升级数据库

当指定的数据库版本号大于当前数据库版本号时，就会进入 `onUpgrade()` 方法中执行更新操作。

```kotlin
val dbHelper = MyDatabaseHelper(this, "BookStore.db", 2)
```

### 添加数据

```kotlin
val db = dbHelper.writableDatabase
val values1 = ContentValues().apply {
    put("name", "The Da Vinci Code")
    put("author", "Dan Brown")
    put("pages", 454)
    put("price", 16.96)
}
db.insert("Book", null, values1)
```

### 更新数据

```kotlin
val db = dbHelper.writableDatabase
val values = ContentValues()
values.put("price", 10.99)
db.update("Book", values, "name = ?", arrayOf("The Da Vinci Code"))
```

### 删除数据

```kotlin
val db = dbHelper.writableDatabase
db.delete("Book", "pages > ?", arrayOf("500"))
```

### 查询数据

`query()` 方法参数的详细解释

| 方法参数 | 对应 SQL 部分 |描述 |
| :------- | :------------ | :-- |
|table|from table_name|指定描述的表名|
|columns|select column1, column2|指定查询的列名|
|selection|where column = value|指定 where 的约束条件|
|selectionArgs|-|为 where 中的占位符提供具体的值|
|groupBy|group by column|指定需要 group by 的列|
|having|having column = value|对 group by 后的结果进一步约束|
|orderBy|order by column1, column2|指定查询结果的排列方式|

```kotlin
val db = dbHelper.writableDatabase
val cursor = db.query("Book", null, null, null, null, null, null)
if (cursor.moveToFirst()) {
    do {
        val name = cursor.getString(cursor.getColumnIndex("name"))
        val author = cursor.getString(cursor.getColumnIndex("author"))
        val pages = cursor.getInt(cursor.getColumnIndex("pages"))
        val price = cursor.getDouble(cursor.getColumnIndex("price"))
        Log.d(TAG, "book name is $name")
        Log.d(TAG, "book author is $author")
        Log.d(TAG, "book pages is $pages")
        Log.d(TAG, "book price is $price")
    } while (cursor.moveToNext())
}
cursor.close()
```

### 使用 SQL 操作数据库

查询数据时调用的是 SQLiteOpenHelper 的 `rawQuery()` 方法，其他操作都是调用的 `execSQL()` 方法。

## SQLite 数据库的最佳实践

### 使用事务

```kotlin
val db = dbHelper.writableDatabase
// 开启事务
db.beginTransaction()
try {
    db.delete("Book", null, null)
    /*if (true) {
        throw NullPointerException()
    }*/
    val values = ContentValues().apply {
        put("name", "Game of Thrones")
        put("author", "George Martin")
        put("pages", 720)
        put("price", 20.85)
    }
    db.insert("Book", null, values)
    // 事务已经执行成功
    db.setTransactionSuccessful()
} catch (e: Exception) {
    e.printStackTrace()
} finally {
    // 结束事务
    db.endTransaction()
}
```

### 升级数据库的最佳写法

每当升级一个数据库版本的时候，`onUpgrade()` 方法里都一定要写一个相应的 if 判断语句。保证 App 在跨版本升级的时候，每一次的数据库修改都能被全部执行。

## Kotlin：高阶函数的应用

### 简化 SharedPreferences 用法

```kotlin
fun SharedPreferences.open(block: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.block()
    editor.apply()
}
```

通过扩展函数的方式向 SharedPreferences 类中添加了一个 open 高阶函数，并且接收一个函数类型参数。

实际上 Google 的 KTX 库已经自带了 edit 函数。

### 简化 ContentValues 的用法

```kotlin
fun cvOf(vararg pairs: Pair<String, Any?>) = ContentValues().apply {
    for (pair in pairs) {
        val key = pair.first
        when (val value = pair.second) {
            is Int -> put(key, value)
            is Long -> put(key, value)
            is Short -> put(key, value)
            is Float -> put(key, value)
            is Double -> put(key, value)
            is Boolean -> put(key, value)
            is String -> put(key, value)
            is Byte -> put(key, value)
            is ByteArray -> put(key, value)
            null -> putNull(key)
        }
    }
}
```

在 Kotlin 中使用 A to B 这样的语法结构会创建一个 Pair 对象。

**`vararg`** 关键字对应的就是 Java 中的可变参数列表，允许想这个方法中传入任意个 Pair 类型的参数。

**`Any`** 是 Kotlin 中所有类的共通基类，相当于 Java 中的 Object 。

KTX 库同样实际上已经提供了 contentValuesOf 函数。



