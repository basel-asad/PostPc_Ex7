//package com.example.firestore_helloworld;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.Observer;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.gson.Gson;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static java.lang.System.exit;
//import static java.util.UUID.randomUUID;
//
//public class firestore_db_instance extends AppCompatActivity {
//
//    static firestore_db_instance instance = null;
//    FirebaseFirestore db = null;
//    private SharedPreferences mprefs;
//    String client_name = "";
//
//    Map<String, Object> running_order = null;
//    String order_id = "";
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        //todo: what to do here?
//        mprefs = this.getSharedPreferences("saved_data", MODE_PRIVATE);
//        load_sp_to_dp();
//        instance = this;
//        // load main activity
//        //todo: decide where to jump
//        if(this.client_name.equals("")) {
//            Intent myIntent = new Intent(firestore_db_instance.this, MainActivity.class);
//            firestore_db_instance.this.startActivity(myIntent);
//        }
//        else{
//            //find proper place to go to
//            if(this.order_id.equals("")){
//                //jump to new order
//                this.startActivity(new Intent(this, new_order_activity_screen.class));
//
//            }
//            else{
//                //get status from fire-store, jump to proper window
////                reload_order();
//                keep_track_of_order(this.order_id);
//                //todo: the screen navigator should move us to next screen
////                Intent intent = new Intent(this, screen_navigator_service.class); // explicit intent
////                intent.putExtra("starting_app", true);
////                this.startService(intent);
////                MainActivity.handle_successful_login(firestore_db_instance.this, firestore_db_instance.this, false);
//
//            }
//        }
//    }
//
//    public firestore_db_instance(){
//        // prepare firestore
////        mprefs = this.getSharedPreferences("saved_data", MODE_PRIVATE);
////        load_sp_to_dp();
//        // define firestore
//        if (db ==  null) {
//            init_db();
////            db = FirebaseFirestore.getInstance();
//        }
//    }
//
//    private void init_db(){
//        db = FirebaseFirestore.getInstance(); //todo: check for errors
//    }
//
//    public void set_name(String name){
//        if(this.client_name.equals("")){
//            this.client_name = name;
//
//        }
//    }
//
//    public static firestore_db_instance get_instance(){
//        if (firestore_db_instance.instance == null){
//            instance = new firestore_db_instance(); //todo: check for errors
//        }
//        return instance;
//    }
//
//
//
//
//    public void add_order(int num_of_pickles, boolean hummus, boolean tahini,
//                           String comment){
//        /**
//         * id: (string) some random-generated string which will be the order's id
//         * customer-name: (string) name of the person who ordered the sandwich (so Rachel could know who to give it to)
//         * pickles: (int) amount of pickles in the sandwich. in the range [0,10]
//         * hummus: (boolean) should we add hummus
//         * tahini: (boolean) should we add tahini
//         * comment: (string) comments from the customer if they want to write anything
//         * status: (string) status of the order. could be one of "waiting", "in-progress", "ready" and "done". when you upload the order, you upload it with state "waiting". as long as the state is still "waiting" you can edit the order. once Rachel starts working on the order she will change the state to "in-progress" and you can't edit the order anymore. once she is in campus she will change the state to "ready" which means that the customer can come grab the sandwich. once you showed the customer that the sandwich is ready, you change the state to "done" and both you and Rachel can stop looking at this order.
//         *
//         * You and Rachel agreed that you will both read & write the sandwich orders to the below collection in firestore:
//         * orders/
//         * in this collection you will add documents. each document is a description of a sandwich order (as described above)
//         *
//         * For example, to create a new sandwich order with the random-generated id "a6sf3", you should upload it to "orders/a6sf3", and have the order's "id" field to be "a6sf3".
//         */
//        if(db == null){
//            init_db();
//        }
//
//        // random id
//        String unique_order_id = randomUUID().toString();
//
//        Map<String, Object> user = new HashMap<>();
//        user.put("id", unique_order_id);
//        user.put("customer-name", this.client_name);
//        user.put("pickles", num_of_pickles);
//        user.put("hummus", hummus);
//        user.put("tahini", tahini);
//        user.put("comment", comment);
//        // status: (string) status of the order. could be one of
//        // "waiting", "in-progress", "ready" and "done".
//        // when you upload the order, you upload it with state "waiting". as long as the state is
//        // still "waiting" you can edit the order. once Rachel starts working on the order she will
//        // change the state to "in-progress" and you can't edit the order anymore.
//        // once she is in campus she will change the state to "ready" which means that the
//        // customer can come grab the sandwich. once you showed the customer that the
//        // sandwich is ready, you change the state to "done" and both you and Rachel can
//        // stop looking at this order.
//        user.put("status", "waiting"); //todo: always start order with "waiting"?
//
//
//        // Add a new document with a generated ID
//        db.collection("orders").document(unique_order_id).set(user)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                send_broadcast("order_saved_successfully", unique_order_id, firestore_db_instance.this);
////                        Toast.makeText(firestore_db_instance.instance.getApplicationContext(), "DocumentSnapshot added with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
//                firestore_db_instance.this.order_id = unique_order_id;
//                firestore_db_instance.this.running_order = user;
//                firestore_db_instance.this.save_db_to_sp();
////                set_running_order(unique_order_id);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(firestore_db_instance.instance.getApplicationContext(), "Error adding document"+ e, Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//    }
//
//    private void send_broadcast(String action, String order_id, Context context){
//        Intent intent = new Intent();
//        intent.setAction(action);
//        intent.putExtra("order_id",order_id); // we can pass data in the intent
//        if(context == null){
//            context=this.getApplicationContext();
//        }
//        context.sendBroadcast(intent);
//    }
//
////    private void get_orders(){
////        //todo: get specific document?
////        db.collection("orders")
////                .get()
////                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////                    @Override
////                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                        if (task.isSuccessful()) {
////                            for (QueryDocumentSnapshot document : task.getResult()) {
////                                Toast.makeText(firestore_db_instance.instance.getApplicationContext(), document.getId() + " => " + document.getData(), Toast.LENGTH_SHORT).show();
////                            }
////                        } else {
////                            Toast.makeText(firestore_db_instance.instance.getApplicationContext(), "Error getting documents."+ task.getException(), Toast.LENGTH_SHORT).show();
////                        }
////                    }
////                });
////
////
////    }
//
//    public void keep_track_of_order(String order_id) {
//        FirestoreHelper fh = new FirestoreHelper();
//        LiveData<Order> orderLiveData = fh._downloadOrder(order_id);
//        orderLiveData.observe(this, new Observer<Order>() {
//            @Override
//            public void onChanged(Order order) {
//                if (order == null) {
//                    // no wallet yet… this is invoked from the first "null" value inside the LiveData...
//                    // TODO: set UI to "loading" state
//                    // show "add new order
////                    firestore_db_instance.this.startActivity(new Intent(firestore_db_instance.this, new_order_activity_screen.class));
//                    //todo: was this right?
//                }
//                else {
//                    // someone put the wallet Inside the LiveData! And we got it! Yay :D
//                    // TODO: set UI based on the wallet data
//                    String status = order.status;
//                    try {
//                        go_to_order_screen(status);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        exit(666); //todo: get rid of this
//                    }
//                }
//
//            }
//        });
//    }
//
//
//    private void go_to_order_screen(String status) throws Exception {
//        if (status.equals("waiting")) {
//            // show "edit order" screen
//            this.startActivity(new Intent(this, edit_order_activity_screen.class));
//        } else if (status.equals("in-progress")) {
//            //show "order in the making" screen
//            this.startActivity(new Intent(this, order_in_the_making_screen.class));
//
//        } else if (status.equals("ready")) {
//            //show "order is ready" screen
//            this.startActivity(new Intent(this, order_ready_activity_screen.class));
//
//        } else {
//            //- no order / order with status "done": delete the order id from the phone and show screen "add new order"
//            throw new Exception("not supposed to happen");
//
//        }
//    }
//
//    public void set_running_order(String order_id){
//
//        if(db == null){
//            init_db();
//        }
//        if (db != null) {
//            db.collection("orders").document(order_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    send_broadcast("updated_order_data_successfully", order_id, firestore_db_instance.this);
//                    firestore_db_instance.this.running_order = documentSnapshot.getData();
//                    Toast.makeText(firestore_db_instance.instance.getApplicationContext(), "updated successfully  "+order_id, Toast.LENGTH_SHORT).show();
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    send_broadcast("updated_order_data_fail", order_id, firestore_db_instance.this);
//
//                    Toast.makeText(firestore_db_instance.instance.getApplicationContext(), "failed to find "+order_id, Toast.LENGTH_SHORT).show();
//                    if(firestore_db_instance.this.running_order == null || firestore_db_instance.this.order_id.equals("")){
//                        firestore_db_instance.this.running_order = null;
//                        firestore_db_instance.this.order_id = "";
//                        //todo: go back to new order screen
//
//                    }
//                }
//            });
//
//        }
//
//    }
//
//
//
//    public void save_db_to_sp(){
////        if(mprefs == null){
////            mprefs = this.getSharedPreferences("saved_data", MODE_PRIVATE);
////
////        }
//        SharedPreferences.Editor editor = mprefs.edit();
//        editor.putString("client_name", this.client_name);
//        editor.putString("order_id", this.order_id);
//        if(!this.order_id.equals("")) {
//            // save order so far
//            Gson gson = new Gson();
//            String order_json = gson.toJson(this.running_order);
//            editor.putString("running_order", order_json);
//        }
//
//        editor.apply();
//
//    }
//
//    public void load_sp_to_dp(){
//        mprefs = this.getSharedPreferences("saved_data", MODE_PRIVATE);
//        this.client_name = mprefs.getString("client_name", "");
//        this.order_id = mprefs.getString("order_id", "");
//        if(!order_id.equals("")) {
//            // get order
//            Gson gson = new Gson();
//            String json = mprefs.getString("running_order", "");
//            Map<String, Object> obj = gson.fromJson(json, Map.class); //todo: get from db instad
//            // todo: fetch updated order from firestore?
//            this.running_order = obj;
////            order_id = (String) running_order.get("id"); //todo: do we need this?
//        }
//
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        load_sp_to_dp();
//    }
//
////    @Override
////    protected void onResume() {
////        super.onResume();
////
////        load_sp_to_dp();
////    }
//
////    @Override
////    protected void onPause() {
////        super.onPause();
////
////        this.save_db_to_sp();
////
////    }
////
////    @Override
////    protected void onStop() {
////        super.onStop();
////        this.save_db_to_sp();
////    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        this.save_db_to_sp();
//    }
//
//    //    @Override
////    protected void onSaveInstanceState(@NonNull Bundle outState) {
////        super.onSaveInstanceState(outState);
////
////        save_db_to_sp();
////    }
////
////    @Override
////    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
////        super.onRestoreInstanceState(savedInstanceState);
////
////        load_sp_to_dp();
////    }
//}
