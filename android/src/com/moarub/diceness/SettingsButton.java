package com.moarub.diceness;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SettingsButton extends View {
	private RadialGradient fHalo;
	private Paint fHaloPaint;
	private Paint fBorderPaint;
	private TextPaint fTextPaint;
	private RectF fBox;
	private Paint fShadowPaint;
	private Context fContext;
	private TextPaint fRollingPaint;
	private Bitmap fSettingsIcon;

	public SettingsButton(Context context) {
		super(context);
		init();
	}

	public SettingsButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SettingsButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		fBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		fBorderPaint.setStyle(Style.STROKE);
		fBorderPaint.setStrokeWidth(1.f);
		fBorderPaint.setColor(Color.argb(0xff, 0x22, 0x22, 0x22));

		fShadowPaint = new Paint(fBorderPaint);
		fShadowPaint.setColor(Color.argb(0x88, 0x22, 0x22, 0x22));
		fShadowPaint.setStyle(Style.FILL_AND_STROKE);
		fShadowPaint.setStrokeWidth(3.f);
		fShadowPaint.setMaskFilter(new BlurMaskFilter(10.f, Blur.NORMAL));

		fSettingsIcon = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_action_setings);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			setPressed(true);
			invalidate();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if(insideBorders(event)){
				launchPreferences();
			}
			setPressed(false);
			invalidate();
		}
		
		return true;
	}

	private boolean insideBorders(MotionEvent event) {
		return true;
	}

	private void launchPreferences() {
		Intent settings = new Intent(getContext(), SettingsActivity.class);
		settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getContext().startActivity(settings);
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

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (fBox != null) {
			if (!isPressed()) {
				canvas.save();
				canvas.translate(1, 1);
				canvas.drawRoundRect(fBox, 8, 8, fShadowPaint);
				canvas.restore();
			}
			canvas.drawRoundRect(fBox, 8, 8, fHaloPaint);
			canvas.drawRoundRect(fBox, 8, 8, fBorderPaint);

			canvas.save();
			// canvas.translate(canvas.getWidth()/2.f-fSettingsIcon.getMinimumWidth()/2.f,
			// canvas.getHeight()/2.f-fSettingsIcon.getMinimumHeight()/2.f);
			canvas.drawBitmap(fSettingsIcon,
					getWidth() / 2.f - fSettingsIcon.getWidth() / 2.f,
					getHeight() / 2.f - fSettingsIcon.getHeight() / 2.f,
					new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
			canvas.restore();

		}
	}
}
