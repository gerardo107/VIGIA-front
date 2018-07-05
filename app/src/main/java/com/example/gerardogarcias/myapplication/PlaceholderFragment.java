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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Fragmento para el contenido principal
 */
public class PlaceholderFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.section_fragment, container, false);

        String URL="http://10.0.2.2:3000/requests";

        RequestQueue requestQueue=  Volley.newRequestQueue(getActivity().getApplicationContext());

        JsonObjectRequest objectRequest =new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse (JSONObject response){
                        Log.e("Rest Response",response.toString());
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e("Rest Response",error.toString());
                    }
                }
        );

        requestQueue.add(objectRequest);



        return view;
    }

}