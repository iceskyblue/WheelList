package com.example.adminstrator.wheellist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WheelView wheel = new WheelView(this);
        wheel.setItems(new String[]{"one", "tow", "three", "four", "five", "6", "7", "8", "9"});
        wheel.setItemHeight(300);

        RelativeLayout rootView = (RelativeLayout)this.findViewById(R.id.root);
        rootView.addView(wheel, new RelativeLayout.LayoutParams(800, 1500));
    }
}
