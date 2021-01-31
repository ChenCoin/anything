package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val jniServer = JniServer()
        // Example of a call to a native method
        val tv: TextView = findViewById(R.id.sample_text)
        tv.text = jniServer.stringFromJNI()

        ViewInitializer(this).init()
    }
}
