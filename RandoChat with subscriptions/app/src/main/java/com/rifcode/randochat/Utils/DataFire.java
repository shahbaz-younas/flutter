package com.rifcode.randochat.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataFire {

    private String userID;
    private FirebaseUser user;
    private DatabaseReference dbRefUsers,dbref,dbPremium;
    private FirebaseAuth mAuth;


    public DataFire() {

    }
    public DatabaseReference getDbRefUsers(){
        dbRefUsers = getDbref().child("Users");
        return dbRefUsers;
    }


    public DatabaseReference getDbref(){
        dbref =  FirebaseDatabase.getInstance().getReference();
        return dbref;
    }

    public FirebaseAuth getmAuth(){
        mAuth = FirebaseAuth.getInstance();
        return mAuth;
    }


    public String getUserID(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        return userID;
    }

    public FirebaseUser getCurrentUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        return user;
    }


}
