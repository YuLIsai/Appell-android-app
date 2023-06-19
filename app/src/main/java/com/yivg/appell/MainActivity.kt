package com.yivg.appell

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Pair
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.yivg.appell.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var topAnimation: Animation
    private lateinit var bottomAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)


        val imaged = binding.imageView2
        val images = binding.imageView4

        imaged.startAnimation(topAnimation)
        images.startAnimation(bottomAnimation)

        Handler().postDelayed({
            val intent = Intent(baseContext, LoginActivity::class.java)

            val pairs = arrayOf(
                Pair<View, String>(images, "splash_img_second")
            )

            val options = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity, *pairs)
            startActivity(intent, options.toBundle())
            finish()
        }, 4000)

        setContentView(view)
    }


}