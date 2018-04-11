package android.sio2.efficom.fr.ppe4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button boutonCo = findViewById(R.id.connexion_button);
        boutonCo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                EditText editLogin = findViewById(R.id.login);
                String login = editLogin.getText().toString();

                //On crée l'AsyncTask
                MonAsyncTask monAsyncTask= new MonAsyncTask();
                //On exécute l'asynctask sans bloquer le thread principal
                monAsyncTask.execute(apiURL,login);

            }
        });
    }

    OkHttpClient client = new OkHttpClient();
    String apiURL ="localhost:82/login.php";

    class MonAsyncTask extends AsyncTask<String, Void, Boolean>{
        @Override
        protected void onPostExecute(Boolean b) {
            if (b) {
                SharedPreferences mySharedPreferences = getSharedPreferences("Preference", Context.MODE_PRIVATE);
                SharedPreferences.Editor myEditor = mySharedPreferences.edit();

                EditText editLogin = findViewById(R.id.login);
                String login = editLogin.getText().toString();

                myEditor.putString("id_user", login).apply();

                Intent intent = new Intent(MainActivity.this, ListInterventionActivity.class);
            }
            else {
                Toast.makeText(MainActivity.this.getApplicationContext(), "Identifiant invalide", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            //traitement en async

            String url = strings[0];
            String login = strings[1];

            //On constitue le contenu du Post (un endroit où mettre les éléments du Post)
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id_user",login)
                    .build();

            //On envoie la requête au serveur + construction de la nouvelle url
            Request request = new Request.Builder()
                    .url(url)//url de base
                    .post(requestBody)
                    .build();

            Response response;
            try {
                response = client.newCall(request).execute();

                //On utilise 2 manières différentes pour vérifier le login
                //mais on ne peut en utiliser qu'une seule
                String s = response.body().string();
                //recup le code retour
                if(response.code()==200 || "logintrue".equals(s)){
                    Log.d("SIO2", "code retour " + response.isSuccessful());
                    Log.d("API1", "body " + response.isSuccessful());
                    return true;
                }
                else{
                    Log.d("API1", "error login"+ s + "code"+response.code());
                    return false;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

