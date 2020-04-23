## WebView 的用法

Android 提供了一个 WebView 控件，借助它就可以在自己的应用程序里嵌入一个浏览器。

访问网络需要声明权限：

```
<uses-permission android:name="android.permission.INTERNET" />
```

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    webView.settings.javaScriptEnabled = true
    // 作用是从一个网页跳转到另一个网页时 目标网页仍然在当前 WebView 中显示
    webView.webViewClient = WebViewClient()
    webView.loadUrl("https://cn.bing.com/")
}
```

## 使用 HTTP 访问网络

### 使用 HttpURLConnection

Android 上发送 HTTP 请求一般有两种方式：HttpURLConnection 和 HttpClient 。在 Android 6.0 系统中，HttpClient 的功能被完全移除了，标志着此功能被正式弃用。

```kotlin
private fun sendRequestWithHttpURLConnection() {
    thread {
        var connection: HttpURLConnection? = null
        try {
            val response = StringBuilder()
            // 获取 HttpURLConnection 实例
            val url = URL("https://cn.bing.com/")
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val input = connection.inputStream
            // 获取到输入流进行读取
            val reader = BufferedReader(InputStreamReader(input))
            reader.use {
                reader.forEachLine {
                    response.append(it)
                }
            }
            showResponse(response.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // 关闭连接
            connection?.disconnect()
        }
    }
}

private fun showResponse(response: String) {
    // runOnUiThread 是对异步消息处理机制进行了一层封装
    runOnUiThread {
        responseText.text = response
    }
}
```

POST 方式提交数据

```kotlin
connection.requestMethod = "POST"
val output = DataOutputStream(connection.outputStream)
output.writeBytes("param1=data1&param2=data2")
```

### 使用 OkHttp

OkHttp 的项目主页地址是：https://github.com/square/okhttp

```
implementation("com.squareup.okhttp3:okhttp:4.5.0")
```

```kotlin
val client = OkHttpClient()
val request = Request.Builder()
        .url("https://cn.bing.com/")
        .build()
val response = client.newCall(request).execute()
val responseData = response.body?.string()

val requestBody = FormBody.Builder()
        .add("param1", "value1")
        .add("param2", "value2")
        .build()
val request2 = Request.Builder()
        .url("https://cn.bing.com/")
        .post(requestBody)
        .build()
```

```kotlin
private fun sendRequestWithOkHttp() {
    thread {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                    .url("https://cn.bing.com/")
                    .build()
            val response = client.newCall(request).execute()
            val responseData = response.body?.string()
            responseData?.let {
                showResponse(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
```

## 解析 XML 格式数据

Pull、SAX、DOM

### Pull 解析方式

```xml
<apps>
    <app>
        <id>1</id>
        <name>Google Maps</name>
        <version>1.0</version>
    </app>
    <app>
        <id>2</id>
        <name>Chrome</name>
        <version>2.1</version>
    </app>
    <app>
        <id>3</id>
        <name>Google Play</name>
        <version>2.3</version>
    </app>
</apps>
```

从 Android 9.0 系统开始，应用程序默认只允许使用 HTTPS 类型的网络请求。需要配置允许以明文的方式在网络上传输数据。

*/res/xml/network_config.xml*

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

*AndroidManifest.xml*

```xml
<application
    ...
    android:networkSecurityConfig="@xml/network_config">
    ...
</application>
```

```kotlin
private fun parseXMLWithPull(xmlData: String) {
    try {
        val factory = XmlPullParserFactory.newInstance()
        val xmlPullParser = factory.newPullParser()
        xmlPullParser.setInput(StringReader(xmlData))
        var eventType = xmlPullParser.eventType
        var id = ""
        var name = ""
        var version = ""
        while (eventType != XmlPullParser.END_DOCUMENT) {
            val nodeName = xmlPullParser.name
            when (eventType) {
                // 开始解析某个节点
                XmlPullParser.START_TAG -> {
                    when (nodeName) {
                        "id" -> id = xmlPullParser.nextText()
                        "name" -> name = xmlPullParser.nextText()
                        "version" -> version = xmlPullParser.nextText()
                    }
                }
                // 完成解析某个节点
                XmlPullParser.END_TAG -> {
                    if ("app" == nodeName) {
                        Log.d(TAG, "id is $id")
                        Log.d(TAG, "name is $name")
                        Log.d(TAG, "version is $version")
                    }
                }
            }
            eventType = xmlPullParser.next()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
```

### SAX 解析方式

使用 SAX 解析，通常情况下我们会新建一个类继承自 DefaultHandler，并重写父类的 5 个方法。

```kotlin
class ContentHandler : DefaultHandler() {

    private var nodeName = ""

    private lateinit var id: StringBuilder

    private lateinit var name: StringBuilder

    private lateinit var version: StringBuilder

    override fun startDocument() {
        id = StringBuilder()
        name = StringBuilder()
        version = StringBuilder()
    }

    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        // 记录当前节点名
        nodeName = localName
        Log.d("ContentHandler", "uri is $uri")
        Log.d("ContentHandler", "localName is $localName")
        Log.d("ContentHandler", "qName is $qName")
        Log.d("ContentHandler", "attributes is $attributes")
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        // 根据当前的节点名判断将内容添加到哪一个 StringBuilder 对象中
        when (nodeName) {
            "id" -> id.append(ch, start, length)
            "name" -> name.append(ch, start, length)
            "version" -> version.append(ch, start, length)
        }
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        if ("app" == localName) {
            Log.d("ContentHandler", "id is ${id.toString().trim()}")
            Log.d("ContentHandler", "name is ${name.toString().trim()}")
            Log.d("ContentHandler", "version is ${version.toString().trim()}")
            // 最后要将StringBuilder清空掉
            id.setLength(0)
            name.setLength(0)
            version.setLength(0)
        }
    }

    override fun endDocument() {
    }

}
```

## 解析 JSON 格式数据

```json
[
    {"id":"5", "version":"5.5", "name":"Clash of Clans"},
    {"id":"6", "version":"7.0", "name":"Boom Beach"},
    {"id":"7", "version":"3.5", "name":"Clash Royale"}
]
```

### 使用 JSONObject

```kotlin
private fun parseJSONWithJSONObject(jsonData: String) {
    try {
        val jsonArray = JSONArray(jsonData)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val id = jsonObject.getString("id")
            val name = jsonObject.getString("name")
            val version = jsonObject.getString("version")
            Log.d(TAG, "id is $id")
            Log.d(TAG, "name is $name")
            Log.d(TAG, "version is $version")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
```

### 使用 GSON

GSON 的项目主页地址是：https://github.com/google/gson

GSON 可以将一段 JSON 格式的字符串自动映射成一个对象。

```kotlin
// {"name":"Tom", "age":20}
val gson = Gson()
val person = gson.fromJson(jsonData, Person::class.java)

// [{"name":"Tom", "age":20}, {"name":"Jack", "age":25}, {"name":"Lily", "age":22}]
val typeOf = object : TypeToken<List<Person>>() {}.type
val person = gson.fromJson<List<Person>>(jsonData, typeOf)
```

```kotlin
class App(val id: String, val name: String, val version: String)

private fun parseJSONWithGSON(jsonData: String) {
    val gson = Gson()
    val typeOf = object : TypeToken<List<App>>() {}.type
    val appList = gson.fromJson<List<App>>(jsonData, typeOf)
    for (app in appList) {
        Log.d(TAG, "id is ${app.id}")
        Log.d(TAG, "name is ${app.name}")
        Log.d(TAG, "version is ${app.version}")
    }
}
```

## 网络请求回调的实现方式

```kotlin
interface HttpCallbackListener {
    fun onFinish(response: String)
    fun onError(e: Exception)
}

fun sendHttpRequest(address: String, listener: HttpCallbackListener) {
    thread { // 开启新线程
        var connection: HttpURLConnection? = null
        try {
            val response = StringBuilder()
            val url = URL(address)
            connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 8000
            connection.readTimeout = 8000
            val input = connection.inputStream
            val reader = BufferedReader(InputStreamReader(input))
            reader.use {
                reader.forEachLine {
                    response.append(it)
                }
            }
            // 回调 onFinish()
            listener.onFinish(response.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            // 回调 onError()
            listener.onError(e)
        } finally {
            connection?.disconnect()
        }
    }
}

// OkHttp 库中自带的回调接口
fun sendOkHttpRequest(address: String, callback: okhttp3.Callback) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(address)
        .build()
    client.newCall(request).enqueue(callback)
}
```

不管使用 HttpURLConnection 还是 OkHttp，最终的回调接口都还是在子线程中运行的，因此不可以在这里执行任何的 UI 操作，除非借助 `runOnUiThread()` 方法来进行线程转换。

### 























