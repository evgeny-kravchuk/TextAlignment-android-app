package com.example.kravc.textalignment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    List<String> name = new ArrayList<String>();
    List<String> text = new ArrayList<String>();
    List<Integer> ids = new ArrayList<Integer>();
    Intent startedIntent;
    MainActivity.DBHelper dbHelper;
    SQLiteDatabase db;
    String TAG = "States";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        startedIntent = getIntent();

        dbHelper = new MainActivity.DBHelper(this);
        db = dbHelper.getWritableDatabase();
        Cursor c = db.query("mytable", null, null, null, null, null, null);
        Toast toast = Toast.makeText(ListActivity.this, "Database is empty", Toast.LENGTH_SHORT);

        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int alignmentColIndex = c.getColumnIndex("alignment");
            do {
                ids.add(c.getColumnIndex("id"));
                name.add(c.getString(nameColIndex));
                text.add(c.getString(alignmentColIndex));
            } while (c.moveToNext());
        } else
            toast.show();
        c.close();

        ListView lvMain = (ListView) findViewById(R.id.lvMain);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, name);

        lvMain.setAdapter(adapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, ResultsActivity.class);
                String message = text.get((int) id);
                intent.putExtra("strRes", message);
                startActivity(intent);
            }
        });

        registerForContextMenu(lvMain);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lvMain) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;

            menu.add(0, 1, 0, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                db.delete("mytable", "id = " + ids.get((int) info.id), null);
                Toast toast = Toast.makeText(ListActivity.this, "id =  " + info.id, Toast.LENGTH_SHORT);
                toast.show();
                finish();
                startActivity(startedIntent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
