package com.example.firestore_helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class order_ready_activity_screen extends AppCompatActivity {

    private BroadcastReceiver order_edited_success_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //reset order & start a new order screen
            start_new_order();
        }

    };

    private BroadcastReceiver order_edited_fail_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(order_ready_activity_screen.this, "order editing failed, try again", Toast.LENGTH_SHORT).show();
        }

    };

    FirestoreHelper inst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_done_screen);

        registerReceiver(order_edited_success_Receiver, new IntentFilter("order_edited_successfully"));
        registerReceiver(order_edited_fail_Receiver, new IntentFilter("editing_order_failed"));


        inst = FirestoreHelper.get_instance(order_ready_activity_screen.this);

    }

    public void got_it_pressed(android.view.View button){
        // mark done
        Order order = inst.running_order;
        inst.edit_order(order.unique_order_id,order.num_of_pickles,order.hummus,order.tahini,
                order.comment,"done");
        //reset order and go to new order when 'order_edited_success_Receiver' is triggered
//        start_new_order(); // done from broadcast receiver
    }

    private void start_new_order(){
        // reset order
        save_empty_order_to_sp();
        inst.reset_order_data();
        // go to loading (no order)
        // (leads to new order page)
        finish();
        order_ready_activity_screen.this.startActivity(new Intent(order_ready_activity_screen.this, loading_screen.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(order_edited_success_Receiver);
        unregisterReceiver(order_edited_fail_Receiver);

    }

    private void save_empty_order_to_sp(){
        SharedPreferences mprefs = this.getSharedPreferences("order_so_far", MODE_PRIVATE);
        SharedPreferences.Editor editor = mprefs.edit();
        editor.putString("pickles_num", "0");
        editor.putString("comment", "");
        editor.putBoolean("tahini_checked?", false);
        editor.putBoolean("hummus_checked?", false);
        editor.apply();
    }
}