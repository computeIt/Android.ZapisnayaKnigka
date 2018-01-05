package com.example.addy.zapisuha;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.addy.zapisuha.DBHelper.KEY_ID;
import static com.example.addy.zapisuha.DBHelper.KEY_NAME;
import static com.example.addy.zapisuha.DBHelper.KEY_PHONE;
import static com.example.addy.zapisuha.DBHelper.TABLE_NAME;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "myLog";

    Button add, delete, show, edit, clear;
    EditText inputName, inputPhone, inputId, editId;
    DBHelper dbHelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add = findViewById(R.id.addButton);
        delete = findViewById(R.id.deleteButton);
        edit = findViewById(R.id.editButton);
        show = findViewById(R.id.showButton);
        clear = findViewById(R.id.clearButton);

        //.setOnClickListener
        add.setOnClickListener(this);
        delete.setOnClickListener(this);
        edit.setOnClickListener(this);
        show.setOnClickListener(this);
        clear.setOnClickListener(this);


        //<editText>
        inputName = findViewById(R.id.name);
        inputPhone = findViewById(R.id.phone);
        inputId = findViewById(R.id.id);//это относится к delete
        editId = findViewById(R.id.editById);//путаница с именами у edit, потом разобраться.

        dbHelper = new DBHelper(this);

    }

    @Override
    public void onClick(View v) {
        database = dbHelper.getWritableDatabase();

        switch (v.getId()) {
            case R.id.addButton:
                addRecord();
                break;
            case R.id.deleteButton:
                deleteRecord();
                break;
            case R.id.editButton:
                editRecord();
                break;
            case R.id.showButton:
                showAllAsList();
                break;
            case R.id.clearButton:
                clearAll();
                break;
            default:
                break;
        }
//        database.close();
        dbHelper.close();
    }

    private void clearAll() {
        database.execSQL("delete from " + TABLE_NAME);//в "delete * from" можно пропустить звездочку
        Toast.makeText(this, "all records successfully deleted", Toast.LENGTH_SHORT).show();
    }

    private void showAllAsList() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {//checking that all is ok
            cursor.moveToFirst();
            do {
                Log.d(TAG, "id = " + cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                Log.d(TAG, "name = " + cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                Log.d(TAG, "phone = " + cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
                Log.d(TAG, "=====");
            } while (cursor.moveToNext());
        }
    }

    private void editRecord() {
        if (editId == null || editId.getText().toString().length() == 0)
            editId.setError("for edit a record input id >= 0");

        else if (checkIdForInt(editId)) {
            String id = editId.getText().toString();
            int idForEdit = Integer.parseInt(id);

            Log.d(TAG, "editRecord id=" + idForEdit);

            Cursor cursor = database.query(TABLE_NAME, null, "_id=" + idForEdit, null, null, null, null);

            Log.d(TAG, "cursor = null? " + (cursor == null));
            if (cursor != null && cursor.moveToFirst()) {
                String nameFromDB = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                String phoneFromDB = cursor.getString(cursor.getColumnIndex(KEY_PHONE));
                Intent intent = new Intent(this, EditActivity.class);
                intent.putExtra(KEY_ID, idForEdit);//_id
                intent.putExtra(KEY_NAME, nameFromDB);
                intent.putExtra(KEY_PHONE, phoneFromDB);
                startActivityForResult(intent, REQUEST_CODE);
            } else if (cursor == null) {
                Toast.makeText(this, "no such id in database", Toast.LENGTH_LONG).show();
                return;
            }
            cursor.close();
        } else
            editId.setError("for edit a record input id >= 0");
    }


    //заменить мошт на Pattern.compile("[0-9]+").matches(string)????
    private boolean checkIdForInt(EditText editId) {
        try {
            int i = Integer.parseInt(editId.getText().toString());
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
//            ContentValues cv = new ContentValues();
////            cv.put(KEY_ID, data.getIntExtra(KEY_ID, Integer.parseInt(editId.getText().toString())));// ???
//            cv.put(KEY_NAME, data.getStringExtra(KEY_NAME));
//            cv.put(KEY_PHONE, data.getStringExtra(KEY_PHONE));
            Log.d(TAG, "новое имя = " + data.getStringExtra(KEY_NAME) + "|| новый телефон = " + data.getStringExtra(KEY_PHONE));
//            database.update(TABLE_NAME, cv, "_id=" + data.getIntExtra(KEY_ID, Integer.parseInt(editId.getText().toString())), null);
//            database.update(TABLE_NAME, cv, KEY_ID + "=" + data.getIntExtra(KEY_ID, 1), null);

            String strSQL = "UPDATE " + TABLE_NAME + " SET "
                    + KEY_NAME + "=" + data.getStringExtra(KEY_NAME) + ", "
                    + KEY_PHONE + "=" + data.getStringExtra(KEY_PHONE)
                    + "WHERE _id = " + KEY_ID;

            database.execSQL(strSQL);


            Toast.makeText(this, data.getStringExtra(KEY_NAME) + "\n" + data.getStringExtra(KEY_PHONE) + "\nдобавлен в базу", Toast.LENGTH_SHORT).show();

        }
    }

    private void deleteRecord() {
        int id = -1;
        if (inputId == null || inputId.getText().toString().length() == 0)//или всетаки .equals("") ???
            inputId.setError("input id number");
//        else {
        String idString = inputId.getText().toString();
        try {
            id = Integer.parseInt(idString);
            if (id < 0)
                throw new NumberFormatException("error");
        } catch (NumberFormatException e) {
            Toast.makeText(this, "id must be integer >= 0 but you inputed " + idString, Toast.LENGTH_LONG);
            return;
        }

        //это просто чтобы вытащить значения по id. наверняка можно проще
        Cursor cursor = database.query(TABLE_NAME, null, KEY_ID + "=" + id, null, null, null, null);
        cursor.moveToFirst();
        int nameIndex = cursor.getColumnIndex(KEY_NAME);
        int phoneIndex = cursor.getColumnIndex(KEY_PHONE);
        Log.d(TAG, "nameIndex = " + nameIndex + "||  phoneIndex = " + phoneIndex);
        Log.d(TAG, "cursor is null ? " + (cursor == null));
        Log.d(TAG, "id = ? = " + id);
        Log.d(TAG, "inputId.getText().toString()" + inputId.getText().toString());

        String name = cursor.getString(nameIndex);
        String phone = cursor.getString(phoneIndex);

        int row = database.delete(TABLE_NAME, KEY_ID + "=" + id, null);
        Toast.makeText(this, "record with id = " + id
                + ",\nname = " + name
                + ",\nphone = " + phone + "\nsuccessfully deleted", Toast.LENGTH_LONG).show();
//        }
    }

    private void addRecord() {
        if (inputName == null || inputName.getText().toString().equals(""))//что быстрее .equals("") или .length()???)
            inputName.setError("name cannot be empty");
        else if (inputPhone == null || inputPhone.getText().toString().equals(""))
            inputPhone.setError("phone cannot be empty");
        else {
            String name = inputName.getText().toString();
            String phone = inputPhone.getText().toString();


            ContentValues cv = new ContentValues();
            cv.put(DBHelper.KEY_NAME, name);
            cv.put(DBHelper.KEY_PHONE, phone);
            database.insert(TABLE_NAME, null, cv);

            inputName.setText(null);
            inputPhone.setText(null);
            inputName.requestFocus();

            Toast.makeText(this, "record with name = " + name + "\nand phone = " + phone + "\n successfully added", Toast.LENGTH_LONG).show();
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        database.close();
//    }
}
