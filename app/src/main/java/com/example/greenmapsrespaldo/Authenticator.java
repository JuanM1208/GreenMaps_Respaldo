package com.example.greenmapsrespaldo;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Authenticator {
    FirebaseAuth mAuth;

    public Authenticator(){
        mAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> registrar(String email, String password){

        return mAuth.createUserWithEmailAndPassword(email, password);

    }

    public void Logout(){
        mAuth.signOut();
    }


}
