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

public class edit_order_activity_screen extends AppCompatActivity {
    private BroadcastReceiver order_fetched_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }

    };

    private BroadcastReceiver order_added_success_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }

    };

    private BroadcastReceiver order_changed_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            save_enabled = false;
//            Toast.makeText(edit_order_activity_screen.this, "order changed (edit)", Toast.LENGTH_LONG).show();
            if (!inst.running_order.status.equals("waiting")) {
                finish();
                edit_order_activity_screen.this.startActivity(new Intent(edit_order_activity_screen.this, loading_screen.class));
            }

            save_enabled = true;
            editing_enabled = true;
            cancel_enabled = true;

        }

    };


    private BroadcastReceiver order_edited_success_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            save_enabled = false;
            Toast.makeText(edit_order_activity_screen.this, "changes Saved!", Toast.LENGTH_SHORT).show();
            save_enabled = true;
            editing_enabled = true;
            cancel_enabled = true;

        }

    };

    private BroadcastReceiver order_edited_fail_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            save_enabled = false;
            Toast.makeText(edit_order_activity_screen.this, "order editing failed, try again!", Toast.LENGTH_LONG).show();

            save_enabled = true;
            editing_enabled = true;
            cancel_enabled = true;

        }

    };


    private BroadcastReceiver order_add_fail_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }

    };


    private BroadcastReceiver order_cancel_failed_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(edit_order_activity_screen.this, "order cancel failed", Toast.LENGTH_LONG).show();
            cancel_enabled = true;

        }

    };

    private BroadcastReceiver order_cancel_success_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(edit_order_activity_screen.this, "order cancel successful ", Toast.LENGTH_LONG).show();
            editing_enabled = false;
            save_enabled = false;
            cancel_enabled = false;

            reset_fields();
            finish();
            inst.reset_order_data();
            edit_order_activity_screen.this.startActivity(new Intent(edit_order_activity_screen.this, loading_screen.class));

            save_enabled = true;
            cancel_enabled = true;
            editing_enabled = true;

        }

    };

    SharedPreferences mprefs;
    FirestoreHelper inst;
    TextView num_of_pickles_tv;
    EditText comment_et;
    CheckBox tahini_CB;
    CheckBox hummus_CB;
    boolean save_enabled = true;
    boolean cancel_enabled = true;
    boolean editing_enabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order_screen);

        //register reciever
        registerReceiver(order_fetched_Receiver, new IntentFilter("order_fetched")); // we get when we download order (when FirestoreHelper loads
        registerReceiver(order_added_success_Receiver, new IntentFilter("order_added_successfully"));
        registerReceiver(order_changed_Receiver, new IntentFilter("order_changed"));

        registerReceiver(order_edited_success_Receiver, new IntentFilter("order_edited_successfully"));
        registerReceiver(order_edited_fail_Receiver, new IntentFilter("editing_order_failed"));

        registerReceiver(order_add_fail_Receiver, new IntentFilter("adding_order_failed"));

        registerReceiver(order_cancel_success_receiver, new IntentFilter("order_cancelled_successfully"));
        registerReceiver(order_cancel_failed_receiver, new IntentFilter("order_cancellation_failed"));

        mprefs = this.getSharedPreferences("order_so_far", MODE_PRIVATE);
        inst = FirestoreHelper.get_instance(edit_order_activity_screen.this);

        // get views
        num_of_pickles_tv = findViewById(R.id.pickleCountTV);
        comment_et = findViewById(R.id.Comment_ET);
        tahini_CB = findViewById(R.id.tahini_CB);
        hummus_CB = findViewById(R.id.hummus_CB);

    }


    public void cancel_button_clicked(android.view.View cancel_button) {
        if (cancel_enabled) {
            editing_enabled = false;
            save_enabled = false;
            cancel_enabled = false;
            inst.cancel_order(inst.order_id);
        }

    }

    public void save_button_clicked(android.view.View button) {
        if (save_enabled && editing_enabled && cancel_enabled) {
            editing_enabled = false;
            save_enabled = false;
            cancel_enabled = false;
            int pickle_num = Integer.parseInt(num_of_pickles_tv.getText().toString());
            inst.edit_order(inst.order_id, pickle_num, hummus_CB.isChecked(), tahini_CB.isChecked(),
                    comment_et.getText().toString(), null);
        }
    }

    public void plus_pressed(android.view.View plus_button) {
        if (editing_enabled) {
            int prev = Integer.parseInt(num_of_pickles_tv.getText().toString());
            if (prev < 10) {
                num_of_pickles_tv.setText(String.valueOf(prev + 1));
            }
        }
    }

    public void minus_pressed(android.view.View plus_button) {
        if (editing_enabled) {
            int prev = Integer.parseInt(num_of_pickles_tv.getText().toString());
            if (prev > 0) {
                num_of_pickles_tv.setText(String.valueOf(prev - 1));
            }
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

    private void save_to_sp() {
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

    private void reset_fields() {
        num_of_pickles_tv.setText("0");
        comment_et.setText("");
        tahini_CB.setChecked(false);
        hummus_CB.setChecked(false);
        boolean save_enabled = true;
        boolean cancel_enabled = true;

        save_to_sp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(order_added_success_Receiver);
        unregisterReceiver(order_add_fail_Receiver);
        unregisterReceiver(order_fetched_Receiver);
        unregisterReceiver(order_changed_Receiver);

        unregisterReceiver(order_edited_success_Receiver);
        unregisterReceiver(order_edited_fail_Receiver);

        unregisterReceiver(order_cancel_success_receiver);
        unregisterReceiver(order_cancel_failed_receiver);

        inst.save_db_to_sp();
    }
}