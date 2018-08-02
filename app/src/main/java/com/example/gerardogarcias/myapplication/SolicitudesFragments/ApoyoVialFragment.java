package com.example.gerardogarcias.myapplication.SolicitudesFragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.example.gerardogarcias.myapplication.MainMenuActivity;
import com.example.gerardogarcias.myapplication.R;
import com.example.gerardogarcias.myapplication.Util.Common;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.view.Gravity.CENTER;


public class ApoyoVialFragment extends Fragment {

    private String drawerTitle;
    ArrayAdapter<String> spinnerArrayAdapter;
    MaterialBetterSpinner materialDesignSpinner;
    RequestQueue requestQueue;
    ArrayList<String> spinnerArray;
    EditText edReporte,edNombre,edApellido, edColonia, edCalle, edCp, edInvolucrados;
    TextView texElementosExtras;
    String name, date, hour,nameSelected, idS;
    DateFormat currentDate, currentHour;
    Random r;
    int folio;
    CardView RegistroButton, CancelarButton;

    //URL para los datos del spiner
    String url="http://10.0.2.2:3000/requests/1/events/8/situations";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.registros_fragment, container, false);

        drawerTitle = getResources().getString(R.string.solicitudes_item);
        requestQueue =  Volley.newRequestQueue(getActivity().getApplicationContext());
        materialDesignSpinner = view.findViewById(R.id.android_material_design_spinner);
        RegistroButton = view.findViewById(R.id.cardViewRegistrar);
        CancelarButton = view.findViewById(R.id.cardViewCancelar);
        edReporte = view.findViewById(R.id.EditTextReporte);
        edNombre = view.findViewById(R.id.EditTextNombre);
        edApellido = view.findViewById(R.id.EditTextApellido);
        edColonia = view.findViewById(R.id.EditTextColonia);
        edCalle = view.findViewById(R.id.EditTextCalle);
        edCp = view.findViewById(R.id.EditTextCP);
        edInvolucrados = view.findViewById(R.id.EditTextInvolucrados);
        texElementosExtras = view.findViewById(R.id.TextViewElementosExtras);

        // informacion de contacto por default
        if(Common.currentUser != null){
            edNombre.setText(Common.currentUser.getName());
            edApellido.setText(Common.currentUser.getLastname());

        }else{
            edNombre.setText("");
            edApellido.setText("");

        }

        jsonParse();
        jsonID();
        Registro();
        Cancelar();
        AgregarElementosExtras();

        return view;


    }
    private void jsonParse(){



        JsonArrayRequest request =new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // crear spinner Array
                            spinnerArray = new ArrayList<String>();
                            materialDesignSpinner.setTextColor(Color.parseColor("#FFFFFF"));
                            materialDesignSpinner.setHintTextColor(Color.parseColor("#FFFFFF"));
                            materialDesignSpinner.setHint("Seleccione tipo de solicitud *");


                            //llenar spiner con info de api
                            for(int i =0; i < response.length(); i++){

                                JSONObject requests = response.getJSONObject(i);
                                name = requests.getString("name" );
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

    //crear registro seleccionando el boton
    private void Registro() {

        RegistroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(idS == null){
                    materialDesignSpinner.setErrorColor(Color.parseColor("#FE2E2E"));
                    materialDesignSpinner.setError("seleccione un tipo de solicitud");
                }
                else if (TextUtils.isEmpty(edColonia.getText())){
                    edColonia.setError("este campo es obligatorio");
                }
                else if (TextUtils.isEmpty(edCalle.getText())){
                    edCalle.setError("este campo es obligatorio");

                }

                else
                {
                    //instanciar variable de fecha año/mes/dia
                    currentDate = new SimpleDateFormat("yyyy-MM-dd");
                    date = currentDate.format(Calendar.getInstance().getTime());

                    //instanciar variable de hora hora/minuto/segundo
                    currentHour = new SimpleDateFormat("HH:mm:ss");
                    hour = currentHour.format(Calendar.getInstance().getTime());
                    //conseguir situation_id
                    jsonID();
                    //asignar un numero random al folio
                    r = new Random();
                    folio = r.nextInt(10000 - 1) + 1;
                    VolleyPost();
                    Intent intent = new Intent(getActivity().getApplicationContext(), MainMenuActivity.class);
                    Toast.makeText(getActivity().getApplicationContext(), "Tu registro se ha creado exitosamente numero de folio: " + folio , Toast.LENGTH_LONG).show();
                    startActivity(intent);

                }



            }
        });
    }

    //conseguir el id de la situacion seleccionada
    private void jsonID(){

        final JsonArrayRequest request =new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(final JSONArray response) {
                        for(int i =0; i < response.length(); i++){
                            materialDesignSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try {
                                        final JSONObject requests = response.getJSONObject(position);
                                        idS = requests.getString("id");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
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

    //conseguir el nombre de la situacion seleccionada
    public void spinnerSelected(){
        materialDesignSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nameSelected = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(),nameSelected, Toast.LENGTH_SHORT).show();
                //Toast.makeText(parent.getContext(),idS, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //hacer el POST de la solicitud
    public void VolleyPost(){

        String urlPost = "http://10.0.2.2:3000/reportes";
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPost,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<String, String>();
                String reporte = edReporte.getText().toString();
                String nombre = edNombre.getText().toString();
                String apellido = edApellido.getText().toString();
                String colonia = edColonia.getText().toString();
                String calle = edCalle.getText().toString();
                String cp = edCp.getText().toString();
                String involucrados = edInvolucrados.getText().toString();

                params.put("requester_name", nombre);
                params.put("requester_lastname", apellido);
                params.put("colony", colonia);
                params.put("street", calle);
                params.put("zip_code", cp);
                params.put("involucrados", involucrados);
                params.put("date", String.valueOf(date));
                params.put("hour", String.valueOf(hour));
                params.put("description", reporte);
                params.put("folio", String.valueOf(folio));
                params.put("place","volcan 107");
                params.put("situation_id",idS);
                params.put("active","true");
                return params;
            }
        };
        requestQueue.add(postRequest);

    }
    //set fragment para regresar a menu anterior
    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 0:
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                SolicitudesFragment solicitudesFragment = new SolicitudesFragment();
                fragmentTransaction.replace(R.id.fragment, solicitudesFragment);
                fragmentTransaction.commit();
                break;

        }
    }
    //boton cancelar
    private void  Cancelar(){
        CancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(0);
            }
        });
    }
    public void AgregarElementosExtras() {
        texElementosExtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "elige los elementos que deceas agregar " , Toast.LENGTH_LONG).show();
            }
        });
    }

}



