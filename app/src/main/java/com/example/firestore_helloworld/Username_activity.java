package com.example.firestore_helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class Username_activity extends AppCompatActivity {
    public EditText name_edit_text;
    FirestoreHelper inst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inst = FirestoreHelper.get_instance(this);


        if(!inst.client_name.equals("")){
            finish();
            this.startActivity(new Intent(this, loading_screen.class));
        }
        else{
            name_edit_text = findViewById(R.id.name_EditText);

        }
    }

    public void start_profile(android.view.View v){
        String potential_name = name_edit_text.getText().toString().trim();
        if (name_edit_text != null && name_valid(potential_name)){
            finish();
            inst.client_name = potential_name;
            // show "loading screen" screen
            this.startActivity(new Intent(this, loading_screen.class));
        }

    }

    private boolean name_valid(String text) {
        return (! text.equals(""));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inst.save_db_to_sp();
    }
}