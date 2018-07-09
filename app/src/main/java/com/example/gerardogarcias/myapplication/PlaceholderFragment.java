package com.example.gerardogarcias.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import android.support.v7.widget.CardView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Fragmento para el contenido principal
 */
public class PlaceholderFragment extends Fragment {

    TextView mainMenuText;
    CardView mainMenuButtons;
    RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.section_fragment, container, false);



        mainMenuText = view.findViewById(R.id.mainMenuText);
        mainMenuButtons = view.findViewById(R.id.mainMenuButtons);
        requestQueue =  Volley.newRequestQueue(getActivity().getApplicationContext());


        jsonParese();










        return view;
    }

    private void jsonParese(){

        String url="http://10.0.2.2:3000/requests";



        JsonArrayRequest request =new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {



                            for(int i =0; i < response.length(); i++){
                                JSONObject requests = response.getJSONObject(i);

                                String name = requests.getString("name" );

                                mainMenuText.append(name + "\n");
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
}