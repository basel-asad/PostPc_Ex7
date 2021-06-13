package com.example.firestore_helloworld;

import java.util.Map;

public class Order {


    public String customer_name;
    public String unique_order_id;
    public int num_of_pickles;
    public boolean hummus;
    public boolean tahini;
    public String comment;
    public String status;

    public Order(String customer_name, String unique_order_id, int num_of_pickles, boolean hummus,
                 boolean tahini, String comment, String status){
        this.customer_name = customer_name;
        this.unique_order_id = unique_order_id;
        this.num_of_pickles = num_of_pickles;
        this.hummus = hummus;
        this.tahini = tahini;
        this.comment = comment;
        this.status = status;

    }

    public Order(Map<String, Object> order_data){
        this.customer_name = (String) order_data.get("customer-name");
        this.unique_order_id = (String) order_data.get("id");

        try {
            this.num_of_pickles = (Integer) order_data.get("pickles");
        }
        catch (Exception e) {
            this.num_of_pickles = Math.toIntExact((Long) order_data.get("pickles"));
        }
        this.hummus = (boolean) order_data.get("hummus");
        this.tahini = (boolean) order_data.get("tahini");
        this.comment = (String) order_data.get("comment");
        this.status = (String) order_data.get("status");

    }


    public Order(){
        // create empty order with status cancelled
            this.customer_name = "";
            this.unique_order_id = "";
            this.num_of_pickles = -1;
            this.hummus = false;
            this.tahini = false;
            this.comment = "";
            this.status = "cancelled";

    }
}
