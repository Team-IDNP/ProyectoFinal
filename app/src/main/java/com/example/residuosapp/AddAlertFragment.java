package com.example.residuosapp;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class AddAlertFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    GoogleMap mapAdd;

    LatLng positionAlert;



    public AddAlertFragment() {
        // Required empty public constructor
    }

    public static AddAlertFragment newInstance(String param1, String param2) {
        AddAlertFragment fragment = new AddAlertFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addalert,
                container, false);
        Button button = view.findViewById(R.id.btn_report);
        button.setOnClickListener(this::sendEmail);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapViewAddAlert);
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title("("+latLng.latitude+", "+latLng.longitude+")");
                        googleMap.clear();
                        googleMap.addMarker(markerOptions);
                        positionAlert = latLng;
                    }
                });
                mapAdd = googleMap;
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView toolbarT = getActivity().findViewById(R.id.toolbar_title);
        toolbarT.setText("Nueva alerta");
    }

    public void sendEmail(View v){
        //CREDENCIALES CORREO
        String appEmail = "";
        String appPassword = "";
        String to = "";
        String subject = "Prueba";
        //AQUI SE RECOGEN LOS DATOS

        //END
        String message = "<!DOCTYPE html><html lang=\"en\" xmlns=\"https://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\"><head> <meta charset=\"utf-8\"> <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"> <meta name=\"x-apple-disable-message-reformatting\"> <title></title> <!--[if mso]> <style> table {border-collapse:collapse;border-spacing:0;border:none;margin:0;} div, td {padding:0;} div {margin:0 !important;} </style> <noscript> <xml> <o:OfficeDocumentSettings> <o:PixelsPerInch>96</o:PixelsPerInch> </o:OfficeDocumentSettings> </xml> </noscript> <![endif]--> <style> table, td, div, h1, p { font-family: Arial, sans-serif; } </style></head><body style=\"margin:0;padding:0;word-spacing:normal;background-color:#939297;\"> <div role=\"article\" aria-roledescription=\"email\" lang=\"en\" style=\"text-size-adjust:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;background-color:#939297;\"><h2 color=\"red\">¡Alerta!<h2> <p>Buenos dias señores de la municipalidad <p><br><p>[AQUI DATA DE ALERTA]<p></div></body></html>";
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

        private String from, to, subject, message;
        private ProgressDialog progressDialog;
        private Session session;

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

}