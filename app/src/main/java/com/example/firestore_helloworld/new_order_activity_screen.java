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
//            Toast.makeText(new_order_activity_screen.this, "order fetched in (new order)", Toast.LENGTH_LONG).show();
//
//            // abort current order if other order is in proccess
//            new_order_activity_screen.this.save_to_sp();
//            finish();
//            new_order_activity_screen.this.startActivity(new Intent(new_order_activity_screen.this, loading_screen.class));
        }

    };

    private BroadcastReceiver order_added_success_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(new_order_activity_screen.this, "addition successful (new order)", Toast.LENGTH_LONG).show();

            // order added, move to loading screen
            new_order_activity_screen.this.save_to_sp();
            finish();
            new_order_activity_screen.this.startActivity(new Intent(new_order_activity_screen.this, loading_screen.class));
        }

    };

    private BroadcastReceiver order_changed_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(new_order_activity_screen.this, "order changed in (new order)", Toast.LENGTH_LONG).show();
//
//            // todo: order changed, what chenged? all of it? ( is this needed here? )
//            finish();
//            new_order_activity_screen.this.startActivity(new Intent(new_order_activity_screen.this, loading_screen.class));
        }

    };

    private BroadcastReceiver order_add_fail_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(new_order_activity_screen.this, "order add failed in (new order)", Toast.LENGTH_LONG).show();

            // failed to add order, show message and re-enable save button

            save_enabled = true;
            Toast.makeText(new_order_activity_screen.this, "addition failed, try again", Toast.LENGTH_LONG).show();

        }

    };


    /**
     * 1. A "new order" screen to add a new order.
     * If there is no order currently running, when the customer opens the app they will see this page.
     * Here they can select how many pickles, tahini, hummus and hand-writing comments.
     *
     * a "save" button will let the user confirm the order and you will upload it to
     *      firestore, and then you will navigate the user to... -->
     */

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

//        tahini_CB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//               @Override
//               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
//
//               }
//           }
//        );

    }

    public void save_button_clicked(android.view.View button){
        if (save_enabled) {
            save_enabled = false;
            // todo: check all data is provided
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
        // todo: save screen info

        outState.putString("pickles_num", this.num_of_pickles_tv.getText().toString());
        outState.putString("comment", this.comment_et.getText().toString());
        outState.putBoolean("tahini_checked?", this.tahini_CB.isChecked());
        outState.putBoolean("hummus_checked?", this.hummus_CB.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // todo: load screen info

        this.num_of_pickles_tv.setText(savedInstanceState.getString("pickles_num", "0"));
        this.comment_et.setText(savedInstanceState.getString("comment", "0"));
        this.tahini_CB.setChecked(savedInstanceState.getBoolean("tahini_checked", false));
        this.hummus_CB.setChecked(savedInstanceState.getBoolean("hummus_checked", false));

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        inst.load_sp_to_dp();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        this.save_db_to_sp();
//
//    }

    @Override
    protected void onStop() {
        //todo: is this even needed? doesnt onstop get called inside firestore_db_instance?
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
        this.comment_et.setText(mprefs.getString("comment", "0"));
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