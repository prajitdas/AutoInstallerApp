package edu.umbc.cs.ebiquity.autoinstallerapp;

import android.app.Application;

/**
 * Created by Prajit on 6/25/2016.
 */

public class AutoInstallerApplication extends Application {
    private static final String url = "https://75280f35.ngrok.io";
    private static final String CONST_DEBUG_TAG = "AUTO_INSTALLER_APP_DEBUG_TAG";
    private static final String allAppsDisplayTag = "allAppsDisplayTag";
    private static final String userAppsDisplayTag = "userAppsDisplayTag";
    private static final String systemAppsDisplayTag = "systemAppsDisplayTag";
    private static final String toInstallAppsDisplayTag = "toInstallAppsDisplayTag";
    private static final String appDisplayTypeTag = "appDisplayTypeTag";

    public static String getDebugTag() {
        return CONST_DEBUG_TAG;
    }

    public static String getAllAppsDisplayTag() {
        return allAppsDisplayTag;
    }

    public static String getUserAppsDisplayTag() {
        return userAppsDisplayTag;
    }

    public static String getSystemAppsDisplayTag() {
        return systemAppsDisplayTag;
    }

    public static String getToInstallAppsDisplayTag() {
        return toInstallAppsDisplayTag;
    }

    public static String getAppDisplayTypeTag() {
        return appDisplayTypeTag;
    }

    public static String getUrl() {
        return url;
    }
}