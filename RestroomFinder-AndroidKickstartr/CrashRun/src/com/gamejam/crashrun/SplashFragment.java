package com.gamejam.crashrun;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.googlecode.androidannotations.annotations.EFragment;

@EFragment
public class SplashFragment extends Fragment {
	//The start button
	Button btn;
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	super.onCreateView(inflater, container, savedInstanceState);
	RelativeLayout RelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_splash, container, false);
	btn = (Button) RelativeLayout.findViewById(R.id.startBtn);
	return RelativeLayout; 
}

public void enableButton(){
	btn.setEnabled(true);
}
public void disableButton(){
	btn.setEnabled(false);
}
}
