# ebook_reader_over_dual_tablets
Creating simple ebook reader that synchronises page turning over 2 tablets

##Implementing PDF reader part.
Awesome library exists thanks to Barteksc/AndroidPdfViewer --> https://github.com/barteksc/AndroidPdfViewer
Easy to integrate in MainActivity.java

which requires reading a file from assets ---> https://medium.com/@mislam_73732/android-reading-files-from-assets-d7200e4a0a03



##For bluetooth dev testing --- need to debug on actual device.  Using Lenovo Tab 7" for BT dev:
      the Google Docs, aint enough for Linux Mint howto --->
      sudo usermod -aG plugdev $LOGNAME
      sudo apt-get install android-sdk-platform-tools-common
      
      Logout then Login to refresh groups list.
  
      ~/Android/Sdk/platform-tools/adb devices
      List of devices attached
      TCQOBYIFY5FEW49P        no permissions (missing udev rules? user is in the plugdev group); 
  
      then follow stackOverflow guide...
      used this guide to connect
      https://stackoverflow.com/questions/53887322/adb-devices-no-permissions-user-in-plugdev-group-are-your-udev-rules-wrong

##Bluetooth Android --->  sending msg txt between two devices:
      https://stackoverflow.com/questions/45140098/how-to-send-receive-messages-via-bluetooth-android-studio

     ### github official Android tutorial:
          https://github.com/android/connectivity-samples/tree/master/BluetoothChat

      1st -->  onCreate -->  include Bluetooth-ON  code.  chk if BT is on.
      2nd -->  create Button, that gets list of all Paired Devices.
              Paired devices not the same as Discoverable Devices ( that show up after Scan )
              show in dialog box, and allow user to select Device to connect with.

              UUID generation issue:  Have to create it outside of app, then insert it at beginning.
              private UUID MY_APP_UUID = UUID.fromString("2532f1e9-6832-4dc0-90f7-bac6b84b50fa");
              // Need to random generate it in linux --- command  uuidgen

      nth -->  need to create Bluetooth as Service Class so that both (n-)activities can use it
          https://proandroiddev.com/android-bluetooth-as-a-service-c39c3d732e56
          https://stackoverflow.com/questions/33461075/implement-bluetooth-connection-into-service-or-application-class-without-losing

##Need to implement FilePicker
 Using --> https://www.geeksforgeeks.org/how-to-implement-pdf-picker-in-android/
 Requires in build.gradle ---> implementation "androidx.activity:activity:1.3.1" --- not 1.6.0, due to some imports not found
 --> https://stackoverflow.com/questions/61622254/androidx-activityresultcontracts-package-not-found-class-not-found
Requires in build.gradle ---> implementation "androidx.fragment:fragment:1.3.0"  --- not 1.5.3, due to minSDK compatibility

##Need to implement Persistance of previously used Settings:
      Good summary --> https://www.vogella.com/tutorials/AndroidFileBasedPersistence/article.html
      Small settings are stored, Key/Value pairs saved and loaded by App.
      tabletNum ( tablet 0 = left page, tablet 1= right page), recentMsg (e.g.  19,20),  filePathStr ( of previous opened PDF file )
      pdfView.onRender(new OnRenderListener()) added to jump to previous pages.
      pdfView.load() takes a full second to load PDF file, needs async processing of command to jumptoPage.

##Need UI improvements, hopefully something similar to BootStrap tricks !!??