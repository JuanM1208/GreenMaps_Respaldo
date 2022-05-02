package com.example.greenmapsrespaldo;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserAuthenticator {

    DatabaseReference databaseReference;

    public UserAuthenticator(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Usuarios");

    }

    public Task<Void> create(ReadWriteUserDetails readWriteUserDetails){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        return databaseReference.child(firebaseUser.getUid()).setValue(readWriteUserDetails);

    }

}
