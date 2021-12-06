package com.example.guessthecelebrityv2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    String []splitStrings;
    ArrayList<String> celebritiesNames;
    ArrayList<String> celebritiesURLs;
    Random random;
    int randCelebrityName;
    int randCelebrityURLAndName;
    Button btns[];
    ImageView myImage;
    //button clicked
    public void selectAnswer(View view)
    {
        Button btn = (Button) view;
        if(btn.getText().equals(celebritiesNames.get(randCelebrityURLAndName)))
            Toast.makeText(MainActivity.this, "correct", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this, "incorrect", Toast.LENGTH_SHORT).show();

        //start loading the new image
        startAgain();

    }
    class ContentDownload extends AsyncTask<String , Void, String>
    {
        @Override
        protected String doInBackground(String... urls) {
            String res="";

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    res += current;
                    data = reader.read();
                }
                //split the web content
                splitStrings = res.split("<div class=\"listedArticles\">");
                //search for the pattern to get the urls of the celebrities
                Pattern p = Pattern.compile("<img src=\"(.*?)\"");
                Matcher m = p.matcher(splitStrings[0]);
                while (m.find()) {
                    celebritiesURLs.add(m.group(1));
                }
                //search for the pattern to get the names of the celebrities
                p = Pattern.compile("alt=\"(.*?)\"");
                m = p.matcher(splitStrings[0]);

                while (m.find()) {
                    celebritiesNames.add(m.group(1));
                }

                //remove unwanted data
                for (int i = 0; i < 4; i++) {
                    celebritiesNames.remove(0);
                    celebritiesURLs.remove(0);
                }
                //print the celebrities names and urls
                for (int i = 0; i < celebritiesNames.size(); i++)
                    System.out.println(celebritiesNames.get(i));

                for (int i = 0; i < celebritiesURLs.size(); i++)
                    System.out.println(celebritiesURLs.get(i));


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    class DownloadImage extends AsyncTask<String , Void , Bitmap>
    {
        //download image from the website
        @Override
        protected Bitmap doInBackground(String... urls) {
            try
            {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in=connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        random = new Random();
        celebritiesURLs = new ArrayList<>();
        celebritiesNames = new ArrayList<>();
        myImage = findViewById(R.id.imageView);
        btns = new Button[4];
        btns[0] = findViewById(R.id.button0);
        btns[1] = findViewById(R.id.button1);
        btns[2] = findViewById(R.id.button2);
        btns[3] = findViewById(R.id.button3);

        ContentDownload contentDownload = new ContentDownload();
        try {
            contentDownload.execute("https://web.archive.org/web/20190119082828/www.posh24.se/kandisar").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        startAgain();
    }

    public void startAgain()
    {
        randCelebrityURLAndName = random.nextInt(celebritiesURLs.size());
        try {
            DownloadImage downloadImage = new DownloadImage();
            Bitmap myBitmap = downloadImage.execute(celebritiesURLs.get(randCelebrityURLAndName)).get();
            myImage.setImageBitmap(myBitmap);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        int randomButton = random.nextInt(4);
        btns[randomButton].setText(celebritiesNames.get(randCelebrityURLAndName));
        for (int i = 0; i < 4; i++) {
            randCelebrityName =random.nextInt(celebritiesNames.size());
            if (i != randomButton)
                btns[i].setText(celebritiesNames.get(randCelebrityName));
        }
    }



}