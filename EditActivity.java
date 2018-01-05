package com.example.addy.zapisuha;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static com.example.addy.zapisuha.DBHelper.KEY_ID;
import static com.example.addy.zapisuha.DBHelper.KEY_NAME;
import static com.example.addy.zapisuha.DBHelper.KEY_PHONE;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    TextView textId;
    EditText name, phone;
    Button send;
    int inputId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent();
        inputId = intent.getIntExtra(KEY_ID, 666);//String
        String inputName = intent.getStringExtra(KEY_NAME);
        String inputPhone = intent.getStringExtra(KEY_PHONE);

        textId = findViewById(R.id.textId);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        send = findViewById(R.id.send);

        textId.setText("id=" + inputId);
        name.setText(inputName);
        phone.setText(inputPhone);

        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (name.getText() == null || name.getText().toString().length() == 0)
            name.setError("cannot be empty");
        else if (phone.getText() == null || phone.getText().toString().length() == 0)
            phone.setError("cannot be empty");
        else {
            Intent intent = new Intent();
            intent.putExtra(KEY_ID, inputId);
            intent.putExtra(KEY_NAME, name.getText().toString());
            intent.putExtra(KEY_PHONE, phone.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
