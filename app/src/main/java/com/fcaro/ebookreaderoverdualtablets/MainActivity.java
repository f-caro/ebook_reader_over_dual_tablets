package com.fcaro.ebookreaderoverdualtablets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


public class MainActivity extends AppCompatActivity {


    private UUID MY_APP_UUID = UUID.fromString("2532f1e9-6832-4dc0-90f7-bac6b84b50fa");
    // Need to random generate it in linux --- command  uuidgen
//            = UUID.fromString(
//            android.provider.Settings.Secure.
//                    getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID) );
    //private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private static final int REQUEST_ENABLE_BT = 1;
    public BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public BluetoothDevice mmDevice;
    private UUID deviceUUID;
    public static ConnectedThread mConnectedThread;
    private Handler handler;

    SharedPreferences ero2t_settings;

    String m_bluetoothName;
    String m_otherBTname;
    String TAG = "MainActivity";
    EditText send_data;
    TextView view_data;
    StringBuilder messages;
    String filePathStr;
    String recentMsg;

    PDFView pdfView;
    Button buttonGotoPage100, connectionReqButton, serverStartButton, sendMessageButton;
    Button jumpLeftButton, jumpRightButton, openEbookReaderButton ;
    ListView listViewBluetoothDevices;
    TextView btDeviceTextView;
    EditText editTextTestConnection, tabletNumEditText;
    TextView textViewTestConnection;
    ListView listBtDevicesView;

    Button btSelect;
    TextView tvUri, tvPath, tvLabelTabletPage;
    ActivityResultLauncher<Intent> resultLauncher;

    int leftTabletPage, rightTabletPage, tabletNum ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ero2t_settings = getSharedPreferences("ERO2T_settings", Context.MODE_PRIVATE);
        filePathStr = ero2t_settings.getString("filePathStr", "");
        tabletNum = Integer.parseInt( ero2t_settings.getString("tabletNum", "0") );
        recentMsg = ero2t_settings.getString("recentMsg", "");

        PDFView pdfView = null;

//        leftTabletPage = 1;
//        rightTabletPage = 2;

        connectionReqButton = (Button) findViewById(R.id.connectionReqButton);
        serverStartButton = (Button) findViewById(R.id.serverStartButton);
        sendMessageButton = (Button) findViewById(R.id.sendMessageButton);

        jumpLeftButton = (Button) findViewById(R.id.jumpLeftButton);
        jumpRightButton = (Button) findViewById(R.id.jumpRightButton);
        buttonGotoPage100 = (Button) findViewById(R.id.buttonGotoPage100);//get id of button 1
        openEbookReaderButton = (Button) findViewById(R.id.openEbookReader);

        editTextTestConnection = (EditText) findViewById(R.id.editTextTestConnection);
        textViewTestConnection = (TextView) findViewById(R.id.textViewTestConnection);
        tabletNumEditText = (EditText) findViewById(R.id.tabletNumEditText);

        send_data =(EditText) findViewById(R.id.editTextTestConnection);
        view_data = (TextView) findViewById(R.id.textViewTestConnection);

        jumpRightButton.setVisibility(View.INVISIBLE);
        jumpLeftButton.setVisibility(View.INVISIBLE);
        buttonGotoPage100.setVisibility(View.INVISIBLE);

        btSelect = (Button) findViewById(R.id.bt_select);
        tvUri = (TextView) findViewById(R.id.tv_uri);
        tvPath = (TextView) findViewById(R.id.tv_path);
        tvLabelTabletPage = (TextView) findViewById(R.id.tvLabelTabletPage);

        if( !"".equals(filePathStr) ){  tvPath.setText( filePathStr );  }

        if( recentMsg.contains(",") ){
            splitMsgStr(recentMsg);
        } else {
            leftTabletPage = 1;
            rightTabletPage = 2;
        }

