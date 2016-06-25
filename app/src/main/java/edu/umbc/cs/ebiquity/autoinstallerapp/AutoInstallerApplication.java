package edu.umbc.cs.ebiquity.autoinstallerapp;

import android.app.Application;

/**
 * Created by Prajit on 6/25/2016.
 */

public class AutoInstallerApplication extends Application {
    private static final String CONST_DEBUG_TAG = "AUTO_INSTALLER_APP_DEBUG_TAG";
    private static final String allAppsDisplayTag = "allApps";

    public static String getConstDebugTag() {
        return CONST_DEBUG_TAG;
    }
    public static String getAllAppsDisplayTag() {
        return allAppsDisplayTag;
    }
}
