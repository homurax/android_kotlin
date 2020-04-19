## ContentProvider 简介

ContentProvider 主要用于在不同的应用程序之间实现数据共享的功能，它提供了一套完整的机制，允许一个程序访问另一个程序中的数据，同时还能保证被访数据的安全性。目前，使用 ContentProvider 是 Android 实现跨程序共享数据的标准方式。

## 运行时权限

### Android 权限机制详解

在 AndroidManifest.xml 文件中添加了权限声明后，实用户主要在两个方面得到了保护。

如果用户在低于 Android 6.0 系统的设备上安装该程序，会在安装界面给出提醒。这样用户就可以清楚地知晓该程序一共申请了哪些权限，从而决定是否要安装这个程序。

另一方面，用户可以随时在应用程序管理界面查看任意一个程序的权限申请情况。

这种权限机制的设计思路其实非常简单，就是用户如果认可你所申请的权限，那么就会安装你的程序，如果不认可你所申请的权限，那么拒绝安装就可以了。但是很多我们所离不开的常用软件普遍存在着滥用权限的情况，不管到底用不用得到，反正先把权限申请了再说。



在 Android 6.0 系统中加入了运行时权限功能。用户不需要在安装软件的时候一次性授权所有申请的权限，而是可以在软件的使用过程中再对某一项权限申请进行授权。

Android 现在将所有的权限归成了两类，一类是**普通权限**，一类是**危险权限**。准确地讲，其实还有第三类特殊权限，不过这种权限使用得很少，因此不在讨论范围之内。

普通权限指的是那些不会直接威胁到用户的安全和隐私的权限，对于这部分权限申请，系统会自动帮我们进行授权，而不需要用户再去手动操作。

危险权限则表示那些可能会触及用户隐私或者对设备安全性造成影响的权限，如获取设备联系人信息、定位设备的地理位置等，对于这部分权限申请，必须要由用户手动点击授权才可以，否则程序就无法使用相应的功能。

除了危险权限之外，剩余的就都是普通权限了。

到 Android 10 系统为止所有的危险权限。

<table>
   <tr>
      <td>权限组名</td>
      <td>权限名</td>
   </tr>
   <tr>
      <th rowspan="2">CALENDAR</th>
      <td>READ_CALENDAR</td>
   </tr>
   <tr>
      <td>WRITE_CALENDAR</td>
   </tr>
   <tr>
      <th rowspan="3">CALL_LOG</th>
      <td>READ_CALL_LOG</td>
   </tr>
   <tr>
      <td>WRITE_CALL_LOG</td>
   </tr>
    <tr>
      <td>PROCESS_OUTGOING_CALLS</td>
   </tr>
   <tr>
      <th>CAMERA</th>
      <td>CAMERA</td>
   </tr>
   <tr>
      <th rowspan="3">CONTACTS</th>
      <td>READ_CONTACTS</td>
   </tr>
   <tr>
      <td>WRITE_CONTACTS</td>
   </tr>
    <tr>
      <td>GET_ACCOUNTS</td>
   </tr>
   <tr>
      <th rowspan="3">LOCATION</th>
      <td>ACCESS_FINE_LOCATION</td>
   </tr>
   <tr>
      <td>ACCESS_COARSE_LOCATION</td>
   </tr>
   <tr>
      <td>ACCESS_BACKGROUND_LOCATION</td>
   </tr>
   <tr>
      <th>MICROPHONE</th>
      <td>RECORD_AUDIO</td>
   </tr>
   <tr>
      <th rowspan="7">PHONE</th>
      <td>READ_PHONE_STATE</td>
   </tr>
   <tr>
      <td>READ_PHONE_NUMBERS</td>
   </tr>
   <tr>
      <td>CALL_PHONE</td>
   </tr>
   <tr>
      <td>ANSWER_PHONE_CALLS</td>
   </tr>
   <tr>
      <td>ADD_VOICEMAIL</td>
   </tr>
    <tr>
      <td>USE_SIP</td>
   </tr>
   <tr>
      <td>ACCEPT_HANDOVER</td>
   </tr>
   <tr>
      <th>SENSORS</th>
      <td>BODY_SENSORS</td>
   </tr>
   <tr>
      <th>ACTIVITY_RECOGNITION</th>
      <td>ACTIVITY_RECOGNITION</td>
   </tr>
   <tr>
      <th rowspan="5">SMS</th>
      <td>SEND_SMS</td>
   </tr>
   <tr>
      <td>RECEIVE_SMS</td>
   </tr>
   <tr>
      <td>READ_SMS</td>
   </tr>
   <tr>
      <td>RECEIVE_WAP_PUSH</td>
   </tr>
    <tr>
      <td>RECEIVE_MMS</td>
   </tr>
   <tr>
      <th rowspan="3">STORAGE</th>
      <td>READ_EXTERNAL_STORAGE</td>
   </tr>
   <tr>
      <td>WRITE_EXTERNAL_STORAGE</td>
   </tr>
   <tr>
      <td>ACCESS_MEDIA_LOCATION</td>
   </tr>
