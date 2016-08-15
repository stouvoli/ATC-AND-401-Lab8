package com.numeris_ci.dailyfortune;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyPreferences pref = new MyPreferences(MainActivity.this);
        if (!pref.isFirstTime()) {
            Intent i = new Intent(getApplicationContext(), FortuneActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        }
    }

    public void SaveUserName(View view) {
        EditText usrName = (EditText) findViewById(R.id.editText1);
        MyPreferences pref = new MyPreferences(MainActivity.this);
        pref.setUserName(usrName.getText().toString().trim());
        Intent i = new Intent(getApplicationContext(), FortuneActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
    }
}
