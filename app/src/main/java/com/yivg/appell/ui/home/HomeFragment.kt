package com.yivg.appell.ui.home

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.yivg.appell.R
import com.yivg.appell.Utils.Adapters.PatientAdapter
import com.yivg.appell.Utils.Constants.Constants
import com.yivg.appell.Utils.KeystoreManager
import com.yivg.appell.Utils.Models.PatientModel
import com.yivg.appell.databinding.FragmentHomeBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var keystoreManager: KeystoreManager
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        keystoreManager = KeystoreManager(requireContext())
        val token = keystoreManager.getToken()
        token?.let { getPatient(it) }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getPatient(token: String) {
        val client = OkHttpClient()
        val gson = Gson()
        val url = Constants.BASE_URL + Constants.PATIENT_ROUTE

        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $token")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    val optionS: String = getString(R.string.option_yes)
                    val errorMessage = getString(R.string.error_message_network)
                    val snackbar = Snackbar.make(view!!, errorMessage, 8000)

                    snackbar.setAction(optionS) {
                        call.cancel()
                    }
                    snackbar.show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseFetch = response.body?.string()

                activity?.runOnUiThread {
                    Log.d(TAG, "response => $responseFetch")
                    val responseData = gson.fromJson(responseFetch, Array<PatientModel>::class.java)
                    println("RESPONSE: ${responseData[0]}")
                    val adapter = PatientAdapter(responseData.toMutableList()) { position ->
                        onDeletePatient(responseData[position].id, token)
                    }
                    val swipeGesture = object : SwipeGesture(requireContext()) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            when (direction) {
                                ItemTouchHelper.RIGHT -> {
                                   adapter.deleteItem(viewHolder.bindingAdapterPosition)
                                }
                            }
                        }
                    }
                    binding.rcPatient.layoutManager = LinearLayoutManager(context)
                    binding.rcPatient.adapter = adapter

                    val touchHelper = ItemTouchHelper(swipeGesture)
                    touchHelper.attachToRecyclerView(binding.rcPatient)
                }

            }
        })
    }

    private fun onDeletePatient(idPatient: Int, token: String) {
        val client = OkHttpClient()

        val url = Constants.BASE_URL + Constants.PATIENT_DELETE_ROUTE.format(idPatient)

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .delete()
            .build()

        Log.d(ContentValues.TAG, "METHOD: " + request.method)
        Log.d(ContentValues.TAG, "BODY: " + request.body.toString())
        Log.d(ContentValues.TAG, "URL: " + request.url)
        Log.d(ContentValues.TAG, "HTTPS: " + request.isHttps)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    val errorMessage = getString(R.string.error_message_network)
                    val snackbar = Snackbar.make(view!!, errorMessage, 3000)
                    snackbar.show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                val ac = response.body?.string()
                println(ac)
                activity?.runOnUiThread {
                    val errorMessage = getString(R.string.Success)
                    val snackbar = Snackbar.make(view!!, errorMessage, 3000)
                    snackbar.show()
                }
            }
        })
    }
}