</table>

如果是属于这张表中的权限，那么就需要进行运行时权限处理，否则，只需要在 AndroidManifest.xml 文件中添加一下权限声明就可以了。
另外注意，表格中每个危险权限都属于一个权限组，我们在进行运行时权限处理时使用的是权限名。原则上用户一旦同意授权了某个权限申请之后，同组的其他权限也会被系统自动授权。但是不要基于次规则来实现任何功能逻辑，因为 Android 系统随时有可能调整权限的分组。

[Android 系统中完整的权限列表](https://developer.android.google.cn/reference/android/Manifest.permission.html)

### 在程序运行时申请权限

运行时权限的核心就是在程序运行过程中由用户授权我们去执行某些危险操作，程序是不可以擅自做主去执行这些危险操作的。

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makeCall.setOnClickListener {
            // 判断是否已经授权
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // 申请授权
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
            } else {
                call()
            }
        }
    }

    // 回调
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call()
                } else {
                    Toast.makeText(this, "You denied the permission.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun call() {
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:10086")
            startActivity(intent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
```

## 访问其他程序中的数据

ContentProvider 的用法一般有两种，一种是使用现有的 ContentProvider 来读取和操作相应程序中的数据，另一种是创建自己的 ContentProvider 给我们程序的数据提供外部访问接口。

### ContentResolver 的基本用法

通过 Context 中的 `getContentResolver()` 方法获取到 ContentResolver 的实例。ContentResolver 中提供了一系列方法用于对数据进行 CRUD 操作。

内容 URI 标准格式：

```
content://com.example.app.provider/table1
content://com.example.app.provider/table2 
```

```kotlin
val uri = Uri.parse("content://com.example.app.provider/table1") 
```

`query()` 方法的参数说明。

| query() 方法参数 | 对应 SQL 部分             | 描述                             |
| :--------------- | :------------------------ | :------------------------------- |
| uri              | from table_name           | 指定查询某个应用程序下的某一张表 |
| projection       | select column1, column2   | 指定查询的列名                   |
| selection        | where column = value      | 指定 where 的约束条件            |
| selectionArgs    | -                         | 为 where 中的占位符提供具体的值  |
| sortOrder        | order by column1, column2 | 指定查询结果的排序方式           |

查询完成后返回的仍然是一个 Cursor 对象。

```kotlin
 while (cursor.moveToNext()) {
	val column1 = cursor.getString(cursor.getColumnIndex("column1"))
	val column2 = cursor.getInt(cursor.getColumnIndex("column2"))
 }
 cursor.close()
```

`insert()`

```kotlin
val values = contentValuesOf("column1" to "text", "column2" to 1)
contentResolver.insert(uri, values)
```

`update()`

```kotlin
val values = contentValuesOf("colunm1" to "")
contentResolver.update(uri, values, "column1 = ? & column2 = ?", arrayOf("text", "1"))
```

`delete()`

```kotlin
contentResolver.delete(uri, "column1 = ?", arrayOf("1"))
```

### 读取系统联系人

```kotlin
class MainActivity : AppCompatActivity() {

    private val contactsList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactsList)
        contactsView.adapter = adapter

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)
        } else {
            readContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts()
                } else {
                    Toast.makeText(this, "You denied the permission.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun readContacts() {

        contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null)?.apply {
            while (moveToNext()) {
                val displayName = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsList.add("$displayName\n$number")
            }
            adapter.notifyDataSetChanged()
            close()
        }

    }
}
```

## 创建自己的 ContentProvider

### 创建 ContentProvider 的步骤

新建类去继承 ContentProvider，重写抽象方法。

- `onCreate()`

  初始化 ContentProvider 的时候调用。通常会在这里完成对数据库的创建和升级等操作，返回 true 表示内容提供器初始化成功，返回 false 则表示失败。

- `query()`

  从 ContentProvider 中查询数据。uri 参数用于确定查询哪张表，projection 参数用于确定查询哪些列，selection 和 selectionArgs 参数用于约束查询哪些行，sortOrder 参数用于对结果进行排序，查询的结果存放在 Cursor 对象中返回。

- `insert()`

  向 ContentProvider 中添加一条数据。uri 参数用于确定要添加到的表，待添加的数据保存在 values 参数中。添加完成后，返回一个用于表示这条新记录的 URI。

- `update()`

  更新 ContentProvider 中已有的数据。uri 参数用于确定更新哪一张表中的数据，新数据保存在 values 参数中，selection 和 selectionArgs 参数用于约束更新哪些行，受影响的行数将作为返回值返回。

- `delete()`

  从 ContentProvider 中删除数据。uri 参数用于确定删除哪一张表中的数据，selection 和 selectionArgs 参数用于约束删除哪些行，被删除的行数将作为返回值返回。

- `getType()`

  根据传入的内容 URI 来返回相应的 MIME 类型。



内容 URI 标准格式：

```
content://com.example.app.provider/table1
```

期望访问的是 com.example.app 这个应用的 table1 表中 id 为 1 的数据。

```
content://com.example.app.provider/table1/1
```

内容 URI 的格式主要就只有以上两种，以路径结尾就表示期望访问该表中所有的数据，以id 结尾就表示期望访问该表中拥有相应 id 的数据。

我们可以使用通配符的方式来分别匹配这两种格式的内容 URI，规则如下。

- `*` 表示匹配任意长度的任意字符。
- `#` 表示匹配任意长度的数字。

