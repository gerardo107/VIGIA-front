package com.example.gerardogarcias.myapplication.SolicitudesFragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.gerardogarcias.myapplication.Adapter.UploadListAdapter;
import com.example.gerardogarcias.myapplication.MainMenuActivity;
import com.example.gerardogarcias.myapplication.Model.Reporte;
import com.example.gerardogarcias.myapplication.R;
import com.example.gerardogarcias.myapplication.Retrofit.VigiaAPI;
import com.example.gerardogarcias.myapplication.Util.Common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


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
    CardView RegistroButton, CancelarButton;
    String reporte, nombre, apellido, colonia, cp, calle, numero,involucrados;

    //localización
    VigiaAPI mService = Common.getApi();
    private FusedLocationProviderClient client;
    Geocoder geocoder;
    List<Address> addresses;
    Address lastLocation;
    CheckBox checkBoxLocalizacion;

    //imagenes
    public static final int RESULT_LOAD_IMAGE1 = 1;
    public static final int REQUEST_CAMERA = 2;
    private RecyclerView mUploadList;

    private List<String> fileNameList;
    private List<String> fileDoneList;

    private UploadListAdapter uploadListAdapter;

    //URL para los datos del spiner
    String url = "https://vigia-back.herokuapp.com/requests/SA/events/ORG/situations";



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
        mUploadList = view.findViewById(R.id.uploadList);

        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();

        uploadListAdapter = new UploadListAdapter(fileNameList, fileDoneList);

        mUploadList.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext()));
        mUploadList.setHasFixedSize(true);
        mUploadList.setAdapter(uploadListAdapter);


        checkBoxLocalizacion = view.findViewById(R.id.CheckBoxLocalizacion);

        checkBoxLocalizacion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("PRESIONADO LOCALIZ", "checkbox pressed");
                if(b){
                    requestPermission();
                    client = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

                    geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                    if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }else{
                        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location != null){
                                    double longitud = location.getLongitude();
                                    double latitud = location.getLatitude();

                                    try {
                                        addresses = geocoder.getFromLocation(latitud, longitud, 1);

                                        lastLocation = addresses.get(addresses.size() -1);

                                        String street = lastLocation.getThoroughfare();
                                        String postalCode = lastLocation.getPostalCode();
                                        String colony = lastLocation.getSubLocality();
                                        String number = lastLocation.getFeatureName();

                                        edColonia.setText(colony);
                                        edCalle.setText(street);
                                        edNumero.setText(number);
                                        edCp.setText(postalCode);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }

                }else{
                    edColonia.setText("");
                    edCp.setText("");
                    edCalle.setText("");
                    edNumero.setText("");
                }
            }
        });

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

    private void requestPermission(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION},1);
    }

    private void jsonParse() {
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


                            //obtener el ancho y el alto de las pantallas
                            DisplayMetrics displayMetrics = getActivity().getApplicationContext().getResources().getDisplayMetrics();

                            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
                            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
                            Log.d("size"+dpWidth,"mssg");

                            //nexus 5
                            if (dpWidth >= 360   && dpWidth < 600 ){

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
                                materialDesignSpinner.setTextSize(18);

                            }

                            //nexus 10
                            else if (dpWidth >= 600) {

                                spinnerArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                        android.R.layout.simple_dropdown_item_1line, spinnerArray) {
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View v = super.getView(position, convertView, parent);
                                        v.setBackgroundResource(R.drawable.gradient);
                                        ((TextView) v).setTextSize(22);
                                        ((TextView) v).setTextColor(Color.parseColor("#FFFFFF"));
                                        return v;
                                    }

                                };
                                materialDesignSpinner.setTextSize(24);
                            }
                            //nexus s
                            else {

                                spinnerArrayAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                        android.R.layout.simple_dropdown_item_1line, spinnerArray) {
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View v = super.getView(position, convertView, parent);
                                        v.setBackgroundResource(R.drawable.gradient);
                                        ((TextView) v).setTextSize(14);
                                        ((TextView) v).setTextColor(Color.parseColor("#FFFFFF"));
                                        return v;
                                    }

                                };
                                materialDesignSpinner.setTextSize(14);


                            }


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
                    retrofitPost();


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
    public void retrofitPost(){
        reporte = edReporte.getText().toString();
        nombre = edNombre.getText().toString();
        apellido = edApellido.getText().toString();
        colonia = edColonia.getText().toString();
        cp = edCp.getText().toString();
        calle = edCalle.getText().toString();
        numero = edNumero.getText().toString();
        involucrados = edInvolucrados.getText().toString();

        mService.reportes(String.valueOf(date),
                String.valueOf(hour),
                reporte,
                "0",
                idS,
                calle,
                colonia,
                cp,
                numero,
                nombre,
                apellido,
                involucrados)
                .enqueue(new Callback<Reporte>() {
                    @Override
                    public void onResponse(Call<Reporte> call, retrofit2.Response<Reporte> response) {
                        if (response.isSuccessful()){
                            Log.d("RESPERR", "registro exitoso");
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
    private void RegistroExitoso(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = this.getLayoutInflater();
        View RegistroExitoso_layout = inflater.inflate(R.layout.dialog_registro_exitoso, null);

        TextView registro_exitoso = (TextView)RegistroExitoso_layout.findViewById(R.id.TextViewRegistroExitoso);

        registro_exitoso.setText("Tu registro se ha creado exitosamente\n folio: "+message);
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
                selectImage();
                //Toast.makeText(getActivity().getApplicationContext(), "elige los elementos que deseas agregar " , Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE1 && resultCode == getActivity().RESULT_OK){
            if(data.getClipData() != null){
                int totalItemSelected = data.getClipData().getItemCount();

                for(int i = 0; i < totalItemSelected; i++){
                    Uri fileuri = data.getClipData().getItemAt(i).getUri();
                    String filename = getFilename(fileuri);

                    fileNameList.add(filename);
                    fileDoneList.add("uploading");
                    uploadListAdapter.notifyDataSetChanged();

                }
                //Toast.makeText(MainActivity.this, "select multiple images", Toast.LENGTH_SHORT).show();
            }else if(data.getData() != null){
                Uri fileuri = data.getData();
                String filename = getFilename(fileuri);

                fileNameList.add(filename);
                fileDoneList.add("uploading");
                uploadListAdapter.notifyDataSetChanged();

                //Toast.makeText(MainActivity.this, "select single images", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == REQUEST_CAMERA && resultCode == getActivity().RESULT_OK){
            if(data != null){
                Bundle extras = data.getExtras();
                Bitmap bitPhoto = (Bitmap) extras.get("data");
                String path = "";
                checkWritePermission();
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }else{
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitPhoto.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitPhoto, "Title", null);
                }

                Uri fileUri = Uri.parse(path);
                String filename = getFilename(fileUri);
                Log.d("CAMARAURI", filename);

                fileNameList.add(filename);
                fileDoneList.add("uploading");
                uploadListAdapter.notifyDataSetChanged();
            }
        }
    }

    public void checkWritePermission(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE},1);
    }

    public String getFilename(Uri uri){
        String result = null;
        if(uri.getScheme().equals("content")){
            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri, null, null, null, null);
            try{
                if(cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }
        if(result == null){
            result = uri.getPath();
            int cut  = result.lastIndexOf('/');
            if(cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void selectImage(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = this.getLayoutInflater();
        View cancelar_layout = inflater.inflate(R.layout.dialog_camara, null);

        Button btn_camara = (Button)cancelar_layout.findViewById(R.id.btn_camara);
        Button btn_galeria = (Button)cancelar_layout.findViewById(R.id.btn_galeria);
        Button btn_cancelar = (Button)cancelar_layout.findViewById(R.id.btn_cancelar);
        builder.setView(cancelar_layout);
        final AlertDialog dialog = builder.create();


        // evento del botón Confirmar
        btn_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("/");
                Intent[] intentArray = new Intent[]{takePictureIntent,takeVideoIntent};
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Escoge una acción");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                if (chooserIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(chooserIntent, 1);
                }
                dialog.dismiss();


            }
        });

        btn_galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select picture"), RESULT_LOAD_IMAGE1);
                dialog.dismiss();

            }
        });
        // evento del botón Cancelar
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }
}