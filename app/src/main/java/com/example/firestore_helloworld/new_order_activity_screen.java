package com.example.firestore_helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class new_order_activity_screen extends AppCompatActivity {
    private BroadcastReceiver order_fetched_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }

    };

    private BroadcastReceiver order_added_success_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(new_order_activity_screen.this, "addition successful (new order)", Toast.LENGTH_LONG).show();

            // order added, move to loading screen
            new_order_activity_screen.this.save_to_sp();
            finish();
            new_order_activity_screen.this.startActivity(new Intent(new_order_activity_screen.this, loading_screen.class));
        }

    };

    private BroadcastReceiver order_changed_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }

    };

    private BroadcastReceiver order_add_fail_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(new_order_activity_screen.this, "order add failed in (new order)", Toast.LENGTH_LONG).show();

            // failed to add order, show message and re-enable save button

            save_enabled = true;
            Toast.makeText(new_order_activity_screen.this, "addition failed, try again", Toast.LENGTH_LONG).show();

        }

    };


    SharedPreferences mprefs;
    FirestoreHelper inst;
    TextView num_of_pickles_tv;
    EditText comment_et;
    CheckBox tahini_CB;
    CheckBox hummus_CB;
    boolean save_enabled = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_screen);

        //register reciever
        registerReceiver(order_fetched_Receiver, new IntentFilter("order_fetched")); // we get when we download order (when FirestoreHelper loads
        registerReceiver(order_added_success_Receiver, new IntentFilter("order_added_successfully"));
        registerReceiver(order_changed_Receiver, new IntentFilter("order_changed"));
        registerReceiver(order_add_fail_Receiver, new IntentFilter("adding_order_failed"));

        mprefs = this.getSharedPreferences("order_so_far", MODE_PRIVATE);
        inst = FirestoreHelper.get_instance(new_order_activity_screen.this);

        // get views
        num_of_pickles_tv = findViewById(R.id.pickleCountTV);
        comment_et = findViewById(R.id.Comment_ET);
        tahini_CB = findViewById(R.id.tahini_CB);
        hummus_CB = findViewById(R.id.hummus_CB);
    }

    public void save_button_clicked(android.view.View button){
        if (save_enabled) {
            save_enabled = false;
            int pickle_num = Integer.parseInt(num_of_pickles_tv.getText().toString());
            inst.add_order(pickle_num, hummus_CB.isChecked(), tahini_CB.isChecked(),
                    comment_et.getText().toString());
        }

    }

    public void plus_pressed(android.view.View plus_button){
        int prev = Integer.parseInt(num_of_pickles_tv.getText().toString());
        if( prev < 10){
            num_of_pickles_tv.setText(String.valueOf(prev + 1));
        }
    }

    public void minus_pressed(android.view.View plus_button){
        int prev = Integer.parseInt(num_of_pickles_tv.getText().toString());
        if( prev > 0){
            num_of_pickles_tv.setText(String.valueOf(prev - 1));
        }

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pickles_num", this.num_of_pickles_tv.getText().toString());
        outState.putString("comment", this.comment_et.getText().toString());
        outState.putBoolean("tahini_checked?", this.tahini_CB.isChecked());
        outState.putBoolean("hummus_checked?", this.hummus_CB.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.num_of_pickles_tv.setText(savedInstanceState.getString("pickles_num", "0"));
        this.comment_et.setText(savedInstanceState.getString("comment", ""));
        this.tahini_CB.setChecked(savedInstanceState.getBoolean("tahini_checked", false));
        this.hummus_CB.setChecked(savedInstanceState.getBoolean("hummus_checked", false));

    }

    @Override
    protected void onStop() {
        super.onStop();
        save_to_sp();
    }

    private void save_to_sp(){
        SharedPreferences.Editor editor = mprefs.edit();
        editor.putString("pickles_num", this.num_of_pickles_tv.getText().toString());
        editor.putString("comment", this.comment_et.getText().toString());
        editor.putBoolean("tahini_checked?", this.tahini_CB.isChecked());
        editor.putBoolean("hummus_checked?", this.hummus_CB.isChecked());
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mprefs == null) {
            mprefs = this.getSharedPreferences("order_so_far", MODE_PRIVATE);
        }
        this.num_of_pickles_tv.setText(mprefs.getString("pickles_num", "0"));
        this.comment_et.setText(mprefs.getString("comment", ""));
        this.tahini_CB.setChecked(mprefs.getBoolean("tahini_checked?", false));
        this.hummus_CB.setChecked(mprefs.getBoolean("hummus_checked?", false));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(order_added_success_Receiver);
        unregisterReceiver(order_add_fail_Receiver);
        unregisterReceiver(order_fetched_Receiver);
        unregisterReceiver(order_changed_Receiver);
        inst.save_db_to_sp();
    }
}