一个能够匹配任意表的内容 URI 格式就可以写成：

```
content://com.example.app.provider/*
```

一个能够匹配 table1 表中任意一行数据的内容 URI 格式就可以写成：

```
content://com.example.app.provider/table1/#
```

UriMatcher 中提供了一个 `addURI()` 方法，这个方法接收 3 个参数，可以分别把 authority、path 和一个自定义代码传进去。

这样，当调用 UriMatcher 的 `match()` 方法时，就可以将一个 Uri 对象传入，返回值是某个能够匹配这个 Uri 对象所对应的自定义代码，利用这个代码，我们就可以判断出调用方期望访问的是哪张表中的数据了。



一个内容 URI 所对应的 MIME 字符串主要由 3 部分组成，Android 对这 3 个部分做出了如下格式规定。

- 必须以 vnd 开头
- 如果内容 URI 以路径结尾，则后接 `android.cursor.dir/`，如果内容 URI 以 id 结尾，则后接 `android.cursor.item/`。
- 最后接上 `vnd.<authority>.<path>`

```
content://com.example.app.provider/table1
vnd.android.cursor.dir/vnd.com.example.app.provider.table1

content://com.example.app.provider/table1/1
vnd.android.cursor.item/vnd.com.example.app.provider.table1
```



```kotlin
class MyProvider : ContentProvider() {

    private val table1Dir = 0
    private val table1Item = 1
    private val table2Dir = 2
    private val table2Item = 3

    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        uriMatcher.addURI("com.example.app.provider", "table1", table1Dir)
        uriMatcher.addURI("com.example.app.provider ", "table1/#", table1Item)
        uriMatcher.addURI("com.example.app.provider ", "table2", table2Dir)
        uriMatcher.addURI("com.example.app.provider ", "table2/#", table2Item)
    }

    override fun onCreate(): Boolean {
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        when (uriMatcher.match(uri)) {
            table1Dir -> {
                // 查询table1表中的所有数据
            }
            table1Item -> {
                // 查询table1表中的单条数据
            }
            table2Dir -> {
                // 查询table2表中的所有数据
            }
            table2Item -> {
                // 查询table2表中的单条数据
            }
        }
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri) = when (uriMatcher.match(uri)) {
        table1Dir -> "vnd.android.cursor.dir/vnd.com.example.app.provider.table1"
        table1Item -> "vnd.android.cursor.item/vnd.com.example.app.provider.table1"
        table2Dir -> "vnd.android.cursor.dir/vnd.com.example.app.provider.table2"
        table2Item -> "vnd.android.cursor.item/vnd.com.example.app.provider.table2"
        else -> null
    }
}
```

