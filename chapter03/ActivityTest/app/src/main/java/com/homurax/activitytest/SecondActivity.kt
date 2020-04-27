package com.homurax.activitytest

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.second_layout.*

class SecondActivity : BaseActivity() {

    companion object {
        fun actionStart(context: Context, data1: String, data2: String) {
            val intent = Intent(context, SecondActivity::class.java).apply {
                putExtra("param1", data1)
                putExtra("param2", data2)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SecondActivity", "Task is is $taskId")
        setContentView(R.layout.second_layout)

        // Serializable
        // val person = intent?.getSerializableExtra("person_date") as Person
        // Parcelable
        val person = intent?.getParcelableExtra("person_date") as Person

        intent?.getStringExtra("extra_data")?.let { Log.d("SecondActivity", it) }

        button2.setOnClickListener {
            // val intent = Intent()
            // intent.putExtra("return_data", "Hello FirstActivity")
            // setResult(Activity.RESULT_OK, intent)
            // finish()
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SecondActivity", "onDestroy")
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("return_data", "Hello FirstActivity")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
