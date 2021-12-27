package com.example.residuosapp.controller.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
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
import com.example.residuosapp.controller.MainActivity;
import com.example.residuosapp.model.Alert;
import com.example.residuosapp.model.Departamento;
import com.example.residuosapp.model.Distrito;
import com.example.residuosapp.model.Provincia;
import com.example.residuosapp.model.Usuario;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    String mapImageUrl;
    String filePath;
    String MYfilePath;
    String myphotoUrl;
    View viewG;
    Distrito forEmail;

    StorageReference sr;

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

        viewG = view;
        sr = FirebaseStorage.getInstance().getReference();

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
                if(MYfilePath == null){
                    Toast.makeText(getContext(), "No se estableció una foto", Toast.LENGTH_LONG).show();
                    return;
                }
                if(positionAlert == null){
                    Toast.makeText(getContext(), "No se estableció un marcador en el mapa", Toast.LENGTH_LONG).show();
                    return;
                }
                SaveAsyncTask task = new SaveAsyncTask();
                task.execute();
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
            mapAdd.animateCamera(CameraUpdateFactory.zoomTo(15));

            LocationManager lm = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            98);
                }
            }
            Location location = lm.getLastKnownLocation(lm.getBestProvider(criteria, false));
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.e("ubi ", latitude+" "+longitude);
            LatLng sydney = new LatLng(latitude, longitude);
            mapAdd.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mapAdd.animateCamera(CameraUpdateFactory.zoomTo(12));
            mapAdd.getUiSettings().setZoomControlsEnabled(true);
            mapAdd.setMyLocationEnabled(true);
            mapAdd.getUiSettings().setMyLocationButtonEnabled(true);
            //mapAdd.getUiSettings().setMapToolbarEnabled(true);
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
                //SE movio a provincia para hacer más rápida la app
                /**optionsDep = new String[listDept.size()];
                for (int i = 0;i<listDept.size();++i){
                    optionsDep[i] = listDept.get(i).getName();
                }
                ArrayAdapter<String> optionsDepAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, optionsDep);
                spDep.setAdapter(optionsDepAdapter);*/
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

                optionsDep = new String[listDept.size()];
                for (int i = 0;i<listDept.size();++i){
                    optionsDep[i] = listDept.get(i).getName();
                }
                ArrayAdapter<String> optionsDepAdapter = new ArrayAdapter<>(getContext(),
                        R.layout.support_simple_spinner_dropdown_item, optionsDep);
                spDep.setAdapter(optionsDepAdapter);

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
                forEmail = listDist.get(i);
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
                if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                }



                String nameImage="";
                File fileImage = new File(getFilePath());
                nameImage = FirebaseAuth.getInstance().getCurrentUser().getUid()+(System.currentTimeMillis()/100)+".png";
                path = fileImage + File.separator+nameImage;
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
                    MediaScannerConnection.scanFile(this.getActivity(), new String[]{path},
                            null, new MediaScannerConnection.MediaScannerConnectionClient() {

                        @Override
                        public void onScanCompleted(String s, Uri uri) {}

                        @Override
                        public void onMediaScannerConnected() {}
                    });
                    MYfilePath = path;
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imgFoto.setImageBitmap(bitmap);
                    break;

                case COD_SELECCIONA:
                    Uri miPath = data.getData();
                    MYfilePath = miPath.getPath();
                    imgFoto.setImageURI(miPath);
                    break;
            }
        }
    }

    public void sendEmail(View v) {
        //CREDENCIALES CORREO
        String appEmail = "alertaresiduos2021@gmail.com";
        String appPassword = "alertaRResiduos123";
        String to = forEmail.getEmailDist(); //correo de municipalidad

        String subject = "Prueba";
        //AQUI SE RECOGEN LOS DATOS

        //END


        String message = "<!DOCTYPE html><html lang=\"en\" xmlns=\"https://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\"><head> <meta charset=\"utf-8\"> <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"> <meta name=\"x-apple-disable-message-reformatting\"> <title></title> <!--[if mso]> <style> table {border-collapse:collapse;border-spacing:0;border:none;margin:0;} div, td {padding:0;} div {margin:0 !important;} </style> <noscript> <xml> <o:OfficeDocumentSettings> <o:PixelsPerInch>96</o:PixelsPerInch> </o:OfficeDocumentSettings> </xml> </noscript> <![endif]--> <style> table, td, div, h1, p { font-family: Arial, sans-serif; } </style></head><body style=\"margin:0;padding:0;word-spacing:normal;background-color:#939297;\"> <div role=\"article\" aria-roledescription=\"email\" lang=\"en\" style=\"text-size-adjust:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;background-color:#939297;\"><h2 color=\"red\">¡Alerta!<h2> "+
                "<p>Buenos dias señores de la municipalidad <p><br><p>"+
                "<br>"+
                "<p>Se reporta un alerta por desperdicios en: <p><br><p>"+
                "Departamento:"+deptTName+"<br>"+
                "Provincia:"+provTName+"<br>"+
                "Distrito:"+distTName+"<br>"+
                "<img src=\""+mapImageUrl+"\" width=\"500\" height=\"600\"/>"+
                "<img src=\""+myphotoUrl+"\" width=\"500\" height=\"600\"/>"+
                "<p></div></body></html>";
        //Log.e("MENSAJE HTML", message);

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
            createAlert(myphotoUrl);
            mapImageUrl = null;
            myphotoUrl = null;
            imgFoto.setImageBitmap(null);
        }
    }

    void createAlert(String myphotoUrl){
        String dateT;

        Alert a = new Alert();
        a.setId("A-"+generateId());
        a.setDistritoId(distT);

        a.setMultimediaSend(myphotoUrl);

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
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        String idUser = u.getEmail();
        a.setUsuarioId(idUser);
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

    public void captureScreen() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override public void onSnapshotReady(Bitmap snapshot) {

                saveImage(snapshot);
                try {
                    Uri uri = Uri.fromFile(new File(filePath));
                    //subida de archivop
                    StorageReference file = sr.child("fotos").child(uri.getLastPathSegment());
                    file.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri downloadUrl)
                                {
                                    mapImageUrl = downloadUrl.toString();
                                }
                            });

                        }
                    });

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.d("ImageCapture", "IOException");
                    Log.d("ImageCapture", e.getMessage());
                }

            }
        };
        mapAdd.snapshot(callback);
    }





    public void upPhoto() {
        Uri uri = Uri.fromFile(new File(MYfilePath));
        //subida de archivop
        StorageReference file = sr.child("fotos").child(uri.getLastPathSegment());
        file.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                {
                    @Override
                    public void onSuccess(Uri downloadUrl)
                    {
                        myphotoUrl = downloadUrl.toString();
                        MYfilePath = null;
                    }
                });

            }
        });
    }




    private void saveImage(Bitmap finalBitmap) {

        String root = getFilePath();

        File myDir = new File(root);
        myDir.mkdirs();
        String fname = FirebaseAuth.getInstance().getCurrentUser().getUid()+System.currentTimeMillis() + ".jpeg";
        filePath = fname;
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        filePath = root + File.separator+fname;
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.i("LOAD", "Guardado");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFilePath(){
        ContextWrapper cw = new ContextWrapper(requireContext());
        File pD = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return pD.getPath();
    }


    private class SaveAsyncTask extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;

        public SaveAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getContext(), "", "Generando reporte...", true);
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "Reporte generado";
            captureScreen();
            upPhoto();
            while(mapImageUrl == null || myphotoUrl == null){

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
            sendEmail(viewG);
        }
    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("resultPermiso", "Entra");
        if (requestCode == 99) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                P2 = false;
            } else {
                Toast.makeText(requireActivity(), "PERMISSION NOT GRANTED", Toast.LENGTH_LONG).show();
                requireActivity().finish(); System.exit(0);
            }
        }
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                P1 = false;
            } else {
                Toast.makeText(requireActivity(), "PERMISSION FOR CAMERA NOT GRANTED", Toast.LENGTH_LONG).show();
                requireActivity().finish(); System.exit(0);
            }
        }
    }*/
}