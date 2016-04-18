package tw.brad.book.mysvtest1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.Toast;

public class MenuSurfaceView extends SurfaceView implements Callback {
	private MainActivity activity;
	private Bitmap b_start, b_start_d, b_start_multi, b_start_multi_d, b_item,
			b_item_d, b_make_item, b_make_item_d, b_logo, bg, b_coin, b_gear,
			b_sgear, ip_config;
	private float width, height;
	private boolean isInited;
	private Resources res;
	private float scal, scal2;
	private float upX, upY, downX, downY;
	private boolean isTouching;
	private MyThread myThread;
	private long startTime, endTime;
	private int diffTime;
	private String pt1, p_money, p_parts, p_sparts;
	public static final int TIME_IN_FRAME = 30;// 更新畫面的時間
	private EditText editText;
	private AlertDialog.Builder editDialog;

	public MenuSurfaceView(MainActivity activity) {
		super(activity);

		this.activity = activity;
		this.getHolder().addCallback(this);
		this.setKeepScreenOn(true);// 使畫面一直亮著

		isInited = false;
		isTouching = false;

		res = getResources();
		// 載入圖片
		b_start = BitmapFactory.decodeResource(res, R.drawable.strat);
		b_start_d = BitmapFactory.decodeResource(res, R.drawable.strat_d);
		b_start_multi = BitmapFactory.decodeResource(res,
				R.drawable.strat_multi);

		b_start_multi_d = BitmapFactory.decodeResource(res,
				R.drawable.strat_multi_d);
		b_item = BitmapFactory.decodeResource(res, R.drawable.item);
		b_item_d = BitmapFactory.decodeResource(res, R.drawable.item_d);
		b_make_item = BitmapFactory.decodeResource(res, R.drawable.make_item);
		b_make_item_d = BitmapFactory.decodeResource(res,
				R.drawable.make_item_d);
		b_logo = BitmapFactory.decodeResource(res, R.drawable.logo_small);
		bg = BitmapFactory.decodeResource(res, R.drawable.bg);

		b_coin = BitmapFactory.decodeResource(res, R.drawable.coin);
		b_gear = BitmapFactory.decodeResource(res, R.drawable.gear);
		b_sgear = BitmapFactory.decodeResource(res, R.drawable.sgear);

		ip_config = BitmapFactory.decodeResource(res, R.drawable.ip_config);

		// Log.i("info","bg's w:"+bg.getWidth()+" h:"+bg.getHeight());
		getPlayerData();
	}

