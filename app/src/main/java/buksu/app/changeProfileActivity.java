package buksu.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class changeProfileActivity extends AppCompatActivity {

    TextView textCity, textTown;
    Button cancelChangesButton, saveChangesButton, goToSearchBookButton, goToMessagesButton;
    String cityText, townText, cityHint, townHint;

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        textCity = findViewById(R.id.editTextCity);
        textTown = findViewById(R.id.editTextTown);

        cancelChangesButton = findViewById(R.id.cancelChangesButton);
        saveChangesButton = findViewById(R.id.saveChangesButton);
        goToSearchBookButton = findViewById(R.id.goToSearchBookButton);
        goToMessagesButton = findViewById(R.id.goToMessagesButton);

        //Se recoge el email usado para acceder a la función.
        SharedPreferences preferences = getSharedPreferences("logInPreferences", Context.MODE_PRIVATE);
        user.setEmail(preferences.getString("email", "Error."));

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(changeProfileActivity.this);

        if (googleSignInAccount != null) {

            user.setEmail(googleSignInAccount.getEmail());

        }

        getLocation("https://buksuapp.000webhostapp.com/getLocation.php?email=" + user.getEmail());

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

                saveChanges("https://buksuapp.000webhostapp.com/changeProfile.php");

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

    }

    //Se guardan los cambios.
    private void saveChanges(String URL) {

        cityText = textCity.getText().toString();
        townText = textTown.getText().toString();
        cityHint = textCity.getHint().toString();
        townHint = textTown.getHint().toString();

        //Con esto, facilitamos al usuario que solo escriba sin borrar, o bien que se queden los mismos datos que estaban.
        if (cityText.isEmpty()) {
            user.setCity(cityHint);
        } else {
            user.setCity(cityText);
        }
        if (townText.isEmpty()) {
            user.setTown(townHint);
        } else {
            user.setTown(townText);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            //Si el registro es correcto, se ejecuta este método.
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), "Datos modificados correctamente.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), profileActivity.class);
                startActivity(intent);
                finish();

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

                params.put("city", user.getCity());
                params.put("town", user.getTown());
                params.put("email", user.getEmail());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    //Se recogen los datos de localización del usuario.
    private void getLocation(String URL) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;

                for (int i = 0; i < response.length(); i++) {

                    try {

                        jsonObject = response.getJSONObject(i);

                        //Mostramos los datos siempre en el "hint", para facilitar al usuario la escritura.
                        textCity.setHint(jsonObject.getString("city"));
                        textTown.setHint(jsonObject.getString("town"));


                    } catch (JSONException e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }

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

    //Si el usuario pulsa el botón del móvil para ir "atrás", se activa esta función.
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), profileActivity.class);
        startActivity(intent);
        finish();

    }

}
