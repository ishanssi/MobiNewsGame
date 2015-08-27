package com.cognizant.mobinewsgame;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {


    public static int FullScore = 0;


    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    private final long startTime = 11 * 1000;

    private final long interval = 1 * 1000;
    public static int index = 0;
    public static Context ctx;


    String strUrl = "https://dl.dropboxusercontent.com/u/30107414/game.json";
    private Button btnAnswer1;
    private Button btnAnswer2;
    private Button btnAnswer3;
    private Button btnSkip;
    private ImageView imagview;
    private Button btnSectionName;

    private int CorrectAnswer;
    private int SelectedAnswer = 0;
    private TextView text;
    public static String imageurl = null;
    public static String MainTitle = null;
    public boolean wronganswer = true;
    public int minusvalue = 0;
    String Storeyurl;

    public int tickvalue;


    SharedPreferences sharedPref;

    private boolean skipped = false;
    private ProgressDialog progress;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countDownTimer = new MyCountDownTimer(startTime, interval);
        sharedPref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = sharedPref.edit();
        findViews();

        ctx=this;
         int savedindex=sharedPref.getInt("index", 0);

        if(savedindex<=0)
        {

            DownloadData();

        }

        else
        {
         if(JsonClass.MainnewsObjects==null)
                {
                    editor.putInt("index", 0);
                    editor.commit();
                    DownloadData();

                }
            else {
                    LoadValues(savedindex);
                }

        }


    }

    public void DownloadData()
    {

        progress = ProgressDialog.show(this, "Please wait!",
                "Loading Data ..", true);

        // Creating a new non-ui thread task to download json data
        DownloadTask downloadTask = new DownloadTask();

        // Starting the download process
        downloadTask.execute(strUrl);

    }

    private void LoadValues(int index) {

        wronganswer = true;
        minusvalue = 0;
        //  skipped=false;
        timerHasStarted = false;

        CorrectAnswer = JsonClass.MainnewsObjects.get(index).getCorrectIndex();

        btnAnswer1.setText(JsonClass.MainnewsObjects.get(index).getHeadline1());
        btnAnswer2.setText(JsonClass.MainnewsObjects.get(index).getHeadline2());
        btnAnswer3.setText(JsonClass.MainnewsObjects.get(index).getHeadline3());
        btnSectionName.setText(JsonClass.MainnewsObjects.get(index).getSection());

        Storeyurl = JsonClass.MainnewsObjects.get(index).getStoryUrl();

        MainTitle = JsonClass.MainnewsObjects.get(index).getMaintitle();
        imageurl = JsonClass.MainnewsObjects.get(index).getImageUrl();


//Download image Task
        new DownloadImageTask(imagview,1)
                .execute(imageurl);


    }

    private void findViews() {


        btnAnswer1 = (Button) findViewById(R.id.button3);
        btnAnswer2 = (Button) findViewById(R.id.button2);
        btnAnswer3 = (Button) findViewById(R.id.button1);
        btnSkip = (Button) findViewById(R.id.button4);

        btnAnswer1.setOnClickListener(this);
        btnAnswer2.setOnClickListener(this);
        btnAnswer3.setOnClickListener(this);
        btnSkip.setOnClickListener(this);


        text = (TextView) this.findViewById(R.id.textView2);


        btnSectionName = (Button) findViewById(R.id.button5);
        imagview = (ImageView) findViewById(R.id.imageView1);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button3:
                SelectedAnswer = 0;

                break;
            case R.id.button2:
                SelectedAnswer = 1;

                break;
            case R.id.button1:
                SelectedAnswer = 2;

                break;

            case R.id.button4:

                skipped = true;

                break;

        }


        if (skipped) {



            countDownTimer.cancel();

            ShowFinishedDialog(imageurl, 0, MainTitle, "ooops");
            skipped = false;
        } else if (SelectedAnswer == CorrectAnswer) {




            countDownTimer.cancel();
            timerHasStarted = false;
            ShowFinishedDialog(imageurl, tickvalue + minusvalue, MainTitle, "That's Right");


        } else {

            if (wronganswer) {


                Toast.makeText(MainActivity.this, " points deducted =  -2", Toast.LENGTH_LONG).show();
                minusvalue = -2;

                wronganswer = false;
            }





        }

    }

    private void ShowFinishedDialog(String imageurl, int points, String title, String headtitle) {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.finisheddialog);

       dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;


        dialog.setCancelable(false);
        TextView txtpoints = (TextView) dialog.findViewById(R.id.mytextView6);
        TextView txtheadtitle = (TextView) dialog.findViewById(R.id.textView3);
        TextView txtTitle = (TextView) dialog.findViewById(R.id.textView6);
        txtpoints.setText(String.valueOf(points));


        txtheadtitle.setText(headtitle);

        FullScore = FullScore + points;



        txtTitle.setText(title);

        ImageView image = (ImageView) dialog.findViewById(R.id.imageView3);


        // download image for the popup
        new DownloadImageTask(image,0)
                .execute(imageurl);


        Button btnnextlevel = (Button) dialog.findViewById(R.id.button6);

        Button btncancel = (Button) dialog.findViewById(R.id.button8);
        Button btnreadfullarticle = (Button) dialog.findViewById(R.id.buttonmore);
        Button btnleaderboard = (Button) dialog.findViewById(R.id.button7);

        btnreadfullarticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Storeyurl));
                startActivity(i);


            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  dialog.setCancelable(true);
                dialog.dismiss();
                LoadValues(index);

            }
        });
        // if button is clicked, close the custom dialog
        btnnextlevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  dialog.setCancelable(true);
                dialog.dismiss();
                LoadValues(++index );

            }
        });

        btnleaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  dialog.setCancelable(true);
                startActivity(new Intent(MainActivity.this,Leaderboard.class));

            }
        });


        dialog.show();

    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception ", e.toString());
        } finally {
            iStream.close();
        }

        return data;
    }


    /**
     * AsyncTask to download json data
     */
    private class DownloadTask extends AsyncTask<String, Integer, String> {
        String data = null;

        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {

            JsonClass jsonobjclass = new JsonClass();
            jsonobjclass.getvalues(result);
            LoadValues(index);
            progress.dismiss();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    /**
     * Timer Class
     */
    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {

            Toast.makeText(MainActivity.this, "TImes Up", Toast.LENGTH_LONG).show();
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(1000);

            ShowFinishedDialog(imageurl, 0, MainTitle, "Times Up!");


        }

        @Override
        public void onTick(long millisUntilFinished) {
            text.setText("Time Remaining: " + millisUntilFinished / 1000 + " Sec");
            tickvalue = Integer.parseInt(String.valueOf(millisUntilFinished / 1000));


        }
    }


    /**
     * A method for timer 10 sec
     */
    public void StartTimer() {
        if (!timerHasStarted) {

            countDownTimer.start();

            timerHasStarted = true;


        } else {

            countDownTimer.cancel();

            timerHasStarted = false;



        }

    }



    @Override
    protected void onPause() {
        super.onPause();

        countDownTimer.cancel();
        editor.putInt("index", index);
        editor.commit();


    }

    @Override
    protected void onResume() {
        super.onResume();

        timerHasStarted = false;
        StartTimer();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        int thisx=0;

        public DownloadImageTask(ImageView bmImage, int x) {
            this.bmImage = bmImage;
            this.thisx=x;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            if(thisx==1)
            {
                timerHasStarted = false;
                StartTimer();

            }


        }
    }
}