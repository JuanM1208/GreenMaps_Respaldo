package com.example.greenmapsrespaldo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {

    private EditText etUsuario, etEmail, etPhone,
            etPassword, etConfirmPassword;
    private ProgressBar progressBar;
    private static final String TAG= "Registro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
        //getSupportActionBar().setTitle("Registro");
        Toast.makeText(Registro.this, "Ahora te puedes registrar", Toast.LENGTH_LONG).show();
        progressBar = findViewById(R.id.progressBar);
        etUsuario = findViewById(R.id.etUsuario);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        Button buttonRegister = findViewById(R.id.btnEnviar);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txtUsuario = etUsuario.getText().toString();
                String txtEmail = etEmail.getText().toString();
                String txtPhone = etPhone.getText().toString();
                String txtPassword = etPassword.getText().toString();
                String txtConfirmPassword = etConfirmPassword.getText().toString();


                String mobileRegex = "[6-9][0-9]{9}";
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
                } else if  (!Patterns.EMAIL_ADDRESS.matcher(txtEmail).matches()) {
                    Toast.makeText(Registro.this, "Por favor re-ingresa tu correo electrónico", Toast.LENGTH_LONG).show();
                    etEmail.setError("Un correo electrónico valido es requerido");
                    etEmail.requestFocus();
                }  else if (TextUtils.isEmpty(txtPhone)) {
                    Toast.makeText(Registro.this, "Por favor ingresa tu numero telefónico", Toast.LENGTH_LONG).show();
                    etPhone.setError("El numero telefónico es requerido");
                    etPhone.requestFocus();
                } else if (txtPhone.length() !=10) {
                    Toast.makeText(Registro.this, "Por favor re-ingresa tu numero telefónico", Toast.LENGTH_LONG).show();
                    etPhone.setError("El numero telefónico debe tener 10 dígitos");
                    etPhone.requestFocus();
                } else if (!mobileMatcher.find()){
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
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(txtUsuario, txtEmail, txtPhone, txtPassword);
                }

            }
        });
    }

    private void registerUser(String textFullName, String textEmail, String textMobile, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(Registro.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textPwd, textMobile);

                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Usuarios registrados");
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //firebaseUser.sendEmailVerification();
                                Toast.makeText(Registro.this, "Usuario registrado correctamente, por favor verifica tu correo electrónico", Toast.LENGTH_LONG).show();
                                /*Intent intent = new Intent(Registro.this, UserProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();*/
                            } else {
                                Toast.makeText(Registro.this, "No se pudo registrar al usuario, por favor intentalo de nuevo", Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        etPassword.setError("Tu contraseña es muy corta, por favor utiliza diferentes letras del alfabeto, números y caracteres especiales");
                        etPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        etEmail.setError("Tu correo electrónico es invalido o ya esta en uso, prueba re-ingresando");
                        etEmail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        etEmail.setError("Usuario actualmente registrado con ese correo electrónico, utiliza otro correo electrónico");
                        etEmail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Registro.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}