package com.repa.skoloyalty;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;


public class Settings extends ActionBarActivity {

    EditText pass;
    EditText filial;
    EditText place;
    EditText us;
    EditText ps;
    CheckBox shs;
    public void onClickSave(View v) {
        Intent intent = new Intent();
        intent.putExtra("pass", pass.getText().toString());
        intent.putExtra("filial", filial.getText().toString());
        intent.putExtra("place", place.getText().toString());
        intent.putExtra("us", us.getText().toString());
        intent.putExtra("ps", ps.getText().toString());
        String showsett;
        showsett="N";
        if (shs.isChecked()) {showsett="Y";}

        intent.putExtra("showsett", showsett);
        setResult(RESULT_OK, intent);
        finish();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        pass = (EditText) findViewById(R.id.password);
        filial = (EditText) findViewById(R.id.filial);
        place = (EditText) findViewById(R.id.place);
        us = (EditText) findViewById(R.id.user);
        ps = (EditText) findViewById(R.id.pass);
        shs= (CheckBox) findViewById(R.id.hidecheckBox);

        pass.setText(getIntent().getStringExtra("password"));
        filial.setText(getIntent().getStringExtra("filial"));
        place.setText(getIntent().getStringExtra("place"));
        us.setText(getIntent().getStringExtra("us"));
        ps.setText(getIntent().getStringExtra("ps"));
        boolean ch;
        ch=getIntent().getStringExtra("showsett").equals("Y");
        shs.setChecked(ch);
    }


}
