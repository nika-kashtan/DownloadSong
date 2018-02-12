package com.example.nika.downloadsong;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText ourUrl;
    private ProgressBar progressBar;
    private TextView error;
    ParseSongToExternal parseSongToExternal;
    String songPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ourUrl = (EditText) findViewById(R.id.ourUrl);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        error = (TextView) findViewById(R.id.error);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSong(v);
            }
        });
    }

    //Method that execute AsyncTasks
    public void saveSong(View v) {
        songPath = ourUrl.getText().toString();
        if (!songPath.equals("") && songPath.contains("mp3") && songPath.contains("http")) {
            parseSongToExternal = new ParseSongToExternal();
            parseSongToExternal.execute();
            progressBar.setVisibility(View.VISIBLE);
        } else {
            error.setVisibility(View.VISIBLE);
            error.setText("Неверный URL-адрес");
            Snackbar.make(v, "Неверный URL-адрес. Проверьте на содержание http", Snackbar.LENGTH_LONG)
                    .setAction("Неверный URL-адрес", null).show();
        }
    }

    //AsyncTask to Parse song from link to External Storage
    private class ParseSongToExternal extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;
            try {
                final String[] separated = songPath.split("/");
                final String myFile = separated[separated.length - 1];
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri downloadUri = Uri.parse(songPath);
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setAllowedOverRoaming(false);
                request.setTitle(songPath);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, myFile);
                request.allowScanningByMediaScanner();
                downloadManager.enqueue(request);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.INVISIBLE);
            if (result) {
                error.setVisibility(View.VISIBLE);
                error.setText("Успешно скачали песню в " + Environment.DIRECTORY_MUSIC);
            } else {
                error.setVisibility(View.VISIBLE);
                error.setText("Пришел пустой ответ от сервера");
            }

        }

    }

}
