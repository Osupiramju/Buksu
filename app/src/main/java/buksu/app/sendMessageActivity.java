package buksu.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
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

public class sendMessageActivity extends AppCompatActivity {

    Button cancelChangesButton, sendMessageButton;

    ListView profileListView;

    TextView warning, profileLabel, message;

    EditText sendMsgEditText;

    String bookData[];

    Book book = new Book();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        String nick = getIntent().getStringExtra("nick");
        final int id_user = getIntent().getIntExtra("id_user", 0);
        final int ownId = getIntent().getIntExtra("ownId", 0);

        cancelChangesButton = findViewById(R.id.cancelChangesButton);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        sendMsgEditText = findViewById(R.id.sendMsgEditText);

        profileListView = findViewById(R.id.profileListView);

        warning = findViewById(R.id.warning);
        warning.setText("¡Aviso! Estás a punto de enviar un mensaje directo a un usuario. " +
                "Por favor, sé respetuoso y educado.");

        profileLabel = findViewById(R.id.profileLabel);
        profileLabel.setText("Libros que posee el usuario " + nick);

        message = findViewById(R.id.message);
        message.setText("Mensaje:");

        getBookData("https://buksuapp.000webhostapp.com/getBookData.php?user_id=" + id_user);

        cancelChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), messagesActivity.class);
                startActivity(intent);
                finish();

            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage("https://buksuapp.000webhostapp.com/automaticMessageSent.php", ownId, id_user);

            }
        });

    }

    //Método para recoger los datos de los libros del usuario.
    private void getBookData(String URL) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;

                bookData = new String[response.length()];

                for (int i = 0; i < response.length(); i++) {

                    try {

                        jsonObject = response.getJSONObject(i);

                        book.setAuthor(jsonObject.getString("author"));
                        book.setBookTitle(jsonObject.getString("bookTitle"));
                        book.setGenre(jsonObject.getString("genre"));
                        book.setNumberPages(jsonObject.getInt("numberPages"));

                        bookData[i] = book.getBookTitle() + ", de " + book.getAuthor()
                                + "\nGénero: " + book.getGenre() + "\nPáginas: " + book.getNumberPages();

                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

                profileListView.setAdapter(new adapterSeeBooks(getApplicationContext(), bookData));

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    //Método para enviar el mensaje deseado.
    private void sendMessage(String URL, final int ownId, final int id_user) {

        final String sentMessage = sendMsgEditText.getText().toString().trim();

        if (!sentMessage.isEmpty()) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    sendMsgEditText.getText().clear();
                    Toast.makeText(getApplicationContext(), "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

                }
            }) {

                //Se recogen los datos.
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("id_user", String.valueOf(ownId));
                    params.put("user_id", String.valueOf(id_user));
                    params.put("message", sentMessage);

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        } else {

            sendMsgEditText.getText().clear();
            Toast.makeText(getApplicationContext(), "¡No se puede enviar un mensaje en blanco!", Toast.LENGTH_SHORT).show();

        }

    }

    //Si el usuario pulsa el botón del móvil para ir "atrás", se activa esta función.
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), messagesActivity.class);
        startActivity(intent);
        finish();

    }

}
