package com.example.firestore_helloworld;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;

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


    private FirestoreHelper() {
    }

    public static FirestoreHelper get_instance(Context context) {
        if (fh == null) {
            fh = new FirestoreHelper();
            fh.context = context; // needed to send broadcasts
            fh.load_sp_to_dp();
        } else {
            fh.context = context; // needed to send broadcasts
        }
        if (context.getClass() == loading_screen.class) {
            called_from_load_screen(context);
        }

        return fh;
    }

    private static void called_from_load_screen(Context context) {
        if (!fh.order_id.equals("")) {
            if (fh.orderLiveData == null) {
                fh.keep_track_of_order(fh.order_id);
            } else {
                ((loading_screen) context).next_screen(true, fh);
            }
        } else if (fh.client_name.equals("")) {
            ((loading_screen) context).next_screen(true, fh);
        } else {
            fh.send_broadcast("no_order_found", "", context);
        }
    }


    public void keep_tabs(String order_id) {
//        Toast.makeText(context, "listening started", Toast.LENGTH_SHORT).show();

        DocumentReference docRef = firestore.collection("orders").document(order_id);
        // unregister previous listener
        unregister_listener();
        // register listener
        registration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed: " + e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {

//                    Toast.makeText(context, "listeningggggg update", Toast.LENGTH_SHORT).show();
                    String status = (String) documentSnapshot.getData().get("status");
                    if (status.equals("done")) {
                        // kill listener
                        unregister_listener();

                    } else if (!status.equals(fh.running_order.status)) {
                        // status changed
                        send_broadcast("order_changed", order_id, context);
                    }
                    fh.running_order = new Order(documentSnapshot.getData());
                    orderLiveData = fh._downloadOrder(order_id);
//                    System.out.println("Current data: " + documentSnapshot.getData());
                }
//                else {
//                    System.out.print("Current data: null");
//                }
            }
        });
    }

    private void unregister_listener() {
        if (registration != null) {
            // Stop listening to changes
            registration.remove();
            registration = null;
        }
    }

    public void keep_track_of_order(String order_id) {
        if (!order_id.equals("")) {
            orderLiveData = fh._downloadOrder(order_id);
            orderLiveData.observeForever(new Observer<Order>() {
                @Override
                public void onChanged(Order order) {
                    if (order != null) {
                        // someone put the order Inside the LiveData! And we got it! Yay :D
//                        Toast.makeText(context.getApplicationContext(), "this order changed (firestorehelper)", Toast.LENGTH_SHORT).show();
                        fh.running_order = order;
                        fh.order_id = order.unique_order_id;
                        fh.save_db_to_sp();
                        send_broadcast("order_changed", order_id, context);
                    }
                }
            });
        }
    }

    public LiveData<Order> _downloadOrder(String OrderId) {
        MutableLiveData<Order> liveData = new MutableLiveData<>();
        liveData.setValue(null); // initial value
        // get order
        firestore.collection("orders").document(OrderId).get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            send_broadcast("order_fetched", order_id, context);
                            Map<String, Object> order_data = documentSnapshot.getData();
                            // save data
                            Order order = new Order(order_data);
                            order_id = OrderId;
                            running_order = order;
                            liveData.setValue(order);
                        }
                    }
                });
        return liveData;
    }

    public void add_order(int num_of_pickles, boolean hummus, boolean tahini,
                          String comment) {
        // random id
        String unique_order_id = randomUUID().toString();
        // other data
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
                        send_broadcast("order_added_successfully", unique_order_id, context);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(context, "Error adding document" + e, Toast.LENGTH_SHORT).show();
                send_broadcast("adding_order_failed", unique_order_id, context);

            }
        });

    }

    public void edit_order(String unique_order_id, int num_of_pickles, boolean hummus, boolean tahini,
                           String comment, String new_status) {
        /**
         * if new status is provided, its also edited in the order
         */

        Map<String, Object> order = new HashMap<>();
        order.put("id", unique_order_id);
        order.put("customer-name", this.client_name);
        order.put("pickles", num_of_pickles);
        order.put("hummus", hummus);
        order.put("tahini", tahini);
        order.put("comment", comment);
        if (new_status != null) {
            order.put("status", new_status);
        }

        // Add a new document with a generated ID
        firestore.collection("orders").document(unique_order_id).update(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirestoreHelper.this.order_id = unique_order_id;
                        keep_track_of_order(unique_order_id);
                        send_broadcast("order_edited_successfully", unique_order_id, context);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                send_broadcast("editing_order_failed", unique_order_id, context);
            }
        });
    }


    public void cancel_order(String unique_order_id) {

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


    protected void send_broadcast(String action, String order_id, Context context) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("order_id", order_id); // we can pass data in the intent
        context.sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregister_listener();
        this.save_db_to_sp();
    }

    public void save_db_to_sp() {
        SharedPreferences mprefs = context.getSharedPreferences("saved_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = mprefs.edit();
        editor.putString("client_name", this.client_name);
        editor.putString("order_id", this.order_id);
        editor.apply();
    }

    public void load_sp_to_dp() {
        SharedPreferences mprefs = context.getSharedPreferences("saved_data", MODE_PRIVATE);
        this.client_name = mprefs.getString("client_name", "");
        this.order_id = mprefs.getString("order_id", "");
    }

    public void reset_order_data() {
        if (fh != null) {
            fh.order_id = "";
            fh.running_order = new Order();
            FirestoreHelper.orderLiveData = null;
            unregister_listener();
        }
        save_db_to_sp();
    }


}

