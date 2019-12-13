package kz.evilteamgenius.chessapp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.evilteamgenius.chessapp.R;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

public class RegistrationActivity extends AppCompatActivity {

    @BindView(R.id.userNameRegistration)
    TextInputEditText userName;
    @BindView(R.id.emailRegistration)
    TextInputEditText email;
    @BindView(R.id.passwordRegistration)
    TextInputEditText password;
    @BindView(R.id.passwordConfRegistration)
    TextInputEditText passwordConf;
    @BindView(R.id.signInButtonRegistration)
    MaterialButton signInButtom;

    final  String url_Register = "https://k-chess.herokuapp.com/api/registration/submit";
    final  String url_IsUsernameAvailable = "https://k-chess.herokuapp.com/api/registration/isUsernameAvailable";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.signInButtonRegistration)
    public void onRegisterClicked() {
        if (userName.getText()!=null && userName.getText().length()>4){
            if (email.getText()!=null && email.getText().length()>1){
                if (password.getText()!=null && passwordConf.getText()!=null
                        && password.getText().toString().equals(passwordConf.getText().toString())){
                    new RegisterUser().execute(userName.getText().toString(), password.getText().toString(), passwordConf.getText().toString());

                }else {
                    Toast.makeText(this,"Confirm your password",Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(this,"Input email",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"Name is empty or less that 4 letters",Toast.LENGTH_SHORT).show();
        }
    }

    private void registrationInServer() {
        Timber.d("You've registered and go to another page");
        Intent intent = new Intent(this,MainAppPage.class);
        startActivity(intent);
    }

    public class RegisterUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String username = strings[0];
            String password = strings[1];
            String passwordVerification = strings[2];

            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .add("passwordVerification", passwordVerification)
                        .build();

                Request request = new Request.Builder()
                        .url(url_Register)
                        .post(formBody)
                        .build();
                Response response = null;

                try {
                    response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String result = response.body().string();

                        JSONObject json = new JSONObject(result);
                        String status = json.getString("status");
                        if (status.equalsIgnoreCase("ok")) {
                            showToast("Register successful");
                            Intent i = new Intent(RegistrationActivity.this,
                                    LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else if (status.equalsIgnoreCase("error")) {
                            String errorMsg = json.getString("content");
                            showToast(errorMsg);
                        } else {
                            showToast("oop! please try again");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }


    public void showToast(final String Text){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegistrationActivity.this,
                        Text, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*

    {
    "status": "error",
    "content": [
        [
            "username.is.taken"
        ]
    ]
}

{
    "status": "ok",
    "content": []
}
     */
}

