package com.cognizant.mobinewsgame;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ishanswarnajith on 20/08/15.
 */
public class JsonClass {

    public static List<NewsValuesCLass> MainnewsObjects;


    public void getvalues(String jsonresult) {
        JSONArray JsonItemsarray = null;
        JSONArray HeadlinesArray = null;
        NewsValuesCLass newsobj;

        MainnewsObjects = new ArrayList<NewsValuesCLass>();

        try {
            JSONObject jsonResponse = new JSONObject(jsonresult);


            JsonItemsarray = jsonResponse.getJSONArray("items");


            for (int i = 0; i < JsonItemsarray.length(); i++) {


                /****** Get Object for each JSON node.***********/
                JSONObject jsonChildNodeobj = JsonItemsarray.getJSONObject(i);


                String CorrectAnswer = jsonChildNodeobj.getString("correctAnswerIndex");
                String Maintitle = jsonChildNodeobj.getString("standFirst");
                String section = jsonChildNodeobj.getString("section");
                String StoryUrl = jsonChildNodeobj.getString("storyUrl");
                String imageUrl = jsonChildNodeobj.getString("imageUrl");


                HeadlinesArray = jsonChildNodeobj.getJSONArray("headlines");
                String headline1 = HeadlinesArray.get(0).toString();
                String headline2 = HeadlinesArray.get(1).toString();
                String headline3 = HeadlinesArray.get(2).toString();

                newsobj = new NewsValuesCLass();
                newsobj.setCorrectIndex(Integer.parseInt(CorrectAnswer));
                newsobj.setMaintitle(Maintitle);
                newsobj.setSection(section);
                newsobj.setStoryUrl(StoryUrl);
                newsobj.setImageUrl(imageUrl);
                newsobj.setHeadline1(headline1);
                newsobj.setHeadline2(headline2);
                newsobj.setHeadline3(headline3);

                MainnewsObjects.add(newsobj);


            }






        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
