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

## 最好用的网络库：Retrofit

Retrofit 的项目主页地址是：https://github.com/square/retrofit

OkHttp 侧重的是底层通信的实现，而 Retrofit 侧重的是上层接口的封装。

### Retrofit 的基本用法

```
implementation 'com.squareup.retrofit2:retrofit:2.8.1'
implementation 'com.squareup.retrofit2:converter-gson:2.8.1'
```

服务器提供的接口通常是可以根据功能来归类的。将服务器接口合理归类能够让代码结构变的更加合理，提高可阅读性和可维护性。

Retrofit 允许我们对服务器接口进行归类，将功能同属一类的服务器接口定义到同一个接口文件当中。可以配置好一个根路径，然后在指定服务器接口地址时只需要使用相对路径即可。

我们不用关心网络通信的细节，只需要在接口文件中声明一系列方法和返回值，然后通过注解的方式指定该方法对应哪个服务器接口，以及需要提供哪些参数。

Retrofit 的接口文件建议以具体的功能种类名开头，并以 Service 结尾。方法的返回值必须声名成 Retrofit 中内置的 Call 类型，并通过泛型来指定服务器响应的数据应该转换成什么对象。Retrofit 结合 RxJava 使用就可以将返回值声明成 Observable、Flowable 等类型。

```kotlin
interface AppService {
    @GET("get_data.json")
    fun getAppData(): Call<List<App>>
}
```

```kotlin
getAppDataBtn.setOnClickListener {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://127.0.0.1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val appService = retrofit.create(AppService::class.java)
    appService.getAppData().enqueue(object : Callback<List<App>> {

        override fun onResponse(call: Call<List<App>>, response: Response<List<App>>) {
            val list = response.body()
            if (list != null) {
                for (app in list) {
                    Log.d("MainActivity", "id is ${app.id}")
                    Log.d("MainActivity", "name is ${app.name}")
                    Log.d("MainActivity", "version is ${app.version}")
                }
            }
        }

        override fun onFailure(call: Call<List<App>>, t: Throwable) {
            t.printStackTrace()
        }
    })
}
```

### 处理复杂的接口地址类型

Retrofit 对所有常用的 HTTP 请求类型都进行了支持，使用 `@GET`、`@POST`、`@PUT`、`@PATCH`、`@DELETE` 注解就可以让 Retrofit 发出响应类型的请求。

`@Path("page")` 声明的参数的值会被替换到地址中占位符的位置。

`@Query("param")`，Retrofit 会自动按照带参数 GET 请求的格式将参数构建到请求地址当中。

使用 ResponseBody 表示 Retrofit 能够接收任意类型的响应数据，并且不会对响应数据进行解析。

`@Body`，Retrofit 会自动将对象中的数据转换成 JSON 格式的文本，并放到 HTTP 请求的 body 部分。

`@Headers("...", "...")` 可以直接指定 header 参数。

`@Header("User-Agent")` 动态指定 header 的值。

```kotlin
@GET("get_data.json")
fun getData(@Header("User-Agent") userAgent: String, @Header("Cache-Control") cacheControl: String): Call<Data>
// fun getData(@Query("user") user: String, @Query("token") token: String): Call<Data>

@DELETE("data/{id}")
fun deleteData(@Path("id") id: String): Call<ResponseBody>

@POST("data/create")
fun createData(@Body data: Data): Call<ResponseBody>
```

### Retrofit 构建器的最佳写法

Retrofit 对象是全局通用的，只需要调用 `create()` 方法时针对不同的 Service 接口传入相应的 Class 类型即可。

```kotlin
object ServiceCreator {

    private const val BASE_URL = "http://127.0.0.1/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

}

val appService = ServiceCreator.create<AppService>()
```

## Kotlin：使用协程编写高效的并发程序

使用协程可以在编程语言的层面实现不同协程之前的切换，从而提升并发编程的运行效率。

```kotlin
fun foo() {
    print(1)
    print(2)
    print(3)
}

fun bar() {
    print(4)
    print(5)
    print(6)
}
```

没有开启线程的情况下，先后调用 `foo()` 和 `bar()` 这两个方法，理论上输出的结果一定是 123456 。

