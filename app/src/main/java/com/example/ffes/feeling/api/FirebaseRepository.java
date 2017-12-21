package com.example.ffes.feeling.api;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.SupportActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


/**
 * Created by Ffes on 2017/12/21.
 */

public class FirebaseRepository implements UploadFeeling{

    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    FirebaseAuth firebaseAuth;

    public FirebaseRepository(FirebaseDatabase firebaseDatabase, FirebaseStorage firebaseStorage, FirebaseAuth firebaseAuth){
        this.firebaseDatabase=firebaseDatabase;
        this.firebaseStorage=firebaseStorage;
        this.firebaseAuth=firebaseAuth;
    }


    private void uploadPhoto(String id, @NonNull byte[] image, final UploadCallBack callBack) {
        StorageReference ref=firebaseStorage.getReference();
        ref.child("account/"+"K8KV6tNDu1X9z4RfjC7WrjgfjOo1/photo/"+id+".jpg").putBytes(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        callBack.onSuccess("Upload Feel Succeed");
                    }
                })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callBack.onSuccess("Upload Feel Fail");
                }
            });
    }

    private void uploadFeel(String id, @NonNull Feel feel, final UploadCallBack callBack) {
        DatabaseReference ref=firebaseDatabase.getReference();
        ref.child("account/K8KV6tNDu1X9z4RfjC7WrjgfjOo1/feeling/"+id+"/feel/").setValue(feel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    callBack.onSuccess("Upload Feel Succeed");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callBack.onFail("Upload Feel Fail");
                }
            });
    }

    @Override
    public void uploadFeeling(@NonNull Feel feel, @NonNull byte[] image, UploadCallBack callBack) {
        String id=firebaseDatabase.getReference().push().getKey();
        uploadPhoto(id, image, callBack);

        uploadFeel(id,feel,callBack);

    }
}
