package com.example.residuosapp.model

import android.app.Activity
import android.content.Context
import android.net.Uri

class Usuario private constructor(
        val nombre: String?,
        val correo: String?,
        val fotoUrl: String?,
) {
    val companion = Companion
    companion object {
        /**
         * Recupera los datos del usuario que ha iniciado sesion
         *
         * @param ctx
         * @return El usuario que inicio sesion
         */
        fun get(ctx: Activity): Usuario {
            val sharedPreferences = ctx.getSharedPreferences("usuario", Context.MODE_PRIVATE)

            val nombre = sharedPreferences.getString("nombre", null)
            val correo = sharedPreferences.getString("correo", null)
            val fotoUrl = sharedPreferences.getString("fotoUrl", null)

            return Usuario(nombre, correo, fotoUrl)
        }

        /**
         * Establece el usuario y lo devuelve
         *
         * @param ctx Contexto
         * @param nombre Nombre del usuario
         * @param correo Correo del usuario
         * @param fotoUrl Url a foto de perfil del usuario
         * @return Un objeto usuario con los valores
         */
        fun set(ctx: Activity, nombre: String?, correo: String?, fotoUrl: Uri?): Usuario {
            val sharedPreferences = ctx.getSharedPreferences("usuario", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("nombre", nombre)
            editor.putString("correo", correo)
            editor.putString("fotoUrl", fotoUrl?.toString())
            editor.apply()
            return Usuario(nombre, correo, fotoUrl?.toString())
        }
    }
}
