package com.example.firestore_helloworld;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;
import static java.lang.System.load;
import static java.util.UUID.randomUUID;

//singleton
class FirestoreHelper extends AppCompatActivity {
    static private FirestoreHelper fh;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Context context;


    String client_name = "";
    String order_id = "";
    protected Order running_order = null;
    protected static LiveData<Order> orderLiveData = null;
    static ListenerRegistration registration = null;



    private FirestoreHelper(){
//        this.save_db_to_sp();  todo: make this
    }

    public static FirestoreHelper get_instance(Context context){
        if (fh == null){
            fh = new FirestoreHelper();
            fh.context = context; // needed to send broadcasts
            fh.load_sp_to_dp();  //todo: make this
        }
        else {
            fh.context = context; // needed to send broadcasts
        }
        if (context.getClass() == loading_screen.class) {
            if (!fh.order_id.equals("")) {
                if(fh.orderLiveData == null) { //todo: fh.orderLiveData.getValue()?
                    fh.keep_track_of_order(fh.order_id); //todo: is this necessary?
                }
                else{
                    ((loading_screen) context).next_screen(true, fh);
                }
            }
            else if(fh.client_name.equals("")) {
                ((loading_screen) context).next_screen(true, fh);
            }
            else{
                fh.send_broadcast("no_order_found", "", context);
            }
        }


//        if(fh.context != context){
//            fh.load_sp_to_dp();  //todo: make this
//        }


//        else if(fh.context != context){
//            fh.context = context; // needed to send broadcasts
//            if(fh.running_order != null && fh.running_order.status.equals("cancelled")){
////                if (context != ((new_order_activity_screen) fh.context)) {
//                if (context.getClass() != new_order_activity_screen.class) {
//                    fh.send_broadcast("no_order_found", "", fh.context);
//                }
//                else{
////                    Toast.makeText(context, "same actovoty avoided", Toast.LENGTH_SHORT).show();
//                }
//            }
//            else if (fh.running_order != null){
//                fh.keep_track_of_order(fh.order_id);
//            }
//        }
        return fh;
    }


    public void keep_tabs(String order_id){
        //todo: kill listener when cancelling order
        Toast.makeText(context, "listeningggggg strat", Toast.LENGTH_SHORT).show();

        DocumentReference docRef = firestore.collection("orders").document(order_id);
        unregister_listener();
        registration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed: " + e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {

                    Toast.makeText(context, "listeningggggg update", Toast.LENGTH_SHORT).show();
                    String status = (String) documentSnapshot.getData().get("status");
                    if(status.equals("done")){
                        // kill listener
                        unregister_listener();

                    }
                    else if(! status.equals(fh.running_order.status)){
                        // status changed
                        send_broadcast("order_changed", order_id, context);

                    }
                    fh.running_order = new Order(documentSnapshot.getData());
                    orderLiveData = fh._downloadOrder(order_id);


                    System.out.println("Current data: " + documentSnapshot.getData());
                } else {
                    System.out.print("Current data: null");
                }
            }
        });
    }

    private void unregister_listener(){
        if(registration != null){
            // Stop listening to changes
            registration.remove();
            registration = null;
        }
    }

    public boolean keep_track_of_order(String order_id) {
        if(order_id.equals("")){
            return false;
        }
        else {
//            if(orderLiveData == null || !running_order.unique_order_id.equals(order_id)) {
//                orderLiveData = fh._downloadOrder(order_id);
//            }
            orderLiveData = fh._downloadOrder(order_id);
//            orderLiveData.observe(this, new Observer<Order>(){});
            orderLiveData.observeForever(new Observer<Order>() {
                @Override
                public void onChanged(Order order) {
                    if (order == null) {
                        // no order yet… this is invoked from the first "null" value inside the LiveData...
                        // TODO: set UI to "loading" state
//                        Toast.makeText(context.getApplicationContext(), "this order doesnt exist yet", Toast.LENGTH_SHORT).show();
//                        reset_order_data();
                    }
                    else {
//                        Toast.makeText(context.getApplicationContext(), "this order changed (firestorehelper)", Toast.LENGTH_SHORT).show();

                        // someone put the order Inside the LiveData! And we got it! Yay :D
                        fh.running_order = order;
                        fh.order_id = order.unique_order_id;
                        fh.save_db_to_sp();
//                        String status = order.status;
                        send_broadcast("order_changed", order_id, context);

//                        try {
//                            go_to_order_screen(status); //todo: swicth screens???????????????????????????????????????
//                        } catch (Exception e) {
//                            //todo: get rid of this
//                            e.printStackTrace();
//                            exit(666);
//                        }
                    }

                }
            });
            return true;
        }
    }

    public LiveData<Order> _downloadOrder(String OrderId) {
        MutableLiveData<Order> liveData = new MutableLiveData<>();


//        if(running_order != null) {
//            liveData.setValue(running_order);
//        }
//        else{
//            liveData.setValue(null);
//        }

        liveData.setValue(null);


        firestore.collection("orders").document(OrderId).get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    send_broadcast("order_fetched", order_id, context);
                    Map<String, Object> order_data = documentSnapshot.getData();
                    Order order = new Order(order_data);
                    order_id = OrderId;
                    running_order = order;
                    liveData.setValue(order);

//                    //todo: change liveData from herE?
//                    orderLiveData = liveData;
                }
            }
        });
        return liveData;
    }

    public void add_order(int num_of_pickles, boolean hummus, boolean tahini,
                          String comment){
        /**
         * id: (string) some random-generated string which will be the order's id
         * customer-name: (string) name of the person who ordered the sandwich (so Rachel could know who to give it to)
         * pickles: (int) amount of pickles in the sandwich. in the range [0,10]
         * hummus: (boolean) should we add hummus
         * tahini: (boolean) should we add tahini
         * comment: (string) comments from the customer if they want to write anything
         * status: (string) status of the order. could be one of "waiting", "in-progress", "ready" and "done". when you upload the order, you upload it with state "waiting". as long as the state is still "waiting" you can edit the order. once Rachel starts working on the order she will change the state to "in-progress" and you can't edit the order anymore. once she is in campus she will change the state to "ready" which means that the customer can come grab the sandwich. once you showed the customer that the sandwich is ready, you change the state to "done" and both you and Rachel can stop looking at this order.
         *
         * You and Rachel agreed that you will both read & write the sandwich orders to the below collection in firestore:
         * orders/
         * in this collection you will add documents. each document is a description of a sandwich order (as described above)
         *
         * For example, to create a new sandwich order with the random-generated id "a6sf3", you should upload it to "orders/a6sf3", and have the order's "id" field to be "a6sf3".
         */

        // random id
        String unique_order_id = randomUUID().toString(); //todo: make shurter id's?

        Map<String, Object> order = new HashMap<>();
        order.put("id", unique_order_id);
        order.put("customer-name", this.client_name);
        order.put("pickles", num_of_pickles);
        order.put("hummus", hummus);
        order.put("tahini", tahini);
        order.put("comment", comment);
        order.put("status", "waiting");


        // Add a new document with a generated ID
        firestore.collection("orders").document(unique_order_id).set(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fh.order_id = unique_order_id;
                        fh.running_order = new Order(order);
                        fh.save_db_to_sp();
                        keep_track_of_order(unique_order_id);

                        //todo: go to edit order activity

                        send_broadcast("order_added_successfully", unique_order_id, context);

//                        FirestoreHelper.this.save_db_to_sp();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error adding document"+ e, Toast.LENGTH_SHORT).show();
                //todo: failed to create new order
                send_broadcast("adding_order_failed", unique_order_id, context);

            }
        });

    }

    public void edit_order(String unique_order_id, int num_of_pickles, boolean hummus, boolean tahini,
                          String comment, String new_status){

        Map<String, Object> order = new HashMap<>();
        order.put("id", unique_order_id);
        order.put("customer-name", this.client_name);
        order.put("pickles", num_of_pickles);
        order.put("hummus", hummus);
        order.put("tahini", tahini);
        order.put("comment", comment);
        if(new_status != null) {
            order.put("status", new_status);
        }


        // Add a new document with a generated ID
        firestore.collection("orders").document(unique_order_id).update(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirestoreHelper.this.order_id = unique_order_id;
                        keep_track_of_order(unique_order_id); //todo: re-enable?
//                        FirestoreHelper.this.running_order = new Order(order);

                        send_broadcast("order_edited_successfully", unique_order_id, context);

//                        FirestoreHelper.this.save_db_to_sp();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //todo: failed to overwrite order
                send_broadcast("editing_order_failed", unique_order_id, context);

            }
        });
    }



    public void cancel_order(String unique_order_id){


        // Add a new document with a generated ID
        firestore.collection("orders").document(unique_order_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                send_broadcast("order_cancelled_successfully", unique_order_id, context);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                send_broadcast("order_cancellation_failed", unique_order_id, context);

            }
        });
    }


