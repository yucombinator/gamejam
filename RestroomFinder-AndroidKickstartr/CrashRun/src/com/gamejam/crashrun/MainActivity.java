
package com.gamejam.crashrun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import com.gamejam.crashrun.ViewMapFragment.onCameraListener;
import com.gamejam.crashrun.api.OSMNode;
import com.gamejam.crashrun.api.OSMWrapperAPI;
import com.gamejam.crashrun.rest.RestClient;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;

@EActivity
public class MainActivity
    extends SherlockFragmentActivity
    implements onCameraListener, TabListener
{
	/*TODO 
	 * ADD CREDITS FOR GMAPS AND OSM
	 * TODO one point per round?
	 */
	

	public static String TAG = "BathroomFinder";
//	static ArrayList <OSMNode> allTapItem = new ArrayList<OSMNode>();
//	static ArrayList <OSMNode> allToiletItem = new ArrayList<OSMNode>();
//	static ArrayList <OSMNode> allFoodItem = new ArrayList<OSMNode>();
	
    Fragment mMapFragment;
    Fragment mListFragment;
    TextView timerText;
    TextView roundText;
    static CountDownTimer cdt;
    int a = 60000; //time remaining
    int rounds = 0;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        View LL = LayoutInflater.from(this).inflate(R.layout.text_counters, null);
        getSupportActionBar().setCustomView(LL);
        timerText = (TextView) LL.findViewById(R.id.textTimeronTheActionBar);
        //timerText.setText("00");
        
        roundText = (TextView) LL.findViewById(R.id.textRounds);
       // roundText.setText("00");

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      if (item.getItemId() == R.id.credits) {
        startActivity(new Intent(this, LegalNoticesActivity.class));

        return(true);
      }else if(item.getItemId() == R.id.satellite){
    	ViewMapFragment mMapFragment = (ViewMapFragment) getSupportFragmentManager().findFragmentByTag("map");
  		mMapFragment.changeView(ViewMapFragment.MapType.Satellite);
    	  
      }else if(item.getItemId() == R.id.hybrid){
      	ViewMapFragment mMapFragment = (ViewMapFragment) getSupportFragmentManager().findFragmentByTag("map");
    		mMapFragment.changeView(ViewMapFragment.MapType.Hybrid);
      }else if(item.getItemId() == R.id.map_only){
      	ViewMapFragment mMapFragment = (ViewMapFragment) getSupportFragmentManager().findFragmentByTag("map");
    		mMapFragment.changeView(ViewMapFragment.MapType.Map);
      }else if(item.getItemId() == R.id.terrain){
      	ViewMapFragment mMapFragment = (ViewMapFragment) getSupportFragmentManager().findFragmentByTag("map");
    		mMapFragment.changeView(ViewMapFragment.MapType.Terrain);
      }else if(item.getItemId() == R.id.add_new){
        	ViewMapFragment mMapFragment = (ViewMapFragment) getSupportFragmentManager().findFragmentByTag("map");
      		mMapFragment.newRound();
      		
    		rounds = 1;
    		roundText.setText("Round " + rounds);
   		
      }
      return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  
        //requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        if(savedInstanceState != null){
//        	allTapItem = (ArrayList<OSMNode>) savedInstanceState.get("allTapItem");
//        	allToiletItem = (ArrayList<OSMNode>) savedInstanceState.get("allToiletItem");
//        	allFoodItem = (ArrayList<OSMNode>) savedInstanceState.get("allFoodItem");
        }
        //TODO Semi-Transparent Action Bar
       // requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);       
       // getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));
        
        setContentView(R.layout.activity_main);
    	configureActionBar();
    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		mMapFragment = getSupportFragmentManager().findFragmentByTag("map");
		if (mMapFragment == null) {
			// If not, instantiate and add it to the activity
			mMapFragment = new ViewMapFragment_();
			ft.add(R.id.containerFrag, mMapFragment, "map").commit();
		} else {
			// If it exists, simply attach it in order to show it
			ft.show(mMapFragment).commit();
		}
		//timerText = (TextView) findViewById(R.id.timerText);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState){