如果使用了协程，在协程 A 中去调用 `foo()` 方法，协程 B 中去调用 `bar()` 方法，它们仍然会运行在同一个线程当中，但是在执行 `foo()` 方法时随时都有可能被挂起转而去执行 `bar()` 方法，执行 `bar()` 方法时也随时都有可能被挂起转而去执行 `foo()` 方法，最终的输出结果也就变得不确定了。

协程允许我们在单线程模式下模拟多线程编程的效果，代码执行时的挂起与恢复完全是由编程语言来控制的，和操作系统无关。

### 协程的基本用法

kotlinx.coroutines : https://github.com/Kotlin/kotlinx.coroutines

Kotlin 并没有将协程纳入标准库的 API 当中，而是以依赖库的形式提供的。

```
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.5'
```

---

```kotlin
fun main() {
    GlobalScope.launch {
        println("codes run in coroutine scope")
        delay(1500)
        println("codes run in coroutine scope finished")
    }
    Thread.sleep(1000)
}
```

**GlobalScope.launch** 函数可以创建一个协程的作用域，传递给 launch 函数的 Lambda 表达式就是在协程中运行的了。并且创建的是顶层协程，协程当应用程序运行结束时也会跟着一起结束。

`delay()` 函数是一个非阻塞式的挂起函数，它只挂起当前协程，并不会影响其他协程的运行。此函数只能在协程的作用域或其他挂起函数中调用。

`Thread.sleep()` 方法阻塞当前的线程，运行在该线程下的所有协程都会被阻塞。

---

```kotlin
fun main() {
    runBlocking {
        println("codes run in coroutine scope")
        delay(1500)
        println("codes run in coroutine scope finished")
    }
    Thread.sleep(1000)
}
```

**runBlocking** 函数同样会创建一个协程的作用域，它保证在协程作用域内的所有代码和子协程没有全部执行完之前一直阻塞当前*线程*。所以两条信息都可以打印出来。runBlocking 函数通常只应该在测试环境下使用。

---

```kotlin
fun main() {
    // 创建多个协程
    runBlocking {
        launch {
            println("launch1")
            delay(1000)
            println("launch1 finished")
        }
        launch {
            println("launch2")
            delay(1000)
            println("launch2 finished")
        }
    }
}
```

**launch** 函数必须在协程的作用域中才能调用，它会在当前协程的作用域下创建子协程。

如果外层作用域的协程结束了，该作用域下的所有子协程也会一同结束。

---

Kotlin 提供了一个 **`suspend`** 关键字，使用它可以将任意函数声明成挂起函数，挂起函数之间都是可以互相调用的。

```kotlin
suspend fun printDot() {
    println(".")
    delay(1000)
}
```

suspend 关键字并不会给函数提供协程作用域，函数中不能直接调用 launch，可以通过 coroutineScope 函数来解决。

---

**coroutineScope** 函数也是一个挂起函数，因此可以在其他挂起函数中调用。它会继承外部的协程作用域并创建一个子作用域。通过这个特性，就可以给任意挂起函数提供协程作用域了。

```kotlin
suspend fun printDot() = coroutineScope {
    launch {
        println(".")
        delay(1000)
    }
}
```

coroutineScope 函数同样保证其作用域内的所有代码和子协程在全部执行完之前，会一直阻塞当前协程。但是不影响其他协程，也不影响任何线程。

```kotlin
fun main() {
    runBlocking {
        coroutineScope {
            launch {
                for (i in 1..10) {
                    println(i)
                    delay(1000)
                }
                println("coroutineScope finished")
            }
        }
        println("runBlocking finished")
    }
}
```

### 更多的作用域构建器

通过调用返回的 Job 对象的 `cancel()` 方法来取消协程。

```kotlin
val job = GlobalScope.launch {
    // TODO
}
job.cancel()
```

如果每次创建的都是顶层协程，当 Activity 关闭时，就要逐个调用已创建协程的 `cancel()` 方法，所以不太建议使用顶层协程。

```kotlin
val job = Job()
// CoroutineScope() 是函数 返回一个 CoroutineScope 对象
val scope = CoroutineScope(job)
scope.launch {
    // TODO
}
job.cancel()
```

