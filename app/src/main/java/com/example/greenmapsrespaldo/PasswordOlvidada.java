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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class PasswordOlvidada extends AppCompatActivity{
    private Button buttonPwdReset, buttonRegister;
    private EditText editTextPwdResetEmail;
    private FirebaseAuth authProfile;
    private final static String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_olvidada);

        editTextPwdResetEmail = findViewById(R.id.editText_password_reset_email);
        buttonPwdReset = findViewById(R.id.button_password_reset);
        buttonRegister = findViewById(R.id.button_register);

        buttonPwdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextPwdResetEmail.getText().toString();

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(PasswordOlvidada.this, "Por favor ingresa tu correo electrónico registrado", Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("El email es requerido");
                    editTextPwdResetEmail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(PasswordOlvidada.this, "Por favor ingresa un correo electrónico valido", Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("Se requiere un email válido");
                    editTextPwdResetEmail.requestFocus();
                } else {
                    resetPassword(email);
                }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PasswordOlvidada.this, Registro.class);
                startActivity(intent);
            }
        });
    }

    private void resetPassword(String email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(PasswordOlvidada.this, "Por favor revisa la bandeja de tu correo electrónico", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent (PasswordOlvidada.this, Bienvenida.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthInvalidUserException e) {
                        editTextPwdResetEmail.setError("El usuario no existe o no es valido, registrese de nuevo");
                    } catch(Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(PasswordOlvidada.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
