package com.fcaro.ebookreaderoverdualtablets;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

public class EbookReaderActivity extends Activity {
    PDFView pdfView;
    Button simpleButton1, simpleButton2;
    Button jumpLeftButton, jumpRightButton ;
    int leftTabletPage, rightTabletPage ;
    String m_bluetoothName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook_reader);


        leftTabletPage = 1;
        rightTabletPage = 2;

        AssetManager assetManager = getAssets();

        InputStream in = null;

        File pdffile = new File("/sdcard/Download/STA4811.pdf");
        Boolean chkFile = pdffile.exists();
        Log.d("pdffile exists? ::: ", chkFile.toString() );
        pdfView = (PDFView)findViewById(R.id.pdfView);
        pdfView.fromFile(pdffile).load();

        jumpLeftButton = (Button) findViewById(R.id.jumpLeftButton);
        jumpRightButton = (Button) findViewById(R.id.jumpRightButton);
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

    public void PageJumpLeft(View v) {
        if(leftTabletPage - 2 > 0) { leftTabletPage -= 2; }
        if(rightTabletPage - 2 > 0) { rightTabletPage -= 2; }
        String msgSend = leftTabletPage  + "," + rightTabletPage;
        Log.e("MAinActivity :: PageJumpLeft() :", ""+  msgSend );
        SendMessage(msgSend);
        byte[] bytes = msgSend.getBytes(Charset.defaultCharset());
        ((MainActivity.ConnectedThread) MainActivity.mConnectedThread).write(bytes);
    }

    public void PageJumpRight(View v) {
        if(leftTabletPage + 2 > 0) { leftTabletPage += 2; }
        if(rightTabletPage + 2 > 0) { rightTabletPage += 2; }
        String msgSend = leftTabletPage  + "," + rightTabletPage;
        Log.e("MAinActivity :: PageJumpRight() :", ""+  msgSend );
        SendMessage(msgSend);
        byte[] bytes = msgSend.getBytes(Charset.defaultCharset());
        ((MainActivity.ConnectedThread) MainActivity.mConnectedThread).write(bytes);
    }

    public void SendMessage(String msgToSend) {
        //byte[] bytes = msgSend.getBytes(Charset.defaultCharset());
        //mConnectedThread.write(bytes);
    }

    public void updatePageNumbers(String msgStr ){
        String[] separated = msgStr.split(",");
        leftTabletPage = Integer.parseInt(separated[0]);
        rightTabletPage = Integer.parseInt(separated[1]);

        String m_bluetoothName = (MainActivity.bluetoothAdapter).getName();

        if( "BG2-W09".equals( m_bluetoothName )){
            pdfView.jumpTo( leftTabletPage );
        }
        if( "IdeaTab A1000-F".equals( m_bluetoothName )  ){
            pdfView.jumpTo( rightTabletPage );
        }

    }
}
