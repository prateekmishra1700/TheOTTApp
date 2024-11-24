package com.example.ottapp2



import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var dotViews: Array<View>
    private var currentDotIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val dot1 = findViewById<View>(R.id.vdot1)
        val dot2 = findViewById<View>(R.id.vdot2)
        val dot3 = findViewById<View>(R.id.vdot3)
        val dot4 = findViewById<View>(R.id.vdot4)
        dotViews = arrayOf(dot1, dot2, dot3, dot4)

        lifecycleScope.launch {
            updateAnimation()
        }

        lifecycleScope.launch {
            delay(3000)
            navigateToMain()
        }
    }
    private suspend fun updateAnimation() {
        while (true) {
            for (i in dotViews.indices) {
                dotViews[i].setBackgroundResource(R.drawable.dot_unselected)
            }
            dotViews[currentDotIndex].setBackgroundResource(R.drawable.dot_selected)
            currentDotIndex = (currentDotIndex + 1) % 4
            delay(1000)
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this@SplashActivity,MainActivity::class.java))
        finish()
    }
}
