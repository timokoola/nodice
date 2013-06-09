package com.moarub.diceness;

import java.util.ArrayList;

import android.app.Application;

public class DiceApplication extends Application {
	private ArrayList<DieView> fDice;

	public DiceApplication() {
		fDice = new ArrayList<DieView>();
	}

	public void addDiceView(DieView view) {
		fDice.add(view);
	}
	
	public void shakeAll(boolean stop) {
		for(DieView dv: fDice) {
			if(dv != null && !dv.isLocked()) {
				if(stop){
					dv.shake();
				} else {
					dv.stopShake();
				}
			}
		}
	}

	public void unlockAll() {
		for(DieView dv: fDice) {
			if(dv != null)  {
				dv.unlock();
			}
		}
	}
	
}
