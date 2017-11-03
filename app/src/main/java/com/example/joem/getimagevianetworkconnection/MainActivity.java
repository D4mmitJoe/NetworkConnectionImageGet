package com.example.joem.getimagevianetworkconnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonCheckConnection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()){
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    //need to pass 'execute' a string, in this case a URL of an image
                    new GetImageAsync((ImageView) findViewById(R.id.imageView)).execute("https://upload.wikimedia.org/wikipedia/commons/6/66/Android_robot.png");
                }else{
                    Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //function enables us to check status of internet
    private boolean isConnected(){
        //ConnectivityManager allows us to get current network information
        ConnectivityManager connectivityManagerName = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfoName = connectivityManagerName.getActiveNetworkInfo();//returns network info

        //checking to see if there's an internet connection
        if (networkInfoName == null || !networkInfoName.isConnected() ||
                (networkInfoName.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfoName.getType() != ConnectivityManager.TYPE_MOBILE)){
            return false;
        }
        return true;
    }
    //1st parameter: URL of image, 2nd: void progress reporting, 3rd: return (changed from "Bitmap" (for image) to "Void"
        //some images maybe too large to pass through messages, so adjustments made
    private class GetImageAsync extends AsyncTask<String, Void, Void>{

        ImageView imageViewName;
        Bitmap bitmapName = null;
        public GetImageAsync(ImageView iv) {
            imageViewName = iv;
        }

        @Override
        protected Void doInBackground(String... paramsName) {
            HttpURLConnection connectionName = null;
            bitmapName = null;
            try {
                //URL passed as parameter
                //A lot of network calls can cause exceptions so should contain try-catch block
                URL urlName = new URL(paramsName[0]);
                connectionName = (HttpURLConnection)urlName.openConnection();//gets connection
                connectionName.connect();
                //'responseCode' checks if status is 'ok,' hence ".http_ok"
                if (connectionName.getResponseCode() == HttpURLConnection.HTTP_OK){
                    bitmapName = BitmapFactory.decodeStream(connectionName.getInputStream());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connectionName != null){
                    connectionName.disconnect();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if (bitmapName != null && imageViewName != null){
                imageViewName.setImageBitmap(bitmapName);
            }
        }
    }
}