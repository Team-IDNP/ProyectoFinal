package com.example.residuosapp.model

import android.util.Log
import com.example.residuosapp.controller.main.home.MapFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

val x = "gh"

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


            //Esto sirve pero no se como enviarlo al otro porque se ejecuta en otro hilo y no tengo referencia del mapa aqui
            /*val db = FirebaseDatabase.getInstance()
            val myRef = db.getReference("alerts")
            // Read from the database
            myRef.addValueEventListener(object: ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    for (singleSnapshot in snapshot.children) {
                        val u = singleSnapshot.getValue(Alert::class.java)
                        if (u != null) {
                            val lat =  u.getUbiLat()
                            val long = u.getUbiLong()
                            arrl.add(Marcador(lat.toDouble(), long.toDouble()))
                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("ERROR", "Failed to read value.", error.toException())
                }

            })*/



            return arrl
        }
    }
}
