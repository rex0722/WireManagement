package com.study.application.leanCloud;

import android.support.annotation.NonNull;
import com.study.application.speech.SpeechMessage;

import java.util.ArrayList;

public class SpeechDataReader {

    /* Disable Speech function temporarily */
    /*
    public static ArrayList<SpeechMessage> messagesArrayList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference = database.getReference("Input");

    public void loginDataLoad(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    messagesArrayList.add(new SpeechMessage(
                                ds.child("classification").getValue().toString(),
                                ds.child("content").getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    */
}
