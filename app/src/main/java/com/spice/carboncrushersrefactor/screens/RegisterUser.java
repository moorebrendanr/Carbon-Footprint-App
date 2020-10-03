package com.spice.carboncrushersrefactor.screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.spice.carboncrushersrefactor.Constants;
import com.spice.carboncrushersrefactor.R;
import static com.spice.carboncrushersrefactor.Utils.getClient;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RegisterUser extends AppCompatActivity {
    private AutoCompleteTextView name, username, email, password, confirm_password;
    private String NAME, USERNAME, EMAIL, PASSWORD, CONFIRM_PASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        name = findViewById(R.id.name);
        username = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        password = findViewById(R.id.user_password);
        confirm_password = findViewById(R.id.user_confirm_password);
        Button submit = findViewById(R.id.register_user_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NAME = name.getText().toString();
                USERNAME = username.getText().toString();
                EMAIL = email.getText().toString();
                PASSWORD = password.getText().toString();
                CONFIRM_PASSWORD = confirm_password.getText().toString();
                //Log.d("inp",NAME+","+USERNAME+","+EMAIL+","+PASSWORD+","+CONFIRM_PASSWORD);
                if(NAME.isEmpty() || USERNAME.isEmpty() || EMAIL.isEmpty() || PASSWORD.isEmpty() || CONFIRM_PASSWORD.isEmpty())
                {
                    makeToast("Please enter all the fields!");
                }
                else if(!PASSWORD.equals(CONFIRM_PASSWORD))
                {
                    makeToast("Password and Confirm Password do not match");
                }
                else if(PASSWORD.length() <= 5)
                {
                    makeToast("Password should be atleast 6 characters long");
                }
                else{
                    try {
                        register(NAME, USERNAME, EMAIL, PASSWORD, CONFIRM_PASSWORD);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    private void register(String name, String username, String email, String password, String confirm_password) throws JSONException {
        String url = Constants.SERVER_URL+"/signup";
        final ProgressDialog progressDialog = new ProgressDialog(RegisterUser.this);
        JSONObject json = new JSONObject()
                .put("name",name)
                .put("username",username)
                .put("email",email)
                .put("password",password)
                .put("confirmPassword",confirm_password);
        String body = json.toString();
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(body, MediaType.get("application/json")))
                .build();
        getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (ResponseBody body = response.body()) {
                    progressDialog.dismiss();
                    if (body != null) {
                        JSONObject jsonResponse = new JSONObject(body.string());
                        Log.d("response", jsonResponse.getString("success"));
                        if (jsonResponse.getString("success").equals("true")) {
                            Intent in = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(in);
                            finish();
                        } else if (jsonResponse.getString("success").equals("false")) {
                            makeToast(jsonResponse.getString("status"));
                        }
                    } else {
                        Toast.makeText(RegisterUser.this, "Could not complete user registration.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException | IOException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
                response.close();
            }
        });
        progressDialog.setMessage("Signing you in..");
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    private void makeToast(String textSequence)
    {
        Toast.makeText(getApplicationContext(),textSequence,Toast.LENGTH_SHORT).show();
    }
}
