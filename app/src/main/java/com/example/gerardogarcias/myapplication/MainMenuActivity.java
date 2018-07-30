package com.example.gerardogarcias.myapplication;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.gerardogarcias.myapplication.Util.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    /**
     * Instancia del drawer
     */
    private DrawerLayout drawerLayout;

    /**
     * Titulo inicial del drawer
     */
    private String drawerTitle;

    LinearLayout parent;
    TextView mainMenuText;
    CardView mainMenuButtons;
    RequestQueue requestQueue;
    int valueDP_Width, Value_In_Pixel_Width;
    int valueDP_Height, Value_In_Pixel_Height;
    int valueDP_Radius, Value_In_Pixel_Radius;
    int valueDP_Elevation, Value_In_Pixel_Elevation;
    Context mContext;

    TextView txt_name, txt_phone,txt_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menus);
        mContext = getApplicationContext();
        parent = findViewById(R.id.parentLayout);

        requestQueue = Volley.newRequestQueue(this);


        setToolbar(); // Setear Toolbar como action bar

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }

        drawerTitle = getResources().getString(R.string.home_item);
        if (savedInstanceState == null) {
            jsonParse();
        }

        View headerView = navigationView.getHeaderView(0);
        txt_name = (TextView)headerView.findViewById(R.id.txt_name);
        txt_mail = (TextView)headerView.findViewById(R.id.txt_mail);
        txt_phone = (TextView)headerView.findViewById(R.id.txt_phone);

        // set information
        if(Common.currentUser != null){
            txt_name.setText(Common.currentUser.getName());
            txt_phone.setText(Common.currentUser.getPhone());
            txt_mail.setText(Common.currentUser.getEmail());
        }else{
            txt_name.setText("Invitado");
            txt_phone.setText("Sin telefono");
            txt_mail.setText("Sin correo");
        }

    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);

        }

    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                menuItem.setChecked(true);
                                goToMain();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.nav_solicitudes:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                goToSolicitudes();
                                return true;
                            case R.id.nav_reportes:
                                menuItem.setChecked(true);
                                goToReportes();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.nav_incidencias:
                                menuItem.setChecked(true);
                                goToIncidencias();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.nav_log_out:
                                menuItem.setChecked(true);
                                goToLogin();
                                //Toast.makeText(MainActivity.this, "Launching " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                        }
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    //metodo para ir a MainActivity


    private void jsonParse() {
        //URL de la api del primer menu
        String url = "http://10.0.2.2:3000/requests";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                final int finalI = i;
                                JSONObject requests = response.getJSONObject(i);

                                String name = requests.getString("name");

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
                                mainMenuButtons = new CardView(mContext);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Value_In_Pixel_Width, Value_In_Pixel_Height);
                                params.setMargins(0, 0, 0, 210);
                                params.gravity = Gravity.CENTER;
                                mainMenuButtons.setLayoutParams(params);
                                mainMenuButtons.setRadius(Value_In_Pixel_Radius);
                                mainMenuButtons.setCardBackgroundColor(Color.parseColor("#00A5E3"));
                                mainMenuButtons.setCardElevation(Value_In_Pixel_Elevation);
                                mainMenuButtons.setId(i);

                                //seleccionar los botones
                                mainMenuButtons.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        goToSecondMenus(finalI);
                                    }
                                });

                                //caracteristicas del texto
                                mainMenuText = new TextView(mContext);
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
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
        requestQueue.add(request);
    }

    private void goToSecondMenus(int id) {

        if (id == 0) {
            goToSolicitudes();
        }
        if (id == 1) {
            goToReportes();
        }
        if (id == 2) {
            goToIncidencias();
        }

    }
    private void  goToMain(){
        Intent intent = new Intent(this,MainMenuActivity.class);
        startActivity(intent);
    }

    //metodo para ir a SolicitudesActivity
    private void  goToSolicitudes(){
        Intent intent = new Intent(this,SolicitudesActivity.class);
        startActivity(intent);
    }

    //metodo para ir a ReportesActivity
    private void  goToReportes(){
        Intent intent = new Intent(this,ReportesActivity.class);
        startActivity(intent);
    }

    //metodo para ir a IncidenciasActivity
    private void  goToIncidencias(){
        Intent intent = new Intent(this,IncidenciasActivity.class);
        startActivity(intent);
    }

    //metodo para ir a LoginActivity
    private void  goToLogin(){
        Intent intent = new Intent(MainMenuActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