### 实现跨程序数据共享

by lazy 代码块是 Kotlin 提供的一种懒加载技术，代码块中的代码一开始并不会执行，只有当 uriMatcher 变量首次被调用的时候才会执行，并且会将代码块中最后一行的代码的返回值赋给 uriMatcher。

```kotlin
class DatabaseProvider : ContentProvider() {

    private val bookDir = 0
    private val bookItem = 1
    private val categoryDir = 2
    private val categoryItem = 3
    private val authority = "com.homurax.databasetest.provider"
    private var dbHelper: MyDatabaseHelper? = null

    private val uriMatcher by lazy {
        val matcher = UriMatcher(UriMatcher.NO_MATCH)
        matcher.addURI(authority, "book", bookDir)
        matcher.addURI(authority, "book/#", bookItem)
        matcher.addURI(authority, "category", categoryDir)
        matcher.addURI(authority, "category/#", categoryItem)
        matcher
    }

    override fun onCreate() = context?.let {
        dbHelper = MyDatabaseHelper(it, "BookStore.db", 2)
        true
    } ?: false

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ) = dbHelper?.let {
        val db = it.readableDatabase
        val cursor = when (uriMatcher.match(uri)) {
            bookDir -> db.query("Book", projection, selection, selectionArgs, null, null, sortOrder)
            bookItem -> {
                val bookId = uri.pathSegments[1]
                db.query("Book", projection, "id = ?", arrayOf(bookId), null, null, sortOrder)
            }
            categoryDir -> db.query("Category", projection, selection, selectionArgs, null, null, sortOrder)
            categoryItem -> {
                val categoryId = uri.pathSegments[1]
                db.query("Category", projection, "id = ?", arrayOf(categoryId), null, null, sortOrder)
            }
            else -> null
        }
        cursor
    }


    override fun insert(uri: Uri, values: ContentValues?) = dbHelper?.let {
        val db = it.writableDatabase
        val uriReturn = when (uriMatcher.match(uri)) {
            bookDir, bookItem -> {
                val newBookId = db.insert("Book", null, values)
                Uri.parse("content://$authority/book/$newBookId")
            }
            categoryDir, categoryItem -> {
                val newCategoryId = db.insert("Category", null, values)
                Uri.parse("content://$authority/category/$newCategoryId")
            }
            else -> null
        }
        uriReturn
    }


    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ) = dbHelper?.let {
            val db = it.writableDatabase
            val updatedRows = when (uriMatcher.match(uri)) {
                bookDir -> db.update("Book", values, selection, selectionArgs)
                bookItem -> {
                    val bookId = uri.pathSegments[1]
                    db.update("Book", values, "id = ?", arrayOf(bookId))
                }
                categoryDir -> db.update("Category", values, selection, selectionArgs)
                categoryItem -> {
                    val categoryId = uri.pathSegments[1]
                    db.update("Category", values, "id = ?", arrayOf(categoryId))
                }
                else -> 0
            }
            updatedRows
        } ?: 0


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?) = dbHelper?.let {
        val db = it.writableDatabase
        val deletedRows = when (uriMatcher.match(uri)) {
            bookDir -> db.delete("Book", selection, selectionArgs)
            bookItem -> {
                val bookId = uri.pathSegments[1]
                db.delete("Book", "id = ?", arrayOf(bookId))
            }
            categoryDir -> db.delete("Category", selection, selectionArgs)
            categoryItem -> {
                val categoryId = uri.pathSegments[1]
                db.delete("Category", "id = ?", arrayOf(categoryId))
            }
            else -> 0
        }
        deletedRows
    } ?: 0


    override fun getType(uri: Uri) = when (uriMatcher.match(uri)) {
        bookDir -> "vnd.android.cursor.dir/vnd.com.homurax.databasetest.provider.book"
        bookItem -> "vnd.android.cursor.item/vnd.com.homurax.databasetest.provider.book"
        categoryDir -> "vnd.android.cursor.dir/vnd.com.homurax.databasetest.provider.category"
        categoryItem -> "vnd.android.cursor.item/vnd.com.homurax.databasetest.provider.category"
        else -> null
    }
}
```

