package buksu.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class newUserActivity extends AppCompatActivity {

    EditText newNick, newEmail, newPassword, repeatNewPassword;

    Button createNewUserButton, cancelChangesButton;

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        newNick = findViewById(R.id.newNick);
        newEmail = findViewById(R.id.newEmail);
        newPassword = findViewById(R.id.newPassword);
        repeatNewPassword = findViewById(R.id.repeatNewPassword);
        createNewUserButton = findViewById(R.id.createNewUserButton);
        cancelChangesButton = findViewById(R.id.cancelChangesButton);

        createNewUserButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                verificationNewUser();

            }
        });

        cancelChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    //Método para verificar lo introducido por el usuario.
    private void verificationNewUser(){

        user.setNick(newNick.getText().toString());
        user.setEmail(newEmail.getText().toString());
        user.setPassword(newPassword.getText().toString());

        //Pattern para validar email.
        Pattern pattern1 = Pattern.compile("^([0-9a-zA-Z]+[-._+&])*[0-9a-zA-Z]+@([-0-9a-zA-Z]+[.])+[a-zA-Z]{2,6}$");

        //Pattern para validar el nick y la contraseña.
        Pattern pattern2 = Pattern.compile("^.[^ ]{4,20}$");

        //Recogida de email, nick y contraseña.
        Matcher matcherEmail = pattern1.matcher(user.getEmail());
        Matcher matcherNick = pattern2.matcher(user.getNick());
        Matcher matcherPassword = pattern2.matcher(user.getPassword());

        //Validación. Si la validación es correcta, se envían los datos con el método "serviceExecution"
        if (!matcherEmail.find()) {

            Toast.makeText(getApplicationContext(), "La dirección de correo electrónico no es correcta.", Toast.LENGTH_SHORT).show();
            newEmail.getText().clear();

        } else if (!matcherNick.find()) {

            Toast.makeText(getApplicationContext(), "El nick tiene que tener entre 5 y 20 caracteres y ningún espacio.", Toast.LENGTH_SHORT).show();
            newNick.getText().clear();

        } else if (!matcherPassword.find()) {

            Toast.makeText(getApplicationContext(), "La contraseña tiene que tener entre 5 y 20 caracteres y ningún espacio.", Toast.LENGTH_SHORT).show();
            newPassword.getText().clear();
            repeatNewPassword.getText().clear();

        } else if (!user.getPassword().equals(repeatNewPassword.getText().toString())) {

            Toast.makeText(getApplicationContext(), "Las contraseñas deben ser idénticas.", Toast.LENGTH_SHORT).show();
            newPassword.getText().clear();
            repeatNewPassword.getText().clear();

        } else {

            serviceExecution("https://buksuapp.000webhostapp.com/insertNewUser.php");

        }

    }

    //Envío de lo escrito por el usuario
    private void serviceExecution (String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            //Si el registro es correcto, se ejecuta este método.
            @Override
            public void onResponse(String response) {

                if (response.isEmpty()){

                    Toast.makeText(getApplicationContext(), "Usuario registrado correctamente.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                } else {

                    Toast.makeText(getApplicationContext(), "El nick o el email existe en la base de datos.", Toast.LENGTH_SHORT).show();
                    newNick.getText().clear();
                    newEmail.getText().clear();

                }
            }
        }, new Response.ErrorListener() {

            //Si hay algún problema, se ejecuta este método.
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

            //Se recogen los datos.
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("nick", user.getNick());
                params.put("email", user.getEmail());
                params.put("password", user.getPassword());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}

