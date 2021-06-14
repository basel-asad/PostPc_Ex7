package com.example.firestore_helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Username_activity extends AppCompatActivity {
//    public FirebaseFirestore db;
    public EditText name_edit_text;
    FirestoreHelper inst;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inst = FirestoreHelper.get_instance(this);


        if(!inst.client_name.equals("")){
            finish();
//            handle_successful_login(MainActivity.this, inst, true);
            this.startActivity(new Intent(this, loading_screen.class));

        }
        else{
            name_edit_text = findViewById(R.id.name_EditText);

        }
    }

    public void start_profile(android.view.View v){
        //todo: if name is valid and non-empty, start activity
        String potential_name = name_edit_text.getText().toString().trim();
        if (name_edit_text != null && name_valid(potential_name)){
            inst.client_name = potential_name;
            // show "loading screen" screen
            this.startActivity(new Intent(this, loading_screen.class));
//            handle_successful_login(MainActivity.this, inst, true);
        }
        //if name isn't defined well, do nothing, make toast

    }

    private boolean name_valid(String text) {
        return (! text.equals(""));
    }

//    static void handle_successful_login(Activity activity, FirestoreHelper inst, boolean finish){
//        if(inst != null) {
//            if (finish) {
//                activity.finish();
//            }
//            // go to loading page?
//
//            //todo: decide which activity to move to
//            if (!inst.order_id.equals("")) {
//                inst.set_running_order(inst.order_id);
//                //todo: the reciever should call other func
//
//            }
//            else {
//                //- no order / order with status "done": delete the order id from the phone and show screen "add new order"
//                restart_order(inst, activity);
//
//            }
//        }
//
//    }

//    static void go_to_order_screen(firestore_db_instance inst, Activity activity){
//        String status = inst.running_order.get("status").toString(); //todo: get updated status
//        if (status.equals("waiting")) {
//            // show "edit order" screen
//            activity.startActivity(new Intent(activity.getApplicationContext(), edit_order_activity_screen.class));
//        } else if (status.equals("in-progress")) {
//            //show "order in the making" screen
//            activity.startActivity(new Intent(activity.getApplicationContext(), order_in_the_making_screen.class));
//
//        } else if (status.equals("ready")) {
//            //show "order is ready" screen
//            activity.startActivity(new Intent(activity.getApplicationContext(), order_ready_activity_screen.class));
//
//        } else {
//            //- no order / order with status "done": delete the order id from the phone and show screen "add new order"
//            restart_order(inst, activity);
//
//        }
//    }

//    static void restart_order(firestore_db_instance inst, Activity activity){
//        inst.order_id = "";
//        inst.running_order = null;
//        inst.save_db_to_sp();
//        // show "add new order
//        activity.startActivity(new Intent(activity.getApplicationContext(), new_order_activity_screen.class));
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        inst.save_db_to_sp();
    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        inst.save_db_to_sp();
//    }




}