package com.example.ffes.feeling.api;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.SupportActivity;

import com.example.ffes.feeling.feelview.FeelPictureAdapter;
import com.example.ffes.feeling.feelview.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


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

    public void getAlbum(final GetCallBack<List<Item>> callBack){
        DatabaseReference ref=firebaseDatabase.getReference();
        final StorageReference sref=firebaseStorage.getReference();
        ref.child("account/K8KV6tNDu1X9z4RfjC7WrjgfjOo1/feeling/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final long total=dataSnapshot.getChildrenCount();
                final long[] count = {0};
                Timber.d(total+" "+count[0]);
                final List<Item> items=new ArrayList<Item>();
                for(DataSnapshot child:dataSnapshot.getChildren()){
                    final Item item=new Item();
                    item.setId(child.getKey());
                    Timber.d(child.getKey());
                    sref.child("account/K8KV6tNDu1X9z4RfjC7WrjgfjOo1/photo/"+child.getKey()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setImage(uri);
                            items.add(item);
                            count[0]++;
                            Timber.d(count[0]+"");
                            if(count[0]==total){
                                callBack.onSuccess(items);
                                Timber.d("success");
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
