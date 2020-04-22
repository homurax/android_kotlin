package com.homurax.webviewtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView.settings.javaScriptEnabled = true
        // 作用是从一个网页跳转到另一个网页时 目标网页仍然在当前 WebView 中显示
        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://cn.bing.com/")
    }
}