        if( tabletNum != 0 ) { tabletNumEditText.setText( Integer.toString( tabletNum ) );  }

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        tabletNumEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                updateKeyValSettings("tabletNum",  tabletNumEditText.getText().toString() );
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Initialize result launcher
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result){
                        // Initialize result data
                        Intent data = result.getData();
                        // check condition
                        if (data != null) {
                            // When data is not equal to empty
                            // Get PDf uri
                            Uri sUri = data.getData();
                            // set Uri on text view
                            tvUri.setText(Html.fromHtml("<big><b>PDF Uri</b></big><br>"+ sUri) );

                            // Get PDF path
                            String sPath = sUri.getPath();
                            filePathStr = sUri.getPath();
                            // Set path on text view
                            tvPath.setText(Html.fromHtml("<big><b>PDF Path</b></big><br>"+ sPath));
                            tvPath.setText( sPath );
                            updateKeyValSettings("filePathStr", filePathStr );
                        }
                    }
                });

        // Set click listener on button
        btSelect.setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View v){
                        // check condition
                        if (ActivityCompat.checkSelfPermission(
                                MainActivity.this,
                                Manifest.permission
                                        .READ_EXTERNAL_STORAGE)
                                != PackageManager
                                .PERMISSION_GRANTED) {
                            // When permission is not granted
                            // Result permission
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[] {
                                            Manifest.permission
                                                    .READ_EXTERNAL_STORAGE },
                                    1);
                        }
                        else {
                            // When permission is granted
                            // Create method
                            selectPDF();
                        }
                    }
                });
    }

    private void selectPDF()
    {
        // Initialize intent
        Intent intent
                = new Intent(Intent.ACTION_GET_CONTENT);
        // set type
        intent.setType("application/pdf");
        // Launch intent
        resultLauncher.launch(intent);
    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        // check condition
        if (requestCode == 1
                && grantResults.length > 0
                && grantResults[0]
                == PackageManager.PERMISSION_GRANTED) {
            // When permission is granted
            // Call method
            selectPDF();
        }
        else {
            // When permission is denied
            // Display toast
            Toast.makeText(getApplicationContext(),
                            "Permission Denied",
                            Toast.LENGTH_SHORT)
                 .show();
        }
    }


    public void SendMessage(View v) {
        byte[] bytes = send_data.getText().toString().getBytes(Charset.defaultCharset());
        mConnectedThread.write(bytes);
    }


    public void PageJumpLeft(View v) {
        if(leftTabletPage - 2 > 0) { leftTabletPage -= 2; }
        if(rightTabletPage - 2 > 0) { rightTabletPage -= 2; }
        String msgSend = leftTabletPage  + "," + rightTabletPage;
        updateKeyValSettings("recentMsg", msgSend );
        byte[] bytes = msgSend.getBytes(Charset.defaultCharset());
        mConnectedThread.write(bytes);
        changePage();
    }

    public void PageJumpRight(View v) {
        if(leftTabletPage + 2 > 0) { leftTabletPage += 2; }
        if(rightTabletPage + 2 > 0) { rightTabletPage += 2; }
        String msgSend = leftTabletPage  + "," + rightTabletPage;
        updateKeyValSettings("recentMsg", msgSend );
        byte[] bytes = msgSend.getBytes(Charset.defaultCharset());
        mConnectedThread.write(bytes);
        changePage();
    }

    public void updatePageNumbers(String msgStr ){
//        String[] separated = msgStr.split(",");
//        leftTabletPage = Integer.parseInt(separated[0]);
//        rightTabletPage = Integer.parseInt(separated[1]);
        splitMsgStr(msgStr);
        updateKeyValSettings("recentMsg", msgStr );
        changePage();
    }

    public void splitMsgStr( String msgStr ){
        String[] separated = msgStr.split(",");
        leftTabletPage = Integer.parseInt(separated[0]);
        rightTabletPage = Integer.parseInt(separated[1]);
    }

    public void updateKeyValSettings(String key, String val){
        SharedPreferences.Editor edit = ero2t_settings.edit();
        edit.putString( key , val );
        edit.apply();
    }

    public void changeViewsWhenEbookReaderIsOpened(View v){
        jumpRightButton.setVisibility(v.VISIBLE);
        jumpLeftButton.setVisibility(v.VISIBLE);
        buttonGotoPage100.setVisibility(v.VISIBLE);

        openEbookReaderButton.setVisibility(v.INVISIBLE);
        connectionReqButton.setVisibility(v.INVISIBLE );
        serverStartButton.setVisibility(v.INVISIBLE);
        sendMessageButton.setVisibility(v.INVISIBLE);
        editTextTestConnection.setVisibility(v.INVISIBLE);
        textViewTestConnection.setVisibility(v.INVISIBLE);
        tabletNumEditText.setVisibility(v.INVISIBLE);
        listBtDevicesView.setVisibility(v.INVISIBLE);
        tvPath.setVisibility(v.INVISIBLE);
        tvUri.setVisibility(v.INVISIBLE);
        btSelect.setVisibility(v.INVISIBLE);
        tvLabelTabletPage.setVisibility(v.INVISIBLE);
    }
    public void changePage(){
        tabletNum = Integer.parseInt( tabletNumEditText.getText().toString() );

        if( tabletNum == 0 ){   pdfView.jumpTo( leftTabletPage );   }
        if( tabletNum == 1 ){   pdfView.jumpTo( rightTabletPage );   }
//        if( "BG2-W09".equals( m_bluetoothName )){
//            pdfView.jumpTo( leftTabletPage );
//        }
//        if( "IdeaTab A1000-F".equals( m_bluetoothName )  ){
//            pdfView.jumpTo( rightTabletPage );
//        }

    }
    public void openEbookReader(View v){
//        final Context context = this;
//        Intent intent = new Intent(context, EbookReaderActivity.class);
//        startActivity(intent);
        if( getSupportActionBar() != null) { getSupportActionBar().hide(); }

        AssetManager assetManager = getAssets();

        InputStream in = null;

        //File pdffile = new File("/sdcard/Download/STA4811.pdf");
        File pdffile = new File(filePathStr);
        Boolean chkFile = pdffile.exists();
        Log.d("pdffile exists? ::: ", chkFile.toString() );
        pdfView = (PDFView)findViewById(R.id.pdfView);
        pdfView.fromFile(pdffile).onRender(new OnRenderListener() {
            @Override
            public void onInitiallyRendered(int pages, float pageWidth, float pageHeight) {
                Log.e("MAinActivity:openEbookReader()::onRender()", recentMsg  );
                if(recentMsg.contains(",") ){   updatePageNumbers(recentMsg);   }
            }
        }).load();

        changeViewsWhenEbookReaderIsOpened(v);

        buttonGotoPage100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfView.jumpTo(100);
                Toast.makeText(getApplicationContext(),
                        buttonGotoPage100.getText() ,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void pairDevice(View v) {
        //having problems generating UUID --->  need to use Linux uuidgen comand instead of...
//        BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        String m_bluetoothAdd = m_BluetoothAdapter.getAddress();
//        Log.e("MAinActivity :: m_bluetoothAdd :", ""
//                + m_bluetoothAdd  );
        // --->  E/MAinActivity :: m_bluetoothAdd :: D4:22:3F:52:D2:CC
//        this.MY_APP_UUID = UUID.fromString( m_bluetoothAdd );
        tabletNum = Integer.parseInt( tabletNumEditText.getText().toString() );
        listBtDevicesView=(ListView)findViewById(R.id.listBtDevicesView);

        m_bluetoothName = bluetoothAdapter.getName();
        Log.e("MAinActivity :: pairDevice() : currentDeviceName", ""
                + m_bluetoothName  );
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        Log.e("MAinActivity", "" + pairedDevices.size() );
        int indexBt = 0;
        int chosenDevice = 0;

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            ArrayList<HashMap<String,String> > arrayList = new ArrayList<>();

            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.e("MAinActivity :: pairDevice() :", ""
                        + deviceName + " --- " + deviceHardwareAddress );
//                if( "BG2-W09".equals( deviceName )){            chosenDevice = indexBt;
//                    Log.e("MAinActivity :: pairDevice() : Connecting to :::", ""
//                            +  indexBt + " -- chosenDevice : " +  chosenDevice );
//                }
//                if( "IdeaTab A1000-F".equals( deviceName )  ){    chosenDevice = indexBt;   }
                //indexBt++;
                HashMap<String,String> hashMapItem = new HashMap<>();
                hashMapItem.put("deviceName", deviceName);
                arrayList.add(hashMapItem);
            }
            String[] from = {"deviceName"};
            int[] to = {R.id.btDeviceTextView};
            SimpleAdapter simpleAdapter=new SimpleAdapter(this,
                    arrayList,
                    R.layout.bt_device_list,
                    from ,
                    to);
            listBtDevicesView.setAdapter(simpleAdapter);
            Object[] devices = pairedDevices.toArray();

            listBtDevicesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    BluetoothDevice device = (BluetoothDevice) devices[i];
                    Log.e("MAinActivity :: pairDevice() :: setOnItemClickListener() : ", ""
                    + device.getName() + " --- " + device.getAddress() );
                    send_data.setText( device.getName() );
                    ConnectThread connect = new ConnectThread(device, MY_APP_UUID );
                    connect.start();

                    AcceptThread accept = new AcceptThread();
                    accept.start();
                }
            });
//            Object[] devices = pairedDevices.toArray();
//            BluetoothDevice device = (BluetoothDevice) devices[(int) chosenDevice];
//
//            Log.e("MAinActivity :: pairDevice() : Connecting to :::", ""
//                    + device.getName() + " --- " + device.getAddress() );
//            ConnectThread connect = new ConnectThread(device, MY_APP_UUID );
//            connect.start();
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



    private void connected(BluetoothSocket mmSocket) {
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    public class ConnectedThread extends Thread {
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
                            if( incomingMessage.contains(",") ) {
                                updatePageNumbers(incomingMessage);
                            }
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

    @Override
    public void onDestroy(){
        super.onDestroy();

        Log.d(TAG, "Activity is being destroyed");
    }
}