//    	outState.putParcelableArrayList("allTapItem", allTapItem);
//    	outState.putParcelableArrayList("allToiletItem", allToiletItem);
//    	outState.putParcelableArrayList("allFoodItem", allFoodItem);
    	super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	
    }


    private void configureActionBar() {
/*        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        Tab tab = actionBar.newTab()
                .setText("Map") //TODO String
                .setTabListener(this);
        actionBar.addTab(tab);

        tab = actionBar.newTab()
            .setText("List") //TODO String
            .setTabListener(this);
        actionBar.addTab(tab);*/
        
    }
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		
		if(tab.getPosition() == 0){
			// Check if the fragment is already initialized
			mMapFragment = getSupportFragmentManager().findFragmentByTag("map");
			if (mMapFragment == null) {
				// If not, instantiate and add it to the activity
				mMapFragment = new ViewMapFragment_();
				ft.add(R.id.containerFrag, mMapFragment, "map");
			} else {
				// If it exists, simply attach it in order to show it
				ft.show(mMapFragment);
			}
		}
		if(tab.getPosition() == 1){
			// Check if the fragment is already initialized
			mMapFragment = getSupportFragmentManager().findFragmentByTag("list");
			if (mListFragment == null) {
				// If not, instantiate and add it to the activity
			//	mListFragment = new RestroomListFragment_();
				ft.add(R.id.containerFrag, mListFragment, "list");
			} else {
				// If it exists, simply attach it in order to show it
				ft.show(mListFragment);
			}
		}

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if(tab.getPosition() == 0){
			if (mMapFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.hide(mMapFragment);
			}
		}
		if(tab.getPosition() == 1){
			if (mListFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.hide(mListFragment);
			}
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraLocationChange(LatLng loc) {
      //  setProgressBarIndeterminateVisibility(true); 
		//updatePOIs(loc);
	}

	@UiThread
	public void stopProgressbar() {
	  //  setProgressBarIndeterminateVisibility(false); 
		
	}

	@Override
	public void onMyLocationChange(Location location) {
		Log.d(TAG, "onMyLocationChange()");

	}
	
	@UiThread
	public void Countdown()
	{		
		if(cdt == null)
		{
			cdt = new CountDownTimer(a, 1000) {

				public void onTick(long millisUntilFinished) 
				{
					long s = 0;
					long m = 0;
					
					//timerText.setText("" + millisUntilFinished / 1000);
					m = millisUntilFinished/60000;
					s = millisUntilFinished/1000 - m * 60;
					timerText.setText("" + m + ":" + s);
					setProgressBarIndeterminateVisibility(true); 
				}
			
				public void onFinish() 
				{
					timerText.setText("Game over!");
					setProgressBarIndeterminateVisibility(false);
					
					// Get instance of Vibrator from current Context
					Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					 
					int dot = 300;
					int short_gap = 200;    // Length of Gap Between dots/dashes
					long[] pattern = {
					    0,  // Start immediately
					    dot, short_gap, dot, short_gap, dot
					};
					 
					// Only perform this pattern one time (-1 means "do not repeat")
					v.vibrate(pattern, -1);
					
				}
			}.start();
		}
	}

	@Override
	public void onOrbGet()
	{
		Log.d(TAG, ""+ a);
		cdt.cancel();
		cdt = null;
		a += 30000;
		Countdown();
		// Get instance of Vibrator from current Context
		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		 
		int dot = 300;
		int short_gap = 100;    // Length of Gap Between dots/dashes
		long[] pattern = {
		    0,  // Start immediately
		    dot, short_gap, dot
		};
		 
		// Only perform this pattern one time (-1 means "do not repeat")
		v.vibrate(pattern, -1);
	}

	@Override
	public void onNewRound() {
		// TODO Auto-generated method stub
		rounds = rounds + 1;
		Log.d(TAG, "rounds: " + rounds);
		roundText.setText("Round " + rounds);
  		Countdown();
	}	

}