所有调用 CoroutineScope 的 launch 函数所创建的协程，都会被关联在 Job 对象的作用域下面。只需要调用一次 `cancel()` 方法，就可以将同一作用域内的所有协程全部取消。

---

**async** 函数会创建一个新的子协程并返回一个 Deferred 对象。如果需要获取 async 函数代码块的执行结果，调用 Deferred 对象的 `await()` 方法即可。async 函数必须在协程作用域当中才能使用。

```kotlin
runBlocking {
    val result = async {
        5 + 5
    }.await()
    println(result)
}
```

实际上调用 async 函数之后，代码块中的代码会立刻开始执行。当调用 `await()` 方法时，如果代码块中的代码还没执行完，那么 `await()` 方法会将当前协程阻塞住，直到可以获得 async 函数的结果。

两个 async 是串行的关系。

```kotlin
runBlocking {
    val start = System.currentTimeMillis()
    val result1 = async {
        delay(1000)
        5 + 5
    }.await()
    val result2 = async {
        delay(1000)
        4 + 6
    }.await()
    println("result is ${result1 + result2}")
    val end = System.currentTimeMillis()
    println("cost ${end - start} ms.")
}
```

两个 async 是并行关系。

```kotlin
runBlocking {
    val start = System.currentTimeMillis()
    val deferred1 = async {
        delay(1000)
        5 + 5
    }
    val deferred2 = async {
        delay(1000)
        4 + 6
    }
    println("result is ${deferred1.await() + deferred2.await()}")
    val end = System.currentTimeMillis()
    println("cost ${end - start} ms.")
}
```

---

**`withContext()`** 函数是一个挂起函数，可以理解成 async 函数的一种简化版。

```kotlin
runBlocking {
    val result = withContext(Dispatchers.Default) {
        5 + 5
    }
    println(result)
}
```

调用 `withContext()` 函数之后会立即执行代码块中代码，同时将当前协程阻塞住，最后一行的执行结果作为 `withContext()` 函数的返回值返回。基本上相当于 `val result = async{ 5 + 5 }.await()` 的写法。

唯一不同的是 `withContext()` 函数强制要求指定一个 *线程参数*。

很多传统编程情况下需要开启多线程执行的并发任务，现在可以在一个线程下开启多个协程来执行。但是并不意味着永远不需要开启线程了，Android 中要求网络请求必须在子线程中进行。如果在主线程中开启了协程去执行网络请求，那么程序仍然会出错。这个时候应该通过 *线程参数* 给协程指定一个具体的运行线程。

线程参数主要有以下 3 种值可选：

- `Dispatchers.Default`

  表示会使用一种默认低并发的线程策略。当要执行的代码属于计算密集型任务时，开启过高的并发反而可能影响任务的运行效率，此时就可以使用这个值。

- `Dispatchers.IO`

  表示会使用一种高并发的线程策略。当要执行的代码大多数时间是在阻塞和等待中，比如执行网络请求是，为了能够支持更高的并发数量，就可以使用这个值。

- `Dispatchers.Main`

  表示不会开启子线程，而是在 Android 主线程中执行代码。这个值只能在 Android 项目中使用，纯 Kotlin 程序使用会出现错误。

### 使用协程简化回调的写法

通过 **suspendCoroutine** 函数可以将传统依靠匿名类来实现的回调机制大幅简化。

suspendCoroutine 函数必须在协程作用域或挂起函数中才能调用，接收一个 Lambda 表达式参数，主要作用是将当前协程立即挂起，然后在一个普通的线程中执行 Lambda 表达式中的代码。Lambda 表达式的参数列表上会传入一个 Continuation 参数，调用它的 `resume()` 或 `resumeWithException()` 方法可以让协程恢复执行。



将 `await()` 定义为 ` Call<T>` 的扩展函数，通过 suspendCoroutine 挂起当前协程：

```kotlin
suspend fun <T> Call<T>.await(): T {
    return suspendCoroutine { continuation ->
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                if (body != null) {
                    continuation.resume(body)
                } else {
                    continuation.resumeWithException(RuntimeException("response body is null"))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}
```

调用：

```kotlin
suspend fun getAppData() {
    try {
        val appList = ServiceCreator.create<AppService>().getAppData().await()
        // TODO
        println(appList)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
```