package com.example.greenmapsrespaldo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Bienvenida extends AppCompatActivity {

    private EditText etLoginEmail, etLoginPassword;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bienvenida);
        //Login Activity
        //getSupportActionBar().setTitle("Login");

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();

        //Mostrar u ocultar contraseña con ícono de ojo
        ImageView imgPasswordVisibility = findViewById(R.id.imgPasswordVisibility);
        imgPasswordVisibility.setImageResource(R.drawable.ic_hide_pwd);
        imgPasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etLoginPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //Si la contraseña es visible, la oculta
                    etLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Cambia el ícono
                    imgPasswordVisibility.setImageResource(R.drawable.ic_hide_pwd);
                }
                else{
                    etLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imgPasswordVisibility.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        //Inicio de sesión
        Button buttonLogin = findViewById(R.id.btnLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmail = etLoginEmail.getText().toString();
                String loginPassword = etLoginPassword.getText().toString();

                if (TextUtils.isEmpty(loginEmail)){
                    Toast.makeText(Bienvenida.this, "Por favor ingresa tu email", Toast.LENGTH_SHORT).show();
                    etLoginEmail.setError("Este campo es obligatorio");
                    etLoginEmail.requestFocus();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()){
                    Toast.makeText(Bienvenida.this, "Por favor ingresa un email válido", Toast.LENGTH_SHORT).show();
                    etLoginEmail.setError("La dirección de correo es inválida");
                    etLoginEmail.requestFocus();
                }
                else if (TextUtils.isEmpty(loginPassword)){
                    Toast.makeText(Bienvenida.this, "Por favor ingresa tu contraseña", Toast.LENGTH_SHORT).show();
                    etLoginPassword.setError("Este campo es obligatorio");
                    etLoginPassword.requestFocus();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(loginEmail, loginPassword);
                }
            }
        });

        //Abrir activity del Registro
        Button btnRegistro = findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Bienvenida.this, Registro.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(Bienvenida.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //Obtener instancia del usuario actual
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    //Revisa si el email es verificado antes de que el usuario pueda acceder a su perfil
                    //if (firebaseUser.isEmailVerified()){
                    Toast.makeText(Bienvenida.this, "Has iniciado sesión", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Bienvenida.this, MainActivity.class));
                    finish();
                    /*}
                    else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }*/
                }
                else {
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        etLoginEmail.setError("El usuario no existe o su longitud es inválida");
                        etLoginEmail.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        etLoginPassword.setError("Usuario y/o contraseña incorrectos. Por favor vuelva a intentar");
                        etLoginEmail.requestFocus();
                    }
                    catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Bienvenida.this, "Ha ocurrido un error!", Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /*private void showAlertDialog() {
        //Coloca el Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Bienvenida.this);
        builder.setTitle("Email no verificado");
        builder.setMessage("Por favor verifica tu email en la bandeja de entrada. No puedes ingresar sin haber hecho este paso");

        //Abrir app de email si el usuario acepta
        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     //Para que gmail abra en una nueva ventana
                startActivity(intent);
            }
        });

        //Crear el AlertDialog
        AlertDialog alertDialog = builder.create();

        //Mostrar el AlertDialog
        alertDialog.show();
    }*/

    //Revisa si el usuario ya había iniciado sesión, de ser el caso, lo lleva directamente a su perfil

    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null){
            Toast.makeText(Bienvenida.this, "Ya habías iniciado sesión", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Bienvenida.this, MainActivity.class));
            finish();
        }
        else {
            Toast.makeText(Bienvenida.this, "Ya puedes iniciar sesión", Toast.LENGTH_SHORT).show();
        }
    }
}