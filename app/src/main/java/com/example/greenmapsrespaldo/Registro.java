package com.example.greenmapsrespaldo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {

    //DECLARACION DE VARIABLES PARA EL REGISTRO EN FIREBASE AUTHENTICATOR
    private Authenticator authenticator;
    private UserAuthenticator userAuthenticator;

    private EditText etUsuario;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etPassword;
    private EditText etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        authenticator = new Authenticator();
        userAuthenticator = new UserAuthenticator();

        Button buttonRegister = findViewById(R.id.btnEnviar);
        etUsuario = findViewById(R.id.etUsuario);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        buttonRegister.setOnClickListener(view -> validacionesUsuario());
    }

    private void validacionesUsuario() {

        String txtUsuario = etUsuario.getText().toString();
        String txtEmail = etEmail.getText().toString().trim();
        String txtPhone = etPhone.getText().toString();
        String txtPassword = etPassword.getText().toString().trim();
        String txtConfirmPassword = etConfirmPassword.getText().toString();

        String mobileRegex = "[0-9][0-9]{9}";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(txtPhone);

        if (TextUtils.isEmpty(txtUsuario)) {
            Toast.makeText(Registro.this, "Por favor ingresa tu nombre de usuario", Toast.LENGTH_LONG).show();
            etUsuario.setError("El usuario es requerido");
            etUsuario.requestFocus();
        } else if (TextUtils.isEmpty(txtEmail)) {
            Toast.makeText(Registro.this, "Por favor ingresa tu correo electrónico", Toast.LENGTH_LONG).show();
            etEmail.setError("El correo electrónico es requerido");
            etEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()) {
            Toast.makeText(Registro.this, "Por favor re-ingresa tu correo electrónico", Toast.LENGTH_LONG).show();
            etEmail.setError("Un correo electrónico valido es requerido");
            etEmail.requestFocus();
        } else if (TextUtils.isEmpty(txtPhone)) {
            Toast.makeText(Registro.this, "Por favor ingresa tu numero telefónico", Toast.LENGTH_LONG).show();
            etPhone.setError("El numero telefónico es requerido");
            etPhone.requestFocus();
        } else if (txtPhone.length() != 10) {
            Toast.makeText(Registro.this, "Por favor re-ingresa tu numero telefónico", Toast.LENGTH_LONG).show();
            etPhone.setError("El numero telefónico debe tener 10 dígitos");
            etPhone.requestFocus();
        } else if (!mobileMatcher.find()) {
            Toast.makeText(Registro.this, "Por favor re-ingresa tu numero telefónico", Toast.LENGTH_LONG).show();
            etPhone.setError("El numero telefónico no es valido");
            etPhone.requestFocus();
        } else if (TextUtils.isEmpty(txtPassword)) {
            Toast.makeText(Registro.this, "Por favor ingresa tu contraseña", Toast.LENGTH_LONG).show();
            etPassword.setError("La contraseña es requerida");
            etPassword.requestFocus();
        } else if (txtPassword.length() < 6) {
            Toast.makeText(Registro.this, "La contraseña debe tener mínimo 6 dígitos", Toast.LENGTH_LONG).show();
            etPassword.setError("La contraseña es muy débil");
            etPassword.requestFocus();
        } else if (TextUtils.isEmpty(txtConfirmPassword)) {
            Toast.makeText(Registro.this, "Por favor confirma tu contraseña", Toast.LENGTH_LONG).show();
            etConfirmPassword.setError("Confirmar la contraseña es requerido");
            etConfirmPassword.requestFocus();
        } else if (!txtPassword.equals(txtConfirmPassword)) {
            Toast.makeText(Registro.this, "Por favor coloque la misma contraseña", Toast.LENGTH_LONG).show();
            etConfirmPassword.setError("Confirmar la contraseña es requerido");
            etConfirmPassword.requestFocus();
            etPassword.clearComposingText();
            etConfirmPassword.clearComposingText();
        } else {
            registerUser(txtUsuario, txtEmail, txtPhone, txtPassword);
        }

    }

    private void registerUser(String textFullName, String textEmail, String textMobile, String textPwd) {
        authenticator.registrar(textEmail,textPwd).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReadWriteUserDetails readWriteUserDetails = new ReadWriteUserDetails(textFullName,textMobile);
                create(readWriteUserDetails);
            }else{
                Toast.makeText(this, "El usuario no se a podido registrar", Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(e -> Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void create(ReadWriteUserDetails readWriteUserDetails) {
        userAuthenticator.create(readWriteUserDetails).addOnCompleteListener(task ->{
           if (task.isSuccessful()){
               Toast.makeText(this, "Registro exitoso, Ya puedes iniciar sesión", Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(this,MainActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
           }else{
               Toast.makeText(this, "El usuario no se ha podido registrar", Toast.LENGTH_SHORT).show();
           }

        }).addOnFailureListener(e -> Toast.makeText(this, "Error:" + e.getMessage(), Toast.LENGTH_LONG).show());

    }
}