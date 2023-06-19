package com.yivg.appell

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.yivg.appell.Utils.Constants.Constants
import com.yivg.appell.Utils.KeystoreManager
import com.yivg.appell.Utils.Models.LoginModel
import com.yivg.appell.Utils.Models.LogoutModel
import com.yivg.appell.databinding.ActivityLoginBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root

        onSessionActive()
        binding.loginButton.setOnClickListener {
            onAuth()
        }

        setContentView(view)
    }

    private fun onSessionActive() {
        val keystoreManager = KeystoreManager(applicationContext)

        val token = keystoreManager.getToken()

        if (token != null) {
            toHome()
        }
//        } else {
//            val rootView = window.decorView.findViewById<View>(android.R.id.content)
//            val optionS: String = getString(R.string.option_yes)
//            val errorMessage = getString(R.string.session_finished)
//            val snackbar = Snackbar.make(rootView, errorMessage, 5000)
//
//            snackbar.setAction(optionS) {
//                Log.d(ContentValues.TAG, "OK DISMISS")
//            }
//            snackbar.show()
//        }
    }

    private fun onAuth() {

        val client = OkHttpClient()

        val formBody: RequestBody = FormBody.Builder()
            .add("email", binding.edtUser.editableText.toString())
            .add("password", binding.edtPass.editableText.toString())
            .build()

        val request = Request.Builder()
            .url(Constants.BASE_URL + Constants.LOGIN_ROUTE)
            .post(formBody)
            .build()

        Log.d(ContentValues.TAG, "METHOD: " + request.method)
        Log.d(ContentValues.TAG, "BODY: " + request.body.toString())
        Log.d(ContentValues.TAG, "URL: " + request.url)
        Log.d(ContentValues.TAG, "HTTPS: " + request.isHttps)
        Log.d(ContentValues.TAG, "HEADERS" + request.headers)

//yulvillegas@wr.xyz

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                call.cancel()
                Log.d(ContentValues.TAG, e.message.toString())
                runOnUiThread {
                    Toast.makeText(
                        baseContext,
                        "Ocurrio un error\n" + e.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
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
                    Log.d(ContentValues.TAG, "RESPONSE => $responseFetch")

                    if (responseData.token.isEmpty()) {
                        runOnUiThread {
                            Toast.makeText(baseContext, responseData.message, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        toHome()
                    }
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                }
            }
        }
        )

    }

    private fun toHome() {
        val intent = Intent(baseContext, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}

