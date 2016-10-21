package com.example.ak.firebaseauthdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AccountActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button mLogOutBtn;
    private Button mButtonSave;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference databaseReference;
    private EditText editTextName;
    private EditText editTextAge;
    private TextView textView;
    private FirebaseUser user;
    private ImageView mImageView;
    private UserInformation userInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mLogOutBtn = (Button) findViewById(R.id.buttonLogOut);
        mButtonSave = (Button) findViewById(R.id.buttonSave);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAge = (EditText) findViewById(R.id.editTextAge);
        textView = (TextView) findViewById(R.id.textView);
        mImageView = (ImageView) findViewById(R.id.imageView);
        userInformation = new UserInformation();
        user = mAuth.getCurrentUser();


            databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInformation user = dataSnapshot.getValue(UserInformation.class);
                    if(user != null){
                        if(user.getName()!= "default") {
                            String string = "Name: " + user.getName() + " Age: " + user.getAge();
                            setImage(user);
                            textView.setText(string);
                        }
                        else{

                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("Failed: ");
                }
            });



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity( new Intent(AccountActivity.this,MainActivity.class));
                }
            }
        };
        mLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
                databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            UserInformation user = dataSnapshot.getValue(UserInformation.class);

                            String string = "Name: "+user.getName()+" Age: "+user.getAge();
                            setImage(user);
                            textView.setText(string);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("Failed: ");
                    }
                });
            }
        });
    }
    private void saveUserInformation(){
        String name = editTextName.getText().toString().trim();
        int age = Integer.parseInt(editTextAge.getText().toString().trim());

        userInformation.setAge(age);
        userInformation.setName(name);

        databaseReference.child(user.getUid()).setValue(userInformation);
        Toast.makeText(this,"Saved",Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_photo, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_photo:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            setEncodeBitmap(imageBitmap);
        }
    }
    public void setEncodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        userInformation.setImageUrl(imageEncoded);
    }
    public void setImage(UserInformation user){
        if(user.getImageUrl() != null){
            byte[] decodedByteArray = Base64.decode(user.getImageUrl(), Base64.DEFAULT);
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            mImageView.setImageBitmap(imageBitmap);
        }
    }
}
