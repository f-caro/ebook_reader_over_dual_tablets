package com.fcaro.ebookreaderoverdualtablets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
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
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    PDFView pdfView;
    Button simpleButton1, simpleButton2;
    private UUID MY_APP_UUID = UUID.fromString("2532f1e9-6832-4dc0-90f7-bac6b84b50fa");
    // Need to random generate it in linux --- command  uuidgen
//            = UUID.fromString(
//            android.provider.Settings.Secure.
//                    getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID) );
    //private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

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
//        String uuidChk = android.provider.Settings.Secure.getString(getContentResolver(),
//                android.provider.Settings.Secure.ANDROID_ID);
//        Log.e("MAinActivity :: uuidChk :", ""
//                + uuidChk  );
//        this.MY_APP_UUID = UUID.fromString( uuidChk );

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

//        if (Build.VERSION.SDK_INT >= 23) {
//            int permissionCheck = ContextCompat.checkSelfPermission( this , Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            }
//        }
        send_data =(EditText) findViewById(R.id.editText);
        view_data = (TextView) findViewById(R.id.textView);

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void SendMessage(View v) {
        byte[] bytes = send_data.getText().toString().getBytes(Charset.defaultCharset());
        mConnectedThread.write(bytes);
    }

    public void pairDevice(View v) {
        //having problems generating UUID --->  need to use Linux uuidgen comand instead of...
//        BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        String m_bluetoothAdd = m_BluetoothAdapter.getAddress();
//        Log.e("MAinActivity :: m_bluetoothAdd :", ""
//                + m_bluetoothAdd  );
        // --->  E/MAinActivity :: m_bluetoothAdd :: D4:22:3F:52:D2:CC
//        this.MY_APP_UUID = UUID.fromString( m_bluetoothAdd );

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        Log.e("MAinActivity", "" + pairedDevices.size() );
        int indexBt = 0;
        int chosenDevice = 0;

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.e("MAinActivity :: pairDevice() :", ""
                        + deviceName + " --- " + deviceHardwareAddress );
                if( "BG2-W09".equals( deviceName )){            chosenDevice = indexBt;
                    Log.e("MAinActivity :: pairDevice() : Connecting to :::", ""
                            +  indexBt + " -- chosenDevice : " +  chosenDevice );
                }
                if( "IdeaTab A1000-F".equals( deviceName )  ){    chosenDevice = indexBt;   }
                indexBt++;
            }
            Object[] devices = pairedDevices.toArray();
            BluetoothDevice device = (BluetoothDevice) devices[(int) chosenDevice];

            Log.e("MAinActivity :: pairDevice() : Connecting to :::", ""
                    + device.getName() + " --- " + device.getAddress() );
            ConnectThread connect = new ConnectThread(device, MY_APP_UUID );
            connect.start();
        }



//        if (pairedDevices.size() > 0) {
//            Object[] devices = pairedDevices.toArray();
//            BluetoothDevice device = (BluetoothDevice) devices[0];
//            ParcelUuid[] uuid = device.getUuids();
//            ArrayList<String> tempListDevices = new ArrayList<>() ;
//
//            for ( Object item : devices){
//                Log.e("MAinActivity :: pairDevice() ::: device :", ""
//                        + (BluetoothDevice) item );
//                Log.e("MAinActivity :: pairDevice() ::: device.getName() :", ""
//                        + ((BluetoothDevice) item).getName() );
//                ParcelUuid[] uuidTemp = ((BluetoothDevice) item).getUuids();
//                Log.e("MAinActivity :: pairDevice() ::: uuids : ", ""
//                        + ((BluetoothDevice) item).getUuids().toString() );
//                ParcelUuid[] tempUuids = ((BluetoothDevice) item).getUuids();
//
//                String tempUuidStr = "";
//                for ( ParcelUuid tUuid : tempUuids ){
//                    Log.e("MAinActivity :: pairDevice() ::: Temp_uuid : ", ""
//                            + tUuid.getUuid() );
//                    tempUuidStr = tempUuidStr + "_" ;
//                }
//
//                tempListDevices.add( (BluetoothDevice) item + " - "
//                        + ((BluetoothDevice) item).getName() + " - "
//                        + tempUuidStr );
//            }
//
//            for ( String item : tempListDevices ){
//                Log.e("MAinActivity: BlueTooth items :: " , item );
//            }
//           // btDeviceTextView =(TextView)findViewById(R.id.btDeviceTextView);
//
////            listViewBluetoothDevices=(ListView)findViewById(R.id.listViewBluetoothDevices);
////            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
////                    android.R.layout.simple_list_item_1, android.R.id.text1, tempListDevices );
////            listViewBluetoothDevices.setAdapter(adapter);
//
//
//            //ConnectThread connect = new ConnectThread(device,MY_UUID_INSECURE);
//            //connect.start();
//        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread ");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        + MY_APP_UUID );
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_APP_UUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket "
                        + e.getMessage());
            }

            mmSocket = tmp;

            // Make a connection to the BluetoothSocket

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();

            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket "
                            + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_APP_UUID );
            }

            //will talk about this in the 3rd video
            connected(mmSocket);
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }

    ConnectedThread mConnectedThread;

    private void connected(BluetoothSocket mmSocket) {
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];  // buffer store for the stream

            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                    final String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            view_data.setText(incomingMessage);
                        }
                    });


                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage() );
                    break;
                }
            }
        }


        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage() );
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public void Start_Server(View view) {

        AcceptThread accept = new AcceptThread();
        accept.start();

    }

    private class AcceptThread extends Thread {

        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null ;

            // Create a new listening server socket
            try{
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
                        "appname", MY_APP_UUID);

                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_APP_UUID);
            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            mmServerSocket = tmp;
        }

        public void run(){
            Log.d(TAG, "run: AcceptThread Running.");

            BluetoothSocket socket = null;

            try{
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.d(TAG, "run: RFCOM server socket start.....");

                socket = mmServerSocket.accept();

                Log.d(TAG, "run: RFCOM server socket accepted connection.");

            }catch (IOException e){
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            //talk about this is in the 3rd
            if(socket != null){
                connected(socket);
            }

            Log.i(TAG, "END mAcceptThread ");
        }

        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. "
                        + e.getMessage() );
            }
        }

    }
}