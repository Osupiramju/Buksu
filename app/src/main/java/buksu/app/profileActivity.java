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

public class profileActivity extends AppCompatActivity {

    TextView nick, email, city, town;

    Button goToChangeProfileButton, goToSeeBooksButton, goToMessagesButton, goToSearchBookButton, signOutButton;

    int getUserId;

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nick = findViewById(R.id.textViewUser);
        email = findViewById(R.id.textViewEmail);
        city = findViewById(R.id.textViewCity);
        town = findViewById(R.id.textViewTown);
        signOutButton = findViewById(R.id.signOutButton);

        goToChangeProfileButton = findViewById(R.id.goToChangeProfileButton);
        goToSeeBooksButton = findViewById(R.id.goToSeeBooksButton);
        goToMessagesButton = findViewById(R.id.goToMessagesButton);
        goToSearchBookButton = findViewById(R.id.goToSearchBookButton);

        SharedPreferences preferences = getSharedPreferences("logInPreferences", Context.MODE_PRIVATE);
        user.setEmail(preferences.getString("email", "Error."));

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(profileActivity.this);

        if (googleSignInAccount != null) {

            user.setEmail(googleSignInAccount.getEmail());

        }

        getProfile("https://buksuapp.000webhostapp.com/getProfile.php?email=" + user.getEmail());

        goToChangeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), changeProfileActivity.class);
                startActivity(intent);

            }
        });

        goToSeeBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), seeBooksActivity.class);
                intent.putExtra("id_user", getUserId);
                intent.putExtra("nick", user.getNick());
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

    }

    private void getProfile(String URL) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;

                for (int i = 0; i < response.length(); i++) {

                    try {

                        jsonObject = response.getJSONObject(i);

                        getUserId = jsonObject.getInt("id_user");
                        user.setNick(jsonObject.getString("nick"));
                        user.setCity(jsonObject.getString("city"));
                        user.setTown(jsonObject.getString("town"));
                        user.setEmail(jsonObject.getString("email"));

                        if (user.getCity().isEmpty()) {
                            user.setCity("Vacío.");
                        } else {
                            user.setCity(jsonObject.getString("city"));
                        }

                        if (user.getTown().isEmpty()) {
                            user.setTown("Vacío.");
                        } else {
                            user.setTown(jsonObject.getString("town"));
                        }

                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

                nick.setText(user.getNick());
                email.setText(user.getEmail());
                city.setText(user.getCity());
                town.setText(user.getTown());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void signOut() {

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(profileActivity.this);

        if (googleSignInAccount != null) {

            GoogleSignInClient mGoogleSignInClient;
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
            mGoogleSignInClient.signOut().addOnCompleteListener(profileActivity.this,
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

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), accessActivity.class);
        startActivity(intent);
        finish();

    }

}
