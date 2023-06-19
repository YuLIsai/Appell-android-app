package com.yivg.appell.ui.register

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.yivg.appell.HomeActivity
import com.yivg.appell.R
import com.yivg.appell.Utils.Constants.Constants
import com.yivg.appell.Utils.KeystoreManager
import com.yivg.appell.Utils.Models.PatientModel
import com.yivg.appell.databinding.FragmentRegisterBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException


private const val ARG_PARAM1 = "jsonPatient"
private var id_paciente: Int = 0

class RegisterFragment : Fragment() {


    private var _binding: FragmentRegisterBinding? = null
    private lateinit var keystoreManager: KeystoreManager
    private var jsonPatient: String? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            jsonPatient = it.getString(ARG_PARAM1)
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        if (jsonPatient != null) {

            val gson = Gson()
            val patient = gson.fromJson(jsonPatient, PatientModel::class.java)

            id_paciente = patient.id
            binding.edtName.setText(patient.nombre)
            binding.bloodType.setText(patient.tipo_sangre)
            binding.edtNss.setText(patient.nss)
            binding.edtAlergy.setText(patient.alergias)
            binding.edtPhone.setText(patient.telefono)
            binding.edtAddress.setText(patient.domicilio)

        }
        val nombre = binding.edtName.text.toString()
        val nss = binding.edtNss.text.toString()
        val tipoSangre = binding.bloodType.text?.toString()
        val alergias = binding.edtAlergy.text.toString()
        val telefono = binding.edtPhone.text.toString()
        val domicilio = binding.edtAddress.text.toString()

        binding.registButton.setOnClickListener {
            keystoreManager = KeystoreManager(requireContext())
            val token = keystoreManager.getToken()
            if (nombre.isNullOrEmpty() && nss.isNullOrEmpty() && tipoSangre.isNullOrEmpty() && alergias.isNullOrEmpty() && telefono.isNullOrEmpty() && domicilio.isNullOrEmpty()) {
                token?.let { it1 -> postPatient(it1, false) }
                Log.d(TAG, "FALSE")
            } else {
                token?.let { it1 -> postPatient(it1, true, id_paciente) }
                Log.d(TAG, "TRUE")
            }

        }
        val items = listOf("AB+", "AB-", "A+","A-", "B+", "B-","O+","O-")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.listItem.editText as? AutoCompleteTextView)?.setAdapter(adapter)


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun postPatient(token: String, isFull: Boolean, idPatient: Int = 0) {
        val client = OkHttpClient()

        val url =
            if (isFull) Constants.BASE_URL + Constants.PATIENT_EDIT_ROUTE.format(idPatient) else Constants.BASE_URL + Constants.PATIENT_NEW_ROUTE


        val formBody: RequestBody = FormBody.Builder()
            .add("nombre", binding.edtName.editableText.toString())
            .add("tipo_sangre",binding.bloodType.text.toString())
            .add("nss", binding.edtNss.editableText.toString())
            .add("alergias", binding.edtAlergy.editableText.toString())
            .add("telefono", binding.edtPhone.editableText.toString())
            .add("domicilio", binding.edtAddress.editableText.toString())
            .build()

        val request = if(isFull) Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .put(formBody)
            .build() else Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .post(formBody)
            .build()

        Log.d(ContentValues.TAG, "METHOD: " + request.method)
        Log.d(ContentValues.TAG, "BODY: " + request.body.toString())
        Log.d(ContentValues.TAG, "URL: " + request.url)
        Log.d(ContentValues.TAG, "HTTPS: " + request.isHttps)
        Log.d(ContentValues.TAG, "HEADERS" + request.headers)

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
                val ac = response.body?.string()
                println(ac)
                activity?.runOnUiThread {
                    binding.edtName.editableText.clear()
                    binding.bloodType.text?.clear()
                    binding.edtNss.editableText.clear()
                    binding.edtAlergy.editableText.clear()
                    binding.edtPhone.editableText.clear()
                    binding.edtAddress.editableText.clear()

                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }

}


