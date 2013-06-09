package com.moarub.diceness;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DieView extends View {
	private Die fDie;
	private RadialGradient fHalo;
	private Paint fHaloPaint;
	private Paint fBorderPaint;
	private TextPaint fTextPaint;
	private RectF fBox;
	private Paint fShadowPaint;
	private Context fContext;
	private TextPaint fRollingPaint;
	private Runnable fRepeater;
	private boolean fLocked;
	protected int fStreamId;
	private Rect fLockIconRect;
	private Paint fGrayPaint;
	private float fOriginalX;
	private float fOriginalY;
	private static Bitmap gLockBitmap;
	private static Bitmap gUnlockBitmap;
	private static Paint gBitmapPaint;

	public DieView(Context context) {
		super(context, null, 0);
		fContext = context;
		init();
	}

	public DieView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		fContext = context;
		init();
	}

	public DieView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		fContext = context;
		init();
	}

	private void init() {
		fDie = new Die(1, 6, 0);
		if (!isInEditMode()) {
			DiceApplication da = ((DiceActivity) fContext).getDiceApplication();
			da.addDiceView(this);

			setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					setPressed(event.getAction() == MotionEvent.ACTION_DOWN && !isLocked());

					if (event.getAction() == MotionEvent.ACTION_UP) {
						cleanRepeater();
						if (!isLockIconHit(null) && !isLocked()) {
							DicePlayer.playRoll(fContext);
							fDie.roll();
						} else if(isLockIconHit(null)) {
							fLocked = !fLocked;
							invalidate();
						}
						saveOrigin(null);
					} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
						saveOrigin(event);
						if (!isLockIconHit(event) && !isLocked()) {
							fStreamId = DicePlayer.playShake(fContext);
							launchRepeater();
						}
					}
					invalidate();
					return true;
				}
			});
		}
		fBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		fBorderPaint.setStyle(Style.STROKE);
		fBorderPaint.setStrokeWidth(1.f);
		fBorderPaint.setColor(Color.argb(0xff, 0x22, 0x22, 0x22));

		fShadowPaint = new Paint(fBorderPaint);
		fShadowPaint.setColor(Color.argb(0x88, 0x22, 0x22, 0x22));
		fShadowPaint.setStyle(Style.FILL_AND_STROKE);
		fShadowPaint.setStrokeWidth(3.f);
		fShadowPaint.setMaskFilter(new BlurMaskFilter(10.f, Blur.NORMAL));

	}

	protected void saveOrigin(MotionEvent event) {
		if (event == null) {
			fOriginalX = Float.MIN_VALUE;
			fOriginalY = Float.MIN_VALUE;
		} else {
			fOriginalX = event.getX();
			fOriginalY = event.getY();
		}
	}

	protected boolean isLockIconHit(MotionEvent event) {
		return event == null ? fLockIconRect.contains((int) fOriginalX,
				(int) fOriginalY) : fLockIconRect.contains((int)event.getX(),
				(int)event.getY());
	}

	private Bitmap getLockedBitmap(boolean locked) {
		if (gLockBitmap == null) {
			gLockBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_action_lock_closed);
		}
		if (gUnlockBitmap == null) {
			gUnlockBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_action__lock_open);
		}

		return locked ? gLockBitmap : gUnlockBitmap;
	}

	protected void cleanRepeater() {
		DicePlayer.stopPlaying(fStreamId);
		fStreamId = 0;
		if (fRepeater != null) {
			removeCallbacks(fRepeater);
			fRepeater = null;
		}
	}

	protected void launchRepeater() {
		fRepeater = getRepeater();
		postDelayed(getRepeater(), 400);
	}

	private Runnable getRepeater() {
		return new Runnable() {

			@Override
			public void run() {
				invalidate();
				postDelayed(getRepeater(), 400);
			}
		};
	}

	public Die getDie() {
		return fDie;
	}

	public void setDie(Die fDie) {
		this.fDie = fDie;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isPressed()) {
			canvas.save();
			canvas.rotate(getRandomDegree());
		}
		if (fBox != null) {
			if (!isPressed() && !isLocked()) {
				canvas.save();
				canvas.translate(1, 1);
				canvas.drawRoundRect(fBox, 8, 8, fShadowPaint);
				canvas.restore();
			}
			canvas.drawRoundRect(fBox, 8, 8, isLocked() ? fGrayPaint
					: fHaloPaint);
			canvas.drawRoundRect(fBox, 8, 8, fBorderPaint);

		}
		if (fDie.isRolled() && !isInEditMode()) {
			canvas.drawText(
					"" + (isPressed() && !isLockIconHit(null) ? getRandomText() : fDie.getLastRoll()),
					fBox.centerX() - fTextPaint.getTextSize() / 4.f,
					fBox.centerY() + fTextPaint.getTextSize() / 2.f,
					isPressed() ? fRollingPaint : fTextPaint);
		}
		Bitmap bmtd = getLockedBitmap(fLocked);
		canvas.drawBitmap(bmtd, getWidth() - 10 - bmtd.getWidth(), 10,
				getBitmapPaint());
		if (isPressed()) {
			canvas.restore();
		}
	}

	private String getRandomText() {
		Random r = new Random();
		String s = "!?-/\\";

		int item = r.nextInt(s.length());
		return s.substring(item, item + 1);
	}

	private float getRandomDegree() {
		Random r = new Random();
		return (float) (r.nextGaussian() * (r.nextFloat()));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		fBox = new RectF(10, 10, w - 10, h - 10);
		int[] ltgr = { Color.argb(0xff, 0xf2, 0xf2, 0xf2),
				Color.argb(0xff, 0xf2, 0xf2, 0xff) };
		float[] ltbl = { 0.f, 1.f };
		fHalo = new RadialGradient(w / 2.f, h / 2.f, w * 0.33f, ltgr, ltbl,
				TileMode.CLAMP);
		fHaloPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		fHaloPaint.setShader(fHalo);
		fTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG | Paint.DITHER_FLAG
				| Paint.SUBPIXEL_TEXT_FLAG);
		fTextPaint.setColor(Color.argb(0xff, 0x22, 0x22, 0x22));
		fTextPaint.setTypeface(Typeface.SERIF);
		fTextPaint.setTextSize(h / 2.5f);
		fTextPaint.setShadowLayer(4.f, 2.f, 2.f,
				Color.argb(0xff, 0x22, 0x22, 0x22));

		fGrayPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		fGrayPaint.setColor(Color.argb(0xff, 0xf2, 0xf2, 0xf2));

		fRollingPaint = new TextPaint(fTextPaint);
		fRollingPaint.setColor(Color.argb(0x88, 0x22, 0x22, 0x22));
		fTextPaint.setShadowLayer(8.f, 4.f, 4.f,
				Color.argb(0x88, 0x22, 0x22, 0x22));

		Bitmap lockedBitmap = getLockedBitmap(fLocked);
		fLockIconRect = new Rect(getWidth() - lockedBitmap.getWidth() - 10,
				0, getWidth(), lockedBitmap.getHeight() + 10);

	}

	public Paint getBitmapPaint() {
		if (gBitmapPaint == null) {
			gBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG
					| Paint.FILTER_BITMAP_FLAG);
		}
		return gBitmapPaint;
	}

	public void shake() {
		setPressed(true);
		invalidate();
	}

	public void stopShake() {
		setPressed(false);
		fDie.roll();
		invalidate();
	}

	public boolean isLocked() {
		return fLocked;
	}

	public void unlock() {
		fLocked = false;
		invalidate();
	}

}
