package buksu.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class accessActivity extends AppCompatActivity {

    Button signOutButton, goToMessageButton, goToSearchBookButton, goToProfileButton;

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        goToMessageButton = findViewById(R.id.goToMessagesButton);
        goToSearchBookButton = findViewById(R.id.goToSearchBookButton);
        goToProfileButton = findViewById(R.id.goToProfileButton);
        signOutButton = findViewById(R.id.signOutButton);

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(accessActivity.this);

        //Con esto, comprobamos si el usuario ha iniciado sesión con una cuenta Google.
        if (googleSignInAccount != null) {

            user.setEmail(googleSignInAccount.getEmail());

            insertGoogleProfileData("https://buksuapp.000webhostapp.com/insertGoogleProfileData.php", user.getEmail());

        }

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

    }

    //Función que inserta datos si es la primera vez que el usuario accede a la app.
    private void insertGoogleProfileData(String URL, final String email) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", email);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    //Función que hace el "Sign out" o deslogueo.
    private void signOut() {

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(accessActivity.this);

        if (googleSignInAccount != null) {

            GoogleSignInClient mGoogleSignInClient;
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
            mGoogleSignInClient.signOut().addOnCompleteListener(accessActivity.this,

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

}
