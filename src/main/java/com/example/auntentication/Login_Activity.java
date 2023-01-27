package com.example.auntentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.auntentication.model.Users;
import com.example.auntentication.prevelant.prevelant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class Login_Activity extends AppCompatActivity {

    private EditText input_email, input_password;
    private Button Login_Button;
    private ProgressDialog LoadingBar;
    private String parentDpName = "Users";
    private CheckBox chck_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Login_Button = (Button) findViewById(R.id.login_button_login_page);
        input_email = (EditText) findViewById(R.id.editTextEmail);
        input_password = (EditText) findViewById(R.id.editTextPassword);
        LoadingBar = new ProgressDialog(this);

        chck_box = (CheckBox) findViewById(R.id.remember_me);
        Paper.init(this);

        Login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LoginUser();
            }
        });
    }

    private void LoginUser() {

        String email = input_email.getText().toString();
        String Password = input_password.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write your Username", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(Password)){
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();
        }

        else {
            LoadingBar.setTitle("Login Account");
            LoadingBar.setMessage("Please wait, we are checking the credentials.");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();

            Access_to_the_account(email,Password);
        }
    }

    private void Access_to_the_account(String email, String password) {

        if(chck_box.isChecked())
        {
            Paper.book().write(prevelant.UserEmail,email);
            Paper.book().write(prevelant.UserPasswordKey,password);
        }


        final DatabaseReference Rootref = FirebaseDatabase.getInstance("https://autenticacion-936ca-default-rtdb.firebaseio.com/").getReference();

        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(parentDpName).child(email).exists())
                {
                    Users usersData = snapshot.child(parentDpName).child(email).getValue(Users.class);




                    if(usersData.getMasterpassword().equals(password))
                    {
                        Toast.makeText(Login_Activity.this, "Logged in successfully!!!", Toast.LENGTH_SHORT).show();
                        LoadingBar.dismiss();
                        Toast.makeText(Login_Activity.this, "Hello "+ usersData.getUsername()+" !!!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Login_Activity.this,Home_page.class );

                        startActivity(intent);


                    }
                    else{
                        LoadingBar.dismiss();
                        Toast.makeText(Login_Activity.this, "Password is incorrect!!!", Toast.LENGTH_SHORT).show();

                    }


                }
                else{
                    Toast.makeText(Login_Activity.this, "Account with this "+ email + " does not exist!!", Toast.LENGTH_SHORT).show();
                    LoadingBar.dismiss();
                    Toast.makeText(Login_Activity.this, "Please Sign Up to create an account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}