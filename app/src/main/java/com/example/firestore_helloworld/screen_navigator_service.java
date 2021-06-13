//package com.example.firestore_helloworld;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.Activity;
//import android.app.IntentService;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.widget.Toast;
//
//public class screen_navigator_service extends IntentService {
//
//    private BroadcastReceiver order_status_update_fetched_Receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // todo: what to do when recieving
//            // move to next activity
//            screen_navigator_service.this.go_to_order_screen(firestore_db_instance.get_instance());
////            MainActivity.handle_successful_login(null, firestore_db_instance.get_instance(), false);
//
//
//
//        }
//    };
//
//    private BroadcastReceiver order_status_update_failed_Receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // todo: what to do when recieving
//            // move to next activity
//            screen_navigator_service.this.restart_order(firestore_db_instance.get_instance());
//
//
//        }
//    };
//
//    /**
//     * Creates an IntentService.  Invoked by your subclass's constructor.
//     *
//     * @param name Used to name the worker thread, important only for debugging.
//     */
//    public screen_navigator_service(String name) {
//        super(name);
//    }
//
//    /**
//     * Creates an IntentService.  Invoked by your subclass's constructor.
//     *
//     * @param name Used to name the worker thread, important only for debugging.
//     */
//    public screen_navigator_service() {
//        super("");
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
//
//
//        registerReceiver(order_status_update_fetched_Receiver, new IntentFilter("updated_order_data_successfully"));
//        registerReceiver(order_status_update_failed_Receiver, new IntentFilter("updated_order_data_fail"));
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        unregisterReceiver(order_status_update_fetched_Receiver);
//        unregisterReceiver(order_status_update_failed_Receiver);
//    }
//
//
//
//
//
//
//
//
//
//
//
//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//        //todo: ??????????????????????????????????????????????????????????????????????????????????????????
//        if(intent.getBooleanExtra("starting_app", false)){
//            handle_successful_login(null,firestore_db_instance.get_instance(), false);
//        }
//        else{
//            //todo:: what to do?
//        }
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//    private void handle_successful_login(Activity activity, firestore_db_instance inst, boolean finish){
//        if(inst != null) {
//            if (finish) {
//                activity.finish();
//            }
//
//            //todo: decide which activity to move to
//            if (!inst.order_id.equals("")) {
//                inst.set_running_order(inst.order_id);
//                //todo: the reciever should call other func
//
//            }
//            else {
//                //- no order / order with status "done": delete the order id from the phone and show screen "add new order"
//                restart_order(inst);
//
//            }
//        }
//
//    }
//
//    private void go_to_order_screen(firestore_db_instance inst){
//        String status = inst.running_order.get("status").toString(); //todo: get updated status
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
//            restart_order(inst);
//
//        }
//    }
//
//    private void restart_order(firestore_db_instance inst){
//        inst.order_id = "";
//        inst.running_order = null;
//        inst.save_db_to_sp();
//        // show "add new order
//        this.startActivity(new Intent(this.getApplicationContext(), new_order_activity_screen.class));
//    }
//
//
//
//}