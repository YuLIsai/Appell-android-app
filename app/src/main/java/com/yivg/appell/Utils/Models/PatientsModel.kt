package com.yivg.appell.Utils.Models

import com.google.gson.annotations.SerializedName


data class PatientModel(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("nss") val nss: String,
    @SerializedName("tipo_sangre") val tipo_sangre: String,
    @SerializedName("alergias") val alergias: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("domicilio") val domicilio: String
)
