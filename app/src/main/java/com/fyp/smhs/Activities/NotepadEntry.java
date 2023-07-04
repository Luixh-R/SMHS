package com.fyp.smhs.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


import com.fyp.smhs.R;

import com.fyp.smhs.Database.AppDatabase;
import com.fyp.smhs.Database.Journal;
import com.fyp.smhs.Database.JournalDao;

public class NotepadEntry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad_entry);

        Button saveBtn = findViewById(R.id.save_entry);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText titleEditText = findViewById(R.id.entry_title);
                String title = titleEditText.getText().toString();

                EditText bodyEditText= findViewById(R.id.entry_body);
                String body = bodyEditText.getText().toString();

                AppDatabase database = AppDatabase.getDatabase(com.fyp.smhs.Activities.NotepadEntry.this);
                JournalDao journalDao = database.journalDao();

                Journal journal = new Journal(title, body);
                journalDao.insert(journal);

                finish();
            }
        });

        EditText titleEditText = findViewById(R.id.entry_title);
        EditText bodyEditText= findViewById(R.id.entry_body);
        if (getIntent().getStringExtra("Title") != null) {
            titleEditText.setText(getIntent().getStringExtra("Title"));
        }
        if (getIntent().getStringExtra("Body") != null) {
            bodyEditText.setText(getIntent().getStringExtra("Body"));
        }
    }
}
