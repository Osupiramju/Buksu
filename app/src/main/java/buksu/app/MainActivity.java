package buksu.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;
import java.util.Map;



public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;

    public static final int SIGNINCODE = 777;

    EditText enterEmail, enterPassword;

    Button simpleSignInButton;

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        enterEmail = findViewById(R.id.email);
        enterPassword = findViewById(R.id.password);

        SignInButton signInButton = findViewById(R.id.signInButton);
        simpleSignInButton = findViewById(R.id.simpleSignInButton);

        getPreferences();

        simpleSignInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                user.setEmail(enterEmail.getText().toString());
                user.setPassword(enterPassword.getText().toString());

                if (!user.getEmail().isEmpty() && !user.getPassword().isEmpty()) {

                    userValidate("https://buksuapp.000webhostapp.com/logInUser.php");

                } else {

                    Toast.makeText(MainActivity.this, "No se permiten campos vacíos.", Toast.LENGTH_SHORT).show();

                }

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Botón de inicio de sesión con Google.
        signInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGNINCODE);

            }
        });

    }

    //Función para la validación del usuario.
    private void userValidate(String URL) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Email y password existen y el servicio PHP envía la fila encontrada.
                if (!response.isEmpty()) {

                    savePreferences();
                    Intent intent = new Intent(getApplicationContext(), accessActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(MainActivity.this, "Email incorrecto o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    enterEmail.getText().clear();
                    enterPassword.getText().clear();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Instancia
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", user.getEmail());
                params.put("password", user.getPassword());
                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Instancia de "stringRequest" para procesar todas las peticiones.
        requestQueue.add(stringRequest);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGNINCODE) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        }
    }

    //Método para comprobar si ha habido problemas al iniciar sesión con Google.
    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {

            goAccessActivity();

        } else {

            Toast.makeText(this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();

        }
    }

    //Método para ir a la pantalla después de haber accedido con las credenciales o por Google.
    private void goAccessActivity() {

        Intent intent = new Intent(this, accessActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    //Método para ir a la pantalla de nuevo usuario.
    public void goNewUserActivity(View view) {

        Intent intent = new Intent(this, newUserActivity.class);
        startActivity(intent);

    }

    //Método para guardar preferencias.
    private void savePreferences() {

        //Con esto, las preferencias solo serán accesibles desde la propia app.
        SharedPreferences preferences = getSharedPreferences("logInPreferences", Context.MODE_PRIVATE);

        //Guardamos o actualizamos datos en la preferencia.
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("email", user.getEmail());
        editor.putString("password", user.getPassword());


        //Esto nos permite guardar la sesión en caso de que ésta sea correcta.
        editor.putBoolean("session", true);

        //Guardamos los cambios.
        editor.apply();

    }

    //Método para recuperar los datos.
    private void getPreferences() {

        SharedPreferences preferences = getSharedPreferences("logInPreferences", Context.MODE_PRIVATE);
        enterEmail.setText(preferences.getString("email", ""));
        enterPassword.setText(preferences.getString("password", ""));

    }
}
