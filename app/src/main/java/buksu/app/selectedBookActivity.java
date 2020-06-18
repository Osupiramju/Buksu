package buksu.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
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

public class selectedBookActivity extends AppCompatActivity {

    Button signOutButton, goToMessageButton, goToSearchBookButton, goToProfileButton;

    TextView text;

    Message message = new Message();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_book);

        int id_user = getIntent().getIntExtra("id_user", 0);
        int id_book = getIntent().getIntExtra("id_book", 0);
        int user_id = getIntent().getIntExtra("user_id", 0);

        goToMessageButton = findViewById(R.id.goToMessagesButton);
        goToSearchBookButton = findViewById(R.id.goToSearchBookButton);
        goToProfileButton = findViewById(R.id.goToProfileButton);
        signOutButton = findViewById(R.id.signOutButton);

        text = findViewById(R.id.textView);

        goToMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), messagesActivity.class);
                startActivity(intent);

            }

        });

        goToSearchBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), searchBookActivity.class);
                startActivity(intent);

            }
        });

        goToProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), profileActivity.class);
                startActivity(intent);

            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOut();

            }
        });

        getData("https://buksuapp.000webhostapp.com/getData.php?id_book=" + id_book, id_user, user_id);

    }
    //Método para recibir los datos para enviar un mensaje automático.
    public void getData(String URL, final int id_user, final int user_id) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;

                for (int i = 0; i < response.length(); i++) {

                    try {

                        jsonObject = response.getJSONObject(i);

                        message.setMsg("¡Hola! me interesa tu libro \"" +
                                jsonObject.getString("bookTitle") + "\" , de " + jsonObject.getString("author")
                                + ". Si lo deseas, ¡puedes intercambiar un libro conmigo!");

                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

                automaticMessageSent("https://buksuapp.000webhostapp.com/automaticMessageSent.php", id_user, user_id);


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

    //Método para mostrar un mensaje de información al usuario, y se aprovecha para hacer la inserción del mensaje.
    public void automaticMessageSent(String URL, final int id_user, final int user_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                String messageToUser = "Se le ha enviado un mensaje al usuario con el cual quieres realizar el intercambio." +
                        " Si está interesado en algún libro que tengas, te enviará un mensaje." +
                        " ¡Paciencia!";

                text.setText(messageToUser);

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

                params.put("id_user", String.valueOf(id_user));
                params.put("user_id", String.valueOf(user_id));
                params.put("message", message.getMsg());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void signOut() {

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(selectedBookActivity.this);

        if (googleSignInAccount != null) {

            GoogleSignInClient mGoogleSignInClient;
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
            mGoogleSignInClient.signOut().addOnCompleteListener(selectedBookActivity.this,
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

        Intent intent = new Intent(getApplicationContext(), searchBookActivity.class);
        startActivity(intent);
        finish();

    }

}
