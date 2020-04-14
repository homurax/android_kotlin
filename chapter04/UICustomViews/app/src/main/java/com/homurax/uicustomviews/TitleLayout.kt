package com.homurax.uicustomviews

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.title.view.*

class TitleLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        // 对标题栏布局进行动态加载 构建 LayoutInflater 对象，动态加载布局文件
        // 传入布局文件id 和 父布局
        LayoutInflater.from(context).inflate(R.layout.title, this)
        titleBack.setOnClickListener {
            // Kotlin 中的类型强制转换使用的关键字是 as
            val activity = context as Activity
            activity.finish()
        }
        titleEdit.setOnClickListener {
            Toast.makeText(context, "You clicked Edit button.", Toast.LENGTH_SHORT).show()
        }
    }
}