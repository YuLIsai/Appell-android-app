package com.yivg.appell

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyboardShortcutGroup
import android.view.Menu
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.yivg.appell.Utils.Constants.Constants
import com.yivg.appell.Utils.KeystoreManager
import com.yivg.appell.Utils.userModel
import com.yivg.appell.databinding.ActivityHomeBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)

        val client: OkHttpClient =  OkHttpClient()

        val KeystoreManager = KeystoreManager(applicationContext)
        val token = KeystoreManager.getToken()

        if ( token != null) {
            Toast.makeText(this, "TOKEN DISPONIBLE", Toast.LENGTH_LONG)
            Log.d(TAG, "token => $token")
            getUserProfile(token, this)
        } else {
            Toast.makeText(this, "TOKEN NO DISPONIBLE", Toast.LENGTH_LONG)
        }




        binding.appBarHome.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_register
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun getUserProfile(token: String, baseContext: Context) {

        val client: OkHttpClient =  OkHttpClient()

        val request = Request.Builder()
            .url(Constants.BASE_URL+ Constants.USER_ROUTE)
            .header("Authorization","Bearer $token")
            .get()
            .build()
        client.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                call.cancel()
                Log.d(ContentValues.TAG, e.message.toString())
                runOnUiThread {
                    Toast.makeText(baseContext, "Ocurrio un error", Toast.LENGTH_LONG)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    Log.d(TAG, "info user ${response.body.toString()}")

                }catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                }
            }

        })


    }
}