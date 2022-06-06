package com.example.greenmapsrespaldo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActualizarPerfil extends AppCompatActivity {
    private EditText editTextUpdateName, editTextUpdateMobile;
    private String textFullName, textMobile, email;
    private FirebaseAuth authProfile;
    private TextView headerNombre, headerUsuario;
    private CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actualizar_perfil);

        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateMobile = findViewById(R.id.editText_update_profile_mobile);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        showProfile(firebaseUser);

        Button buttonUploadProfilePic = findViewById(R.id.button_upload_profile_pic);
        buttonUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActualizarPerfil.this, SubirFotoPerfil.class);
                startActivity(intent);
                finish();
            }
        });

        Button buttonUpdateEmail = findViewById(R.id.button_profile_update_email);
        buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActualizarPerfil.this, ActualizarEmail.class);
                startActivity(intent);
                finish();
            }
        });

        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textFullName = editTextUpdateName.getText().toString();
                textMobile = editTextUpdateMobile.getText().toString();
                String mobileRegex = "[0-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);

                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(ActualizarPerfil.this, "Por favor ingresa tu nombre completo", Toast.LENGTH_LONG).show();
                    editTextUpdateName.setError("El nombre de usuario es requerido");
                    editTextUpdateName.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(ActualizarPerfil.this, "Por favor ingresa tu numero telefónico", Toast.LENGTH_LONG).show();
                    editTextUpdateMobile.setError("El numero telefónico es requerido");
                    editTextUpdateMobile.requestFocus();
                } else if (textMobile.length() != 10) {
                    Toast.makeText(ActualizarPerfil.this, "Por favor re-ingresa tu numero telefónico", Toast.LENGTH_LONG).show();
                    editTextUpdateMobile.setError("El numero telefónico debe tener 10 dígitos");
                    editTextUpdateMobile.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(ActualizarPerfil.this, "Por favor re-ingresa tu numero telefónico", Toast.LENGTH_LONG).show();
                    editTextUpdateMobile.setError("El numero telefónico no es valido");
                    editTextUpdateMobile.requestFocus();
                } else{
                    updateProfile(firebaseUser);
                }
            }
        });

        MaterialToolbar toolbar = findViewById(R.id.topAppbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_refreshProfile);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        View view = navigationView.getHeaderView(0);
        circleImageView = view.findViewById(R.id.profilepic);
        headerNombre = view.findViewById(R.id.nombre_menu);
        headerUsuario = view.findViewById(R.id.usuario_menu);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActualizarPerfil.this, PerfilUsuario.class);
                startActivity(intent);
            }
        });

        if(firebaseUser == null) {
            Toast.makeText(ActualizarPerfil.this, "Algo ocurrió mal, detalles del usuario no disponibles en este momento", Toast.LENGTH_LONG).show();

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
                    Intent intent = new Intent (ActualizarPerfil.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if(id == R.id.menu_refresh) {
                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0, 0);
                } else if (id == R.id.menu_update_profile) {
                    Intent intent = new Intent (ActualizarPerfil.this, ActualizarPerfil.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_update_email) {
                    Intent intent = new Intent (ActualizarPerfil.this, ActualizarEmail.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_change_password) {
                    Intent intent = new Intent (ActualizarPerfil.this, CambiarPassword.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_delete_profile) {
                    Intent intent = new Intent (ActualizarPerfil.this, EliminarPerfil.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_logout) {
                    authProfile.signOut();
                    Toast.makeText(ActualizarPerfil.this, "Desconectado", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent (ActualizarPerfil.this, Bienvenida.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ActualizarPerfil.this, "¡Algo ocurrió mal!", Toast.LENGTH_LONG).show();
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

                    Picasso.with(ActualizarPerfil.this).load(uri).into(circleImageView);
                } else {
                    Toast.makeText(ActualizarPerfil.this, "Algo ocurrió mal", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActualizarPerfil.this, "Algo ocurrió mal", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        textFullName = editTextUpdateName.getText().toString();
        textMobile = editTextUpdateMobile.getText().toString();

        ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textMobile);

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        String userID = firebaseUser.getUid();

        referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().
                            setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileUpdates);

                    Toast.makeText(ActualizarPerfil.this, "¡Actualización completada!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(ActualizarPerfil.this, PerfilUsuario.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(ActualizarPerfil.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null) {
                    textFullName = readUserDetails.getUsuario();
                    textMobile = readUserDetails.getPhone();

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateMobile.setText(textMobile);

                    Uri uri = firebaseUser.getPhotoUrl();
                } else {
                    Toast.makeText(ActualizarPerfil.this, "¡Algo ocurrio mal!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActualizarPerfil.this, "¡Algo ocurrio mal!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
