package com.pokidin.a.notekeeper.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pokidin.a.notekeeper.R;
import com.pokidin.a.notekeeper.viewmodel.NoteViewModel;

public class NoteDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "reply";
    public static final String EXTRA_REPLY_ID = "reply_id";

    private EditText mEditText;
    private Button mButton;
    private Bundle extras;
    private NoteViewModel mNoteViewModel;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        mEditText = findViewById(R.id.et_text);
        extras = getIntent().getExtras();
        if (extras != null) {
            String text = extras.getString(MainActivity.EXTRA_DATA_UPDATE_NOTE, "");
            if (!text.isEmpty()) {
                mEditText.setText(text);
                mEditText.setSelection(text.length());
                mEditText.requestFocus();
            }
        }

        mButton = findViewById(R.id.btn_done);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setExtraReply();
        super.onBackPressed();
    }

    private void setExtraReply() {
        Intent replyIntent = new Intent();
        if (TextUtils.isEmpty(mEditText.getText())) {
            setResult(RESULT_CANCELED, replyIntent);
        } else {
            String text = mEditText.getText().toString();
            replyIntent.putExtra(EXTRA_REPLY, text);
            if (extras != null && extras.containsKey(MainActivity.EXTRA_DATA_ID)) {
                id = extras.getInt(MainActivity.EXTRA_DATA_ID, -1);
                if (id != -1) {
                    replyIntent.putExtra(EXTRA_REPLY_ID, id);
                }
            }
            setResult(RESULT_OK, replyIntent);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_details, menu);
        getMenuInflater().inflate(R.menu.share_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            removeCurrentNote();
            return true;
        }
        if (id == R.id.action_share) {
            shareNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeCurrentNote() {
        mNoteViewModel.deleteNote(id);
        onBackPressed();
    }

    private void shareNote() {
        if (TextUtils.isEmpty(mEditText.getText())) {
            Toast.makeText(this, R.string.unable_to_share, Toast.LENGTH_SHORT).show();
        } else {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, mEditText.getText());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
    }
}
