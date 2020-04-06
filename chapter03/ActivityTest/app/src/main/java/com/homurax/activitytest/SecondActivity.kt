package com.homurax.activitytest

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.second_layout.*

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_layout)

        val data = intent?.getStringExtra("extra_data")
        data?.let { Log.d("SecondActivity", it) }

        button2.setOnClickListener {
            val intent = Intent()
            intent.putExtra("return_data", "Hello FirstActivity")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("return_data", "Hello FirstActivity")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
