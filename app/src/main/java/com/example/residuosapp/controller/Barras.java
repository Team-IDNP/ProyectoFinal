package com.example.residuosapp.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.residuosapp.model.Alert;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

public class Barras extends View {

    private int alto, ancho;
    private int temp1, temp2;
    private int colorBar = Color.BLUE;
    private int[] vals;
    private String[] names;
    private int cant;
    public static Canvas cv;
    int max = 0;

    public Barras(Context context, AttributeSet as) {
        super(context, as);
    }

    public void setColor(int color) {
        colorBar = color;
    }

    public void setAtributos(String[] nombres, int[] valores) {
        names = nombres;
        vals = valores;
    }

    public void drawBars() {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference("alerts");
        Map<String, Integer> fechas = new HashMap<>();
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {


            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Alert u = ds.getValue(Alert.class);
                    if (u != null) {
                        if(fechas.containsKey(u.getFecha())){
                            Integer c = fechas.get(u.getFecha());
                            assert c != null;
                            Integer newC = c +1;
                            fechas.replace(u.getFecha(), newC);
                        }else{
                            fechas.put(u.getFecha(), 1);
                        }
                    }
                }
                String [] namesF = new String[fechas.size()];
                int[] vF = new int[fechas.size()];
                int ind = 0;
                for(String k : fechas.keySet()){
                    namesF[ind] = k;
                    Integer a = fechas.get(k);
                    assert a != null;
                    vF[ind] = a;
                    max = Math.max(max, a);
                    ind++;
                }

                setAtributos(namesF, vF);
                cantidad(names.length);
                for(int iN = 0; iN < names.length; ++iN) {
                    DibujarBarra(vals[iN], names[iN]);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }


    @Override
    protected void onDraw(Canvas canvas) {
        //Esto solo es para TEST
        DibujarBase(canvas);
        drawBars();

    }

    public void DibujarBase(Canvas canvas) {
        cv = canvas;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        ancho = canvas.getWidth();
        alto = canvas.getHeight();
        canvas.drawRect(10, 10, ancho - 10, alto- 10, paint);
        int lineB = alto - 50;
        paint.setStrokeWidth(10);
        canvas.drawLine(25, lineB,ancho-25, lineB, paint);
        //canvas.drawLine(40, lineB+25,40, (alto/4)- 30, paint);

    }

    public void DibujarBarra(int peso, String str) {

        Paint paint = new Paint();

        paint.setStrokeWidth(60);
        paint.setColor(Color.BLUE);
        int lineB = alto - 50;
        int maxD = alto/5*4;
        int grosorUnit = maxD/max;
        cv.drawLine(temp1, lineB,temp1, lineB-(peso*grosorUnit), paint);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20 - cant);
        paint.setFakeBoldText(true);
        cv.drawText(str, temp1, lineB+30, paint);
        paint.setTextSize(30);
        cv.drawText(peso+"", temp1, lineB-(peso*grosorUnit)-10, paint);

        temp1 += temp2;

    }

    public void cantidad(int x) {
        temp1 = ancho / (x + 1) + 30;
        temp2 = ancho / (x + 1);
        cant = x;

    }


}

