# ebook_reader_over_dual_tablets
Creating simple ebook reader that synchronises page turning over 2 tablets.  One acts as a leftpage, the other as a rightpage

1. User define which tablet is LeftPage=0 and which is RightPage=1
2. User selects which file to load (using FilePicker).  In-other-words,  user has to copy same PDF file to both tablets.
3. Bluetooth Connection Steps to Follow
 "LeftPage Tablet" --->  Click button "ConnectionReq" --> select Already Paired Bluetooth Tablet --> on RightPage tablet, press  "SERVER" button
 "RightPage Tablet" ---> Click button "ConnectionReq" --> select Already Paired Bluetooth Tablet --> on LeftPage tablet, press  "SERVER" button
4. TestSendMessage buttton :::
Press button "TestSendMessage" on LeftPage Tablet ( should send a String to RightPage )
Press button "TestSendMessage" on RightPage Tablet ( should send a String to LeftPage )
5. If successful with TestSendMessage --->  go ahead and "Ready to jump to Ebook Reader" button.
It should load the Ebook.  --->  [thanks Barteksc/AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer)

6.  The Invisible buttons:
Bottom Left ::  changes pages leftwards  ( i.o.w.  flips eBook pages backward by one )
Bottom center :: opens TableOfContents as a listView that covers the whole tablet.
Bottom right :: changes pages Rightwards ( i.o.w.  flips eBook pages forward by one )


7.  Be prepared for bugs... e.g. The Bluetooth implemented code needs to be refactored and used as a service class.

## -

## Implementing PDF reader part.
Awesome library exists thanks to [Barteksc/AndroidPdfViewer --> https://github.com/barteksc/AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer)
    Easy to integrate in MainActivity.java

which requires reading a file from assets ---> (https://medium.com/@mislam_73732/android-reading-files-from-assets-d7200e4a0a03)



## For bluetooth dev testing --- need to debug on actual device.  Using Lenovo Tab 7" for BT dev:
the Google Docs, aint enough for Linux Mint howto --->

    sudo usermod -aG plugdev $LOGNAME
    sudo apt-get install android-sdk-platform-tools-common

Logout then Login to refresh groups list.

    ~/Android/Sdk/platform-tools/adb devices
    List of devices attached
    TCQOBYIFY5FEW49P        no permissions (missing udev rules? user is in the plugdev group);

then follow stackOverflow guide...
used this guide to connect

(https://stackoverflow.com/questions/53887322/adb-devices-no-permissions-user-in-plugdev-group-are-your-udev-rules-wrong)

## Bluetooth Android --->  sending msg txt between two devices:
(https://stackoverflow.com/questions/45140098/how-to-send-receive-messages-via-bluetooth-android-studio)

github official Android tutorial:  (https://github.com/android/connectivity-samples/tree/master/BluetoothChat)

1st -->

    onCreate -->  include Bluetooth-ON  code.  chk if BT is on.

2nd -->  create Button, that gets list of all Paired Devices.

    Paired devices not the same as Discoverable Devices ( that show up after Scan )
    show in dialog box, and allow user to select Device to connect with.

    UUID generation issue:  Have to create it outside of app, then insert it at beginning.
    private UUID MY_APP_UUID = UUID.fromString("2532f1e9-6832-4dc0-90f7-bac6b84b50fa");
    // Need to random generate it in linux --- command  uuidgen

nth -->  need to create Bluetooth as Service Class so that both (n-)activities can use it
(https://proandroiddev.com/android-bluetooth-as-a-service-c39c3d732e56)
(https://stackoverflow.com/questions/33461075/implement-bluetooth-connection-into-service-or-application-class-without-losing)

## Need to implement FilePicker
Using --> (https://www.geeksforgeeks.org/how-to-implement-pdf-picker-in-android/)

Requires in build.gradle --->

    implementation "androidx.activity:activity:1.3.1" --- not 1.6.0, due to some imports not found
--> (https://stackoverflow.com/questions/61622254/androidx-activityresultcontracts-package-not-found-class-not-found)

Requires in build.gradle --->

    implementation "androidx.fragment:fragment:1.3.0"  --- not 1.5.3, due to minSDK compatibility

## Need to implement Persistance of previously used Settings:
Good summary --> (https://www.vogella.com/tutorials/AndroidFileBasedPersistence/article.html)

Small settings are stored, Key/Value pairs saved and loaded by App.

    tabletNum ( tablet 0 = left page, tablet 1= right page), recentMsg (e.g.  19,20),  filePathStr ( of previous opened PDF file )
    pdfView.onRender(new OnRenderListener()) added to jump to previous pages.
    pdfView.load() takes a full second to load PDF file, needs async processing of command to jumptoPage.

## Need UI improvements, hopefully something similar to BootStrap tricks !!??

Material design is vast and dangerous. Ionic/ReactNavtive UI's cover a lot of gruntwork when compared to Android UI.
Tried implementing a
---> ViewPager2 component ---> (https://github.com/foxandroid/ViewPager2_)
---> Youtube tutorial ---> (https://www.youtube.com/watch?v=O8LA26sAt7Y)

But realised that it is too much for something as sincere as a settings page.
So used RelativeLayout ---> and its basic, but good enough for now.

    Future revision ---> implement Better UI components.

Wanted to hide ActionBar after ebook is loaded ---> (https://stackoverflow.com/questions/36236181/how-to-remove-title-bar-from-the-android-activity)

    getSupportActionBar().hide() does the trick.

## Need TableOfContents to quickly jump to other sections in the book.
It wasn't in the PDFreader documentation, but a method to getTableOfContents() exists and is inherited by the pdfView object written by Barteksc/AndroidPdfViewer.

    ---> List<PdfDocument.Bookmark> pdfTableOfContents = pdfView.getTableOfContents();
    forLoop() between each Bookmark obj,  getTitle() ,  then  getPageIdx() -->  output to a ListView
    that is invisible when not in use, and becomes VISIBLE when TableOfContents button is pressed

# What to improve --->
* Need UX improvements (eg. better TableOfContents listView interaction)
* Need better UI components (eg. StartPage wizard to help user click buttons in correct order).
* Need user friendly buttons while reading PDF (eg. Buttons are invisible, would be nice to FadeOut ).
* Need better Bluetooth connection reliability (while tethered to AndroidStudio, BT connects 8/10 times, then hard reset is needed)
* Need automatic Bluetooth connectivity ( "blue tuuf deviy connektd susseffully",  you switch it on, the tablet auto connects )
* Need Bluetooth file Transfer ( PDF that opens in one tablet, syncs with 2nd tablet )

* Need to open over 3-tablets, 4-tablets .... n-tablets ( That would be a challenge ) seems 4 is the limit --> (https://stackoverflow.com/questions/3943182/multiple-bluetooth-connection)
  * but someone managed to connect upto 7 devices simultaneously ---> (http://arissa34.github.io/Android-Multi-Bluetooth-Library/). Yip, and you can daisy chain them by configuring a client to be a server to 7 more. Since a String "tab1,tab2,tab3,tab4,...,tabN" is synchronised between tablets , daisy chain 1->2,  2->3, 3->4, ... (n-1)-> n)

