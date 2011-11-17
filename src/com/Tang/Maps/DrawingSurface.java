package com.Tang.Maps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class DrawingSurface extends android.view.SurfaceView implements SurfaceHolder.Callback{

	/** Members */
	private float[] north = {0, 0, 90};
	private float[] current = new float[3];
	
	
	public DrawingSurface(Context context) {
		super(context);
		this.setWillNotDraw(false);
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onDraw(Canvas canvas){
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setAlpha(100);
		float h,w;
		h = canvas.getHeight();
		w = canvas.getWidth();
		canvas.drawCircle(w/2, h/2, 30, paint);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	
	/** Utilities */
	public void setTarget(float[] direction){
		
	}
	
	public void setCurrent(float[] direction){
		current = direction;
	}
	

}
