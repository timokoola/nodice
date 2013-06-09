package com.moarub.diceness;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;

public class RollAllButton extends ActionButton {
	private DiceApplication fApplication;
	private Runnable fShaker;

	public RollAllButton(Context context) {
		super(context);
	}

	public RollAllButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RollAllButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void doAction2() {
		fApplication = fApplication != null ? fApplication: ((DiceActivity) fContext).getDiceApplication();
		fApplication.shakeAll(true);
		DicePlayer.playShake(fContext);
		if (fShaker == null) {
			fShaker = getShaker();
			postDelayed(fShaker, 900);
		}
	}
	
	@Override
	protected void doAction() {
		fApplication = fApplication != null ? fApplication: ((DiceActivity) fContext).getDiceApplication();
		fApplication.unlockAll();
	}

	private Runnable getShaker() {
		return new Runnable() {
			
			@Override
			public void run() {
				DicePlayer.playRoll(fContext);
				fApplication.shakeAll(false);
				
				fShaker = null;
			}
		};
	}

	@Override
	protected void initIcon() {
		fIcon2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_action_rollall);
		fIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_action__lock_open);
	}
}
