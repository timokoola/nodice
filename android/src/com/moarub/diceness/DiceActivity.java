/*******************************************************************************
 * Copyright (c) 2013 Moarub Oy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Timo Koola - initial API and implementation
 ******************************************************************************/
package com.moarub.diceness;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

public class DiceActivity extends Activity implements SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private int fStreamId = 0;
	private Runnable fStopper;
	private Handler fHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fHandler = new Handler();
		setContentView(R.layout.activity_dice);
		DicePlayer.initPlayer(this);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float load = event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2];
		if(load > 350.f) {
			cancelStopper();
			fStreamId = DicePlayer.playShake(this);
			getDiceApplication().shakeAll(true);
		} else if(load < 150.f && fStreamId > 1) {
			startStopper();
		}
	}

	private void startStopper() {
		if (fStopper == null) {
			fStopper = getStopper();
			fHandler.postDelayed(fStopper, 800);
		}
	}

	private void cancelStopper() {
		if(fStopper != null) {
			fHandler.removeCallbacks(fStopper);
			fStopper = null;
		}
	}

	public Runnable getStopper() {
		return new Runnable() {
			
			@Override
			public void run() {
				stopShaking();
			}
		};
	}
	
	
	private void stopShaking() {
		getDiceApplication().shakeAll(false);
		DicePlayer.stopPlaying(fStreamId);
		fStreamId = 0;
	}
	
	public DiceApplication getDiceApplication() {
		return (DiceApplication) getApplication();
	}

}
