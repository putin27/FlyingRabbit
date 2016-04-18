package tw.brad.book.mysvtest1;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class StartSurfaceView extends SurfaceView implements Callback {

	private MainActivity activity;
	private Resources res;
	private Paint paint;
	private MyThread myThread;
	private float upX, upY, downX, downY, touchX, touchY, screenNowX, moveX;
	private int pageNow;
	private boolean isTouching = false;
	private boolean isLoading;
	private float width, height;
	private boolean isInited;
	private float scal, scal2;
	private Bitmap bg, stage1, stage2, stage3, stage4, stage5, stage1_d,
			stage2_d, stage3_d, stage4_d, stage5_d, back,loading,ip_config;

	public StartSurfaceView(MainActivity activity) {
		super(activity);

		this.activity = activity;
		this.getHolder().addCallback(this);
		this.setKeepScreenOn(true);// 使畫面一直亮著
		isTouching = false;
		isInited = false;
		isLoading = false;
		screenNowX = 0;
		pageNow = 0;
		
		res = getResources();
		bg = BitmapFactory.decodeResource(res, R.drawable.bg);

		stage1 = BitmapFactory.decodeResource(res, R.drawable.stage1);
		stage2 = BitmapFactory.decodeResource(res, R.drawable.stage2);
		stage3 = BitmapFactory.decodeResource(res, R.drawable.stage3);
		stage4 = BitmapFactory.decodeResource(res, R.drawable.stage4);
		stage5 = BitmapFactory.decodeResource(res, R.drawable.stage5);
		stage1_d = BitmapFactory.decodeResource(res, R.drawable.stage1_d);
		stage2_d = BitmapFactory.decodeResource(res, R.drawable.stage2_d);
		stage3_d = BitmapFactory.decodeResource(res, R.drawable.stage3_d);
		stage4_d = BitmapFactory.decodeResource(res, R.drawable.stage4_d);
		stage5_d = BitmapFactory.decodeResource(res, R.drawable.stage5_d);
		
		loading = BitmapFactory.decodeResource(res, R.drawable.loading);

		back = BitmapFactory.decodeResource(res, R.drawable.back);
		
	}

	private float scalCal(float width, float scal, float imageW) {
		float temp = 0;

		// 所需圖片尺寸
		temp = width * scal;
		// 原圖尺寸/所需尺寸
		temp = temp / imageW;

		return temp;
	}

	private void init() {

		// 取得手機寬高
		width = getWidth();
		height = getHeight();
		// 計算縮放比例
		Matrix matrix = new Matrix();

		scal = scalCal((float) height, (float) 1 / 4,
				(float) stage1.getHeight());
		// 設定放大倍數
		matrix.setScale(scal, scal);
		// 縮放按鈕圖檔
		stage1 = Bitmap.createBitmap(stage1, 0, 0, stage1.getWidth(),
				stage1.getHeight(), matrix, true);
		stage2 = Bitmap.createBitmap(stage2, 0, 0, stage2.getWidth(),
				stage2.getHeight(), matrix, true);
		stage3 = Bitmap.createBitmap(stage3, 0, 0, stage3.getWidth(),
				stage3.getHeight(), matrix, true);
		stage4 = Bitmap.createBitmap(stage4, 0, 0, stage4.getWidth(),
				stage4.getHeight(), matrix, true);
		stage5 = Bitmap.createBitmap(stage5, 0, 0, stage5.getWidth(),
				stage5.getHeight(), matrix, true);

		stage1_d = Bitmap.createBitmap(stage1_d, 0, 0, stage1_d.getWidth(),
				stage1_d.getHeight(), matrix, true);
		stage2_d = Bitmap.createBitmap(stage2_d, 0, 0, stage2_d.getWidth(),
				stage2_d.getHeight(), matrix, true);
		stage3_d = Bitmap.createBitmap(stage3_d, 0, 0, stage3_d.getWidth(),
				stage3_d.getHeight(), matrix, true);
		stage4_d = Bitmap.createBitmap(stage4_d, 0, 0, stage4_d.getWidth(),
				stage4_d.getHeight(), matrix, true);
		stage5_d = Bitmap.createBitmap(stage5_d, 0, 0, stage5_d.getWidth(),
				stage5_d.getHeight(), matrix, true);
		
		scal = scalCal((float) height, (float) 1 / 2,
				(float) loading.getHeight());
		matrix.setScale(scal, scal);
		loading = Bitmap.createBitmap(loading, 0, 0, loading.getWidth(),
				loading.getHeight(), matrix, true);

		scal = scalCal((float) height, (float) 1 / 8, (float) back.getHeight());
		matrix.setScale(scal, scal);
		back = Bitmap.createBitmap(back, 0, 0, back.getWidth(),
				back.getHeight(), matrix, true);
		
		
		
		// 縮放背景圖檔
		scal = scalCal((float) width, (float) 1, (float) bg.getWidth());
		scal2 = scalCal((float) height, (float) 1, (float) bg.getHeight());
		matrix.setScale(scal, scal2);
		bg = Bitmap.createBitmap(bg, 0, 0, bg.getWidth(), bg.getHeight(),
				matrix, true);

		isInited = true;
	}

	void drawCanvas(Canvas canvas) {
		paint = new Paint();
		paint.setTextSize(100);
		paint.setColor(Color.WHITE);
		canvas.drawBitmap(bg, screenNowX + moveX, 0, null);
		canvas.drawBitmap(bg, screenNowX + width + moveX, 0, null);
		canvas.drawBitmap(stage1, screenNowX + width / 5 + moveX, height / 5,
				null);
		canvas.drawBitmap(stage2, screenNowX + width / 5 * 3 + moveX,
				height / 5, null);
		canvas.drawBitmap(stage3, screenNowX + width / 5 + moveX,
				height / 5 * 3, null);
		canvas.drawBitmap(stage4, screenNowX + width / 5 * 3 + moveX,
				height / 5 * 3, null);
		canvas.drawBitmap(stage5, screenNowX + width + width / 5 + moveX,
				height / 5, null);

		canvas.drawBitmap(back, 0, height / 8 * 7, null);

		if (isTouching) {
			if (moveX == 0) {
				if (pageNow == 0) {
					if (downX > (width / 5)
							&& downX < (width / 5) + stage1_d.getWidth()
							&& downY > (height / 5)
							&& downY < (height / 5) + stage1_d.getHeight()) {
						canvas.drawBitmap(stage1_d, width / 5, height / 5, null);
					}
					if (downX > (width / 5 * 3)
							&& downX < (width / 5 * 3) + stage2_d.getWidth()
							&& downY > (height / 5)
							&& downY < (height / 5) + stage2_d.getHeight()) {
						canvas.drawBitmap(stage2_d, width / 5 * 3, height / 5,
								null);
					}
					if (downX > (width / 5)
							&& downX < (width / 5) + stage3_d.getWidth()
							&& downY > (height / 5 * 3)
							&& downY < (height / 5 * 3) + stage3_d.getHeight()) {
						canvas.drawBitmap(stage3_d, width / 5, height / 5 * 3,
								null);
					}
					if (downX > (width / 5 * 3)
							&& downX < (width / 5 * 3) + stage4_d.getWidth()
							&& downY > (height / 5 * 3)
							&& downY < (height / 5 * 3) + stage4_d.getHeight()) {
						canvas.drawBitmap(stage4_d, width / 5 * 3,
								height / 5 * 3, null);
					}
				} else if (pageNow == 1) {
					if (downX > (width / 5)
							&& downX < (width / 5) + stage5_d.getWidth()
							&& downY > (height / 5)
							&& downY < (height / 5) + stage5_d.getHeight()) {
						canvas.drawBitmap(stage5_d, width / 5, height / 5, null);
					}
				}

			}
		}
		if(isLoading){
			canvas.drawBitmap(loading,width/2-loading.getWidth()/2,height/2-loading.getHeight()/2 ,null);
		}

	}

	private class MyThread extends Thread {

		private boolean run;

		public MyThread() {
			run = true;
		}

		public void setRun(boolean run) {
			this.run = run;
		}

		@Override
		public void run() {

			if (!isInited) {
				init();
			}

			while (run) {
				SurfaceHolder myholder = StartSurfaceView.this.getHolder();
				Canvas canvas = myholder.lockCanvas();

				try {
					synchronized (myholder) {
						drawCanvas(canvas);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (canvas != null) {
						myholder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		touchX = event.getX();
		touchY = event.getY();

		// 判斷手觸摸瞬間
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			isTouching = true;
			downX = event.getX();
			downY = event.getY();
		}
		// 判斷手離開瞬間
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			isTouching = false;
			upX = event.getX();
			upY = event.getY();
		}

		if (isTouching) {
			// 計算moveX

			moveX = touchX - downX;
			// 限制可移動的邊界不超過設定的範圍(螢幕的範圍)
			if (screenNowX + moveX > 0) {
				screenNowX = 0;
				moveX = 0;
			}
			if (screenNowX + moveX < -width) {
				screenNowX = -width;
				moveX = 0;
			}
			if (pageNow == 0) {
				if (screenNowX + moveX < width / 18
						&& screenNowX + moveX > -width / 18) {
					screenNowX = 0;
					moveX = 0;
				}
			} else if (pageNow == 1) {
				if (screenNowX + moveX < width / 18 - width
						&& screenNowX + moveX > -width / 18 - width) {
					screenNowX = -width;
					moveX = 0;
				}
			}

		}
		if (!isTouching) {

			screenNowX = screenNowX + moveX;

			if (upX > 0 && upX <0+ back.getWidth()
					&& downX > 0
					&& downX < 0 + back.getWidth()
					&& upY > (height / 8 * 7)
					&& upY < (height / 8 * 7) + back.getHeight()
					&& downY > (height / 8 * 7)
					&& downY < (height / 8 * 7) + back.getHeight()) {
				activity.setUiNow(0);
				activity.sendMessage(0);
				myThread.setRun(false);
			}
			if (moveX == 0) {
				if (pageNow == 0) {

					if (downX > (width / 5)
							&& downX < (width / 5) + stage1_d.getWidth()
							&& downY > (height / 5)
							&& downY < (height / 5) + stage1_d.getHeight()
							&& upX > (width / 5)
							&& upX < (width / 5) + stage1_d.getWidth()
							&& upY > (height / 5)
							&& upY < (height / 5) + stage1_d.getHeight()

					) {
						activity.setUiNow(11);
						activity.sendMessage(0);
						isLoading = true;
						myThread.setRun(false);
					}
					if (downX > (width / 5 * 3)
							&& downX < (width / 5 * 3) + stage2_d.getWidth()
							&& downY > (height / 5)
							&& downY < (height / 5) + stage2_d.getHeight()
							&& upX > (width / 5 * 3)
							&& upX < (width / 5 * 3) + stage2_d.getWidth()
							&& upY > (height / 5)
							&& upY < (height / 5) + stage2_d.getHeight()) {
						activity.setUiNow(12);
						activity.sendMessage(0);
						isLoading = true;
						myThread.setRun(false);
					}
					if (downX > (width / 5)
							&& downX < (width / 5) + stage3_d.getWidth()
							&& downY > (height / 5 * 3)
							&& downY < (height / 5 * 3) + stage3_d.getHeight()
							&& upX > (width / 5)
							&& upX < (width / 5) + stage3_d.getWidth()
							&& upY > (height / 5 * 3)
							&& upY < (height / 5 * 3) + stage3_d.getHeight()) {
						activity.setUiNow(13);
						activity.sendMessage(0);
					}
					if (downX > (width / 5 * 3)
							&& downX < (width / 5 * 3) + stage4_d.getWidth()
							&& downY > (height / 5 * 3)
							&& downY < (height / 5 * 3) + stage4_d.getHeight()
							&& upX > (width / 5 * 3)
							&& upX < (width / 5 * 3) + stage4_d.getWidth()
							&& upY > (height / 5 * 3)
							&& upY < (height / 5 * 3) + stage4_d.getHeight()) {
						activity.setUiNow(14);
						activity.sendMessage(0);
					}
				} else if (pageNow == 1) {
					if (downX > (width / 5)
							&& downX < (width / 5) + stage5_d.getWidth()
							&& downY > (height / 5)
							&& downY < (height / 5) + stage5_d.getHeight()
							&& upX > (width / 5)
							&& upX < (width / 5) + stage5_d.getWidth()
							&& upY > (height / 5)
							&& upY < (height / 5) + stage5_d.getHeight()) {
						activity.setUiNow(15);
						activity.sendMessage(0);
					}
				}
			} else {

				if (screenNowX >= -(width / 2)) {

					touchX = 0;
					downX = 0;
					moveX = 0;
					pageNow = 0;
					float temp = 0 - screenNowX;
					for (int i = 0; i < 200; i++) {
						try {
							Thread.sleep(2);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						screenNowX = screenNowX + (temp / 200);
					}
					screenNowX = 0;
				} else if (screenNowX < -(width / 2)) {

					touchX = 0;
					downX = 0;
					moveX = 0;
					pageNow = 1;
					float temp = -width - screenNowX;
					for (int i = 0; i < 200; i++) {
						try {
							Thread.sleep(2);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						screenNowX = screenNowX + (temp / 200);
					}
					screenNowX = -width;
				}
			}

		}

		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

		myThread = new MyThread();
		myThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

		boolean retry = true;
		myThread.setRun(false);
		while (retry) {
			try {
				myThread.join();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}// ���ϵ�ѭ����ֱ��ˢ֡�߳̽���
		}
	}

}
