package com.example.gerardogarcias.myapplication.ReportesFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.gerardogarcias.myapplication.MainMenuActivity;
import com.example.gerardogarcias.myapplication.R;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DeficienciaFragment extends Fragment {

    private String drawerTitle;
    ArrayAdapter<String> spinnerArrayAdapter;
    MaterialBetterSpinner materialDesignSpinner;
    RequestQueue requestQueue;
    ArrayList<String> spinnerArray;
    CardView Registrobutton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.registros_fragment, container, false);

        drawerTitle = getResources().getString(R.string.solicitudes_item);
        requestQueue =  Volley.newRequestQueue(getActivity().getApplicationContext());
        materialDesignSpinner = view.findViewById(R.id.android_material_design_spinner);
        Registrobutton = view.findViewById(R.id.cardViewRegistrar);
        jsonParse();
        goToMainI();

        return view;

    }
    private void jsonParse(){
        //URL de la api del primer menu
        String url="http://10.0.2.2:3000/requests/2/events/5/situations";


        JsonArrayRequest request =new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // crear spinner Array
                            spinnerArray = new ArrayList<String>();
                            materialDesignSpinner.setTextColor(Color.parseColor("#FFFFFF"));
                            materialDesignSpinner.setHintTextColor(Color.parseColor("#FFFFFF"));
                            materialDesignSpinner.setHint("Seleccione tipo de reporte:");

                            //llenar spiner con info de api
                            for(int i =0; i < response.length(); i++){

                                JSONObject requests = response.getJSONObject(i);
                                String name = requests.getString("name" );
                                spinnerArray.add(name);

                            }
                            spinnerArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                    android.R.layout.simple_dropdown_item_1line, spinnerArray)
                            {
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View v = super.getView(position, convertView, parent);
                                    v.setBackgroundResource(R.drawable.gradient);
                                    ((TextView) v).setTextSize(18);
                                    ((TextView) v).setTextColor(Color.parseColor("#FFFFFF"));
                                    return v;
                                }

                            };

                            materialDesignSpinner.setAdapter(spinnerArrayAdapter);

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
    private void  goToMainI(){

        Registrobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(),MainMenuActivity.class);
                Toast.makeText(getActivity().getApplicationContext(), "Tu registro se ha creado exitosamente numero de folio: 1234 " , Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });
    }

}