//    public void edit_order(String unique_order_id, String Field_name, Object field_Value){
//
//        Map<String, Object> order = new HashMap<>();
//        order.put(Field_name, field_Value);
//
//
//
//        // Add a new document with a generated ID
//        firestore.collection("orders").document(unique_order_id).update(order)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        FirestoreHelper.this.order_id = unique_order_id;
//                        FirestoreHelper.this.running_order = new Order(order);
//                        keep_track_of_order(unique_order_id);
//
//                        send_broadcast("order_edited_successfully", unique_order_id, context);
//
////                        FirestoreHelper.this.save_db_to_sp();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                //todo: failed to overwrite order
//                send_broadcast("editing_order_failed", unique_order_id, context);
//
//            }
//        });
//    }



    protected void send_broadcast(String action, String order_id, Context context){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("order_id",order_id); // we can pass data in the intent
        context.sendBroadcast(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        load_sp_to_dp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregister_listener();
        this.save_db_to_sp();
    }

    public void save_db_to_sp(){
        SharedPreferences mprefs = context.getSharedPreferences("saved_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = mprefs.edit();
        editor.putString("client_name", this.client_name);
        editor.putString("order_id", this.order_id);
//        if(!this.order_id.equals("")) {
//            // save order so far
//            Gson gson = new Gson();
//            String order_json = gson.toJson(this.running_order);
//            editor.putString("running_order", order_json);
//        }

        editor.apply();

    }

    public void load_sp_to_dp(){
        SharedPreferences mprefs = context.getSharedPreferences("saved_data", MODE_PRIVATE);
        this.client_name = mprefs.getString("client_name", "");
        this.order_id = mprefs.getString("order_id", "");

//        if(!order_id.equals("")) {
//            // get order
//            Gson gson = new Gson();
//            String json = mprefs.getString("running_order", "");
//            Map<String, Object> obj = gson.fromJson(json, Map.class); //todo: get from db instad
//            // todo: fetch updated order from firestore?
//            this.running_order = obj;
////            order_id = (String) running_order.get("id"); //todo: do we need this?
//        }


    }

    public void reset_order_data(){
        if (fh != null) {
            fh.order_id = "";
            fh.running_order = new Order();
            FirestoreHelper.orderLiveData = null;
            unregister_listener();
        }
        save_db_to_sp();
    }


}