```kotlin
class MainActivity : AppCompatActivity() {

    var bookId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addData.setOnClickListener {
            val uri = Uri.parse("content://com.homurax.databasetest.provider/book")
            val values = contentValuesOf("name" to "A Clash of Kings", "author" to "George Martin", "pages" to 1040, "price" to 22.85)
            val newUri = contentResolver.insert(uri, values)
            bookId = newUri?.pathSegments?.get(1)
        }

        queryData.setOnClickListener {
            val uri = Uri.parse("content://com.homurax.databasetest.provider/book")
            contentResolver.query(uri, null, null, null, null)?.apply {
                while (moveToNext()) {
                    val name = getString(getColumnIndex("name"))
                    val author = getString(getColumnIndex("author"))
                    val pages = getInt(getColumnIndex("pages"))
                    val price = getDouble(getColumnIndex("price"))
                    Log.d("MainActivity", "book name is $name")
                    Log.d("MainActivity", "book author is $author")
                    Log.d("MainActivity", "book pages is $pages")
                    Log.d("MainActivity", "book price is $price")
                }
                close()
            }
        }

        updateData.setOnClickListener {
            bookId?.let {
                val uri = Uri.parse("content://com.homurax.databasetest.provider/book/$it")
                val values = contentValuesOf("name" to "A Storm of Swords", "pages" to 1216, "price" to 24.05)
                contentResolver.update(uri, values, null, null)
            }
        }

        deleteData.setOnClickListener {
            bookId?.let {
                val uri = Uri.parse("content://com.homurax.databasetest.provider/book/$it")
                contentResolver.delete(uri, null, null)
            }
        }
    }
}
```

## Kotlin：泛型和委托

### 泛型的基本用法

Kotlin 中的泛型和 Java 中的泛型有同有异。

泛型主要有两种定义方式：一种是定义泛型类，另一种是定义泛型方法，使用的语法结构都是 `<T>` 。

```kotlin
class MyClass<T> {
    fun method(param: T): T {
        return param
    }
}

val myClass = MyClass<Int>()
val result = myClass.method(123)
```

```kotlin
class MyClass {
    fun <T> method(param: T): T {
        return param
    }
}

val myClass = MyClass()
val result = myClass.method(123)
```

Kotlin 允许我们对泛型的类型进行限制。可以通过指定上界的方式来对泛型的类型进行约束。

```kotlin
class MyClass {
    fun <T : Number> method(param: T): T {
        return param
    }
}

val myClass = MyClass()
val result = myClass.method(123)
```

默认情况下，所有的泛型都是可以指定成可控类型的，因为泛型默认是 `Any?` ，如果要让泛型的类型不可空，指定成 `Any` 即可。

```kotlin
fun StringBuilder.build(block: StringBuilder.() -> Unit): StringBuilder {
    block()
    return this
}
// 使用泛型
fun <T> T.build(block: T.() -> Unit): T {
    block()
    return this
}
```

### 类委托和委托属性

委托是一种设计模式，它的基本理念是：操作对象自己不会去处理某段逻辑，而是会把工作委托给另外一个辅助对象去处理。

Kotlin 中将委托功能分为了两种：类委托和委托属性

#### 类委托

类委托的核心思想在于将一个类的具体实现委托给另一个类去完成。

