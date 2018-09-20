package com.example.kravc.textalignment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class ResultsActivity extends AppCompatActivity implements View.OnClickListener {

    int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
    int i;
    int langInt;
    String temp;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(wrapContent, wrapContent);
        LinearLayout llMain = (LinearLayout) findViewById(R.id.llMain);

        Intent intent = getIntent();
        temp = intent.getStringExtra("strRes");
        langInt = intent.getIntExtra("langInt", 0);

        temp = temp.replaceAll("\t", "\r\n");
        String[] strRes_arr = temp.split("&&");

        for (i = 0; i < strRes_arr.length; i++) {
            if (i % 2 == 0) {
                TextView textView = new TextView(this);
                textView.setText(strRes_arr[i]);
                textView.setTextColor(Color.BLACK);
                textView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                textView.setClickable(true);
                textView.setOnClickListener(this);
                textView.setId(i);
                llMain.addView(textView, lParams);
            } else {
                TextView textView = new TextView(this);
                textView.setText(strRes_arr[i]);
                textView.setTextColor(Color.RED);
                textView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                textView.setVisibility(View.GONE);
                textView.setId(i);
                llMain.addView(textView, lParams);
            }
        }

    }

    @Override
    public void onClick(View v) {
        int tempId = v.getId();
        if (tempId % 2 == 0) {
            if (findViewById(tempId + 1).isShown()) {
                findViewById(tempId + 1).setVisibility(View.GONE);
            } else {
                findViewById(tempId + 1).setVisibility(View.VISIBLE);
            }
        }
    }

}
