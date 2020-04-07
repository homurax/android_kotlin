package com.homurax.activitytest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.third_layout.*

class ThirdActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ThirdActivity", "Task is is $taskId")
        setContentView(R.layout.third_layout)

        button3.setOnClickListener {
            ActivityCollector.finishAll()
            // kill 当前进程
            // killProcess() 只能用于杀掉当前程序的进程 不能用于杀掉其他程序
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }
}
