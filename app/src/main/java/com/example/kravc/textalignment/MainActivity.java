package com.example.kravc.textalignment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE1 = 42;
    private static final int READ_REQUEST_CODE2 = 44;
    String TAG = "States";
    String strEn, strUa, strRes;
    float x1, x2, y1, y2;
    TextView tvLanguage, tvSFFinfo, tvSSFinfo;
    boolean tvLangClick = false;
    boolean ffsel = false, sfsel = false;
    Integer langInt = 0;
    Button btnSelectEn, btnSelectUa, btnGo;
    Toast toast = null;
    DBHelper dbHelper;
    TextMixer textMixer = new TextMixer();
    Converter converter = new Converter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLanguage = (TextView) findViewById(R.id.tvLanguage);
        btnSelectEn = (Button) findViewById(R.id.btnSelectEn);
        btnSelectUa = (Button) findViewById(R.id.btnSelectUa);
        btnGo = (Button) findViewById(R.id.btnGo);
        tvSFFinfo = (TextView) findViewById(R.id.tvSFFinfo);
        tvSSFinfo = (TextView) findViewById(R.id.tvSSFinfo);
        dbHelper = new DBHelper(this);

        View.OnClickListener oclTvLanguage = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (langInt) {
                    case 0:
                        if (!tvLangClick) {
                            tvLanguage.setText("English - Russian");
                            tvLangClick = true;
                        } else {
                            tvLanguage.setText("English - Ukrainian");
                            tvLangClick = false;
                        }
                        break;
                    case 1:
                        if (!tvLangClick) {
                            tvLanguage.setText("Англійська - Російська");
                            tvLangClick = true;
                        } else {
                            tvLanguage.setText("Англійська - Українська");
                            tvLangClick = false;
                        }
                        break;
                    case 2:
                        if (!tvLangClick) {
                            tvLanguage.setText("Английский - Русский");
                            tvLangClick = true;
                        } else {
                            tvLanguage.setText("Английский - Украинский");
                            tvLangClick = false;
                        }
                        break;
                }
            }
        };

        tvLanguage.setOnClickListener(oclTvLanguage);

        View.OnClickListener oclBtns = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                switch (v.getId()) {
                    case R.id.btnSelectEn:
                        performFileSearch1();
                        break;
                    case R.id.btnSelectUa:
                        performFileSearch2();
                        break;
                    case R.id.btnGo:
                        if (!ffsel || !sfsel) {
                            switch (langInt) {
                                case 0:
                                    toast = Toast.makeText(MainActivity.this, "Choose files", Toast.LENGTH_SHORT);
                                    break;
                                case 1:
                                    toast = Toast.makeText(MainActivity.this, "Оберіть файли", Toast.LENGTH_SHORT);
                                    break;
                                case 2:
                                    toast = Toast.makeText(MainActivity.this, "Выберите файлы", Toast.LENGTH_SHORT);
                                    break;
                            }
                            toast.show();
                        } else {
                            String english = strEn;
                            String ukrainian = strUa;
                            ukrainian = ukrainian.replaceAll("  ", " ");
                            byte[] enBytes = converter.strToByte(english);
                            byte[] uaBytes = converter.strToByte(ukrainian);
                            try {
                                strRes = textMixer.alignTxt(0, 0, enBytes, uaBytes);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            
                            ffsel = false;
                            sfsel = false;
                            String name = tvSFFinfo.getHint().toString() + " | " + tvSSFinfo.getHint().toString();
                            tvSFFinfo.setHint(R.string.noFS);
                            tvSSFinfo.setHint(R.string.noFS);
                            Log.i(TAG, "strRes: " + strRes);

                            cv.put("name", name);
                            cv.put("alignment", strRes);
                            long rowID = db.insert("mytable", null, cv);
                            Log.i(TAG, "row inserted, ID = " + rowID);

                            Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                            intent.putExtra("strRes", strRes);
                            intent.putExtra("langInt", langInt);
                            startActivity(intent);
                            break;
                        }
                }
            }
        };

        btnSelectEn.setOnClickListener(oclBtns);
        btnSelectUa.setOnClickListener(oclBtns);
        btnGo.setOnClickListener(oclBtns);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        switch (langInt) {
            case 0:
                menu.add(0, 1, 0, "Settings");
                menu.add(0, 2, 0, "About");
                break;
            case 1:
                menu.add(0, 1, 0, "Налаштування");
                menu.add(0, 2, 0, "Про застосунок");
                break;
            case 2:
                menu.add(0, 1, 0, "Настройки");
                menu.add(0, 2, 0, "О приложении");
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent intent1 = new Intent(MainActivity.this, MenuActivity.class);
                intent1.putExtra("langInt", langInt);
                startActivityForResult(intent1, 5);
                return true;
            case 2:
                Intent intent2 = new Intent(MainActivity.this, AboutActivity.class);
                intent2.putExtra("langInt", langInt);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        langInt = data.getIntExtra("langInt", 5);
        switch (langInt) {
            case 0:
                btnSelectEn.setText("Select first file");
                btnSelectUa.setText("Select second file");
                btnGo.setText("Get alignment");
                if (!tvLangClick) {
                    tvLanguage.setText("English - Russian");
                } else {
                    tvLanguage.setText("English - Ukrainian");
                }
                tvSFFinfo.setHint("No file selected");
                tvSSFinfo.setHint("No file selected");
                break;
            case 1:
                btnSelectEn.setText("Оберіть перший файл");
                btnSelectUa.setText("Оберіть другий файл");
                btnGo.setText("Вирівняти тексти");
                if (!tvLangClick) {
                    tvLanguage.setText("Англійська - Російська");
                } else {
                    tvLanguage.setText("Англійська - Українська");
                }
                tvSFFinfo.setHint("Файл не обрано");
                tvSSFinfo.setHint("Файл не обрано");
                break;
            case 2:
                btnSelectEn.setText("Выберите первый файл");
                btnSelectUa.setText("Выберите второй файл");
                btnGo.setText("Вировнять тексты");
                if (!tvLangClick) {
                    tvLanguage.setText("Английский - Русский");
                } else {
                    tvLanguage.setText("Английский - Украинский");
                }
                tvSFFinfo.setHint("Файл не выбран");
                tvSSFinfo.setHint("Файл не выбран");
                break;
        }

        if (requestCode == READ_REQUEST_CODE1 && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                tvSFFinfo.setHint(getFilenameUri(uri));
                ffsel = true;
                Log.i(TAG, "Uri: " + uri.toString());
                try {
                    strEn = readTextFromUri(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "strEn: " + strEn);
            }
        }
        if (requestCode == READ_REQUEST_CODE2 && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                tvSSFinfo.setHint(getFilenameUri(uri));
                sfsel = true;
                Log.i(TAG, "Uri: " + uri.toString());
                try {
                    strUa = readTextFromUri(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "strUa: " + strUa);
            }
        }
    }

    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchevent.getX();
                y2 = touchevent.getY();
                if (x1 > x2) {
                    Intent intent = new Intent(MainActivity.this, ListActivity.class);
                    startActivity(intent);
                }
                break;
        }
        return false;
    }

    public void performFileSearch1() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, READ_REQUEST_CODE1);
    }

    public void performFileSearch2() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(intent, READ_REQUEST_CODE2);
    }

    public String getFilenameUri (Uri uri) {
        String file = "";
        if (uri.getScheme().equals("file")) {
            file = uri.getLastPathSegment();
        } else {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DISPLAY_NAME}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    file = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    Log.d(TAG, "name is " + file);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return file;
    }

    static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "name text,"
                    + "alignment text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
