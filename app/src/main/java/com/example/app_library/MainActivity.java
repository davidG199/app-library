package com.example.app_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText idHolder, fullname, email, password,phone;
    Spinner role;
    ImageButton ibSave, ibSearch, ibEdit, ibDelete, ibList;
    TextView tvMessage;

    //arreglo para el spinner de los generos de los libros
    String[] generos = { "Accion", "Ciencia fición", "Comedia", "Romance"};
    //Instanciar la clase de FirebaseFirestore para esta clase MainActivity
    FirebaseFirestore db =  FirebaseFirestore.getInstance();
    //Definision de variable idAutomatic para utilizarla en el borrado y actualizacion del documento
    String idAutomatic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        idHolder = findViewById(R.id.etidHolder);
        fullname = findViewById(R.id.etfullName);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        phone = findViewById(R.id.etPhone);
        role = findViewById(R.id.spRole);
        tvMessage = findViewById(R.id.tvMessage);
        ibSave = findViewById(R.id.ibSave);
        ibSearch = findViewById(R.id.ibSearch);
        ibEdit = findViewById(R.id.ibEdit);
        ibDelete = findViewById(R.id.ibDelete);
        ibList = findViewById(R.id.ibList);
        // Llenar el spinner con los valores: Administrador y Usuario
        ArrayAdapter adpRoles = new ArrayAdapter(this,android.R.layout.simple_list_item_checked,roles);
        role.setAdapter(adpRoles);
        //Eventos de los botones
        ibSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                if (!idHolder.getText().toString().isEmpty()){
                    //Buscar el idHolder
                    db.collection("user")
                            .whereEqualTo("idholder", idHolder.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        if(!task.getResult().isEmpty()){
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                idAutomatic = document.getId();
                                                fullname.setText(document.getString("fullname"));
                                                email.setText(document.getString("email"));
                                                phone.setText(document.getString("phone"));
                                            }
                                        }
                                        else{
                                            tvMessage.setTextColor(Color.RED);
                                            tvMessage.setText("Identificacion de usuario no existe...");
                                        }
                                    }
                                }
                            });

                }
                else{
                    tvMessage.setTextColor(Color.RED);
                    tvMessage.setText("Debe ingresar el id del usuario que desea buscar");
                }
            }
        });
        ibSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String mIdHolder = idHolder.getText().toString();
                String mFullName = fullname.getText().toString();
                String mEmail = email.getText().toString();
                String mPassword = password.getText().toString();
                String mPhone = phone.getText().toString();
                if (checkData(mIdHolder,mFullName,mEmail,mPassword,mPhone)){
                    //Crear objeto con la info del documento y sus campos
                    Map<String, Object> oUser =  new HashMap<>();
                    oUser.put("idholder", mIdHolder);
                    oUser.put("fullname", mFullName);
                    oUser.put("email", mEmail);
                    oUser.put("password", mPassword);
                    oUser.put("phone", mPhone);
                    if (role.getSelectedItem().equals("Administrador")){
                        oUser.put("role", 1);
                    }else {
                        oUser.put("role", 0);
                    }
                    // Guardar el documento (registro) en la coleccion (tabla) user
                    db.collection("user")
                            .add(oUser)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    tvMessage.setTextColor(Color.GREEN);
                                    tvMessage.setText("Usuario agregado correctamente");
                                    idHolder.setText("");
                                    fullname.setText("");
                                    email.setText("");
                                    password.setText("");
                                    phone.setText("");
                                    idHolder.requestFocus();
                                }
                            });


                }
                else{
                    tvMessage.setTextColor(Color.RED);
                    tvMessage.setText("Debe ingresar todos los datos...");
                }


            }
        });
    }

    private boolean checkData(String mIdHolder, String mFullName, String mEmail, String mPassword, String mPhone) {
        return (!mIdHolder.isEmpty() && !mFullName.isEmpty() && !mEmail.isEmpty() && !mPassword.isEmpty() && !mPhone.isEmpty());
    }
}