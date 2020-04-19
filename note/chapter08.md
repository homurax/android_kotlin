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
private val uriMatcher by lazy {
    val matcher = UriMatcher(UriMatcher.NO_MATCH)
    matcher.addURI(authority, "book", bookDir)
    matcher.addURI(authority, "book/#", bookItem)
    matcher.addURI(authority, "category", categoryDir)
    matcher.addURI(authority, "category/#", categoryItem)
    matcher
}
```



