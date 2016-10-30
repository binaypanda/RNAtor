package in.ganitlabs.rnator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Date;

import in.ganitlabs.rnator.Helpers.DataBaseHelper;

public class SplashActivity extends AppCompatActivity implements Config{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LoadDB().execute();
    }

    private class LoadDB extends AsyncTask<Void, Void, Void> {

        private boolean allOK = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            long startTime = new Date().getTime();
            try {
                new DataBaseHelper(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
                allOK = false;
                return null;
            }
            long timeLeft = SPLASH_TIME_MIN - (new Date().getTime() - startTime);
            if (timeLeft > 0){
                try {
                    Thread.sleep(timeLeft);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(allOK) {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
            }
            finish();
        }

    }
}