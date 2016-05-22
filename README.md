# DeviceLogger
How to use this app:
========
Generate QR code for a device in the format:

{"deviceName": "iPhone 6 plus",   "serialNumber": "sjdhayu232hjksada" }

You can use http://www.qr-code-generator.com to generate code for the devices. Mentioned this in app help page inside app. 

How to build this project:
============

1) Create gradle.properties in root folder: ~/DeviceLogger

2) Create two valiables as shown below in gradle.properties file
- UniqueFirebaseRootUrl="will explain below how to get this" 
- UniqueGoogleClientId="will explain below how to get this"

3) Create google-services.json and place it in ~/DeviceLogger/app folder. Will explain below on how to create 

4) Enable google sign-in on Firebase

How to get a UniqueFirebaseRootUrl
============
1) Create a new project in Firebase dashboard

2) Click the new project 

3) Once you navigate to the newly created project page copy the URL and update "UniqueFirebaseRootUrl" in gradle.properties file

How to get a google-services.json
============
Step 1: Create a Project in https://console.developers.google.com
⇒	Go to console.developers.google.com

⇒	Click on create Project button  and enter the project name and click create(Eg: Project name :DeviceLogger)
 
Step 2: Now go to https://developers.google.com/identity/sign-in/android/start-integrating

⇒	Click on Get a Configuration file button
 
⇒	Choose the app Name from the drop down (created in console.developers.google.com, DeviceLogger) and enter the package name as from which android app you need to access this service app. To see android app package name go to your android app’s Manifest file and get it and add it in package name field(in this case: com.santoshmandadi.deviceloggerone). Click choose and Configure services

⇒	In  choose and configure services Page we need to enter the Android signing  certificate SHA-1. We have link below that field to find how to find the SHA-1. For now getting the SHA-1 for debug mode.

Command to find out Android signing certificate SHA-1 

keytool -exportcert -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore

Enter the password as “android”

⇒	Click on Enable Google sign –in button and choose Generate Configuration files button

⇒	Download the google-services.json and add it to your android project’s app folder as mentioned in the site.

How to get a UniqueGoogleClientId
============
Follow the below steps to get the  servers side client id. 

Go to console.developers.google.com. choose the project project created in "How to get a google-services.json" . From left navigation menu choose API Manager

Click on credentials in the API Manager.

Now in Credentials choose web client clientid .Although we are doing android we should choose web client client id. Else the token will be returned as null. We need the token to pass it to Firebase. Below is the stack overflow link 
http://stackoverflow.com/questions/34111978/why-is-requestidtoken-returning-null

Update the UniqueGoogleClientId in gradle.properties with the above web client clientid

How to enable google sign-in on Firebase
============
Launch Firebase and go to the project dahsboard. 

Go to Login & Auth tab on left

Click Google tab

Check the check box to "Enable Google Authentication"

Now go to console.developers.google.com. choose the project project created in "How to get a google-services.json" . From left navigation menu choose API Manager

Click on credentials in the API Manager.

Now in Credentials choose Android client clientid(previously we chose web client client id)

Update the Firebase "Google Client ID" with Android clientid which starts with and number and ends with googleusercontent.com

All set. 
