package buksu.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.HashMap;
import java.util.Map;

public class messagesActivity extends AppCompatActivity {

    TextView valorationLabel, starsLabel;

    EditText writeNick;

    RatingBar ratingBar;

    Button goToSearchBookButton, signOutButton, goToProfileButton, sendValorationButton;

    ListView receivedMsgsListView, sentMsgsListView;

    String[] receivedMessages, sentMessages, nickReceived, nickReceived2, nickSent, nickSent2, date;

    int[] idReceived, idSent;

    int id_user, ownId;

    String nickUser;

    User user = new User();
    Message message = new Message();
    Rating rating = new Rating();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        SharedPreferences preferences = getSharedPreferences("logInPreferences", Context.MODE_PRIVATE);
        user.setEmail(preferences.getString("email", "Error."));

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(messagesActivity.this);

        if (googleSignInAccount != null) {

            user.setEmail(googleSignInAccount.getEmail());

        }

        writeNick = findViewById(R.id.writeNick);
        writeNick.setVisibility(View.INVISIBLE);

        valorationLabel = findViewById(R.id.valorationLabel);
        valorationLabel.setVisibility(View.INVISIBLE);

        starsLabel = findViewById(R.id.starsLabel);
        starsLabel.setVisibility(View.INVISIBLE);

        goToSearchBookButton = findViewById(R.id.goToSearchBookButton);
        signOutButton = findViewById(R.id.signOutButton);
        goToProfileButton = findViewById(R.id.goToProfileButton);
        sendValorationButton = findViewById(R.id.sendValorationButton);
        sendValorationButton.setVisibility(View.INVISIBLE);

        receivedMsgsListView = findViewById(R.id.receivedMsgsListView);
        sentMsgsListView = findViewById(R.id.sentMsgsListView);

        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setVisibility(View.INVISIBLE);

        valorationLabel = findViewById(R.id.valorationLabel);
        starsLabel = findViewById(R.id.starsLabel);

        goToSearchBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), searchBookActivity.class);
                startActivity(intent);

            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOut();

            }
        });

        goToProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), profileActivity.class);
                startActivity(intent);

            }
        });

        sendValorationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rating.setValoration(Math.round(ratingBar.getRating()));

                sendValoration("https://buksuapp.000webhostapp.com/sendValoration.php");

            }
        });

        receivedMsgs("https://buksuapp.000webhostapp.com/receivedMessages.php?email=" + user.getEmail());
        sentMsgs("https://buksuapp.000webhostapp.com/sentMessages.php?email=" + user.getEmail());

    }

    //Método para recoger los mensajes recibidos del usuario y mostrarlos.
    private void receivedMsgs(String URL) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;

                receivedMessages = new String[response.length()];
                idReceived = new int[response.length()];
                nickReceived = new String[response.length()];
                nickReceived2 = new String[response.length()];
                date = new String[response.length()];

                for (int i = 0; i < response.length(); i++) {

                    try {

                        jsonObject = response.getJSONObject(i);

                        message.setSentTo(jsonObject.getInt("id_user"));
                        ownId = message.getSentTo();

                        receivedMessages[i] = jsonObject.getString("message");
                        idReceived[i] = jsonObject.getInt("sentBy");
                        date[i] = jsonObject.getString("date");

                        getNick1("https://buksuapp.000webhostapp.com/getNick1.php?sentBy=" + idReceived[i], i, date[i]);

                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

                selectedReceivedMsg();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;

                if (networkResponse != null) {

                    Toast.makeText(getApplicationContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getApplicationContext(), "No hay ningún mensaje recibido.", Toast.LENGTH_SHORT).show();

                }

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    //Método para conseguir el nick del usuario que nos envía el mensaje.
    private void getNick1(String URL, final int x, final String date) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;

                for (int i = 0; i < response.length(); i++) {

                    try {

                        jsonObject = response.getJSONObject(i);

                        nickReceived[x] = "Enviado por " + jsonObject.getString("nick") + " el " + date;
                        nickReceived2[x] = jsonObject.getString("nick");

                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

                if(nickReceived2.length != 0) {

                    valorationLabel.setVisibility(View.VISIBLE);
                    starsLabel.setVisibility(View.VISIBLE);
                    ratingBar.setVisibility(View.VISIBLE);
                    sendValorationButton.setVisibility(View.VISIBLE);
                    writeNick.setVisibility(View.VISIBLE);

                }

                receivedMsgsListView.setAdapter(new adapterMessage(getApplicationContext(), receivedMessages, nickReceived));

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

    //Método para recibir los mensajes enviados por el usuario y mostrarlos.
    private void sentMsgs(String URL) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;

                sentMessages = new String[response.length()];
                idSent = new int[response.length()];
                nickSent = new String[response.length()];
                nickSent2 = new String[response.length()];
                date = new String[response.length()];

                for (int i = 0; i < response.length(); i++) {

                    try {

                        jsonObject = response.getJSONObject(i);

                        message.setSentBy(jsonObject.getInt("id_user"));
                        ownId = message.getSentBy();

                        sentMessages[i] = jsonObject.getString("message");
                        idSent[i] = jsonObject.getInt("sentTo");
                        date[i] = jsonObject.getString("date");

                        getNick2("https://buksuapp.000webhostapp.com/getNick2.php?sentTo=" + idSent[i], i, date[i]);

                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

                selectedSentMsg();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;

                if (networkResponse != null) {

                    Toast.makeText(getApplicationContext(), "Error de conexión.", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getApplicationContext(), "No hay ningún mensaje enviado.", Toast.LENGTH_SHORT).show();

                }

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);


    }
    //Método para conseguir el nick del usuario que recibe el mensaje por parte nuestra.
    private void getNick2(String URL, final int x, final String date) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;

                for (int i = 0; i < response.length(); i++) {

                    try {

                        jsonObject = response.getJSONObject(i);

                        nickSent[x] = "Enviado a " + jsonObject.getString("nick") + " el " + date;
                        nickSent2[x] = jsonObject.getString("nick");

                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

                sentMsgsListView.setAdapter(new adapterMessage(getApplicationContext(), sentMessages, nickSent));

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

    //Método para otorgar una acción a los "items" de la lista de mensajes recibidos cuando se pulsan.
    private void selectedReceivedMsg() {

        receivedMsgsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(messagesActivity.this);
                builder.setMessage("¿Quieres enviar un mensaje a este usuario?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        id_user = idReceived[position];
                        nickUser = nickReceived2[position];

                        Intent intent = new Intent(getApplicationContext(), sendMessageActivity.class);
                        intent.putExtra("nick", nickUser);
                        intent.putExtra("id_user", id_user);
                        intent.putExtra("ownId", ownId);
                        startActivity(intent);

                    }

                }).setNegativeButton("No", null);
                builder.create();
                builder.show();

            }
        });

    }

    //Método para otorgar una acción a los "items" de la lista de mensajes enviados cuando se pulsan.
    private void selectedSentMsg() {

        sentMsgsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(messagesActivity.this);
                builder.setMessage("¿Quieres enviar un mensaje a este usuario?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        id_user = idSent[position];
                        nickUser = nickSent2[position];

                        Intent intent = new Intent(getApplicationContext(), sendMessageActivity.class);
                        intent.putExtra("nick", nickUser);
                        intent.putExtra("id_user", id_user);
                        intent.putExtra("ownId", ownId);
                        startActivity(intent);

                    }

                }).setNegativeButton("No", null);
                builder.create();
                builder.show();

            }
        });

    }

    //Método para enviar una valoración.
    private void sendValoration(String URL) {

        user.setNick(writeNick.getText().toString());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), "Valoración recibida. ¡Gracias!", Toast.LENGTH_SHORT).show();
                writeNick.getText().clear();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();

            }
        }) {

            //Se recogen los datos.
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("valoration", String.valueOf(rating.getValoration()));
                params.put("nick", user.getNick());

                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    private void signOut() {

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(messagesActivity.this);

        if (googleSignInAccount != null) {

            GoogleSignInClient mGoogleSignInClient;
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
            mGoogleSignInClient.signOut().addOnCompleteListener(messagesActivity.this,
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
