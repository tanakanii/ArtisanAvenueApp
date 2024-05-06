package com.example.artisanavenue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    EditText username, first_name, last_name, password, repassword;
    ImageButton btnsignup, btnsignin3;
    DBhelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = (EditText) findViewById(R.id.txtUsername1);
        first_name = (EditText) findViewById(R.id.txtFirstName);
        last_name = (EditText) findViewById(R.id.txtLastName);
        password = (EditText) findViewById(R.id.txtPassword1);
        repassword = (EditText) findViewById(R.id.txtConfirmPass);
        btnsignup = (ImageButton) findViewById(R.id.btnSignUp);
        btnsignin3 = (ImageButton) findViewById(R.id.btnSignIn3);
        DB = new DBhelper(this);

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString().trim();
                String firstname = first_name.getText().toString().trim();
                String lastname = last_name.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String repass = repassword.getText().toString().trim();

                if (user.equals("") || pass.equals("") || firstname.equals("") ||
                        lastname.equals("") || repass.equals(""))
                    Toast.makeText(SignUp.this, "Please fill all of the entry!", Toast.LENGTH_SHORT).show();
                else {
                    if (pass.equals(repass)) {
                        Boolean checkuser = DB.checkusername(user);
                        if(checkuser==false){
                            Boolean insert = DB.insertData(user, pass, firstname, lastname);
                            if(insert==true){
                                Toast.makeText(SignUp.this, "Registered Success!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SignUp.this, "Registered Failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(SignUp.this, "User already exists, Sign-in!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUp.this, "Password do not match!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnsignin3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignUp.this, "Registered Success!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), signin.class);
                startActivity(intent);
            }
        });
    }

}