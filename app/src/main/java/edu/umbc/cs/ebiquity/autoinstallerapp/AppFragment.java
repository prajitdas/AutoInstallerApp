package edu.umbc.cs.ebiquity.autoinstallerapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.umbc.cs.ebiquity.autoinstallerapp.model.AppMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AppFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    /**
     * An array of app items.
     */
    private List<AppMetadata> allAppMetadataItems = new ArrayList<>();
    private List<AppMetadata> systemAppMetadataItems = new ArrayList<>();
    private List<AppMetadata> userAppMetadataItems = new ArrayList<>();
    private List<AppMetadata> toInstallAppMetadataItems = new ArrayList<>();
    private Map<String, AppMetadata> appMetadataMap = new HashMap<>();
    private PackageManager packageManager;
    private View view;
    private String mAppDisplayType;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AppFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AppFragment newInstance(int columnCount, String appDisplayType) {
        AppFragment fragment = new AppFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(AutoInstallerApplication.getAppDisplayTypeTag(), appDisplayType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mAppDisplayType = getArguments().getString(AutoInstallerApplication.getAppDisplayTypeTag());
        } else {
            Log.d(AutoInstallerApplication.getDebugTag(),"Something is wrong");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app_list, container, false);
        packageManager = view.getContext().getPackageManager();
        initData();
        initView();
        return view;
    }

    private void initView() {
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1)
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            else
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

            recyclerView.setAdapter(new MyAppRecyclerViewAdapter(allAppMetadataItems, mListener));
            if (mAppDisplayType.equals(AutoInstallerApplication.getAllAppsDisplayTag()))
                recyclerView.setAdapter(new MyAppRecyclerViewAdapter(allAppMetadataItems, mListener));
            else if (mAppDisplayType.equals(AutoInstallerApplication.getSystemAppsDisplayTag()))
                recyclerView.setAdapter(new MyAppRecyclerViewAdapter(systemAppMetadataItems, mListener));
            else if (mAppDisplayType.equals(AutoInstallerApplication.getUserAppsDisplayTag()))
                recyclerView.setAdapter(new MyAppRecyclerViewAdapter(userAppMetadataItems, mListener));
            else if (mAppDisplayType.equals(AutoInstallerApplication.getToInstallAppsDisplayTag()))
                recyclerView.setAdapter(new MyAppRecyclerViewAdapter(toInstallAppMetadataItems, mListener));

            /**
             * Item decoration added
             */
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    /**
     * Finds all the applications on the phone and stores them in a database accessible to the whole app
     */
    private void initData() {
//        /**
//         * Data loading: get all apps
//         */
//        getAllApps();
//
//        for(Map.Entry<String, AppMetadata> entry : appMetadataMap.entrySet()) {
////            Log.d("MithrilAppManager", entry.toString());
//            allAppMetadataItems.add(entry.getValue());
//        }
//        Collections.sort(allAppMetadataItems);
//        appMetadataMap.clear();
//
//        /**
//         * Data loading: get all system apps
//         */
//        getSystemApps();
//
//        for(Map.Entry<String, AppMetadata> entry : appMetadataMap.entrySet()) {
////            Log.d("MithrilAppManager", entry.toString());
//            systemAppMetadataItems.add(entry.getValue());
//        }
//        Collections.sort(systemAppMetadataItems);
//        appMetadataMap.clear();
//
//        /**
//         * Data loading: get all user apps
//         */
//        getUserApps();
//
//        for(Map.Entry<String, AppMetadata> entry : appMetadataMap.entrySet()) {
////            Log.d("MithrilAppManager", entry.toString());
//            userAppMetadataItems.add(entry.getValue());
//        }
//        Collections.sort(userAppMetadataItems);
//        appMetadataMap.clear();

        /**
         * Data loading: get apps to install
         */
        getAppsToInstall();

        for(Map.Entry<String, AppMetadata> entry : appMetadataMap.entrySet()) {
//            Log.d("MithrilAppManager", entry.toString());
            toInstallAppMetadataItems.add(entry.getValue());
            Log.d(AutoInstallerApplication.getDebugTag(),entry.getValue().toString());
        }
        Log.d(AutoInstallerApplication.getDebugTag(),"Size is: "+Integer.toString(toInstallAppMetadataItems.size()));
        Collections.sort(toInstallAppMetadataItems);
        appMetadataMap.clear();
    }

    private void getAppsToInstall() {
        try {
            JSONObject jsonRootObject = new JSONObject(loadJSONFromAsset());
            JSONArray jsonArray = jsonRootObject.optJSONArray("applist");
            AppMetadata tempAppMetaData = new AppMetadata("dummyApp");
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject apps = jsonArray.optJSONObject(i);
                Iterator iter = apps.keys();
                while(iter.hasNext()) {
                    String key = (String) iter.next();
                    JSONObject appInfo = apps.optJSONObject(key);
                    tempAppMetaData.setPackageName(appInfo.optString("packageName").toString());
                    tempAppMetaData.setAppName(appInfo.optString("appName").toString());
                    tempAppMetaData.setVersionInfo(appInfo.optString("versionInfo").toString());
//                    tempAppMetaData.setIcon(BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.ic_launcher));
                }
//                tempAppMetaData.setIcon(getBitmap(jsonObject.optString("icon").toString()));
            }
            appMetadataMap.put(tempAppMetaData.getPackageName(), tempAppMetaData);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
    }

    private Bitmap getBitmapFromURL(String bitmapUrl) {
        try {
            URL url = new URL(bitmapUrl);
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void getAllApps() {
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_PERMISSIONS;
        for(PackageInfo pack : packageManager.getInstalledPackages(flags)) {
            if ((pack.applicationInfo.flags) != 1) {
                try {
                    AppMetadata tempAppMetaData = new AppMetadata("dummyApp");
                    if (pack.packageName != null) {
                        tempAppMetaData.setPackageName(pack.packageName);
                        tempAppMetaData.setAppName(pack.applicationInfo.loadLabel(packageManager).toString());
                        tempAppMetaData.setVersionInfo(pack.versionName);
                        tempAppMetaData.setIcon(((BitmapDrawable) pack.applicationInfo.loadIcon(packageManager)).getBitmap());
                    }
                    appMetadataMap.put(tempAppMetaData.getPackageName(), tempAppMetaData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getSystemApps() {
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_PERMISSIONS;
        for(PackageInfo pack : packageManager.getInstalledPackages(flags)) {
            if ((pack.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                try {
                    AppMetadata tempAppMetaData = new AppMetadata("dummyApp");
                    if (pack.packageName != null) {
                        tempAppMetaData.setPackageName(pack.packageName);
                        tempAppMetaData.setAppName(pack.applicationInfo.loadLabel(packageManager).toString());
                        tempAppMetaData.setVersionInfo(pack.versionName);
                        tempAppMetaData.setIcon(((BitmapDrawable) pack.applicationInfo.loadIcon(packageManager)).getBitmap());
                    }
                    appMetadataMap.put(tempAppMetaData.getPackageName(), tempAppMetaData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getUserApps() {
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_PERMISSIONS;
        for(PackageInfo pack : packageManager.getInstalledPackages(flags)) {
            if ((pack.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                try {
                    AppMetadata tempAppMetaData = new AppMetadata("dummyApp");
                    if (pack.packageName != null) {
                        tempAppMetaData.setPackageName(pack.packageName);
                        tempAppMetaData.setAppName(pack.applicationInfo.loadLabel(packageManager).toString());
                        tempAppMetaData.setVersionInfo(pack.versionName);
                        tempAppMetaData.setIcon(((BitmapDrawable) pack.applicationInfo.loadIcon(packageManager)).getBitmap());
                    }
                    appMetadataMap.put(tempAppMetaData.getPackageName(), tempAppMetaData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    public String loadJSONFromAsset() {
        String json = new String();
        try {
            InputStream inputStream = getActivity().getAssets().open("applist.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(AppMetadata item);
    }
}
