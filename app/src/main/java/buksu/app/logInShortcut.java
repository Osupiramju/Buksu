package buksu.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

public class logInShortcut extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_in_shortcut);

        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        //Se crea una tarea que se ejecute. Con 2 segundos de duración.
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                //Se recuperan las preferencias y se verifica si hay una sesión guardada o no.
                SharedPreferences preferences = getSharedPreferences("logInPreferences", Context.MODE_PRIVATE);

                boolean session = preferences.getBoolean("session", false);

                if(session){

                    Intent intent = new Intent(getApplicationContext(), accessActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                }

            }
        }, 2000);
    }
}
