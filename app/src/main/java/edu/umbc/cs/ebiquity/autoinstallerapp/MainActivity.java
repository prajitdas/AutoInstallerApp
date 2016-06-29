package edu.umbc.cs.ebiquity.autoinstallerapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import edu.umbc.cs.ebiquity.autoinstallerapp.model.AppMetadata;
import edu.umbc.cs.ebiquity.autoinstallerapp.util.VolleySingleton;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   AppFragment.OnListFragmentInteractionListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private AppMetadata appMetadataItemSelected = new AppMetadata("dummy");
    private FloatingActionButton fab;
    private String fragmentBeingDisplayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /**
         * Get the json of list of apps to experiment with and display that list
         */
        loadJSONFromWebService();
//        defaultFragmentLoad();
    }

    private void defaultFragmentLoad() {
        loadToInstallAppsFragment();
    }

    private void loadAllAppsFragment() {
        Bundle data = new Bundle();
        data.putInt(ARG_COLUMN_COUNT, mColumnCount);
        data.putString(AutoInstallerApplication.getAppDisplayTypeTag(), AutoInstallerApplication.getAllAppsDisplayTag());
        fragmentBeingDisplayed = AutoInstallerApplication.getAllAppsDisplayTag();
        loadFragment(data);
    }

    private void loadSystemAppsFragment() {
        Bundle data = new Bundle();
        data.putInt(ARG_COLUMN_COUNT, mColumnCount);
        data.putString(AutoInstallerApplication.getAppDisplayTypeTag(), AutoInstallerApplication.getSystemAppsDisplayTag());
        fragmentBeingDisplayed = AutoInstallerApplication.getSystemAppsDisplayTag();
        loadFragment(data);
    }

    private void loadUserAppsFragment() {
        Bundle data = new Bundle();
        data.putInt(ARG_COLUMN_COUNT, mColumnCount);
        data.putString(AutoInstallerApplication.getAppDisplayTypeTag(), AutoInstallerApplication.getUserAppsDisplayTag());
        fragmentBeingDisplayed = AutoInstallerApplication.getUserAppsDisplayTag();
        loadFragment(data);
    }

    private void loadToInstallAppsFragment() {
        Bundle data = new Bundle();
        data.putInt(ARG_COLUMN_COUNT, mColumnCount);
        data.putString(AutoInstallerApplication.getAppDisplayTypeTag(), AutoInstallerApplication.getToInstallAppsDisplayTag());
        data.putString("json",json);
        fragmentBeingDisplayed = AutoInstallerApplication.getToInstallAppsDisplayTag();
        loadFragment(data);
    }

    private void loadFragment(Bundle data) {
        AppFragment aAppFragment = new AppFragment();
        aAppFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, aAppFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_slideshow) {
            loadToInstallAppsFragment();
        } else if (id == R.id.nav_camera) {
//            loadUserAppsFragment();
        } else if (id == R.id.nav_gallery) {
//            loadSystemAppsFragment();
        } else if (id == R.id.nav_manage) {
//            loadAllAppsFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(AppMetadata item) {
        //TODO Do something with the item selected
        appMetadataItemSelected = item;

        if(fragmentBeingDisplayed == AutoInstallerApplication.getToInstallAppsDisplayTag()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id="+appMetadataItemSelected.getPackageName()));
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(),"No actions have been defined for the current view", Toast.LENGTH_LONG).show();
        }
    }

    private static String json = new String();

    public void loadJSONFromWebService() {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, AutoInstallerApplication.getUrl(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        json = response.toString();
                        defaultFragmentLoad();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(AutoInstallerApplication.getDebugTag(),"Error response"+error.getMessage());
                    }
                });

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        /**
         * We were reading the data from the asset but now we will do so from the webservice
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
         */
    }
}
