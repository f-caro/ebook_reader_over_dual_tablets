//package com.fcaro.ebookreaderoverdualtablets;
//
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothSocket;
//import android.util.Log;
//
//import java.io.IOException;
//import java.util.UUID;
//
//public class ConnectThread extends Thread {
//    private BluetoothSocket mmSocket;
//    private BluetoothDevice mmDevice;
//    private UUID deviceUUID;
//
//    String TAG = "MainActivity";
//
//    public ConnectThread(BluetoothDevice device, UUID uuid) {
//        Log.d(TAG, "ConnectThread: started.");
//        mmDevice = device;
//        deviceUUID = uuid;
//    }
//
//    public void run(){
//        BluetoothSocket tmp = null;
//        Log.i(TAG, "RUN mConnectThread ");
//
//        // Get a BluetoothSocket for a connection with the
//        // given BluetoothDevice
//        try {
//            Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
//                    + this.deviceUUID );
//            tmp = mmDevice.createRfcommSocketToServiceRecord( this.deviceUUID );
//        } catch (IOException e) {
//            Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
//        }
//
//        mmSocket = tmp;
//
//        // Make a connection to the BluetoothSocket
//
//        try {
//            // This is a blocking call and will only return on a
//            // successful connection or an exception
//            mmSocket.connect();
//
//        } catch (IOException e) {
//            // Close the socket
//            try {
//                mmSocket.close();
//                Log.d(TAG, "run: Closed Socket.");
//            } catch (IOException e1) {
//                Log.e(TAG, "mConnectThread: run: Unable to close connection in socket "
//                        + e1.getMessage());
//            }
//            Log.d(TAG, "run: ConnectThread: Could not connect to UUID: "
//                    + this.deviceUUID );
//        }
//
//        //will talk about this in the 3rd video
//        connected(mmSocket);
//    }
//
//    public void cancel() {
//        try {
//            Log.d(TAG, "cancel: Closing Client Socket.");
//            mmSocket.close();
//        } catch (IOException e) {
//            Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
//        }
//    }
//}