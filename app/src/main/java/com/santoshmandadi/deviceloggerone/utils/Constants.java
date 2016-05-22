package com.santoshmandadi.deviceloggerone.utils;

/**
 * Created by santosh on 5/21/16.
 */

import com.santoshmandadi.deviceloggerone.BuildConfig;

/**
 * Constants class store most important strings and paths of the app
 */
public final class Constants {

    public static final String FIREBASE_LOCATION_CURRENT_LIST = "currentStatus";
    /**
     * Constants related to locations in Firebase, such as the name of the node
     * where active lists are stored (ie "activeLists")
     */
    public static String FIREBASE_LOCATION_DEVICES_LIST = "devicesList";
    public static String FIREBASE_URL_DEVICES_LIST = BuildConfig.UNIQUE_FIREBASE_ROOT_URL + FIREBASE_LOCATION_DEVICES_LIST;

    /**
     * Constants for Firebase object properties
     */


    /**
     * Constants for Firebase URL
     */
    public static String FIREBASE_URL = BuildConfig.UNIQUE_FIREBASE_ROOT_URL;


    /**
     * Constants for bundles, extras and shared preferences keys
     */

    public static String UNIQUE_GOOGLE_CLIENT_ID = BuildConfig.UNIQUE_GOOGLE_CLIENT_ID;

}