	private void showEdit() {
		editText = new EditText(this.activity);
		new AlertDialog.Builder(this.activity)
				.setTitle("目前IP:"+activity.dbhelper.getIp())
				.setView(editText)
				.setPositiveButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {

							}
						})
				.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						if (editText.getText().toString().replaceAll(" ", "").equals("")) {

						} else {
							activity.dbhelper.updateIp(editText.getText().toString());
							Toast.makeText(activity,
									"ip change to:"+editText.getText().toString(), 1000).show();
						}
					}
				}).show();
	}

	// 計算所需圖片尺寸
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
		scal = scalCal((float) width, (float) 1 / 4, (float) b_start.getWidth());
		// 設定放大倍數
		matrix.setScale(scal, scal);
		// 縮放按鈕圖檔
		b_start = Bitmap.createBitmap(b_start, 0, 0, b_start.getWidth(),
				b_start.getHeight(), matrix, true);
		b_start_multi = Bitmap.createBitmap(b_start_multi, 0, 0,
				b_start_multi.getWidth(), b_start_multi.getHeight(), matrix,
				true);
		b_item = Bitmap.createBitmap(b_item, 0, 0, b_item.getWidth(),
				b_item.getHeight(), matrix, true);
		b_make_item = Bitmap.createBitmap(b_make_item, 0, 0,
				b_make_item.getWidth(), b_make_item.getHeight(), matrix, true);

		b_start_d = Bitmap.createBitmap(b_start_d, 0, 0, b_start_d.getWidth(),
				b_start_d.getHeight(), matrix, true);
		b_start_multi_d = Bitmap.createBitmap(b_start_multi_d, 0, 0,
				b_start_multi_d.getWidth(), b_start_multi_d.getHeight(),
				matrix, true);
		b_item_d = Bitmap.createBitmap(b_item_d, 0, 0, b_item_d.getWidth(),
				b_item_d.getHeight(), matrix, true);
		b_make_item_d = Bitmap.createBitmap(b_make_item_d, 0, 0,
				b_make_item_d.getWidth(), b_make_item_d.getHeight(), matrix,
				true);

		// 縮放ICON
		scal = scalCal((float) width, (float) 1 / 13, (float) b_coin.getWidth());
		scal2 = scalCal((float) height, (float) 1 / 13,
				(float) b_coin.getHeight());
		matrix.setScale(scal, scal);
		b_coin = Bitmap.createBitmap(b_coin, 0, 0, b_coin.getWidth(),
				b_coin.getHeight(), matrix, true);
		b_gear = Bitmap.createBitmap(b_gear, 0, 0, b_gear.getWidth(),
				b_gear.getHeight(), matrix, true);
		b_sgear = Bitmap.createBitmap(b_sgear, 0, 0, b_sgear.getWidth(),
				b_sgear.getHeight(), matrix, true);

		// 縮放LOGO圖
		scal = scalCal((float) width, (float) 1 / 2, (float) b_logo.getWidth());
		scal2 = scalCal((float) height, (float) 1 / 2,
				(float) b_logo.getHeight());
		matrix.setScale(scal, scal2);
		b_logo = Bitmap.createBitmap(b_logo, 0, 0, b_logo.getWidth(),
				b_logo.getHeight(), matrix, true);

		scal = scalCal((float) height, (float) 1 / 6,
				(float) ip_config.getHeight());
		matrix.setScale(scal, scal);
		ip_config = Bitmap.createBitmap(ip_config, 0, 0, ip_config.getWidth(),
				ip_config.getHeight(), matrix, true);

		// 縮放背景圖檔
		scal = scalCal((float) width, (float) 1, (float) bg.getWidth());
		scal2 = scalCal((float) height, (float) 1, (float) bg.getHeight());
		matrix.setScale(scal, scal2);
		bg = Bitmap.createBitmap(bg, 0, 0, bg.getWidth(), bg.getHeight(),
				matrix, true);

		// Log.i("info","new bg's w:"+bg.getWidth()+" h:"+bg.getHeight());
		// Log.i("info","w:"+width+"  h:"+height);

		isInited = true;
	}

	void drawCanvas(Canvas canvas) {

		canvas.drawBitmap(bg, 0, 0, null);
		canvas.drawBitmap(b_start, width / 5 * 3, height / 5, null);
		canvas.drawBitmap(b_start_multi, width / 5 * 3, height / 5 * 2, null);
		canvas.drawBitmap(b_item, width / 5 * 3, height / 5 * 3, null);
		canvas.drawBitmap(b_make_item, width / 5 * 3, height / 5 * 4, null);
		canvas.drawBitmap(b_logo, width / 6, height / 5, null);

		Paint paint = new Paint();
		paint.setTextSize(height / 10);
		// paint.setStyle(Paint.Style.STROKE);
		// paint.setStrokeWidth(3);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		paint.setColor(Color.rgb(255, 128, 0));

		canvas.drawText(pt1, 0, height / 12, paint);
		paint.setColor(Color.rgb(217, 0, 108));
		canvas.drawBitmap(b_coin, 0, height / 9, null);
		canvas.drawText(p_money, width / 16, height / 5, paint);
		canvas.drawBitmap(b_gear, width / 16 * 4, height / 9, null);
		canvas.drawText(p_parts, width / 16 * 5, height / 5, paint);
		canvas.drawBitmap(b_sgear, width / 16 * 7, height / 9, null);
		canvas.drawText(p_sparts, width / 16 * 8, height / 5, paint);

		canvas.drawBitmap(ip_config, 0, height - ip_config.getHeight(), null);

		// 按下去按鈕變色
		if (isTouching) {
			if (downX > (width / 5 * 3)
					&& downX < (width / 5 * 3) + b_start.getWidth()
					&& downY > (height / 5)
					&& downY < (height / 5) + b_start.getHeight()) {
				canvas.drawBitmap(b_start_d, width / 5 * 3, height / 5, null);
			}
			if (downX > (width / 5 * 3)
					&& downX < (width / 5 * 3) + b_start.getWidth()
					&& downY > (height / 5 * 2)
					&& downY < (height / 5 * 2) + b_start.getHeight()) {
				canvas.drawBitmap(b_start_multi_d, width / 5 * 3,
						height / 5 * 2, null);
			}
			if (downX > (width / 5 * 3)
					&& downX < (width / 5 * 3) + b_start.getWidth()
					&& downY > (height / 5 * 3)
					&& downY < (height / 5 * 3) + b_start.getHeight()) {
				canvas.drawBitmap(b_item_d, width / 5 * 3, height / 5 * 3, null);
			}
			if (downX > (width / 5 * 3)
					&& downX < (width / 5 * 3) + b_start.getWidth()
					&& downY > (height / 5 * 4)
					&& downY < (height / 5 * 4) + b_start.getHeight()) {
				canvas.drawBitmap(b_make_item_d, width / 5 * 3, height / 5 * 4,
						null);
			}
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// 判斷手離開瞬間
		if (event.getAction() == MotionEvent.ACTION_UP) {
			isTouching = false;
			upX = event.getX();
			upY = event.getY();
		}
		// 判斷手觸摸瞬間
		else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			isTouching = true;

			downX = event.getX();
			downY = event.getY();
		}
		Log.i("info", "downX:" + downX + " downY:" + downY + " upX:" + upX
				+ " upY" + upY);

		// 按鈕觸碰事件

		if (!isTouching) {
			if (upX > (width / 5 * 3)
					&& upX < (width / 5 * 3) + b_start.getWidth()
					&& downX > (width / 5 * 3)
					&& downX < (width / 5 * 3) + b_start.getWidth()
					&& downY > (height / 5)
					&& downY < (height / 5) + b_start.getHeight()
					&& upY > (height / 5)
					&& upY < (height / 5) + b_start.getHeight()) {
				// Log.i("info","DrawCanvas downX:"+downX+" downY:"+downY+" upX:"+upX+" upY"+upY);
				activity.setUiNow(1);
				activity.sendMessage(0);
				myThread.setRun(false);
			}
			if (upX > (width / 5 * 3)
					&& upX < (width / 5 * 3) + b_start_multi.getWidth()
					&& downX > (width / 5 * 3)
					&& downX < (width / 5 * 3) + b_start_multi.getWidth()
					&& downY > (height / 5 * 2)
					&& downY < (height / 5 * 2) + b_start_multi.getHeight()
					&& upY > (height / 5 * 2)
					&& upY < (height / 5 * 2) + b_start_multi.getHeight()) {
				// Log.i("info","DrawCanvas downX:"+downX+" downY:"+downY+" upX:"+upX+" upY"+upY);
				activity.setUiNow(2);
				activity.sendMessage(0);
				// myThread.setRun(false);
			}
			if (upX > (width / 5 * 3)
					&& upX < (width / 5 * 3) + b_start_multi.getWidth()
					&& downX > (width / 5 * 3)
					&& downX < (width / 5 * 3) + b_start_multi.getWidth()
					&& downY > (height / 5 * 3)
					&& downY < (height / 5 * 3) + b_start_multi.getHeight()
					&& upY > (height / 5 * 3)
					&& upY < (height / 5 * 3) + b_start_multi.getHeight()) {
				// Log.i("info","DrawCanvas downX:"+downX+" downY:"+downY+" upX:"+upX+" upY"+upY);
				activity.setUiNow(3);
				activity.sendMessage(0);
				myThread.setRun(false);
			}
			if (upX > (width / 5 * 3)
					&& upX < (width / 5 * 3) + b_start_multi.getWidth()
					&& downX > (width / 5 * 3)
					&& downX < (width / 5 * 3) + b_start_multi.getWidth()
					&& downY > (height / 5 * 4)
					&& downY < (height / 5 * 4) + b_start_multi.getHeight()
					&& upY > (height / 5 * 4)
					&& upY < (height / 5 * 4) + b_start_multi.getHeight()) {
				// Log.i("info","DrawCanvas downX:"+downX+" downY:"+downY+" upX:"+upX+" upY"+upY);
				activity.setUiNow(4);
				activity.sendMessage(0);
				myThread.setRun(false);
			}
			if (upX > 0 && upX < 0 + ip_config.getWidth() && downX > 0
					&& downX < 0 + ip_config.getWidth()
					&& downY > height - ip_config.getHeight() && downY < height
					&& upY > height - ip_config.getHeight() && upY < height) {
				showEdit();
			}
		}

		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		Log.i("info", "Change");

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.i("info", "Create");
		myThread = new MyThread();
		myThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

		Log.i("info", "Destroy");

		boolean retry = true;
		myThread.setRun(false);
				while (retry) {
			try {
				myThread.join();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void getPlayerData() {
		PlayerData playerdata = activity.dbhelper.getPlayerData();
		pt1 = String
				.format("%s   lv:% 3d  exp:% 5d  cost:% 3d", playerdata.name,
						playerdata.lv, playerdata.exp, playerdata.cost);
		p_money = String.format("% 6d", playerdata.money);
		p_parts = String.format("% 3d", playerdata.parts);
		p_sparts = String.format("% 3d", playerdata.sparts);
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
			while (run) {
				// 開始時間
				startTime = System.currentTimeMillis();
				// 檢查是否有初始設定，並設定初始值
				if (!isInited) {
					init();
				}
				SurfaceHolder myholder = MenuSurfaceView.this.getHolder();
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

				endTime = System.currentTimeMillis();
				diffTime = (int) (endTime - startTime);

				while (diffTime <= TIME_IN_FRAME) {
					diffTime = (int) (System.currentTimeMillis() - startTime);
					Thread.yield();// 暫停thread
				}
			}
		}
	}

}
