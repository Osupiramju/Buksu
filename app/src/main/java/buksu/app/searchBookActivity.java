package buksu.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class searchBookActivity extends AppCompatActivity {

    Button goToMessagesButton, signOutButton, goToProfileButton, searchBookButton;

    Spinner numberPagesList;

    TextView city, town, author, bookTitle, genre;

    ListView listBook;

    String[] bookData;

    int[] getId_user, rating, getId_book;

    int spinnerSelection, getNumberPages;

    String getCity, getTown, getAuthor, getBookTitle, getGenre;

    Book book = new Book();
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);

        city = findViewById(R.id.textCity);
        town = findViewById(R.id.textTown);
        author = findViewById(R.id.textAuthor);
        bookTitle = findViewById(R.id.textBookTitle);
        genre = findViewById(R.id.textGenre);

        listBook = findViewById(R.id.listBook);

        SharedPreferences preferences = getSharedPreferences("logInPreferences", Context.MODE_PRIVATE);
        user.setEmail(preferences.getString("email", "Error."));

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(searchBookActivity.this);

        if (googleSignInAccount != null) {

            user.setEmail(googleSignInAccount.getEmail());

        }

        String[] list = {"Menos de 300", "300-800", "Más de 800", "Indiferente"};
        ArrayAdapter spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);

        numberPagesList = findViewById(R.id.numberPagesList);
        numberPagesList.setAdapter(spinnerAdapter);
        numberPagesList.setSelection(3);

        goToMessagesButton = findViewById(R.id.goToMessagesButton);
        signOutButton = findViewById(R.id.signOutButton);
        goToProfileButton = findViewById(R.id.goToProfileButton);
        searchBookButton = findViewById(R.id.searchBookButton);

        searchBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                book.setAuthor(author.getText().toString());
                book.setBookTitle(bookTitle.getText().toString());
                book.setGenre(genre.getText().toString());
                user.setCity(city.getText().toString());
                user.setTown(town.getText().toString());
                selectedSpinnerItem();

                searchBookValidation("https://buksuapp.000webhostapp.com/searchBookValidation.php?email=" + user.getEmail());

            }
        });

        goToProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), profileActivity.class);
                startActivity(intent);

            }
        });

        goToMessagesButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), messagesActivity.class);
                startActivity(intent);

            }
        }));

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOut();

            }
        });
    }

    private void searchBookValidation(String URL) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;

                try {

                    jsonObject = response.getJSONObject(0);

                    if (jsonObject.getString("city").equals("") || jsonObject.getString("town").equals("")) {

                        Toast.makeText(getApplicationContext(), "Por favor, completa el perfil antes de hacer una búsqueda.", Toast.LENGTH_SHORT).show();

                    } else {

                        searchBookData("https://buksuapp.000webhostapp.com/searchBook.php?author=" + book.getAuthor()
                                + "&bookTitle=" + book.getBookTitle()
                                + "&genre=" + book.getGenre()
                                + "&city=" + user.getCity()
                                + "&town=" + user.getTown()
                                + "&numberPages=" + spinnerSelection + "&email="
                                + user.getEmail());

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                selectedBook();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;

                if (networkResponse != null) {

                    Toast.makeText(getApplicationContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();


                } else {

                    Toast.makeText(getApplicationContext(), "Tienes que tener al menos un libro antes de hacer una búsqueda.", Toast.LENGTH_SHORT).show();

                }

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    private void searchBookData(String URL) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                Toast.makeText(getApplicationContext(), "Selecciona el libro con el que desees hacer el intercambio.", Toast.LENGTH_SHORT).show();

                JSONObject jsonObject = null;

                bookData = new String[response.length()];
                getId_user = new int[response.length()];
                getId_book = new int[response.length()];
                rating = new int[response.length()];

                for (int i = 0; i < response.length(); i++) {

                    try {

                        jsonObject = response.getJSONObject(i);

                        getId_book[i] = jsonObject.getInt("id_book");
                        getId_user[i] = jsonObject.getInt("id_user");

                        getAuthor = jsonObject.getString("author");
                        getBookTitle = jsonObject.getString("bookTitle");
                        getGenre = jsonObject.getString("genre");
                        getCity = jsonObject.getString("city");
                        getTown = jsonObject.getString("town");
                        getNumberPages = jsonObject.getInt("numberPages");

                        bookData[i] = getBookTitle + ", de " + getAuthor + "\nGénero: " + getGenre
                                + "\nProvincia: " + getCity + "\nLocalidad: " + getTown + "\nPáginas: " + getNumberPages;

                        searchBookValoration("https://buksuapp.000webhostapp.com/valoration.php?user_id=" + getId_user[i], i);

                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

                selectedBook();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;

                if (networkResponse != null) {

                    Toast.makeText(getApplicationContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();


                } else {

                    bookData = new String[0];
                    rating = new int[0];
                    listBook.setAdapter(new adapterSearchBook(getApplicationContext(), bookData, rating));
                    Toast.makeText(getApplicationContext(), "No hay libros con los criterios de búsqueda seleccionados.", Toast.LENGTH_SHORT).show();

                }

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    private void searchBookValoration(String URL, final int x) {

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;

                for (int i = 0; i < response.length(); i++) {

                    try {

                        jsonObject = response.getJSONObject(i);

                        rating[x] = jsonObject.getInt("averageValoration");

                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

                listBook.setAdapter(new adapterSearchBook(getApplicationContext(), bookData, rating));

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;

                if (networkResponse != null) {

                    Toast.makeText(getApplicationContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();

                } else {

                    rating[x] = 0;

                }

            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

        /*Está doble a posta. Si solo hay uno, las valoraciones salen en blanco hasta que haces scroll.
        No hay ninguna respuesta que haya podido encontrar para este error salvo ésta, de cosecha propia.*/
        listBook.setAdapter(new adapterSearchBook(getApplicationContext(), bookData, rating));

    }

    private void selectedBook() {

        listBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(searchBookActivity.this);
                builder.setMessage("¿Quieres el libro seleccionado?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final int user_id = getId_user[position];
                        final int id_book = getId_book[position];

                        final String[] nick = new String[1];

                        final int[] id_user = new int[1];

                        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest("https://buksuapp.000webhostapp.com/idUserSelectBook.php?email=" + user.getEmail(), new Response.Listener<JSONArray>() {

                            @Override
                            public void onResponse(JSONArray response) {

                                JSONObject jsonObject = null;

                                for (int i = 0; i < response.length(); i++) {

                                    try {

                                        jsonObject = response.getJSONObject(i);

                                        nick[i] = jsonObject.getString("nick");
                                        id_user[i] = jsonObject.getInt("id_user");

                                    } catch (JSONException e) {

                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }

                                }

                                Intent intent = new Intent(getApplicationContext(), selectedBookActivity.class);
                                intent.putExtra("nick", nick[0]);
                                intent.putExtra("id_user", id_user[0]);
                                intent.putExtra("id_book", id_book);
                                intent.putExtra("user_id", user_id);
                                startActivity(intent);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(getApplicationContext(), "Error de conexión2.", Toast.LENGTH_SHORT).show();

                            }

                        });

                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(jsonArrayRequest);

                    }

                }).setNegativeButton("No", null);
                builder.create();
                builder.show();

            }
        });

    }

    //Se guarda la selección realizada en el "Spinner".
    private void selectedSpinnerItem() {

        switch (numberPagesList.getSelectedItem().toString()) {

            case "Menos de 300":
                spinnerSelection = 0;
                break;

            case "300-800":
                spinnerSelection = 1;
                break;

            case "Más de 800":
                spinnerSelection = 2;
                break;

            case "Indiferente":
                spinnerSelection = 3;
                break;

        }

    }

    private void signOut() {

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(searchBookActivity.this);

        if (googleSignInAccount != null) {

            GoogleSignInClient mGoogleSignInClient;
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
            mGoogleSignInClient.signOut().addOnCompleteListener(searchBookActivity.this,
                    //Cierre de sesión con Google.
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Cierre de sesión con Firebase
                            FirebaseAuth.getInstance().signOut();
                            Intent setupIntent = new Intent(getBaseContext(), MainActivity.class);
                            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(setupIntent);
                            finish();
                        }
                    });

        } else {


            //Se recupera la preferencia guardada.
            SharedPreferences preferences = getSharedPreferences("logInPreferences", Context.MODE_PRIVATE);

            //Limpiamos dicha preferencia.
            preferences.edit().clear().apply();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(intent);

            finish();
        }
    }

    //Si el usuario pulsa el botón del móvil para ir "atrás", se activa esta función.
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), accessActivity.class);
        startActivity(intent);
        finish();

    }

}
