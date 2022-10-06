package com.fcaro.ebookreaderoverdualtablets;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    PDFView pdfView;
    Button simpleButton1, simpleButton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //PDFView pdfView = null;
        Uri uri = null;

            AssetManager assetManager = getAssets();

            InputStream in = null;

            File pdffile = new File("/sdcard/Download/STA4811.pdf");
            Boolean chkFile = pdffile.exists();
            Log.d("pdffile exists? ::: ", chkFile.toString() );
            pdfView = (PDFView)findViewById(R.id.pdfView);
            pdfView.fromFile(pdffile).load();



        simpleButton1 = (Button) findViewById(R.id.buttonGotoPage100);//get id of button 1

        simpleButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfView.jumpTo(100);
                Toast.makeText(getApplicationContext(),
                        simpleButton1.getText() ,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}