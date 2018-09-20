package com.example.kravc.textalignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Intent intent = getIntent();
        int langInt = intent.getIntExtra("langInt", 0);

        String en = "TextAlignment Android Application v.1.0 beta</p><p>" +
                "Odesa I. I. Mechnikov National University</p><p>" +
                "Department of Mathematical support of computer systems</p><p>" +
                "Developed as a bachelor's degree project</p><p>" +
                "V.H. Penko, Y.I. Kravchuk</p>";

        String ua = "TextAlignment Android Application v.1.0 beta</p><p>" +
                "Одеський національний універитет імені І.І. Мечникова</p><p>" +
                "Кафедра математичного забезпечення комп’ютерних систем</p><p>" +
                "Розроблено в якості бакалаврського дипломного проекту</p><p>" +
                "В.Г. Пенко, Є.І. Кравчук</p>";

        String ru = "TextAlignment Android Application v.1.0 beta</p><p>" +
                "Одесский национальный университет имени И.И. Мечникова</p><p>" +
                "Кафедра математического обеспечения компьютерных систем</p><p>" +
                "Разработано в качестве бакалаврского дипломного проекта</p><p>" +
                "В.Г. Пенко, Е.И. Кравчук</p>";

        WebView view = (WebView) findViewById(R.id.textContent);
        String text;
        text = "<html><body><p align=\"justify\">";

        switch (langInt) {
            case 0:
                text += en;
                break;
            case 1:
                text += ua;
                break;
            case 2:
                text += ru;
                break;
        }

        text += "</p></body></html>";
        view.loadData(text, "text/html", "utf-8");
    }
}
