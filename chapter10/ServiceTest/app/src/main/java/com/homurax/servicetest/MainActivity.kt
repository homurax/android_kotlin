package com.homurax.servicetest

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var downloadBinder: MyService.DownloadBinder

    private val connection = object : ServiceConnection {
        // Activity 与 成功绑定时调用
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            downloadBinder = service as MyService.DownloadBinder
            downloadBinder.startDownload()
            downloadBinder.getProgress()
        }
        // Service 的创建进程崩溃或者被 kill 掉时调用
        override fun onServiceDisconnected(name: ComponentName) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startServiceBtn.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            startService(intent)
        }

        stopServiceBtn.setOnClickListener {
            // Service 内部调用 stopSelf() 方法亦可自我停止
            val intent = Intent(this, MyService::class.java)
            stopService(intent)
        }

        bindServiceBtn.setOnClickListener {
            val intent = Intent(this, MyService::class.java)
            // 绑定 Service
            // 标志位 BIND_AUTO_CREATE 表示绑定后自动创建 Service
            // 且 MyService 中 onCreate() 执行 onStartCommand() 不会执行
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        unbindServiceBtn.setOnClickListener {
            unbindService(connection)
        }

        startIntentServiceBtn.setOnClickListener {
            Log.d("MainActivity", "Thread id is ${Thread.currentThread().name}")
            val intent = Intent(this, MyIntentService::class.java)
            startService(intent)
        }
    }
}
