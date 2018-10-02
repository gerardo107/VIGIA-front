package com.example.gerardogarcias.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gerardogarcias.myapplication.Model.CheckUserResponse;
import com.example.gerardogarcias.myapplication.Model.User;
import com.example.gerardogarcias.myapplication.Retrofit.VigiaAPI;
import com.example.gerardogarcias.myapplication.Util.Common;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.rengwuxian.materialedittext.MaterialEditText;


import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity {

    // Botón para registro
    private static final int REQUEST_CODE = 1000;
    //Button btn_continue;
    CardView btn_continue;
    VigiaAPI mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mService = Common.getApi();

        btn_continue = (CardView) findViewById(R.id.mainButton);
        //btn_continue = (Button)findViewById(R.id.mainButton);
        btn_continue.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(AccountKit.getCurrentAccessToken() != null){
                    final AlertDialog alertDialog = new SpotsDialog.Builder()
                            .setContext(LoginActivity.this)
                            .setMessage("Por favor espera...")
                            .build();
                    alertDialog.show();

                    // auto login
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {
                            mService.checkuser(account.getPhoneNumber().toString())
                                    .enqueue(new Callback<CheckUserResponse>() {
                                        @Override
                                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                            CheckUserResponse userResponse = response.body();
                                            if(userResponse.isExists()){
                                                Log.d("SI USER ON AUTO-LOGIN", "user exists");
                                                // Get user information

                                                mService.getuser(account.getPhoneNumber().toString())
                                                        .enqueue(new Callback<User>() {
                                                            @Override
                                                            public void onResponse(Call<User> call, Response<User> response) {
                                                                // If user already exists, just start new activity
                                                                alertDialog.dismiss();
                                                                Common.currentUser = response.body();

                                                                startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
                                                                finish(); // Cerrar LoginActivity
                                                            }

                                                            @Override
                                                            public void onFailure(Call<User> call, Throwable t) {
                                                                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }else{
                                                // Registrar nuevo usuario si no existe
                                                Log.d("NO USER", "no exists");
                                                alertDialog.dismiss();

                                                showRegisterDialog(account.getPhoneNumber().toString());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                            Log.d("ERROR ON FAILURE", t.getMessage());
                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Log.d("ERROR", accountKitError.getErrorType().getMessage());
                        }
                    });
                }else {
                    startLoginPage(LoginType.PHONE);
                }
            }
        });


        goToMainI();
        requestPermission();


    }

    // Método que llama a la página de autenticación con el número telefonico
    private void startLoginPage(LoginType loginType) {
        Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder builder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, builder.build());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE){
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if(result.getError() != null){
                Log.d("SHOW ERROR", result.getError().getErrorType().toString());
                Toast.makeText(this, ""+result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
            }else if(result.wasCancelled()){
                Log.d("SHOW CANCELLED", "Was cancelled");
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }else{
                Log.d("SHOW DIALOG", "This is waiting the dialog message");
                if(result.getAccessToken() != null){
                    final AlertDialog alertDialog = new SpotsDialog.Builder()
                            .setContext(LoginActivity.this)
                            .setMessage("Por favor espera...")
                            .build();
                    alertDialog.show();

                    // Get user phone and verify if exists on server
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {
                            mService.checkuser(account.getPhoneNumber().toString())
                                    .enqueue(new Callback<CheckUserResponse>() {
                                        @Override
                                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                            Log.d("SOME ERROR", response.message());
                                            CheckUserResponse userResponse = response.body();
                                            if(userResponse.isExists()){
                                                Log.d("SI USER", "user exists");
                                                // Get user information

                                                mService.getuser(account.getPhoneNumber().toString())
                                                        .enqueue(new Callback<User>() {
                                                            @Override
                                                            public void onResponse(Call<User> call, Response<User> response) {
                                                                // If user already exists, just start new activity
                                                                alertDialog.dismiss();
                                                                Common.currentUser = response.body();
                                                                startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
                                                                finish(); // Cerrar LoginActivity
                                                            }

                                                            @Override
                                                            public void onFailure(Call<User> call, Throwable t) {
                                                                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }else{
                                                // Registrar nuevo usuario si no existe
                                                Log.d("NO USER", "no exists");
                                                alertDialog.dismiss();

                                                showRegisterDialog(account.getPhoneNumber().toString());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                            Log.d("ERROR ON FAILURE", t.getMessage());
                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Log.d("ERROR", accountKitError.getErrorType().getMessage());
                        }
                    });
                }
            }
        }
    }

    private void showRegisterDialog(final String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View register_layout = inflater.inflate(R.layout.activity_register, null);

        final MaterialEditText edt_name = (MaterialEditText)register_layout.findViewById(R.id.edt_name);
        edt_name.setTextColor(Color.parseColor("#FFFFFF"));
        edt_name.setHintTextColor(Color.parseColor("#FFFFFF"));

        final MaterialEditText edt_lastname = (MaterialEditText)register_layout.findViewById(R.id.edt_lastname);
        edt_lastname.setTextColor(Color.parseColor("#FFFFFF"));
        edt_lastname.setHintTextColor(Color.parseColor("#FFFFFF"));

        final MaterialEditText edt_email = (MaterialEditText)register_layout.findViewById(R.id.edt_email);
        edt_email.setTextColor(Color.parseColor("#FFFFFF"));
        edt_email.setHintTextColor(Color.parseColor("#FFFFFF"));



        Button btn_register = (Button)register_layout.findViewById(R.id.btn_register);

        builder.setView(register_layout);
        final AlertDialog dialog = builder.create();

        // evento del botón registrar
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if(TextUtils.isEmpty(edt_name.getText().toString())){
                    edt_name.setError("este campo es obligatorio");
                    //ErrorCamposObligatorios();
                    return;
                }
                else if(TextUtils.isEmpty(edt_lastname.getText().toString())){
                    edt_lastname.setError("este campo es obligatorio");
                    //ErrorCamposObligatorios();
                    return;
                }
                else if(TextUtils.isEmpty(edt_email.getText().toString())){
                    edt_email.setError("este campo es obligatorio");
                    //ErrorCamposObligatorios();
                    return;
                }
                else {
                    dialog.dismiss();
                }

                final AlertDialog waitingDialog = new SpotsDialog.Builder()
                        .setContext(LoginActivity.this)
                        .setMessage("Please waiting...")
                        .build();
                waitingDialog.show();

                mService.register(phone,
                        edt_name.getText().toString(),
                        edt_lastname.getText().toString(),
                        edt_email.getText().toString())
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                waitingDialog.dismiss();
                                User user = response.body();
                                if(TextUtils.isEmpty(user.getError_msg())){
                                    Toast.makeText(LoginActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                                    Common.currentUser = response.body();
                                    //start new activity
                                    startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
                                    finish(); // Cerrar LoginActivity
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                waitingDialog.dismiss();
                            }
                        });
            }
        });

        dialog.show();
    }
//pedir permiso de localización
    private void requestPermission(){
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public void checkWritePermission(){
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE},1);
    }
    //go to main layout without login
    private void  goToMainI(){
        CardView MainButton = (CardView) findViewById(R.id.invitadoButtom);
        MainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.currentUser = null;
                Intent intent = new Intent(LoginActivity.this,MainMenuActivity.class);
                startActivity(intent);
            }
        });
    }

    public void goToTwitter (View view) {
        goToUrl ( "https://twitter.com/gobsma?lang=es");
    }
    public void goToWeb (View view) {
        goToUrl ( "http://sanmigueldeallende.gob.mx");
    }
    public void goToFaceBook (View view) {
        goToUrl ( "https://www.facebook.com/sanmigueldeallendeunidospodemos?fref=ts");
    }
    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
    //AlertDialog campos obligatorios sin llenar
    private void ErrorCamposObligatorios() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

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
}
