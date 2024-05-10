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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText bookTitle, autorBook,year,numberPages;
    Spinner genero;
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
        bookTitle = findViewById(R.id.etBookTitle);
        autorBook = findViewById(R.id.etAuthor);
        year = findViewById(R.id.etYear);
        numberPages = findViewById(R.id.etPages);
        genero = findViewById(R.id.etGenre);
        tvMessage = findViewById(R.id.tvMessage);
        ibSave = findViewById(R.id.ibSaveBook);
        ibSearch = findViewById(R.id.ibSearchBook);
        ibEdit = findViewById(R.id.ibEditBook);
        ibDelete = findViewById(R.id.ibDeleteBook);
        ibList = findViewById(R.id.ibListBooks);
        // Llenar el spinner con los valores: Administrador y Usuario
        ArrayAdapter adpGeneros = new ArrayAdapter(this,android.R.layout.simple_list_item_checked,generos);
        genero.setAdapter(adpGeneros);
        //Eventos de los botones

        //evento para buscar algun libro
        ibSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                if (!bookTitle.getText().toString().isEmpty()){
                    //Buscar el titulo del libro
                    db.collection("book")
                            .whereEqualTo("bookTitle", bookTitle.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        if(!task.getResult().isEmpty()){
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                idAutomatic = document.getId();
                                                autorBook.setText(document.getString("autorBook"));
                                                year.setText(document.getString("year"));
                                                numberPages.setText(document.getString("numberPages"));
                                            }
                                        }
                                        else{
                                            tvMessage.setTextColor(Color.RED);
                                            tvMessage.setText("Identificacion de libro no existe...");
                                        }
                                    }
                                }
                            });

                }
                else{
                    tvMessage.setTextColor(Color.RED);
                    tvMessage.setText("Debe ingresar el titulo del libro que desea buscar");
                }
            }
        });

        //evento para guardar los libros
        ibSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String mBookTitle = bookTitle.getText().toString();
                String mAutorBook = autorBook.getText().toString();
                String mYear = year.getText().toString();
                String mNumberPages = numberPages.getText().toString();
                if (checkData(mBookTitle,mAutorBook,mYear,mNumberPages)){
                    //Crear objeto con la info del documento y sus campos
                    Map<String, Object> oBook =  new HashMap<>();
                    oBook.put("titleBook", mBookTitle);
                    oBook.put("fullname", mAutorBook);
                    oBook.put("Year", mYear);
                    oBook.put("numberPages", mNumberPages);
                    if (genero.getSelectedItem().equals("accion")){
                        oBook.put("genero", 1);
                    }else {
                        oBook.put("genero", 0);
                    }
                    // Guardar el documento (registro) en la coleccion (tabla) book
                    db.collection("book")
                            .add(oBook)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    tvMessage.setTextColor(Color.GREEN);
                                    tvMessage.setText("libro agregado correctamente");
                                    bookTitle.setText("");
                                    autorBook.setText("");
                                    year.setText("");
                                    numberPages.setText("");
                                    bookTitle.requestFocus();
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

    private boolean checkData(String mBookTitle, String mAutorBook, String mYear, String mNumberPages) {
        return (!mBookTitle.isEmpty() && !mAutorBook.isEmpty() && !mYear.isEmpty() && !mNumberPages.isEmpty());
    }
}