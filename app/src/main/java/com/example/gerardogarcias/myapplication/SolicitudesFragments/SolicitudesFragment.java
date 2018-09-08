package com.example.gerardogarcias.myapplication.SolicitudesFragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gerardogarcias.myapplication.LoginActivity;
import com.example.gerardogarcias.myapplication.MainMenuActivity;
import com.example.gerardogarcias.myapplication.Model.Reporte;
import com.example.gerardogarcias.myapplication.Model.User;
import com.example.gerardogarcias.myapplication.R;
import com.example.gerardogarcias.myapplication.Retrofit.VigiaAPI;
import com.example.gerardogarcias.myapplication.Util.Common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class SolicitudesFragment extends Fragment {

    String URL_POST = "https://vigia-back.herokuapp.com/reportes";

    private String drawerTitle;
    LinearLayout parent;
    TextView mainMenuText;
    CardView mainMenuButtons;
    RequestQueue requestQueue;
    Handler handle;
    Runnable run;
    String codigo;
    LinearLayout.LayoutParams params;
    int valueDP_Width, Value_In_Pixel_Width;
    int valueDP_Height,Value_In_Pixel_Height;
    int valueDP_Radius,Value_In_Pixel_Radius;
    int valueDP_Elevation,Value_In_Pixel_Elevation;

    //localización
    private FusedLocationProviderClient client;
    Geocoder geocoder;
    List<Address> addresses;
    Address lastLocation;
    VigiaAPI mService = Common.getApi();
    String colony, street, postalCode, number, date, hour, userName, userLastname, userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.menus_fragment, container, false);

        drawerTitle = getResources().getString(R.string.solicitudes_item);
        requestQueue =  Volley.newRequestQueue(getActivity().getApplicationContext());
        parent=view.findViewById(R.id.parentLayout);
        handle = new Handler();
        jsonParse();

        return view;


    }
    private void jsonParse(){
        //URL de la api del primer menu
        String url="https://vigia-back.herokuapp.com/requests/SA/events";

        JsonArrayRequest request =new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {



                            //crear los botones con la info de la api
                            for(int i =0; i < response.length(); i++){

                                final JSONObject requests = response.getJSONObject(i);

                                String name = requests.getString("name" );

                                //obtener el ancho y el alto de las pantallas
                                DisplayMetrics displayMetrics = getActivity().getApplicationContext().getResources().getDisplayMetrics();

                                float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
                                float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

                                //definir botones y texto
                                mainMenuButtons = new CardView(getActivity().getApplicationContext());
                                mainMenuText = new TextView(getActivity().getApplicationContext());

                                //obtener el codigo del tipo de solicitud
                                mainMenuButtons.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try {

                                            codigo = requests.getString("code");
                                            goToSolicitudesFragments(codigo);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                });

                                if (dpWidth >= 360   && dpWidth < 600 ){

                                    //cambiando de valores de dp a px para el Width de los botones
                                    valueDP_Width = 340;//value in dp
                                    Value_In_Pixel_Width = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, valueDP_Width, getResources().getDisplayMetrics());

                                    //cambiando de valores de dp a px para el Height de los botones
                                    valueDP_Height = 60;//value in dp
                                    Value_In_Pixel_Height = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, valueDP_Height, getResources().getDisplayMetrics());

                                    //cambio de valores de dp a px para radius
                                    valueDP_Radius = 25;//value in dp
                                    Value_In_Pixel_Radius = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, valueDP_Radius, getResources().getDisplayMetrics());

                                    //cambio de valores de dp a px para CardElevation
                                    valueDP_Elevation = 10;//value in dp
                                    Value_In_Pixel_Elevation = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, valueDP_Elevation, getResources().getDisplayMetrics());

                                    params = new LinearLayout.LayoutParams(Value_In_Pixel_Width, Value_In_Pixel_Height);
                                    params.setMargins(0, 0, 0, 210);

                                    //tamaño de letra
                                    mainMenuText.setTextSize(18);
                                    //Toast.makeText(getActivity().getApplicationContext(), "nexus 5 " , Toast.LENGTH_SHORT).show();
                                }

                                //nexus 10
                                else if (dpWidth >= 600) {
                                    //cambiando de valores de dp a px para el Width de los botones
                                    valueDP_Width = 510;//value in dp
                                    Value_In_Pixel_Width = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, valueDP_Width, getResources().getDisplayMetrics());

                                    //cambiando de valores de dp a px para el Height de los botones
                                    valueDP_Height = 80;//value in dp
                                    Value_In_Pixel_Height = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, valueDP_Height, getResources().getDisplayMetrics());

                                    //cambio de valores de dp a px para radius
                                    valueDP_Radius = 40;//value in dp
                                    Value_In_Pixel_Radius = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, valueDP_Radius, getResources().getDisplayMetrics());

                                    //cambio de valores de dp a px para CardElevation
                                    valueDP_Elevation = 10;//value in dp
                                    Value_In_Pixel_Elevation = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, valueDP_Elevation, getResources().getDisplayMetrics());

                                    params = new LinearLayout.LayoutParams(Value_In_Pixel_Width, Value_In_Pixel_Height);
                                    params.setMargins(0, 30, 0, 170);

                                    //tamaño de letra
                                    mainMenuText.setTextSize(28);

                                    //Toast.makeText(getActivity().getApplicationContext(), "nexus 10 " , Toast.LENGTH_SHORT).show();

                                }
                                //nexus s
                                else {

                                    //cambiando de valores de dp a px para el Width de los botones
                                    valueDP_Width = 260;//value in dp
                                    Value_In_Pixel_Width = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, valueDP_Width, getResources().getDisplayMetrics());

                                    //cambiando de valores de dp a px para el Height de los botones
                                    valueDP_Height = 50;//value in dp
                                    Value_In_Pixel_Height = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, valueDP_Height, getResources().getDisplayMetrics());

                                    //cambio de valores de dp a px para radius
                                    valueDP_Radius = 25;//value in dp
                                    Value_In_Pixel_Radius = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, valueDP_Radius, getResources().getDisplayMetrics());

                                    //cambio de valores de dp a px para CardElevation
                                    valueDP_Elevation = 10;//value in dp
                                    Value_In_Pixel_Elevation = (int) TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, valueDP_Elevation, getResources().getDisplayMetrics());

                                    params = new LinearLayout.LayoutParams(Value_In_Pixel_Width, Value_In_Pixel_Height);
                                    params.setMargins(0, 0, 0, 85);

                                    //tamaño de letra
                                    mainMenuText.setTextSize(14);
                                }



                                params.gravity = Gravity.CENTER;

                                //caracteristicas de los botones
                                mainMenuButtons.setLayoutParams(params);
                                mainMenuButtons.setRadius(Value_In_Pixel_Radius);
                                mainMenuButtons.setCardBackgroundColor(Color.parseColor("#00A5E3"));
                                mainMenuButtons.setCardElevation(Value_In_Pixel_Elevation);




                                //caracteristicas del texto

                                mainMenuText.setText(name);
                                mainMenuText.setGravity(Gravity.CENTER);
                                mainMenuText.setTextColor(Color.parseColor("#FFFFFF"));

                                //agregar el texto a los botones
                                mainMenuButtons.addView(mainMenuText);

                                //agregar los botones al layout
                                parent.addView(mainMenuButtons);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        error.printStackTrace();
                    }
                }
        );
        requestQueue.add(request);

    }
    private void  goToSolicitudesFragments(String codigo){
        Toast.makeText(getActivity().getApplicationContext(), codigo, Toast.LENGTH_SHORT).show();
        if(codigo.equals("BP")){
            setFragment(0);
        }
        if(codigo.equals("AV")){
            setFragment(1);
        }
        if (codigo.equals("ORG")){
            setFragment(2);
        }

    }

    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 0:
                findLocation();
                setUser();
                startPanicButton();
                break;
            case 1:
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                ApoyoVialFragment apoyoVialFragment = new ApoyoVialFragment();
                fragmentTransaction.replace(R.id.fragment, apoyoVialFragment);
                fragmentTransaction.commit();
                break;
            case 2:
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                OrganismosFragment organismosFragment = new OrganismosFragment();
                fragmentTransaction.replace(R.id.fragment, organismosFragment);
                fragmentTransaction.commit();
                break;

        }
    }

    public void findLocation(){
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

        geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        }
        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d("ResponseLocation", location.toString());
                if(location != null){
                    double longitud = location.getLongitude();
                    double latitud = location.getLatitude();

                    try {
                        addresses = geocoder.getFromLocation(latitud, longitud, 1);

                        lastLocation = addresses.get(addresses.size() -1);
                        Log.d("ResponseLastL", lastLocation.toString());

                        street = lastLocation.getThoroughfare();
                        postalCode = lastLocation.getPostalCode();
                        colony = lastLocation.getSubLocality();
                        number = lastLocation.getFeatureName();


                        DateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
                        date = currentDate.format(Calendar.getInstance().getTime());

                        //instanciar variable de hora hora/minuto/segundo
                        DateFormat currentHour = new SimpleDateFormat("HH:mm:ss");
                        hour = currentHour.format(Calendar.getInstance().getTime());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setUser(){
        if(Common.currentUser != null){
            userID = "0";
            userName = Common.currentUser.getName();
            userLastname = Common.currentUser.getLastname();

        }else{
            userID = "0";
            userName = "";
            userLastname = "";
        }

        Log.d("RESPNAM", userName);
        Log.d("RESPID", userID);
        Log.d("RESPLAST", userLastname);
    }

    public void startPanicButton(){
        mService.reportes(String.valueOf(date),
                String.valueOf(hour),
                "Solicitud de pánico activado1",
                userID,
                "50",
                street,
                colony,
                postalCode,
                number,
                userName,
                userLastname)
                .enqueue(new Callback<Reporte>() {
                    @Override
                    public void onResponse(Call<Reporte> call, retrofit2.Response<Reporte> response) {
                        if (response.isSuccessful()){
                            String msg = response.body().getFolio();
                            RegistroExitoso(msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<Reporte> call, Throwable t) {
                        Log.d("RESPERR", t.getMessage().toString());
                        Toast.makeText(getContext(), "Imposible enviar solicitud", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION},1);
    }

    //AlertDialog registro exitoso
    private void RegistroExitoso(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = this.getLayoutInflater();
        View RegistroExitoso_layout = inflater.inflate(R.layout.dialog_registro_exitoso, null);

        TextView registro_exitoso = (TextView)RegistroExitoso_layout.findViewById(R.id.TextViewRegistroExitoso);

        registro_exitoso.setText("Solicitud de pánico enviada\nFolio: "+message);
        Button btn_ok = (Button)RegistroExitoso_layout.findViewById(R.id.btn_ok);
        builder.setView(RegistroExitoso_layout);
        final AlertDialog dialog = builder.create();

        // evento del botón ok
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MainMenuActivity.class);
                startActivity(intent);
                dialog.dismiss();

            }
        });
        dialog.show();
    }
}