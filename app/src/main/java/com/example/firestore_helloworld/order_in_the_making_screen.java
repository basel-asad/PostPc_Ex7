package com.example.firestore_helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

public class order_in_the_making_screen extends AppCompatActivity {

    private BroadcastReceiver order_changed_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(order_in_the_making_screen.this, "order changed (in_the_making)", Toast.LENGTH_LONG).show();

            if(!inst.running_order.status.equals("waiting")){
                finish();
                order_in_the_making_screen.this.startActivity(new Intent(order_in_the_making_screen.this, loading_screen.class));
            }


        }

    };
    /**
     * 3. an "your order is in the making..." screen. when the order status is "in-progress" this
     * will be the only screen that the user will see. in this screen there will be some
     * explanation, like "Rachel is working on your order, she will let you know once it is
     * ready." if you want to add nice enhancements it could be cool. like an animation of
     * a ketchup being poured into a sandwich. or just a loading scrollbar. whatever you want
     * to give the user a feeling of "you have nothing to do but Rachel is working hard on
     * your sandwich"
     */

    FirestoreHelper inst;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress_screen);

        registerReceiver(order_changed_Receiver, new IntentFilter("order_changed"));

        inst = FirestoreHelper.get_instance(order_in_the_making_screen.this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(order_changed_Receiver);
    }
}