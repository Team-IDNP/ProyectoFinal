package com.example.residuosapp.model

import java.util.ArrayList

data class Marcador(
    val latitud: Double,
    val longitud: Double,
) {
    companion object {
        // Recupera los marcadores de la base de dates
        fun getMarcadores(): ArrayList<Marcador> {
            val arrl = arrayListOf<Marcador>()
            // Mockup
            arrl.add(Marcador(10.0, 20.0))
            arrl.add(Marcador(20.0, 12.0))
            return arrl
        }
    }
}
