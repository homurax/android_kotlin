package com.homurax.helloworld

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // verbose - debug - info - waring - error
        Log.d("MainActivity", "onCreate execute")
    }
}
