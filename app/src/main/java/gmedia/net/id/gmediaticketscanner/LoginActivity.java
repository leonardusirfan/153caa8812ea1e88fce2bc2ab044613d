package gmedia.net.id.gmediaticketscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.gmediaticketscanner.Util.ApiVolleyManager;
import gmedia.net.id.gmediaticketscanner.Util.AppLoading;
import gmedia.net.id.gmediaticketscanner.Util.AppRequestCallback;
import gmedia.net.id.gmediaticketscanner.Util.AppSharedPreferences;
import gmedia.net.id.gmediaticketscanner.Util.JSONBuilder;

public class LoginActivity extends AppCompatActivity {

    private EditText txt_username, txt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_username = findViewById(R.id.txt_username);
        txt_password = findViewById(R.id.txt_password);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_username.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this,
                            "Kolom username belum terisi", Toast.LENGTH_SHORT).show();
                }
                else if(txt_password.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this,
                            "Kolom password belum terisi", Toast.LENGTH_SHORT).show();
                }
                else{
                    login(txt_username.getText().toString(),
                            txt_password.getText().toString());
                }
            }
        });
    }

    private void login(String username, String password){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("username", username);
        body.add("password", password);

        ApiVolleyManager.getInstance().addSecureRequest(this, Constant.URL_LOGIN,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(this),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result, String message) {
                        try{
                            String id = new JSONObject(result).getString("users_id");
                            String token = new JSONObject(result).getString("token");

                            AppSharedPreferences.Login(LoginActivity.this, id, token);
                            startActivity(new Intent(LoginActivity.this, ScannerActivity.class));
                            finish();
                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }
}
