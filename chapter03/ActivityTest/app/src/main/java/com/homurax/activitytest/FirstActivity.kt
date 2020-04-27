package com.homurax.activitytest

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.first_layout.*

class FirstActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("FirstActivity", "Task is is $taskId")
        // 给当前的 Activity 加载一个布局
        // 项目中添加的任何资源都会在R文件中生成一个相应的资源 id
        setContentView(R.layout.first_layout)
        // val button1: Button = findViewById(R.id.button1)

        button1.setOnClickListener {
            // Toast.makeText(this, "You clicked Button1", Toast.LENGTH_SHORT).show()

            // finish()

            // 显式意图 FirstActivity 作为上下文    SecondActivity 作为目标
            // val intent = Intent(this, SecondActivity::class.java)

            // 隐式意图
            // val intent = Intent("com.homurax.activitytest.ACTION_START")
            // intent.addCategory("com.homurax.activitytest.MY_CATEGORY")

            // 启动其他程序的 Activity
            // val intent = Intent(Intent.ACTION_VIEW)
            // intent.data = Uri.parse("https://www.baidu.com")
            // intent.data = Uri.parse("tel:10086")

            // 传递数据
            // val intent = Intent(this, SecondActivity::class.java)
            // val  data = "Hello SecondActivity"
            // intent.putExtra("extra_data", data)
            // startActivity(intent)

            // 启动的 Activity 在销毁时返回一个结果给上一个 Activity
            // val intent = Intent(this, SecondActivity::class.java)
            // requestCode 请求码唯一即可
            // startActivityForResult(intent, 1)

            // standard
            // val intent = Intent(this, FirstActivity::class.java)
            // singleTop
            // val intent = Intent(this, SecondActivity::class.java)
            // startActivity(intent)

            // Serializable Parcelable
            val person = Person("Tom", 20)
            val intent = Intent(this, SecondActivity::class.java).apply {
                putExtra("person_date", person)
            }
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("FirstActivity", "onRestart")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> if (resultCode == Activity.RESULT_OK) {
                data?.getStringExtra("return_data")?.let {
                    Log.d("FirstActivity", it)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // menuInflater 为 调用 getMenuInflater() 获得的 MenuInflater 对象
        menuInflater.inflate(R.menu.main, menu)
        // 允许创建的菜单显示出来
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_item -> Toast.makeText(this, "You clicked Add", Toast.LENGTH_SHORT).show()
            R.id.remove_item -> Toast.makeText(this, "You clicked Remove", Toast.LENGTH_SHORT).show()
        }
        return true
    }
}