```kotlin
class MySet<T>(val helperSet: HashSet<T>) : Set<T>  {

    override val size: Int
        get() = helperSet.size

    override fun contains(element: T) = helperSet.contains(element)

    override fun containsAll(elements: Collection<T>) = helperSet.containsAll(elements)

    override fun isEmpty() = helperSet.isEmpty()

    override fun iterator() = helperSet.iterator()
}
```

接收的 HashSet 参数相当于一个辅助对象，Set 接口中的所有的方法实现中，都是调用了辅助对象中相应的方法。

委托模式的意义在于让大部分方法实现调用辅助对象中的方法，少部分的方法实现由自己来重写，甚至加入一些自己独有的方法，那么 MySet 就会成为一个全新的数据结构类。

Kotlin 中委托使用的关键字是 **`by`** ，在接口声明的后面使用 by 关键字，再接上受委托的辅助对象，就可以免去一大堆模板式的代码了。

```kotlin
class MySet<T>(val helperSet: HashSet<T>) : Set<T> by helperSet {
}
```

新增方法，单独重写想要重写的方法即可。

```kotlin
class MySet<T>(val helperSet: HashSet<T>) : Set<T> by helperSet {
    
    fun helloWorld() = println("Hello World")

    override fun isEmpty() = false
}
```

#### 委托属性

委托属性的核心思想是将一个属性（字段）的具体实现委托给另一个类去完成。

委托属性的语法结构：

```kotlin
class MyClass {
    var p by Delegate()
}

class Delegate {
    var propValue: Any? = null

    operator fun getValue(myClass: MyClass, prop: KProperty<*>): Any? {
        return propValue
    }

    operator fun setValue(myClass: MyClass, prop: KProperty<*>, value: Any?) {
        propValue = value
    }
}
```

by 关键字连接了左边的 p 属性和右边的的 Delegate 实例。

这种写法就代表着将 p 属性的具体实现委托给了 Delegate 类去完成。当调用 p 属性的时候会自动调用 Delegate 类的 `getValue()` 方法，当给 p 属性赋值的时候会自动调用 Delegate 类的 `setValue()` 方法。



这是一种标准的代码实现模板，在 Delegate 类中必须实现 `getValue()` 和 `setValue()` 方法，并且使用 operator 关键字进行声明。

`getValue()` 方法接收参数：第一个参数用于声明该 Delegate 类的委托功能可以在什么类中使用；第二个参数 `KProperty<*>` 是 Kotlin 中的一个属性操作类，可以用于获取各种属性相关的值。`<*>` 这种泛型的写法表示你不知道或者不关心泛型的具体类型。返回值可以声明成任何类型，根据具体的实现逻辑去写就行了。

`setValue()` 方法类似。前两个参数与 `getValue()` 方法相同，最后一个参数表示具体要赋值给委托属性的值，这个参数的类型必须和 `getValue()`  方法返回值的类型保持一致。

现在当给 MyClass 的 p 属性赋值时，就会调用 Delegate 类的 `setValue()` 方法，当获取 MyClass 的 p 属性的值时，就会调用 Delegate 类的 `getValue()` 方法。

如果 MyClass 中的 p 属性使用 val 关键字声明，可以不用在 Delegate 类中实现 `setValue()` 方法。

### 实现一个自己的 lazy 函数

```kotlin
val p by lazy {}
```

只有 by 才是 Kotlin 中的关键字，lazy 在这里只是一个高阶函数而已。在 lazy  函数中会创建并返回一个 Delegate  对象。

当我们调用 p 属性的时候，调用的是 Delegate 对象的 `getValue()` 方法，`getValue()` 方法又会调用 lazy 函数传入的 Lambda 表达式，这样表达式中的代码就可以得到执行了。调用 p 属性后得到的值就是 Lambda 表达式中最后一行代码的返回值。

```kotlin
class Later<T>(val block: () -> T) {

    var value: Any? = null

    operator fun getValue(any: Any?, prop: KProperty<*>): T {
        if (value == null) {
            value = block()
        }
        return value as T
    }

}

fun <T> later(block: () -> T) = Later(block)
```

*这里只是大致还原了 lazy 函数的基本实现原理，在诸如同步、空值处理等方面没有实现得很严谨。*