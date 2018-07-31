package com.example.gerardogarcias.myapplication;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.gerardogarcias.myapplication.IncidenciasFragments.IncidenciasFragment;
import com.example.gerardogarcias.myapplication.Util.Common;
import com.facebook.accountkit.AccountKit;


public class IncidenciasActivity extends AppCompatActivity {
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

    TextView txt_name, txt_phone, txt_mail;

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

        drawerTitle = getResources().getString(R.string.incidencias_item);
        if (savedInstanceState == null) {
            setFragment(0);
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
                                Log.d("CLOSE", "CLOSE SESSION");
                                AccountKit.logOut();
                                Common.currentUser = null;
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

    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 0:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                IncidenciasFragment incidenciasFragment = new IncidenciasFragment();
                fragmentTransaction.replace(R.id.fragment, incidenciasFragment);
                fragmentTransaction.commit();
                break;

        }
    }



    //metodo para ir a MainActivity
    private void  goToMain(){
        Intent intent = new Intent(IncidenciasActivity.this,MainMenuActivity.class);
        startActivity(intent);
    }

    //metodo para ir a SolicitudesActivity
    private void  goToSolicitudes(){
        Intent intent = new Intent(IncidenciasActivity.this,SolicitudesActivity.class);
        startActivity(intent);
    }

    //metodo para ir a ReportesActivity
    private void  goToReportes(){
        Intent intent = new Intent(IncidenciasActivity.this,ReportesActivity.class);
        startActivity(intent);
    }

    //metodo para ir a IncidenciasActivity
    private void  goToIncidencias(){
        Intent intent = new Intent(IncidenciasActivity.this,IncidenciasActivity.class);
        startActivity(intent);
    }

    //metodo para ir a LoginActivity
    private void  goToLogin(){
        Intent intent = new Intent(IncidenciasActivity.this,LoginActivity.class);
        startActivity(intent);
    }

}
