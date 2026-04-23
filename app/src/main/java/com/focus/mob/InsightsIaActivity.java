package com.focus.mob;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class InsightsIaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights_ia);

        NavigationUtils.setupBottomNavigation(this, NavigationUtils.Tab.INSIGHTS_IA);
    }
}
