package com.example.firestore_helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class order_in_the_making_screen extends AppCompatActivity {
    /**
     * 3. an "your order is in the making..." screen. when the order status is "in-progress" this
     * will be the only screen that the user will see. in this screen there will be some
     * explanation, like "Rachel is working on your order, she will let you know once it is
     * ready." if you want to add nice enhancements it could be cool. like an animation of
     * a ketchup being poured into a sandwich. or just a loading scrollbar. whatever you want
     * to give the user a feeling of "you have nothing to do but Rachel is working hard on
     * your sandwich"
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress_screen);
    }
}