package com.example.firestore_helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

public class loading_screen extends AppCompatActivity {
    FirestoreHelper fh;

    private BroadcastReceiver order_added_success_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }

    };

    private BroadcastReceiver order_changed_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                finish();
                next_screen(false, fh);
            }
//        }

    };

    private BroadcastReceiver order_fetched_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // add listener to order
            fh.keep_tabs(fh.order_id);
        }

    };

    private BroadcastReceiver no_order_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
            // get new order
            loading_screen.this.startActivity(new Intent(loading_screen.this, new_order_activity_screen.class));
        }

    };


    private BroadcastReceiver order_add_fail_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
            loading_screen.this.startActivity(new Intent(loading_screen.this, new_order_activity_screen.class));

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        //register reciever
        registerReceiver(order_fetched_Receiver, new IntentFilter("order_fetched")); // we get when we download order (when FirestoreHelper loads
        registerReceiver(order_added_success_Receiver, new IntentFilter("order_added_successfully"));
        registerReceiver(order_changed_Receiver, new IntentFilter("order_changed"));

        registerReceiver(order_add_fail_Receiver, new IntentFilter("adding_order_failed"));

        registerReceiver(no_order_Receiver, new IntentFilter("no_order_found"));

        fh = FirestoreHelper.get_instance(loading_screen.this);

    }

    public void next_screen(boolean finish, FirestoreHelper fh_){
        if(fh==null){
            fh = fh_;
        }
        if(finish){
            finish();
        }
        if(fh.running_order != null) {
            go_to_order_screen(fh.running_order.status, fh.running_order.customer_name);
        }
        else{
            // set a new username
            go_to_order_screen("", "");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(order_added_success_Receiver);
        unregisterReceiver(order_add_fail_Receiver);
        unregisterReceiver(order_fetched_Receiver);
        unregisterReceiver(order_changed_Receiver);

        unregisterReceiver(no_order_Receiver);

        this.fh.save_db_to_sp();

    }

    protected void go_to_order_screen(String status, String client_name) {
        finish();
        if(client_name.equals("") && status.equals("")){
            // show "edit order" screen
            this.startActivity(new Intent(this, Username_activity.class));
        }
        else if (status.equals("waiting")) {
            // show "edit order" screen
            this.startActivity(new Intent(this, edit_order_activity_screen.class));
        }
        else if (status.equals("in-progress")) {
            //show "order in the making" screen
            this.startActivity(new Intent(this, order_in_the_making_screen.class));

        }
        else if (status.equals("ready")) {
            //show "order is ready" screen
            this.startActivity(new Intent(this, order_ready_activity_screen.class));

        }
        else if(status.equals("cancelled") || status.equals("done")){
//            Toast.makeText(loading_screen.this, "cancelled/done ! ", Toast.LENGTH_LONG).show();
            // make new order
            this.startActivity(new Intent(this, new_order_activity_screen.class));

        }
        else{
            Toast.makeText(this, "status unidentified : '" + status + "'", Toast.LENGTH_SHORT).show();
        }
    }
}