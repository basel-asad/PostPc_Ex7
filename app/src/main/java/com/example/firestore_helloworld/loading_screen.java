package com.example.firestore_helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

public class loading_screen extends AppCompatActivity {

    private BroadcastReceiver order_added_success_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(loading_screen.this, "order added scuccess in (loading)", Toast.LENGTH_LONG).show();

//            finish();
//            next_screen();
        }

    };

    private BroadcastReceiver order_changed_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if(context.getClass() == loading_screen.class) {
                Toast.makeText(loading_screen.this, "order changed (loading)", Toast.LENGTH_LONG).show();

                finish();
                next_screen(false, fh); //todo: do we want to do this here? order isnt supposed to change when were in this window?
            }
//        }

    };

    private BroadcastReceiver order_fetched_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(loading_screen.this, "fetched (from loading)", Toast.LENGTH_LONG).show();

//            finish();
//            next_screen();
        }

    };

    private BroadcastReceiver no_order_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(loading_screen.this, "noooo order (loading)", Toast.LENGTH_SHORT).show();
            finish();
            // get new order
            loading_screen.this.startActivity(new Intent(loading_screen.this, new_order_activity_screen.class));

        }

    };


    private BroadcastReceiver order_add_fail_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(loading_screen.this, "order add failed ", Toast.LENGTH_LONG).show();


            finish();
            loading_screen.this.startActivity(new Intent(loading_screen.this, new_order_activity_screen.class));

        }

    };











    FirestoreHelper fh;

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
        String status = fh.running_order.status;
//            finish();
        go_to_order_screen(status); //todo: swicth screens

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this,"loading done", Toast.LENGTH_SHORT).show();

        unregisterReceiver(order_added_success_Receiver);
        unregisterReceiver(order_add_fail_Receiver);
        unregisterReceiver(order_fetched_Receiver);
        unregisterReceiver(order_changed_Receiver);

        unregisterReceiver(no_order_Receiver);

        this.fh.save_db_to_sp();

    }

//    protected void restart_order(){
//        this.fh.order_id = "";
//        this.fh.running_order = null;
//        this.fh.save_db_to_sp();
//        // show "add new order
//        finish();
//        this.startActivity(new Intent(this.getApplicationContext(), new_order_activity_screen.class));
//    }

    protected void go_to_order_screen(String status) {
        finish();
        if (status.equals("waiting")) {
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
        else  if(status.equals("cancelled")){
            Toast.makeText(loading_screen.this, "cancelled! ", Toast.LENGTH_LONG).show();
            // make new order
            this.startActivity(new Intent(this, new_order_activity_screen.class));

        }
        else{
            Toast.makeText(this, "wtfff!!!!!!!", Toast.LENGTH_SHORT).show();
            //todo: throw this
            //- no order / order with status "done": delete the order id from the phone and show screen "add new order"
//            throw new Exception("not supposed to happen");

        }
    }
}