package com.example.gerardogarcias.myapplication;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        goToRegistro();
        goToMainI();
        goToMain();

    }
    //go to register  layout
    private void  goToRegistro(){

        TextView RegisterButton = (TextView) findViewById(R.id.botonRegistro);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }

    //go to main layout without login
    private void  goToMainI(){
        CardView MainButton = (CardView) findViewById(R.id.invitadoButtom);
        MainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,MainMenuActivity.class);
                startActivity(intent);
            }
        });
    }

    private void  goToMain(){

    }


}
