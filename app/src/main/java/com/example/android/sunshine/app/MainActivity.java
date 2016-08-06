package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.example.android.sunshine.app.sync.SunshineSyncAdapter;


public class MainActivity extends ActionBarActivity implements ForecastFragment.Callback{

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private String mLocation;
    private boolean mTwoPane;

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        Log.i(LOG_TAG, "onStart Called");
        super.onStart();
    }


    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        Log.i(LOG_TAG, "onResume Called");
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        // update the location in our second pane using the fragment manager
        if(location != null && !location.equals(mLocation)){
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if(ff != null){
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if(df != null){
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }

    /**
     * DetailFragmentCallback for when an item has been selected.
     *
     * @param contentUri
     */
    @Override
    public void onItemSelected(Uri contentUri) {
        if(!mTwoPane){
            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(contentUri);
            startActivity(intent);
        }
        else{
            //In two pane mode
            // Show the detail view by adding or replacing detail fragment to this activity
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG_TAG, "onDestroy Called");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.i(LOG_TAG, "onStop Called");
        super.onStop();
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "onPause Called");
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate Called");
        mLocation = Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.weather_detail_container) == null){
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        else{
            //Two-pane mode
            mTwoPane = true;
            //Show the detail view by adding or replacing detail fragment to this activity
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }

        ForecastFragment forecastFragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        forecastFragment.setUseTodayLayout(!mTwoPane);

        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        Log.i(LOG_TAG, "onCreateView Called");
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


}
