package jp.gr.java_conf.ya.passstore; // Copyright (c) 2017 YA<ya.androidapp@gmail.com> All rights reserved.

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    // UI
    private Button button_pw_register;
    private LinearLayout content_mpw;
    private EditText edittext_pw1;
    private EditText edittext_pw2;
    private TextView textview_pw;
    // UI

    private static final String PREF_KEY_MASTER_PW = "MPW";
    private static final String TAG = "PassStore";

    private TextWatcher textWatcher_edittext_pw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            confirmPassword();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CryptUtil.prepareKeyStore();

        // UI
        setContentView(R.layout.activity_main);

        button_pw_register = findViewById(R.id.button_pw_register);
        content_mpw = findViewById(R.id.content_mpw);
        edittext_pw1 = findViewById(R.id.edittext_pw1);
        edittext_pw2 = findViewById(R.id.edittext_pw2);
        textview_pw = findViewById(R.id.textview_pw);

        if(CryptUtil.isSet()){ // PWを既にセットしていれば登録フォームを表示しない
            // content_mpw.setVisibility(View.GONE);
        }else{
            content_mpw.setVisibility(View.VISIBLE);
        }

        button_pw_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textPw1 = edittext_pw1.getText().toString();
                String textPw2 = edittext_pw2.getText().toString();
                if (textPw1.equals(textPw2) && !textPw1.equals("")) {
                    boolean result = registerMasterPassword(textPw1);
                    if (result)
                        Toast.makeText(MainActivity.this, getString(R.string.registered), Toast.LENGTH_LONG).show();
                }
            }
        });
        edittext_pw1.addTextChangedListener(textWatcher_edittext_pw);
        edittext_pw2.addTextChangedListener(textWatcher_edittext_pw);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // UI
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_password_reset) {
            CryptUtil.resetPw();
            content_mpw.setVisibility(View.VISIBLE);
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmPassword(){
        if ((edittext_pw1.getText().toString()).equals(edittext_pw2.getText().toString())) {
            textview_pw.setText(""); // textview_pw.setText(getString(R.string.passwords_matched));
            button_pw_register.setEnabled(true);
        } else {
            textview_pw.setText(getString(R.string.passwords_not_matched));
            button_pw_register.setEnabled(false);
        }
    }

    private String loadPref(String key, String defaultValue) {
        try {
            SharedPreferences data = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
            return data.getString(key, defaultValue);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return defaultValue;
        }
    }

    private boolean registerMasterPassword(final String textPw) {
        String encryptedText = CryptUtil.encryptString(textPw);
        String decryptedText = CryptUtil.decryptString(encryptedText);

        if (textPw.equals(decryptedText)) { // 暗号化前の平文と複合化した平文が一致
            savePref(PREF_KEY_MASTER_PW, encryptedText); // 暗号文を保存
            return true;
        } else {
            return false;
        }
    }

    private boolean savePref(String key, String value) {
        try {
            SharedPreferences data = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = data.edit();
            editor.putString(key, value);
            editor.apply();

            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }
}
