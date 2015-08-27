package com.cognizant.mobinewsgame;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Leaderboard extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        TextView txtscore=(TextView)findViewById(R.id.txtlb);
        txtscore.setText("Your Score is : "+MainActivity.FullScore);
    }




}
