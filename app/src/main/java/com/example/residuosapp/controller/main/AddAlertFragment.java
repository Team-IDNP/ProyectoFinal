package com.example.residuosapp.controller.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.residuosapp.R;
import com.example.residuosapp.model.Alert;
import com.example.residuosapp.model.Departamento;
import com.example.residuosapp.model.Distrito;
import com.example.residuosapp.model.Provincia;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class AddAlertFragment extends Fragment {

    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;

    GoogleMap mapAdd;

    LatLng positionAlert;

    ImageButton buttonUp;
    ImageView imgFoto;
    FirebaseDatabase db;
    String path;
    ArrayList<Departamento> listDept;
    ArrayList<Provincia> listProv;
    ArrayList<Distrito> listDist;
    Map<String, ArrayList<Provincia>> deptProvMap;
    Map<String, ArrayList<Distrito>> provDistMap;
    String[] optionsDep, optionsProv, optionsDist;
    String deptT, provT, distT;
    String deptTName, provTName, distTName;
    String idCount;
    String imagen64encode;

    Spinner spDep, spProv, spDist;

    public AddAlertFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addalert,
                container, false);
        //Carga Imagen ----------------

        buttonUp = view.findViewById(R.id.imageButtonUp);
        imgFoto = view.findViewById(R.id.imgEvidence1);
        buttonUp.setOnClickListener(this::upImage);
        db = FirebaseDatabase.getInstance();
        listDept = new ArrayList<>();
        listProv = new ArrayList<>();
        listDist = new ArrayList<>();
        spDep = view.findViewById(R.id.Departamento);
        spProv = view.findViewById(R.id.Provincia);
        spDist = view.findViewById(R.id.Distrito);

        ///---------------------------


        //spDep.

        Button button = view.findViewById(R.id.btn_report);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendEmail(view);
                createAlert();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapViewAddAlert);

        assert mapFragment != null;
        mapFragment.getMapAsync(googleMap -> {
            googleMap.setOnMapClickListener(latLng -> {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("(" + latLng.latitude + ", " + latLng.longitude + ")");
                googleMap.clear();
                googleMap.addMarker(markerOptions);
                positionAlert = latLng;

            });
            mapAdd = googleMap;
        });

        //Departamento
        db.getReference("departamento").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listDept.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Departamento s = ds.getValue(Departamento.class);
                    s.setId(ds.getKey());

                    listDept.add(s);
                }
                Collections.sort(listDept, Departamento.NameAZComparator);
                optionsDep = new String[listDept.size()];
                for (int i = 0;i<listDept.size();++i){
                    optionsDep[i] = listDept.get(i).getName();
                }
                ArrayAdapter<String> optionsDepAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, optionsDep);
                spDep.setAdapter(optionsDepAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });

        spDep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                deptT = listDept.get(i).getId();
                deptTName = listDept.get(i).getName();
                listProv = deptProvMap.get(deptT);
                if (listProv != null) {
                    Collections.sort(listProv, Provincia.NameAZComparator);
                    optionsProv = new String[listProv.size()];
                    for (int iL = 0;iL<listProv.size();++iL){
                        optionsProv[iL] = listProv.get(iL).getName();
                    }
                    ArrayAdapter<String> optionsProvAdapter = new ArrayAdapter<>(getContext(),
                            R.layout.support_simple_spinner_dropdown_item, optionsProv);
                    spProv.setAdapter(optionsProvAdapter);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        //Provincia
        db.getReference("provincia").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProv.clear();
                int cant = (int) snapshot.getChildrenCount();
                deptProvMap = new HashMap<>();
                deptProvMap.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Provincia s = ds.getValue(Provincia.class);
                    assert s != null;
                    s.setId(ds.getKey());
                    if(deptProvMap.containsKey(s.getDepartamentoId())){
                        deptProvMap.get(s.getDepartamentoId()).add(s);
                    }else{
                        ArrayList<Provincia> tmp = new ArrayList<>();
                        tmp.add(s);
                        deptProvMap.put(s.getDepartamentoId(), tmp);
                    }
                }
                listProv = deptProvMap.get(deptT);
                if (listProv != null) {
                    Collections.sort(listProv, Provincia.NameAZComparator);
                    optionsProv = new String[listProv.size()];
                    for (int i = 0;i<listProv.size();++i){
                        optionsProv[i] = listProv.get(i).getName();
                    }
                    ArrayAdapter<String> optionsProvAdapter = new ArrayAdapter<>(getContext(),
                            R.layout.support_simple_spinner_dropdown_item, optionsProv);
                    spProv.setAdapter(optionsProvAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });

        spProv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                provT = listProv.get(i).getId();
                provTName = listProv.get(i).getName();
                listDist = provDistMap.get(provT);
                if (listDist != null) {
                    Collections.sort(listDist, Distrito.NameAZComparator);
                    optionsDist = new String[listDist.size()];
                    for (int iL = 0;iL<listDist.size();++iL){
                        optionsDist[iL] = listDist.get(iL).getName();
                    }
                    ArrayAdapter<String> optionsProvAdapter = new ArrayAdapter<>(getContext(),
                            R.layout.support_simple_spinner_dropdown_item, optionsDist);
                    spDist.setAdapter(optionsProvAdapter);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        //Distrito
        db.getReference("distrito").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listDist.clear();
                int cant = (int) snapshot.getChildrenCount();
                provDistMap = new HashMap<>();
                provDistMap.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Distrito s = ds.getValue(Distrito.class);
                    assert s != null;
                    s.setId(ds.getKey());
                    if(provDistMap.containsKey(s.getProvinciaId())){
                        provDistMap.get(s.getProvinciaId()).add(s);
                    }else{
                        ArrayList<Distrito> tmp = new ArrayList<>();
                        tmp.add(s);
                        provDistMap.put(s.getProvinciaId(), tmp);
                    }
                }
                listDist = provDistMap.get(provT);
                if (listDist != null) {
                    Collections.sort(listDist, Distrito.NameAZComparator);
                    optionsDist = new String[listDist.size()];
                    for (int i = 0;i<listDist.size();++i){
                        optionsDist[i] = listDist.get(i).getName();
                    }
                    ArrayAdapter<String> optionsProvAdapter = new ArrayAdapter<>(getContext(),
                            R.layout.support_simple_spinner_dropdown_item, optionsDist);
                    spDist.setAdapter(optionsProvAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PRUEBA", ": "+error);
            }
        });

        spDist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                distT = listDist.get(i).getId();
                distTName = listDist.get(i).getName();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //id
        db.getReference("idCont").child("idAlert").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idCount = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error", error+"");
            }
        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView toolbarT = requireActivity().findViewById(R.id.toolbar_title);
        toolbarT.setText(R.string.nuevaAlerta);
    }

    @SuppressLint("IntentReset")
    public void upImage(View v) {
        final CharSequence[] opciones = {"Tomar Foto", "Elegir de Galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Elige una Opción");
        builder.setItems(opciones, (dialogInterface, i) -> {
            if (opciones[i].equals("Tomar Foto")) {
                if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }

                String nameImage="";
                File fileImage = new File(Environment.getExternalStorageDirectory(), "FotosResiduos");
                boolean e = fileImage.exists();
                if(!e){
                    e = fileImage.mkdirs();
                }

                if(e){
                    nameImage = (System.currentTimeMillis()/100)+".png";
                }
                path = Environment.getExternalStorageDirectory() + File.separator + "FotosResiduos" + File.separator + nameImage;
                File img = new File(path);
                Intent intent;
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    String auth = requireActivity().getApplicationContext().getPackageName()+".provider";
                    Uri uriIamge = FileProvider.getUriForFile(requireActivity(), auth, img);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIamge);
                }else{
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(img));
                }
                //noinspection deprecation
                startActivityForResult(intent, COD_FOTO);


            } else {
                if (opciones[i].equals("Elegir de Galeria")) {
                    Intent intent = new Intent(
                            Intent.ACTION_GET_CONTENT,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    );
                    intent.setType("image/");
                    startActivityForResult(Intent.createChooser(intent, "Seleccione"), COD_SELECCIONA);
                } else {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();


        //Toast.makeText(getContext(),"Mostrar opciones",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case COD_FOTO:
                    MediaScannerConnection.scanFile(this.getActivity(), new String[]{path}, null, new MediaScannerConnection.MediaScannerConnectionClient() {

                        @Override
                        public void onScanCompleted(String s, Uri uri) {

                        }

                        @Override
                        public void onMediaScannerConnected() {

                        }
                    });
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imgFoto.setImageBitmap(bitmap);
                    break;

                case COD_SELECCIONA:
                    Uri miPath = data.getData();
                    imgFoto.setImageURI(miPath);
                    break;
            }
        }
    }

    public void sendEmail(View v) {
        //CREDENCIALES CORREO
        String appEmail = "";
        String appPassword = "";
        String to = "";
        String subject = "Prueba";
        //AQUI SE RECOGEN LOS DATOS

        //END
        String message = "<!DOCTYPE html><html lang=\"en\" xmlns=\"https://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\"><head> <meta charset=\"utf-8\"> <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"> <meta name=\"x-apple-disable-message-reformatting\"> <title></title> <!--[if mso]> <style> table {border-collapse:collapse;border-spacing:0;border:none;margin:0;} div, td {padding:0;} div {margin:0 !important;} </style> <noscript> <xml> <o:OfficeDocumentSettings> <o:PixelsPerInch>96</o:PixelsPerInch> </o:OfficeDocumentSettings> </xml> </noscript> <![endif]--> <style> table, td, div, h1, p { font-family: Arial, sans-serif; } </style></head><body style=\"margin:0;padding:0;word-spacing:normal;background-color:#939297;\"> <div role=\"article\" aria-roledescription=\"email\" lang=\"en\" style=\"text-size-adjust:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;background-color:#939297;\"><h2 color=\"red\">¡Alerta!<h2> <p>Buenos dias señores de la municipalidad <p><br><p>"+
                "Departamento:"+deptTName+"<br>"+
                "Departamento:"+provTName+"<br>"+
                "Departamento:"+distTName+"<br>"+
                "<p></div></body></html>";
        sendEmailWithGmail(appEmail, appPassword, to, subject, message);
    }


    private void sendEmailWithGmail(final String recipientEmail, final String recipientPassword,
                                    String to, String subject, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(recipientEmail, recipientPassword);
            }
        });

        SenderAsyncTask task = new SenderAsyncTask(session, recipientEmail, to, subject, message);
        task.execute();
    }


    @SuppressLint("StaticFieldLeak")
    private class SenderAsyncTask extends AsyncTask<String, String, String> {

        private final String from;
        private final String to;
        private final String subject;
        private final String message;
        private ProgressDialog progressDialog;
        private final Session session;

        public SenderAsyncTask(Session session, String from, String to, String subject, String message) {
            this.session = session;
            this.from = from;
            this.to = to;
            this.subject = subject;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getContext(), "", "Enviando...", true);
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "Reporte enviado";
            try {
                Message mimeMessage = new MimeMessage(session);
                mimeMessage.setFrom(new InternetAddress(from));
                mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                mimeMessage.setSubject(subject);
                mimeMessage.setContent(message, "text/html; charset=utf-8");
                Transport.send(mimeMessage);
            } catch (MessagingException e) {
                e.printStackTrace();
                //return e.getMessage();
                result = "ERROR: COMPLETA LAS CREDENCIALES";
            } catch (Exception e) {
                e.printStackTrace();
                // return e.getMessage();
                result = "ERROR: REVISA LAS CREDENCIALES";
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressDialog.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
        }
    }

    void createAlert(){
        String dateT;

        Alert a = new Alert();
        a.setId("A-"+generateId());
        a.setDistritoId(distT);

        Bitmap bm=((BitmapDrawable)imgFoto.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        imagen64encode = Base64.encodeToString(b, Base64.URL_SAFE | Base64.NO_WRAP);


        a.setMultimedia(imagen64encode);

        Date date = new Date();
        Date enrollmentDate = new Date(date.getTime());
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        dateT = formatDate.format(enrollmentDate);

        a.setFecha(dateT);
        a.setEstado("Pendiente");

        if(positionAlert == null){
            Toast.makeText(getContext(), "No se estableció un marcador en el mapa", Toast.LENGTH_LONG).show();
            return;
        }

        a.setUbiLat(positionAlert.latitude+"");
        a.setUbiLong(positionAlert.longitude+"");
        a.setUsuarioId("U-2");



        db.getReference("alerts").child(a.getId()).updateChildren(a.toMap());
        Toast.makeText(getContext(), "Guardado", Toast.LENGTH_LONG).show();

    }

    public String generateId(){

        int idInt = Integer.parseInt(idCount);
        idInt++;
        String idS = idInt+"";
        Map<String, Object> idMap = new HashMap<>();
        idMap.put("idAlert", idInt+"");
        db.getReference("idCont").updateChildren(idMap);
        return idS;
    }


}