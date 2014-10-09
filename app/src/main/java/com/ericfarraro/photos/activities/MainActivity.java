package com.ericfarraro.photos.activities;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ericfarraro.photos.R;
import com.ericfarraro.photos.fragments.MainFragment;


public class MainActivity extends Activity {

    protected final String MAIN_FRAGMENT_TAG = "MainFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = getFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
        if(fragment == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment(), MAIN_FRAGMENT_TAG)
                    .commit();
        }
    }
}
