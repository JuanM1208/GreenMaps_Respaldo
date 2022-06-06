package com.example.greenmapsrespaldo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.view.Change;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CambiarPassword extends AppCompatActivity{
    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr, editTextPwdNew, editTextPwdConfirmNew;
    private TextView textViewAuthenticated;
    private TextView headerNombre, headerUsuario;
    private CircleImageView circleImageView;
    private String fullName, email, mobile;
    private Button buttonChangePwd, buttonReAuthenticate;
    private String userPwdCurr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cambiar_password);

        editTextPwdNew = findViewById(R.id.editText_change_pwd_new);
        editTextPwdCurr = findViewById(R.id.editText_change_pwd_current);
        editTextPwdConfirmNew = findViewById(R.id.editText_change_pwd_new_confirm);
        textViewAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
        buttonReAuthenticate = findViewById(R.id.button_change_pwd_authenticate);
        buttonChangePwd = findViewById(R.id.button_change_pwd);

        editTextPwdNew.setEnabled(false);
        editTextPwdConfirmNew.setEnabled(false);
        buttonChangePwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser.equals("")){
            Toast.makeText(CambiarPassword.this, "¡Algo ocurrio mal!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CambiarPassword.this, PerfilUsuario.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }

        MaterialToolbar toolbar = findViewById(R.id.topAppbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_changePassword);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        View view = navigationView.getHeaderView(0);
        circleImageView = view.findViewById(R.id.profilepic);
        headerNombre = view.findViewById(R.id.nombre_menu);
        headerUsuario = view.findViewById(R.id.usuario_menu);

        if(firebaseUser == null) {
            Toast.makeText(CambiarPassword.this, "Algo ocurrió mal, detalles del usuario no disponibles en este momento", Toast.LENGTH_LONG).show();

        } else {
            //checkIfEmailVerified(firebaseUser);
            showUserProfile(firebaseUser);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);

                if(id == R.id.menu_home) {
                    Intent intent = new Intent (CambiarPassword.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if(id == R.id.menu_refresh) {
                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0, 0);
                } else if (id == R.id.menu_update_profile) {
                    Intent intent = new Intent (CambiarPassword.this, ActualizarPerfil.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_update_email) {
                    Intent intent = new Intent (CambiarPassword.this, ActualizarEmail.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_change_password) {
                    Intent intent = new Intent (CambiarPassword.this, CambiarPassword.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_delete_profile) {
                    Intent intent = new Intent (CambiarPassword.this, EliminarPerfil.class);
                    startActivity(intent);
                } else if (id == R.id.menu_logout) {
                    authProfile.signOut();
                    Toast.makeText(CambiarPassword.this, "Desconectado", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent (CambiarPassword.this, Bienvenida.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CambiarPassword.this, "¡Algo ocurrió mal!", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    String name = readUserDetails.getUsuario();
                    String email = firebaseUser.getEmail();

                    headerNombre.setText(name);
                    headerUsuario.setText(email);

                    Uri uri = firebaseUser.getPhotoUrl();

                    Picasso.with(CambiarPassword.this).load(uri).into(circleImageView);
                } else {
                    Toast.makeText(CambiarPassword.this, "Algo ocurrió mal", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CambiarPassword.this, "Algo ocurrió mal", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPwdCurr = editTextPwdCurr.getText().toString();

                if (TextUtils.isEmpty(userPwdCurr)) {
                    Toast.makeText(CambiarPassword.this, "La contraseña es requerida",
                            Toast.LENGTH_SHORT).show();
                    editTextPwdCurr.setText("Por favor ingresa tu actual contraseña para autenticarla");
                    editTextPwdCurr.requestFocus();
                } else {
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                editTextPwdCurr.setEnabled(false);
                                editTextPwdNew.setEnabled(true);
                                editTextPwdConfirmNew.setEnabled(true);

                                buttonReAuthenticate.setEnabled(false);
                                buttonChangePwd.setEnabled(true);

                                textViewAuthenticated.setText("Estas autenticado/verificado" +
                                        "Ahora puedes cambiar tu contraseña");
                                Toast.makeText(CambiarPassword.this, "La contraseña ha sido verificada" +
                                        "Cambiar contraseña ahora", Toast.LENGTH_SHORT).show();

                                buttonChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(
                                        CambiarPassword.this, R.color.dark_green));

                                buttonChangePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(CambiarPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirmNew = editTextPwdConfirmNew.getText().toString();

        if (TextUtils.isEmpty(userPwdNew)) {
            Toast.makeText(CambiarPassword.this, "La nueva contraseña es requerida",
                    Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Por favor ingresa tu nueva contraseña");
            editTextPwdNew.requestFocus();
        } else if (TextUtils.isEmpty(userPwdConfirmNew)) {
            Toast.makeText(CambiarPassword.this, "La confirmacion de la nueva contraseña es requerida",
                    Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Por favor confirma tu nueva contraseña");
            editTextPwdConfirmNew.requestFocus();
        } else if (!userPwdNew.matches(userPwdConfirmNew)) {
            Toast.makeText(CambiarPassword.this, "Las contraseñas no coinciden",
                    Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Por favor ingresa la misma contraseña");
            editTextPwdConfirmNew.requestFocus();
        } else if (userPwdCurr.matches(userPwdNew)) {
            Toast.makeText(CambiarPassword.this, "La nueva contraseña no puede ser igual que la anterior",
                    Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Por favor ingresa una contraseña diferente");
            editTextPwdConfirmNew.requestFocus();
        } else {
            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CambiarPassword.this, "La contraseña ha sido cambiada",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CambiarPassword.this, PerfilUsuario.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(CambiarPassword.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}
