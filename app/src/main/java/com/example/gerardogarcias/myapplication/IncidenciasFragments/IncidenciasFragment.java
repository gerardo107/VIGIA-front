package com.example.gerardogarcias.myapplication.IncidenciasFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.gerardogarcias.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class IncidenciasFragment extends Fragment {

    private String drawerTitle;
    LinearLayout parent;
    TextView mainMenuText;
    CardView mainMenuButtons;
    RequestQueue requestQueue;
    int valueDP_Width, Value_In_Pixel_Width;
    int valueDP_Height,Value_In_Pixel_Height;
    int valueDP_Radius,Value_In_Pixel_Radius;
    int valueDP_Elevation,Value_In_Pixel_Elevation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.menus_fragment, container, false);

        drawerTitle = getResources().getString(R.string.solicitudes_item);
        requestQueue =  Volley.newRequestQueue(getActivity().getApplicationContext());
        parent=view.findViewById(R.id.parentLayout);

        jsonParse();

        return view;


    }
    private void jsonParse(){
        //URL de la api del primer menu
        String url="http://10.0.2.2:3000/requests/3/events";

        JsonArrayRequest request =new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {


                            for(int i =0; i < response.length(); i++){
                                final int finalI = i;

                                JSONObject requests = response.getJSONObject(i);

                                String name = requests.getString("name" );

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

                                //caracteristicas de los botones
                                mainMenuButtons = new CardView(getActivity().getApplicationContext());
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Value_In_Pixel_Width,Value_In_Pixel_Height);
                                params.setMargins(0,0,0,210);
                                params.gravity = Gravity.CENTER;
                                mainMenuButtons.setLayoutParams(params);
                                mainMenuButtons.setRadius(Value_In_Pixel_Radius);
                                mainMenuButtons.setCardBackgroundColor(Color.parseColor("#00A5E3"));
                                mainMenuButtons.setCardElevation(Value_In_Pixel_Elevation);
                                mainMenuButtons.setId(finalI);

                                //seleccionar los botones
                                mainMenuButtons.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        goToSolicitudesFragments(finalI);
                                    }
                                });

                                //caracteristicas del texto
                                mainMenuText =new TextView(getActivity().getApplicationContext());
                                mainMenuText.setText(name);
                                mainMenuText.setGravity(Gravity.CENTER);
                                mainMenuText.setTextColor(Color.parseColor("#FFFFFF"));
                                mainMenuText.setTextSize(18);


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
    private void  goToSolicitudesFragments(int id){

        if(id ==0){
            setFragment(0);
        }
        if(id ==1){
            setFragment(1);
        }
        if (id ==2){
            setFragment(2);
        }

    }
    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 0:
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                EventosProteccionFragment eventosProteccionFragment = new EventosProteccionFragment();
                fragmentTransaction.replace(R.id.fragment, eventosProteccionFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                EventosSeguridadFragment eventosSeguridadFragment = new EventosSeguridadFragment();
                fragmentTransaction.replace(R.id.fragment, eventosSeguridadFragment);
                fragmentTransaction.commit();
                break;
            case 2:
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                EventosVialesFragment eventosVialesFragment = new EventosVialesFragment();
                fragmentTransaction.replace(R.id.fragment, eventosVialesFragment);
                fragmentTransaction.commit();
                break;
        }
    }
}