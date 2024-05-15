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
        // Evento para buscar libros por el título
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

                                            // Muestra el género del libro
                                            int genreIndex = document.getLong("genero").intValue();
                                            genero.setSelection(genreIndex);

                                            tvMessage.setTextColor(Color.GREEN);
                                            tvMessage.setTypeface(null, Typeface.BOLD);
                                            tvMessage.setTextSize(18);
                                            tvMessage.setText("El libro ha sido encontrado.");
                                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvMessage.getLayoutParams();
                                            params.setMargins(0, 50, 0, -65);
                                            tvMessage.setLayoutParams(params);

                                            return;
                                        }
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

        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mBookTitle = bookTitle.getText().toString();
                final String mAutorBook = autorBook.getText().toString();
                final String mYear = year.getText().toString();
                final String mNumberPages = numberPages.getText().toString();
                if (checkData(mBookTitle, mAutorBook, mYear, mNumberPages)) {
                    // Verificar si ya existe el libro en la base de datos
                    db.collection("book")
                            .whereEqualTo("titleBook", mBookTitle)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                            // Si no existe el título, guardar el libro
                                            Map<String, Object> oBook =  new HashMap<>();
                                            oBook.put("titleBook", mBookTitle);
                                            oBook.put("author", mAutorBook);
                                            oBook.put("Year", mYear);
                                            oBook.put("numberPages", mNumberPages);
                                            if (genero.getSelectedItem().equals("accion")) {
                                                oBook.put("genero", 0);
                                            } else if(genero.getSelectedItem().equals("Ciencia ficción")){
                                                oBook.put("genero", 1);
                                            }else if(genero.getSelectedItem().equals("Comedia")){
                                                oBook.put("genero", 2);
                                            }else{
                                                oBook.put("genero", 3);
                                            }
                                            // Guardar el libro
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
                                                            tvMessage.setText("Libro agregado correctamente");
                                                            bookTitle.setText("");
                                                            autorBook.setText("");
                                                            year.setText("");
                                                            numberPages.setText("");
                                                            bookTitle.requestFocus();
                                                        }
                                                    });
                                        } else {
                                            // Si hay documentos con el mismo título, mostrar un mensaje de error
                                            tvMessage.setTextColor(Color.RED);
                                            tvMessage.setText("El título del libro ya existe en la base de datos.");
                                            tvMessage.setTextColor(Color.RED);
                                            tvMessage.setTypeface(null, Typeface.BOLD);
                                            tvMessage.setTextSize(18);
                                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvMessage.getLayoutParams();
                                            params.setMargins(0, 50, 0, -65);
                                            tvMessage.setLayoutParams(params);
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
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
//Ultimos cambios