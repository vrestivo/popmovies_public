package com.example.android.popmoviesstage2;


import android.content.Context;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;


/**
 * Simple test class that clicks on the 3rd grid view item
 */

@RunWith(AndroidJUnit4.class)

public class MyTest {

    private Context context;

    @Before
    public void setContext(){
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void clickThirdGridItem(){
        onData(instanceOf(MovieItem.class)).inAdapterView(
                allOf(ViewMatchers.withId(R.id.id_grid_view) ,isDisplayed()))
                .atPosition(3).perform(click());
    }
}
