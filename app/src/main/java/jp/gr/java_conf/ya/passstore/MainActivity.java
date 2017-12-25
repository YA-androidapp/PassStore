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
    private Button button_jpp_register;
    private Button button_mpw_register;
    private LinearLayout content_jpp;
    private LinearLayout content_mpw;
    private EditText edittext_jpp_hint1_q;
    private EditText edittext_jpp_hint1_a;
    private EditText edittext_jpp_hint2_q;
    private EditText edittext_jpp_hint2_a;
    private EditText edittext_jpp_hint3_q;
    private EditText edittext_jpp_hint3_a;
    private EditText edittext_jpp_num;
    private EditText edittext_jpp_pw;
    private EditText edittext_mpw1;
    private EditText edittext_mpw2;
    private TextView textview_mpw;
    // UI

    private static final String PREF_KEY_JPP_HINT1Q = "JPPHINT1Q";
    private static final String PREF_KEY_JPP_HINT1A = "JPPHINT1A";
    private static final String PREF_KEY_JPP_HINT2Q = "JPPHINT2Q";
    private static final String PREF_KEY_JPP_HINT2A = "JPPHINT2A";
    private static final String PREF_KEY_JPP_HINT3Q = "JPPHINT3Q";
    private static final String PREF_KEY_JPP_HINT3A = "JPPHINT3A";
    private static final String PREF_KEY_JPP_NUM = "JPPNUM";
    private static final String PREF_KEY_JPP_PW = "JPPPW";
    private static final String PREF_KEY_MASTER_PW = "MPW";
    private static final String TAG = "PassStore";

    private TextWatcher textWatcher_edittext_mpw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            confirmMasterPassword();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private TextWatcher textWatcher_edittext_jpp = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            confirmJppPassword();
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

        button_jpp_register = findViewById(R.id.button_jpp_register);
        button_mpw_register = findViewById(R.id.button_mpw_register);
        content_jpp = findViewById(R.id.content_jpp);
        content_mpw = findViewById(R.id.content_mpw);
        edittext_jpp_hint1_q = findViewById(R.id.edittext_jpp_hint1_q);
        edittext_jpp_hint1_a = findViewById(R.id.edittext_jpp_hint1_a);
        edittext_jpp_hint2_q = findViewById(R.id.edittext_jpp_hint2_q);
        edittext_jpp_hint2_a = findViewById(R.id.edittext_jpp_hint2_a);
        edittext_jpp_hint3_q = findViewById(R.id.edittext_jpp_hint3_q);
        edittext_jpp_hint3_a = findViewById(R.id.edittext_jpp_hint3_a);
        edittext_jpp_num = findViewById(R.id.edittext_jpp_num);
        edittext_jpp_pw = findViewById(R.id.edittext_jpp_pw);
        edittext_mpw1 = findViewById(R.id.edittext_mpw1);
        edittext_mpw2 = findViewById(R.id.edittext_mpw2);
        textview_mpw = findViewById(R.id.textview_mpw);

        if (issetPref(PREF_KEY_MASTER_PW)) { // PWを既にセットしていれば登録フォームを表示しない
            content_mpw.setVisibility(View.GONE);
        } else {
            content_mpw.setVisibility(View.VISIBLE);
        }

        if (issetPref(PREF_KEY_JPP_PW)) { // PWを既にセットしていれば登録フォームを表示しない
            content_jpp.setVisibility(View.GONE);
        } else {
            content_jpp.setVisibility(View.VISIBLE);
        }

        // MPW
        button_mpw_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textPw1 = edittext_mpw1.getText().toString();
                String textPw2 = edittext_mpw2.getText().toString();
                if (textPw1.equals(textPw2) && !textPw1.equals("")) {
                    boolean result = registerMasterPassword(textPw1);
                    if (result)
                        Toast.makeText(MainActivity.this, getString(R.string.registered_mpw), Toast.LENGTH_LONG).show();
                }
            }
        });
        edittext_mpw1.addTextChangedListener(textWatcher_edittext_mpw);
        edittext_mpw2.addTextChangedListener(textWatcher_edittext_mpw);

        // JPP
        button_jpp_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textJppNum = edittext_jpp_num.getText().toString();
                String textJppPw = edittext_jpp_pw.getText().toString();
                String textJppHint1Q = edittext_jpp_hint1_q.getText().toString();
                String textJppHint1A = edittext_jpp_hint1_a.getText().toString();
                String textJppHint2Q = edittext_jpp_hint2_q.getText().toString();
                String textJppHint2A = edittext_jpp_hint2_a.getText().toString();
                String textJppHint3Q = edittext_jpp_hint3_q.getText().toString();
                String textJppHint3A = edittext_jpp_hint3_a.getText().toString();
                if (!textJppNum.equals("") &&
                        !textJppPw.equals("") &&
                        !textJppHint1Q.equals("") &&
                        !textJppHint1A.equals("") &&
                        !textJppHint2Q.equals("") &&
                        !textJppHint2A.equals("") &&
                        !textJppHint3Q.equals("") &&
                        !textJppHint3A.equals("")) {
                    boolean result = registerJppPassword(textJppNum, textJppPw, textJppHint1Q, textJppHint1A, textJppHint2Q, textJppHint2A, textJppHint3Q, textJppHint3A);
                    if (result)
                        Toast.makeText(MainActivity.this, getString(R.string.registered_jpp), Toast.LENGTH_LONG).show();
                }
            }
        });

        edittext_jpp_num.addTextChangedListener(textWatcher_edittext_jpp);
        edittext_jpp_pw.addTextChangedListener(textWatcher_edittext_jpp);
        edittext_jpp_hint1_q.addTextChangedListener(textWatcher_edittext_jpp);
        edittext_jpp_hint1_a.addTextChangedListener(textWatcher_edittext_jpp);
        edittext_jpp_hint2_q.addTextChangedListener(textWatcher_edittext_jpp);
        edittext_jpp_hint2_a.addTextChangedListener(textWatcher_edittext_jpp);
        edittext_jpp_hint3_q.addTextChangedListener(textWatcher_edittext_jpp);
        edittext_jpp_hint3_a.addTextChangedListener(textWatcher_edittext_jpp);

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
        if (id == R.id.action_jpp_reset) {
            resetJppPassword();
            content_jpp.setVisibility(View.VISIBLE);
            return true;
        } else if (id == R.id.action_mpw_reset) {
            resetMasterPassword();
            content_mpw.setVisibility(View.VISIBLE);
            return true;
        } else if (id == R.id.action_keystore_reset) {
            CryptUtil.resetKeyStore();
            content_mpw.setVisibility(View.VISIBLE);
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // UI
    private void confirmMasterPassword() {
        if ((edittext_mpw1.getText().toString()).equals(edittext_mpw2.getText().toString())) {
            textview_mpw.setText(""); // textview_pw.setText(getString(R.string.passwords_matched));
            button_mpw_register.setEnabled(true);
        } else {
            textview_mpw.setText(getString(R.string.passwords_not_matched));
            button_mpw_register.setEnabled(false);
        }
    }

    private void confirmJppPassword() {
        String textJppNum = edittext_jpp_num.getText().toString();
        String textJppPw = edittext_jpp_pw.getText().toString();
        String textJppHint1Q = edittext_jpp_hint1_q.getText().toString();
        String textJppHint1A = edittext_jpp_hint1_a.getText().toString();
        String textJppHint2Q = edittext_jpp_hint2_q.getText().toString();
        String textJppHint2A = edittext_jpp_hint2_a.getText().toString();
        String textJppHint3Q = edittext_jpp_hint3_q.getText().toString();
        String textJppHint3A = edittext_jpp_hint3_a.getText().toString();
        if (!textJppNum.equals("") &&
                !textJppPw.equals("") &&
                !textJppHint1Q.equals("") &&
                !textJppHint1A.equals("") &&
                !textJppHint2Q.equals("") &&
                !textJppHint2A.equals("") &&
                !textJppHint3Q.equals("") &&
                !textJppHint3A.equals("")) {
            button_mpw_register.setEnabled(true);
        } else {
            button_mpw_register.setEnabled(false);
        }
    }
    //

    // Pref
    private boolean issetPref(String key) {
        try {
            SharedPreferences data = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
            if (data.contains(key))
                return true;
        } catch (Exception e) {
        }
        return false;
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

    private boolean resetPref(String key) {
        try {
            SharedPreferences data = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = data.edit();
            editor.remove(key);
            editor.apply();

            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
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
    //

    // MPW
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

    private boolean resetMasterPassword() {
        try {
            resetPref(PREF_KEY_MASTER_PW);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }
    //

    // JPP
    private boolean registerJppPassword(final String textNum, final String textPw, final String textHint1Q, final String textHint1A, final String textHint2Q, final String textHint2A, final String textHint3Q, final String textHint3A) {
        String encryptedTextNum = CryptUtil.encryptString(textNum);
        String encryptedTextPw = CryptUtil.encryptString(textPw);

        String encryptedTextHint1Q = CryptUtil.encryptString(textHint1Q);
        String encryptedTextHint1A = CryptUtil.encryptString(textHint1A);
        String encryptedTextHint2Q = CryptUtil.encryptString(textHint2Q);
        String encryptedTextHint2A = CryptUtil.encryptString(textHint2A);
        String encryptedTextHint3Q = CryptUtil.encryptString(textHint3Q);
        String encryptedTextHint3A = CryptUtil.encryptString(textHint3A);

        try {
            savePref(PREF_KEY_JPP_NUM, encryptedTextNum);
            savePref(PREF_KEY_JPP_PW, encryptedTextPw);

            savePref(PREF_KEY_JPP_HINT1Q, encryptedTextHint1Q);
            savePref(PREF_KEY_JPP_HINT1A, encryptedTextHint1A);
            savePref(PREF_KEY_JPP_HINT2Q, encryptedTextHint2Q);
            savePref(PREF_KEY_JPP_HINT2A, encryptedTextHint2A);
            savePref(PREF_KEY_JPP_HINT3Q, encryptedTextHint3Q);
            savePref(PREF_KEY_JPP_HINT3A, encryptedTextHint3A);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }

    private boolean resetJppPassword() {
        try {
            resetPref(PREF_KEY_JPP_NUM);
            resetPref(PREF_KEY_JPP_PW);

            resetPref(PREF_KEY_JPP_HINT1Q);
            resetPref(PREF_KEY_JPP_HINT1A);
            resetPref(PREF_KEY_JPP_HINT2Q);
            resetPref(PREF_KEY_JPP_HINT2A);
            resetPref(PREF_KEY_JPP_HINT3Q);
            resetPref(PREF_KEY_JPP_HINT3A);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }
    //
}
