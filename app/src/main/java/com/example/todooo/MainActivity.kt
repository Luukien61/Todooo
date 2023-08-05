package com.example.todooo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }
}