package com.yivg.appell

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.yivg.appell.Utils.Constants.Constants
import com.yivg.appell.Utils.KeystoreManager
import com.yivg.appell.Utils.LoginModel
import com.yivg.appell.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root


        binding.loginButton.setOnClickListener{
            val user = binding.username.text.toString()
            val pass = binding.password.text.toString()

            if( user.isNullOrEmpty()  && pass.isNullOrEmpty()){
                Toast.makeText(this, "ERROR NO SE ADMITEN ESO", Toast.LENGTH_LONG)
            } else {
                onAuth()
            }

        }

        setContentView(view)
    }


    fun onAuth() {


        val client: OkHttpClient =  OkHttpClient()

        val formBody: RequestBody = FormBody.Builder()
            .add("email",binding.username.text.toString())
            .add("password",binding.password.text.toString())
            .build()

        val request = Request.Builder()
            .url(Constants.BASE_URL+Constants.LOGIN_ROUTE)
            .post(formBody)
            .build()

        Log.d(TAG, "METHOD: "+ request.method)
        Log.d(TAG, "BODY: " +request.body.toString())
        Log.d(TAG,"URL: " + request.url)
        Log.d(TAG,"HTTPS: " + request.isHttps)
        Log.d(TAG,"HEADERS" + request.headers)

//yulvillegas@wr.xyz

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                call.cancel()
                Log.d(TAG, e.message.toString())
                runOnUiThread {
                    Toast.makeText(baseContext, "Ocurrio un error\n"+e.message.toString(), Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                try {
                    val gson = Gson()
                    val responseFetch = response.body?.string()
                    val responseData = gson.fromJson(responseFetch, LoginModel::class.java)

                    val secureTokens = KeystoreManager(applicationContext)
                    val token = responseData.token
                    secureTokens.saveTokenToKeystore(token)
                    Log.d(TAG,"RESPONSE => $responseFetch")

                    if (responseData.token.isEmpty()) {
                        runOnUiThread {
                            Toast.makeText(baseContext, responseData.message, Toast.LENGTH_LONG)
                        }
                    }

                    else {
                        toHome()
                    }
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                }

            }
        }
        )

    }
    fun toHome(){
        finishActivity(1)
        val intent = Intent(baseContext, HomeActivity::class.java)
        startActivity(intent)
    }
}