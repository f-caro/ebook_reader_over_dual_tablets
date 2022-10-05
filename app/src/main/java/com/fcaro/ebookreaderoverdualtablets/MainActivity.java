package com.fcaro.ebookreaderoverdualtablets;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    PDFView pdfView;

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



//        new Thread(new Runnable() {
//            public void run(){
//                if (pdffile.exists() && pdffile.canRead()) {
//                    pdfView.fromFile(pdffile).load();
//                }
//            }
//        }).start();

            //pdfView.fromAsset("STA4811.pdf");

//        try {
//            pdfView.fromStream( assetManager.open("STA4811.pdf") ) ;
//            //pdfView.fromStream( (InputStream) getAssets().open("STA4811.pdf") );
//        }
//        catch(IOException ex){
//            return;
//        }

    }
}