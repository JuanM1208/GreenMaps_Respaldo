package com.example.greenmapsrespaldo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubirFotoPerfil extends AppCompatActivity {
    private ImageView imageViewUploadPic;
    private FirebaseAuth authProfile;
    private TextView headerNombre, headerUsuario;
    private CircleImageView circleImageView;
    private String fullName, email, mobile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subir_foto);

        Button buttonUploadPicChoose = findViewById(R.id.upload_pic_choose_button);
        Button buttonUploadPic = findViewById(R.id.upload_pic_button);
        imageViewUploadPic = findViewById(R.id.imageView_profile_dp);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("Mostrar fotos");

        Uri uri = firebaseUser.getPhotoUrl();

        Picasso.with(SubirFotoPerfil.this).load(uri).into(imageViewUploadPic);

        buttonUploadPicChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        buttonUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadPic();
            }
        });

        MaterialToolbar toolbar = findViewById(R.id.topAppbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_uploadPic);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        View view = navigationView.getHeaderView(0);
        circleImageView = view.findViewById(R.id.profilepic);
        headerNombre = view.findViewById(R.id.nombre_menu);
        headerUsuario = view.findViewById(R.id.usuario_menu);

        if(firebaseUser == null) {
            Toast.makeText(SubirFotoPerfil.this, "Algo ocurrió mal, detalles del usuario no disponibles en este momento", Toast.LENGTH_LONG).show();

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
                    Intent intent = new Intent (SubirFotoPerfil.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if(id == R.id.menu_refresh) {
                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0, 0);
                } else if (id == R.id.menu_update_profile) {
                    Intent intent = new Intent (SubirFotoPerfil.this, ActualizarPerfil.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_update_email) {
                    Intent intent = new Intent (SubirFotoPerfil.this, ActualizarEmail.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_change_password) {
                    Intent intent = new Intent (SubirFotoPerfil.this, CambiarPassword.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_delete_profile) {
                    Intent intent = new Intent (SubirFotoPerfil.this, EliminarPerfil.class);
                    startActivity(intent);
                    finish();
                } else if (id == R.id.menu_logout) {
                    authProfile.signOut();
                    Toast.makeText(SubirFotoPerfil.this, "Desconectado", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent (SubirFotoPerfil.this, Bienvenida.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SubirFotoPerfil.this, "¡Algo ocurrió mal!", Toast.LENGTH_LONG).show();
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

                    Picasso.with(SubirFotoPerfil.this).load(uri).into(circleImageView);
                } else {
                    Toast.makeText(SubirFotoPerfil.this, "Algo ocurrió mal", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SubirFotoPerfil.this, "Algo ocurrió mal", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            imageViewUploadPic.setImageURI(uriImage);
        }
    }

    private void UploadPic() {
        if(uriImage != null) {
            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid() + "."
                    + getFileExtension(uriImage));

            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = authProfile.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);

                        }
                    });
                    Toast.makeText(SubirFotoPerfil.this, "¡Subida exitosa!",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SubirFotoPerfil.this, PerfilUsuario.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SubirFotoPerfil.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(SubirFotoPerfil.this, "Ningun archivo seleccionado",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
