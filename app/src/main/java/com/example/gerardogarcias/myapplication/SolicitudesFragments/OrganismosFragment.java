package com.example.gerardogarcias.myapplication.SolicitudesFragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.gerardogarcias.myapplication.LoginActivity;
import com.example.gerardogarcias.myapplication.MainMenuActivity;
import com.example.gerardogarcias.myapplication.Model.User;
import com.example.gerardogarcias.myapplication.R;
import com.example.gerardogarcias.myapplication.Util.Common;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;


public class OrganismosFragment extends Fragment {

    private String drawerTitle;
    ArrayAdapter<String> spinnerArrayAdapter;
    MaterialBetterSpinner materialDesignSpinner;
    ArrayList<String> spinnerArray;
    RequestQueue requestQueue;
    EditText edReporte, edNombre, edApellido, edColonia, edCalle, edCp, edInvolucrados, edNumero;
    TextView textElementosExtras;
    String name, date, hour, nameSelected, idS;
    DateFormat currentDate, currentHour;
    Random r;
    int folio;
    CardView RegistroButton, CancelarButton;
    AlertDialog CancelRegistro;

    //URL para los datos del spiner
    String url = "https://vigia-back.herokuapp.com/requests/1/events/9/situations";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.registros_fragment, container, false);

        drawerTitle = getResources().getString(R.string.solicitudes_item);
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        materialDesignSpinner = view.findViewById(R.id.android_material_design_spinner);
        RegistroButton = view.findViewById(R.id.cardViewRegistrar);
        CancelarButton = view.findViewById(R.id.cardViewCancelar);
        edReporte = view.findViewById(R.id.EditTextReporte);
        edNombre = view.findViewById(R.id.EditTextNombre);
        edApellido = view.findViewById(R.id.EditTextApellido);
        edColonia = view.findViewById(R.id.EditTextColonia);
        edCp = view.findViewById(R.id.EditTextCP);
        edCalle = view.findViewById(R.id.EditTextCalle);
        edNumero = view.findViewById(R.id.EditTextNum);
        edInvolucrados = view.findViewById(R.id.EditTextInvolucrados);
        textElementosExtras = view.findViewById(R.id.TextViewElementosExtras);

        // informacion de contacto por default
        if (Common.currentUser != null) {
            edNombre.setText(Common.currentUser.getName());
            edApellido.setText(Common.currentUser.getLastname());

        } else {
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

    private void jsonParse() {
        //URL de la api del primer menu


        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // crear spinner Array
                            spinnerArray = new ArrayList<String>();
                            materialDesignSpinner.setTextColor(Color.parseColor("#FFFFFF"));
                            materialDesignSpinner.setHintTextColor(Color.parseColor("#FFFFFF"));
                            materialDesignSpinner.setHint("Seleccione tipo de solicitud *");

                            //llenar spiner con info de api
                            for (int i = 0; i < response.length(); i++) {
                                final JSONObject requests = response.getJSONObject(i);
                                name = requests.getString("name");
                                spinnerArray.add(name);
                            }


                            spinnerArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                    android.R.layout.simple_dropdown_item_1line, spinnerArray) {
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
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
                     ErrorCamposObligatorios();
                 }
                else if (TextUtils.isEmpty(edColonia.getText())){
                    edColonia.setError("este campo es obligatorio");
                     ErrorCamposObligatorios();
                 }
                else if (TextUtils.isEmpty(edCalle.getText())){
                    edCalle.setError("este campo es obligatorio");
                     ErrorCamposObligatorios();
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
                    RegistroExitoso();
                }
            }
        });
    }

    //conseguir el id de la situacion seleccionada
    private void jsonID() {

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
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
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        requestQueue.add(request);
    }

    //conseguir el nombre de la situacion seleccionada
    public void spinnerSelected() {
        materialDesignSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nameSelected = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), nameSelected, Toast.LENGTH_SHORT).show();
                //Toast.makeText(parent.getContext(),idS, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //hacer el POST de la solicitud
    public void VolleyPost() {

        String urlPost = "https://vigia-back.herokuapp.com/reportes";
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlPost,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String reporte = edReporte.getText().toString();
                String nombre = edNombre.getText().toString();
                String apellido = edApellido.getText().toString();
                String colonia = edColonia.getText().toString();
                String cp = edCp.getText().toString();
                String calle = edCalle.getText().toString();
                String numero = edNumero.getText().toString();
                String involucrados = edInvolucrados.getText().toString();

                params.put("requester_name", nombre);
                params.put("requester_lastname", apellido);
                params.put("colony", colonia);
                params.put("zip_code", cp);
                params.put("street", calle);
                params.put("house_number", numero);
                params.put("involucrados", involucrados);
                params.put("date", String.valueOf(date));
                params.put("hour", String.valueOf(hour));
                params.put("description", reporte);
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
    private void Cancelar() {
        CancelarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelarRegistro();
            }
        });
    }

    //metodo para crear la alerta de cancelar el registro
    private void CancelarRegistro() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = this.getLayoutInflater();
        View cancelar_layout = inflater.inflate(R.layout.dialog_cancelar, null);

        Button btn_cancelar = (Button)cancelar_layout.findViewById(R.id.btn_cancelar);
        Button btn_confirmar = (Button)cancelar_layout.findViewById(R.id.btn_confirmar);
        builder.setView(cancelar_layout);
        final AlertDialog dialog = builder.create();

        // evento del botón Cancelar
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        // evento del botón Confirmar
        btn_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                setFragment(0);

            }
        });

        dialog.show();
    }

    //AlertDialog campos obligatorios sin llenar
    private void ErrorCamposObligatorios() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = this.getLayoutInflater();
        View ErrorCampos_layout = inflater.inflate(R.layout.dialog_error_campos, null);

        TextView error_campos = (TextView)ErrorCampos_layout.findViewById(R.id.TextViewCamposSinLlenar);


        error_campos.setText("Hay campos obligatorios sin llenar ");
        Button btn_ok = (Button)ErrorCampos_layout.findViewById(R.id.btn_ok);
        builder.setView(ErrorCampos_layout);
        final AlertDialog dialog = builder.create();

        // evento del botón ok
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    //AlertDialog registro exitoso
    private void RegistroExitoso() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = this.getLayoutInflater();
        View RegistroExitoso_layout = inflater.inflate(R.layout.dialog_registro_exitoso, null);

        TextView registro_exitoso = (TextView)RegistroExitoso_layout.findViewById(R.id.TextViewRegistroExitoso);


        registro_exitoso.setText("Tu registro se ha creado exitosamente numero de folio: ");
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


    public void AgregarElementosExtras() {
textElementosExtras.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Toast.makeText(getActivity().getApplicationContext(), "elige los elementos que deseas agregar " , Toast.LENGTH_LONG).show();
    }
});
    }
}