package com.example.app_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp; // Importa la clase FirebaseApp
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
    String[] generos = { "Accion", "Ciencia ficción", "Comedia", "Romance"};
    //Instanciar la clase de FirebaseFirestore para esta clase MainActivity
    FirebaseFirestore db;

    //Definision de variable idAutomatic para utilizarla en el borrado y actualizacion del documento
    String idAutomatic;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase
        FirebaseApp.initializeApp(this);

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

        // Instanciar la clase de FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Llenar el spinner con los valores: Administrador y Usuario
        ArrayAdapter<String> adpGeneros = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, generos);
        genero.setAdapter(adpGeneros);

        //Eventos de los botones
        // Evento para buscar un libro por su título
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTitle = bookTitle.getText().toString();
                if (!searchTitle.isEmpty()) {
                    db.collection("book")
                            .whereEqualTo("titleBook", searchTitle)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            idAutomatic = document.getId();
                                            autorBook.setText(document.getString("author"));
                                            year.setText(document.getString("Year"));
                                            numberPages.setText(document.getString("numberPages"));
                                            // Se encontró el libro, no se necesita seguir buscando
                                            return;
                                        }
                                        // Si no se encontró ningún libro, mostrar un mensaje
                                        tvMessage.setTextColor(Color.RED);
                                        tvMessage.setTypeface(null, Typeface.BOLD);
                                        tvMessage.setTextSize(18);
                                        tvMessage.setText("El libro no se encontró en la base de datos.");
                                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvMessage.getLayoutParams();
                                        params.setMargins(0, 50, 0, -65);
                                        tvMessage.setLayoutParams(params);
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                } else {
                    tvMessage.setTextColor(Color.RED);
                    tvMessage.setText("Debe ingresar el título del libro.");
                }
            }
        });

        //evento para guardar los libros
        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mBookTitle = bookTitle.getText().toString();
                String mAutorBook = autorBook.getText().toString();
                String mYear = year.getText().toString();
                String mNumberPages = numberPages.getText().toString();
                if (checkData(mBookTitle,mAutorBook,mYear,mNumberPages)) {
                    //Crear objeto con la info del documento y sus campos
                    Map<String, Object> oBook =  new HashMap<>();
                    oBook.put("titleBook", mBookTitle);
                    oBook.put("author", mAutorBook);
                    oBook.put("Year", mYear);
                    oBook.put("numberPages", mNumberPages);
                    if (genero.getSelectedItem().equals("accion")) {
                        oBook.put("genero", 1);
                    } else {
                        oBook.put("genero", 0);
                    }
                    // Guardar el documento (registro) en la coleccion (tabla) book
                    db.collection("book")
                            .add(oBook)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    tvMessage.setTextColor(Color.WHITE);
                                    tvMessage.setTypeface(null, Typeface.BOLD);
                                    tvMessage.setTextSize(24);
                                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvMessage.getLayoutParams();
                                    params.setMargins(0, 40, 0, -65);
                                    tvMessage.setLayoutParams(params);
                                    tvMessage.setText("libro agregado correctamente");
                                    bookTitle.setText("");
                                    autorBook.setText("");
                                    year.setText("");
                                    numberPages.setText("");
                                    bookTitle.requestFocus();
                                }
                            });
                } else {
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