package com.yivg.appell.Utils.Constants

object Constants {
    const val BASE_URL: String = "http://192.168.1.75:8000/api"
    const val LOGIN_ROUTE: String = "/login"
    const val LOGOUT: String = "/logout"
    const val USER_ROUTE: String = "/me"
    const val MEDIC_ROUTE: String ="/medicos"
    const val PATIENT_ROUTE: String ="/pacientes"
    const val PATIENT_NEW_ROUTE: String ="/pacientes/nuevo"
    const val PATIENT_EDIT_ROUTE: String ="/pacientes/edit/%d"
    const val PATIENT_DELETE_ROUTE: String = "/pacientes/delete/%d"
}