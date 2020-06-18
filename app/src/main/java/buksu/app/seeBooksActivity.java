package buksu.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class seeBooksActivity extends AppCompatActivity {

    Button cancelChangesButton, saveChangesButton, deleteBookButton;

    ListView listBook;

    TextView bookTitle, author, genre, numPages;

    String getAuthor, getBookTitle, getGenre;

    Integer getNumberPages;

    String bookData[];

    int[] getId_book;

    Book book = new Book();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_books);

        listBook = findViewById(R.id.listBook);

        bookTitle = findViewById(R.id.textViewBookTitle);
        author = findViewById(R.id.textViewAuthor);
        genre = findViewById(R.id.textViewGenre);
        numPages = findViewById(R.id.textViewNumPages);

        book.setUser_id(getIntent().getIntExtra("id_user", 0));

        cancelChangesButton = findViewById(R.id.cancelChangesButton);
        saveChangesButton = findViewById(R.id.saveChangesButton);
        deleteBookButton = findViewById(R.id.deleteBookButton);

        getBookData("https://buksuapp.000webhostapp.com/getBookData.php?user_id=" + book.getUser_id());

        cancelChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), profileActivity.class);
                startActivity(intent);
                finish();

            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                book.setAuthor(author.getText().toString());
                book.setBookTitle(bookTitle.getText().toString());
                book.setGenre(genre.getText().toString());
                if (numPages.getText().toString().isEmpty()) {
                    book.setNumberPages(0);
                } else {
                    book.setNumberPages(Integer.parseInt(numPages.getText().toString()));
                }

                if (author.getText().toString().equals("")
                        || bookTitle.getText().toString().equals("")
                        || genre.getText().toString().equals("")
                        || book.getNumberPages() == 0) {

                    Toast.makeText(getApplicationContext(), "No se permiten campos vacíos.", Toast.LENGTH_SHORT).show();

                } else {

                    saveBookData("https://buksuapp.000webhostapp.com/saveBookData.php");

                }
            }
        });

        deleteBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteBook("https://buksuapp.000webhostapp.com/deleteBook.php");

            }
        });

    }

    public void getBookData(String URL) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;

                //Ponemos el tamaño del array de "getId_book" y de "bookData".
                getId_book = new int[response.length()];
                bookData = new String[response.length()];

                for (int i = 0; i < response.length(); i++) {

                    try {

                        jsonObject = response.getJSONObject(i);

                        //Guardamos el id_book en el array
                        getId_book[i] = jsonObject.getInt("id_book");
                        getAuthor = jsonObject.getString("author");
                        getBookTitle = jsonObject.getString("bookTitle");
                        getGenre = jsonObject.getString("genre");
                        getNumberPages = jsonObject.getInt("numberPages");

                        bookData[i] = getBookTitle + ", de " + getAuthor
                                + "\nGénero: " + getGenre + "\nPáginas: " + getNumberPages;

                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

                listBook.setAdapter(new adapterSeeBooks(getApplicationContext(), bookData));

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;

                if (networkResponse != null) {

                    Toast.makeText(getApplicationContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getApplicationContext(), "No tienes ningún libro inscrito. ¡Pon alguno!", Toast.LENGTH_SHORT).show();

                }

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    public void saveBookData(String URL) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            //Si el registro es correcto, se ejecuta este método.
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), "Libro guardado correctamente.", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());

            }
        }, new Response.ErrorListener() {

            //Si hay algún problema, se ejecuta este método.
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {

            //Se recogen los datos.
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", String.valueOf(book.getUser_id()));
                params.put("author", book.getAuthor());
                params.put("bookTitle", book.getBookTitle());
                params.put("genre", book.getGenre());
                params.put("numberPages", String.valueOf(book.getNumberPages()));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void deleteBook(final String URL) {

        Toast.makeText(getApplicationContext(), "Elige el libro que quieras borrar.", Toast.LENGTH_SHORT).show();

        listBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(seeBooksActivity.this);
                builder.setMessage("¿Quieres eliminar el libro seleccionado?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                            //Si el registro es correcto, se ejecuta este método.
                            @Override
                            public void onResponse(String response) {

                                Toast.makeText(getApplicationContext(), "Libro eliminado correctamente.", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());

                            }
                        }, new Response.ErrorListener() {

                            //Si hay algún problema, se ejecuta este método.
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }) {

                            //Se recogen los datos.
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();

                                //la posición "position" coincide con la posición del "id_book".
                                params.put("id_book", String.valueOf(getId_book[position]));

                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);

                    }
                });

                builder.setNegativeButton("No", null);
                builder.create();
                builder.show();

            }
        });

    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), profileActivity.class);
        startActivity(intent);
        finish();

    }

}

