package com.yivg.appell.Utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE
import androidx.security.crypto.MasterKey.DEFAULT_MASTER_KEY_ALIAS


class KeystoreManager(private val context: Context) {
    fun saveTokenToKeystore( token: String){
        try {

            val spec = KeyGenParameterSpec.Builder(
                DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                .build()

            val masterKeyAlias = MasterKey.Builder(context)
                .setKeyGenParameterSpec(spec)
                .build()
            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                "my_encrypted_prefs",
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            val editor = sharedPreferences.edit()
            editor.putString("token", token)
            editor.apply()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun getToken(): String? {
        try {
            val masterKeyAlias = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                "my_encrypted_prefs",
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            return sharedPreferences.getString("token", null)
        } catch (e: Exception) {
            e.printStackTrace()
            // Manejar el error al obtener el token del Keystore aquí
        }

        return null
    }
}