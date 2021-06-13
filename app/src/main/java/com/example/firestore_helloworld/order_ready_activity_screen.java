package com.example.firestore_helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class order_ready_activity_screen extends AppCompatActivity {
    /**
     * 4. An "your order is ready!" screen. when the order status is "ready" this will be the only screen that the user will see.
     * in this screen: let the user know that Rachel is in campus with their sandwich. the user will have some way of acknowledge (for example, a "got it" button). once the user acknowledge, we will mark this order as done and will navigate the user to the "new order" screen.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_done_screen);
    }

    public void got_it_pressed(android.view.View button){
        //todo: back to main screen

    }
}