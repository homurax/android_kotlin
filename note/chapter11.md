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

