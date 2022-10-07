package com.fcaro.ebookreaderoverdualtablets;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    PDFView pdfView;
    Button simpleButton1, simpleButton2;
    //private static final UUID MY_UUID_INSECURE =
    //        UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    //ConnectedThread mConnectedThread;
    private Handler handler;

    String TAG = "MainActivity";
    EditText send_data;
    TextView view_data;
    StringBuilder messages;

    ListView listViewBluetoothDevices;
    TextView btDeviceTextView;

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

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void pairDevice(View v) {

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        Log.e("MAinActivity", "" + pairedDevices.size() );
        if (pairedDevices.size() > 0) {
            Object[] devices = pairedDevices.toArray();
            BluetoothDevice device = (BluetoothDevice) devices[0];
            ParcelUuid[] uuid = device.getUuids();
            ArrayList<String> tempListDevices = new ArrayList<>() ;

            for ( Object item : devices){
                Log.e("MAinActivity :: pairDevice() ::: device :", ""
                        + (BluetoothDevice) item );
                Log.e("MAinActivity :: pairDevice() ::: device.getName() :", ""
                        + ((BluetoothDevice) item).getName() );
                ParcelUuid[] uuidTemp = ((BluetoothDevice) item).getUuids();
                Log.e("MAinActivity :: pairDevice() ::: uuid : ", ""
                        + ((BluetoothDevice) item).getUuids().toString() );

                tempListDevices.add( (BluetoothDevice) item + " - "
                        + ((BluetoothDevice) item).getName() + " - "
                        + ((BluetoothDevice) item).getUuids().toString() );
            }

            btDeviceTextView =(TextView)findViewById(R.id.btDeviceTextView);

            listViewBluetoothDevices=(ListView)findViewById(R.id.listViewBluetoothDevices);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, tempListDevices );
            listViewBluetoothDevices.setAdapter(adapter);


            //ConnectThread connect = new ConnectThread(device,MY_UUID_INSECURE);
            //connect.start();
        }
    }
}