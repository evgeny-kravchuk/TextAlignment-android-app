package com.example.kravc.textalignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity{

    String[] data = {"English", "Українська", "Русский"};
    Integer langIntSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent i = getIntent();
        Integer langIntGet = i.getIntExtra("langInt", 0);

        TextView tvMenuLang = (TextView) findViewById(R.id.tvMenuLang);
        tvMenuLang.setText("Language");
        switch (langIntGet) {
            case 0:
                tvMenuLang.setText("Language");
                break;
            case 1:
                tvMenuLang.setText("Мова");
                break;
            case 2:
                tvMenuLang.setText("Язык");
                break;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Choose language");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                langIntSet = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        Button btnAccept = (Button) findViewById(R.id.btnAccept);

        View.OnClickListener oclTvLanguage = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("langInt", langIntSet);
                setResult(RESULT_OK, intent);
                finish();
            }
        };

        btnAccept.setOnClickListener(oclTvLanguage);
    }

}
