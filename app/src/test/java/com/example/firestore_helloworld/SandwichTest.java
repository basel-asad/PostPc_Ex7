package com.example.firestore_helloworld;

import android.app.Application;
import android.widget.Button;
import android.widget.TextView;


import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;


import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class)
public class SandwichTest {

    private final int MAX_PICKLES = 10;
    private final int MIN_PICKLES = 0;
    ActivityController<new_order_activity_screen> activityController;
    new_order_activity_screen activityUnderTest;
    //    private Button save_button;
    private Button plus_pickles_button;
    private Button minus_pickles_button;
    private TextView pickles_text;

    @Before
    public void setup() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getContext());
        activityController = Robolectric.buildActivity(new_order_activity_screen.class);
        activityUnderTest = activityController.get();
        activityController.create().start().resume();
//        save_button = activityUnderTest.findViewById(R.id.save_button);
        plus_pickles_button = activityUnderTest.findViewById(R.id.plusButton);
        minus_pickles_button = activityUnderTest.findViewById(R.id.minusButton);
        pickles_text = activityUnderTest.findViewById(R.id.pickleCountTV);

    }

    @Test
    public void plus_pickles_pressed_once_test() {
        // pressing plus pickles button increases by 1 (as long were less than 10)
        int pickles_prev = Integer.parseInt(pickles_text.getText().toString()); // should be 0
        plus_pickles_button.performClick();
        assertEquals(pickles_prev + 1, Integer.parseInt(pickles_text.getText().toString()));
    }

    @Test
    public void minus_pickles_pressed_once_test() {
        // presseing plus pickles button increases by 1 (as long were less than 10)
        int pickles_prev = Integer.parseInt(pickles_text.getText().toString()); // should be 0
        plus_pickles_button.performClick();
        assertEquals(pickles_prev + 1, Integer.parseInt(pickles_text.getText().toString()));
        // minus
        minus_pickles_button.performClick();
        assertEquals(pickles_prev, Integer.parseInt(pickles_text.getText().toString()));
    }

    @Test
    public void max_pickles_test() {
        //TEST
        // perform 20 clicks
        for (int i = 0; i < MAX_PICKLES * 2; i++) {
            plus_pickles_button.performClick();
        }

        // maximal pickles we can get is 10
        assertEquals(MAX_PICKLES, Integer.parseInt(pickles_text.getText().toString()));


    }

    @Test
    public void min_pickles_test() {
        //SETUP
        // perform 10 clicks
        for (int i = 0; i < MAX_PICKLES; i++) {
            plus_pickles_button.performClick();
        }

        // maximal pickles we can get is 10
        assertEquals(MAX_PICKLES, Integer.parseInt(pickles_text.getText().toString()));

        //TEST
        // perform 20 more clicks
        for (int i = 0; i < MAX_PICKLES * 2; i++) {
            minus_pickles_button.performClick();
        }

        // maximal pickles we can get is 10
        assertEquals(MIN_PICKLES, Integer.parseInt(pickles_text.getText().toString()));
    }


}

