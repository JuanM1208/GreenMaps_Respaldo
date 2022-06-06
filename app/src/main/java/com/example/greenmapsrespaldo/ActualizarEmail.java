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
import android.util.Patterns;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActualizarEmail extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private TextView textViewAuthenticated;
    private TextView headerNombre, headerUsuario;
    private CircleImageView circleImageView;
    private String fullName, email, mobile;
    private String userOldEmail, userNewEmail, userPwd;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail, editTextPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actualizar_email);

        editTextPwd = findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail = findViewById(R.id.editText_update_email_new);
        textViewAuthenticated = findViewById(R.id.textView_update_email_authenticated);
        buttonUpdateEmail = findViewById(R.id.button_update_email);

        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        userOldEmail = firebaseUser.getEmail();
        TextView textViewOldEmail = findViewById(R.id.textView_update_email_old);
        textViewOldEmail.setText(userOldEmail);

        if(firebaseUser.equals("")){
            Toast.makeText(ActualizarEmail.this, "¡Algo ocurrio mal! Detalles del usuario no disponibles",
                    Toast.LENGTH_LONG).show();
        } else {
            reAuthenticate(firebaseUser);
        }

        MaterialToolbar toolbar = findViewById(R.id.topAppbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_refreshEmail);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        View view = navigationView.getHeaderView(0);
        circleImageView = view.findViewById(R.id.profilepic);
        headerNombre = view.findViewById(R.id.nombre_menu);
        headerUsuario = view.findViewById(R.id.usuario_menu);

        if(firebaseUser == null) {
            Toast.makeText(ActualizarEmail.this, "Algo ocurrió mal, detalles del usuario no disponibles en este momento", Toast.LENGTH_LONG).show();

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
                    Intent intent = new Intent (ActualizarEmail.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if(id == R.id.menu_refresh) {
                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0, 0);
                } else if (id == R.id.menu_update_profile) {
                    Intent intent = new Intent (ActualizarEmail.this, ActualizarPerfil.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_update_email) {
                    Intent intent = new Intent (ActualizarEmail.this, ActualizarEmail.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_change_password) {
                    Intent intent = new Intent (ActualizarEmail.this, CambiarPassword.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_delete_profile) {
                    Intent intent = new Intent (ActualizarEmail.this, EliminarPerfil.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_logout) {
                    authProfile.signOut();
                    Toast.makeText(ActualizarEmail.this, "Desconectado", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent (ActualizarEmail.this, Bienvenida.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ActualizarEmail.this, "¡Algo ocurrió mal!", Toast.LENGTH_LONG).show();
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

                    Picasso.with(ActualizarEmail.this).load(uri).into(circleImageView);
                } else {
                    Toast.makeText(ActualizarEmail.this, "Algo ocurrió mal", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActualizarEmail.this, "Algo ocurrió mal", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser = findViewById(R.id.button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPwd = editTextPwd.getText().toString();

                if(TextUtils.isEmpty(userPwd)) {
                    Toast.makeText(ActualizarEmail.this, "La contraseña es necesaria para continuar",
                            Toast.LENGTH_LONG).show();
                    editTextPwd.setError("Por favor ingresa tu contraseña para autenticar");
                    editTextPwd.requestFocus();
                } else {
                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(ActualizarEmail.this, "La contraseña ha sido verificada" +
                                        "Ahora puedes actualizar tu correo electrónico", Toast.LENGTH_LONG).show();

                                textViewAuthenticated.setText("Estas autenticado, ahora puedes actualizar tu correo electrónico");

                                editTextNewEmail.setEnabled(true);
                                editTextPwd.setEnabled(false);
                                buttonVerifyUser.setEnabled(false);
                                buttonUpdateEmail.setEnabled(true);

                                buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(ActualizarEmail.this,
                                        R.color.dark_green));

                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        userNewEmail = editTextNewEmail.getText().toString();
                                        if (TextUtils.isEmpty(userNewEmail)) {
                                            Toast.makeText(ActualizarEmail.this, "Nuevo correo electrónico es requerido",
                                                    Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Por favor ingresa el nuevo correo electrónico");
                                            editTextNewEmail.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()){
                                            Toast.makeText(ActualizarEmail.this, "Ingresa un  correo electrónico valido",
                                                    Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Por favor ingresa un correo electrónico valido");
                                            editTextNewEmail.requestFocus();
                                        } else if (userOldEmail.matches(userNewEmail)) {
                                            Toast.makeText(ActualizarEmail.this, "El correo electrónico no puede ser el mismo que el pasado",
                                                    Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Por favor ingresa un nuevo correo electrónico");
                                            editTextNewEmail.requestFocus();
                                        } else {
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(ActualizarEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()) {
                    //firebaseUser.sendEmailVerification();
                    Toast.makeText(ActualizarEmail.this, "El correo electrónico ha sido actualizado", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ActualizarEmail.this, PerfilUsuario.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(ActualizarEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
