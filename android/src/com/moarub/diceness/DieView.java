package com.moarub.diceness;

import java.util.Random;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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
		DiceApplication da = ((DiceActivity)fContext).getDiceApplication();
		da.addDiceView(this);
		fDie = new Die(1, 6, 0);
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				fDie.roll();
				invalidate();
			}
		});
		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				setPressed(event.getAction() == MotionEvent.ACTION_DOWN);

				if (event.getAction() == MotionEvent.ACTION_UP) {
					DicePlayer.playRoll(fContext);
					fDie.roll();
				} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
					DicePlayer.playRoll(fContext);
				}
				invalidate();
				return true;
			}
		});

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

	public Die getDie() {
		return fDie;
	}

	public void setDie(Die fDie) {
		this.fDie = fDie;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(isPressed()) {
			canvas.save();
			canvas.rotate(getRandomDegree());
		}
		if (fBox != null) {
			if (!isPressed()) {
				canvas.save();
				canvas.translate(2, 2);
				canvas.drawRoundRect(fBox, 8, 8, fShadowPaint);
				canvas.restore();
			}
			canvas.drawRoundRect(fBox, 8, 8, fHaloPaint);
			canvas.drawRoundRect(fBox, 8, 8, fBorderPaint);

		}
		if (fDie.isRolled()) {
			canvas.drawText("" + (isPressed() ? getRandomText() : fDie.getLastRoll()),
					fBox.centerX() - fTextPaint.getTextSize() / 4.f,
					fBox.centerY() + fTextPaint.getTextSize() / 2.f, isPressed() ? fRollingPaint: fTextPaint);
		}
		canvas.restore();
	}

	private String getRandomText() {
		Random r = new Random();
		String s = "!?-/\\";
		
		int item = r.nextInt(s.length());
		return s.substring(item, item+1);
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
		
		fRollingPaint = new TextPaint(fTextPaint);
		fRollingPaint.setColor(Color.argb(0x88, 0x22, 0x22, 0x22));
		fTextPaint.setShadowLayer(8.f, 4.f, 4.f,
				Color.argb(0x88, 0x22, 0x22, 0x22));
		
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
	
}
