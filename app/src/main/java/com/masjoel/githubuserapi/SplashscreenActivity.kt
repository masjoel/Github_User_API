@file:Suppress("DEPRECATION")

package com.masjoel.githubuserapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashscreenActivity : AppCompatActivity() {
    private val timeLoading = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        Handler().postDelayed({
            val moveActivity = Intent(this@SplashscreenActivity, MainActivity::class.java)
            startActivity(moveActivity)
            finish()
        }, timeLoading.toLong())
    }
}