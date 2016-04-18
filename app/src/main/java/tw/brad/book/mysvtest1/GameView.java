package tw.brad.book.mysvtest1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/*
 * 1.GameView的傳入參數，多了一個int stage關卡資料 
 * 
 * 2.震動傳的msg改為-1
 * 
 * 3.SurfaceView使用的Thread改為 "非直接new出來使用"，而是存在變數裡，
 * 用來控制關閉，以防關閉SurfaceView時崩潰
 * 
 * 4.Thread設置了2個method用來設定開始或停止的flag
 * 
 * 5.判斷回到主畫面時，傳送0給MainActivity，並停止Thread
 * 
 * 6.從資料庫取得IP activity.dbhelper.getIp()
 * 新增變數String ip
 * 用來存IP
 * 
 * 7.把Canvas提到drawCanvas外面 防止縮小遊戲崩潰
 */

public class GameView extends SurfaceView implements Callback {
	MainActivity activity;
	public static final int MAX_ENEMY = 20, MAX_BEFFECT = 10,
			MAX_ENEMYBIT = 30, MAX_WEAPONBIT = 30, MAX_BEFFECTBIT = 30,
			MAX_BG = 130;
	private SurfaceHolder holder;// *SurfaceView必須，畫面控制用

	private float screenW, screenH;// 螢幕的寬和長
	private boolean isInit;// 判斷螢幕是否存在、回主畫面的flag
	int totalcount = 0, clearcount = 0,clearcount2=0; // drawCanvas的迴圈次數，和遊戲經過的時間有關。遊戲通關時間。對手通關時間

	Player player = new Player();// 建立玩家物件
	Enemy[] enemy = new Enemy[MAX_ENEMY];// 創立敵人的陣列，畫面上最多存在的敵人數10個
	EnemySP[] enemySP = new EnemySP[MAX_ENEMY];
	Weapon[] weapon = new Weapon[2];// 建立武器物件陣列
	int chargeTime, chargeAtk;// 蓄力武器的蓄力時間,攻擊倍率
	int selectWeapon = 0;// 選擇中的武器0or1
	SpWeapon spWeapon;
	BulletEffect[] bEffect = new BulletEffect[MAX_BEFFECT];// 創立子彈效果的陣列，畫面上最多存在的敵人數10個

	int esp_exist = 0;
	float esp_x, esp_w;// 敵人的特殊攻擊

	float upX, upY, downX, downY, lastDownX, lastDownY, chargeX, chargeY; // 觸控放開的位置、按下的位置
	private boolean isTouching, firstTouching = true,// 是否觸碰中、是否第一次觸碰
			isSelecting = false, isMoving = false, isAttacking = false;// 是否在選武器、是否在移動玩家、玩家是否攻擊

	int stage;
	private Paint bgpaint, paintLife, paintWeapon, paintWeapon2, paintScore,
			paintBlack, paintRed, paintDanger, paintTscore, paintClear,
			paintBlack2;// 畫筆，記得要宣告new

	int kill = 0, damage = 0;// 測試用 擊殺數和受傷害數
	private int bgloop = 0, bgloop2 = 0, bgy = 220;// 背景LOOP、上下背景的分隔點

	private Bitmap bg,
			bg1_1_0,
			bg1_1_1,// 背景
			player01,
			player01_i,// 玩家
			lock, lock2, boom, enemyBit[] = new Bitmap[MAX_ENEMYBIT],
			enemyBit_i[] = new Bitmap[MAX_ENEMYBIT],
			weaponBit[] = new Bitmap[MAX_WEAPONBIT],
			weaponBit_s[] = new Bitmap[MAX_WEAPONBIT], spBit[] = new Bitmap[6],
			bEffectBit[] = new Bitmap[MAX_BEFFECTBIT],
			bgBit[] = new Bitmap[MAX_BG];
	/*
	 * private Bitmap Thunder = BitmapFactory.decodeResource(getResources(),
	 * R.drawable.thunder1); private Bitmap Thunder01 =
	 * Bitmap.createBitmap(Thunder, 192, 0, 192, 192); // 原圖,開始x,開始y,範圍x,範圍y;
	 * private Bitmap Thunder02 = Bitmap.createBitmap(Thunder, 192 * 4, 192,
	 * 192, 192); // 原圖,開始x,開始y,範圍x,範圍y;
	 */
	private MyThread myThread;

	// 網路連線用變數
	static Socket clntSock, clntSock2;
	static InputStream in, in2;
	static OutputStream out, out2;
	static int show;
	static String e;
	static int s_kill = 0, e_kill = 0, e_tkill = 0;
	static int s_time = 0, e_time = -1;
	static int s_dead = 0, e_dead = 0;
	//static int servPort = 6666;
	//static int servPort2 = 6667;
	static int servPort = 6668;
	static int servPort2 = 6669;
	static String ip;

	// 網路傳輸用
	private static class thread extends Thread {
		public void run() {
			try {
				// clntSock = new Socket("192.168.2.67", servPort);
				// clntSock = new Socket("192.168.1.24", servPort);
				clntSock = new Socket(ip, servPort);
				in = clntSock.getInputStream();
				out = clntSock.getOutputStream();
				Log.i("info3", "1success");
				TimerTask o = new TimerTask() {
					@Override
					public void run() {
						e = "" + s_time + "," + s_dead + "," + s_kill + ",";

						Log.i("netinfo", "e:" + e);
						Log.i("netinfo", "s_time:" + s_time + " s_dead:"
								+ s_dead + " s_kill:" + s_kill);
						s_kill = 0;
						// TODO Auto-generated method stub
						try {
							out.write(e.getBytes());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							try {
								clntSock.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				};
				Timer timer = new Timer();
				timer.schedule(o, 1000, 3000);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 網路接收用
	private static class thread2 extends Thread {
		public void run() {
			try {
				Log.i("info3", "2connect");
				// clntSock2 = new Socket("192.168.2.67", servPort2);
				// clntSock2 = new Socket("192.168.1.24", servPort2);
				clntSock2 = new Socket(ip, servPort2);

				in2 = clntSock2.getInputStream();
				out2 = clntSock2.getOutputStream();
				Log.i("info3", "2success");
				// 計時檢查接收訊息
				TimerTask o = new TimerTask() {
					int in_o = 0;
					int in_n = 0;
					String str = "";
					String s = "";
					String[] token;
					byte[] buf = new byte[100];

					@Override
					public void run() {
						try {
							str = "";
							in_o = in_n;
							in_n = in2.available(); // 判斷有沒有接收

							if ((in_n - in_o == 0)) {
								show = 0;

							} else {
								show = 1;
								in2.read(buf);
								str = new String(buf);
								in_n = in2.available();
							}

							System.out.println(str.trim());
							if (str.trim() == null)
								s = "";
							else
								s = str.trim();

							if (s != "") {
								token = s.split(",");
								e_time = Integer.parseInt(token[0]);
								e_dead = Integer.parseInt(token[1]);
								e_kill = Integer.parseInt(token[2]);
								if (e_kill < 50) {
									e_tkill = e_tkill + e_kill;
								}
								// System.out.printf("%d %d %d\n",k,t,d);
							} else {
								System.out.println("null");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				Timer timer = new Timer();
				timer.schedule(o, 1000, 3000);

			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}

	// 設定初值的函式
	public GameView(MainActivity mainActivity, int stage) // 建構子，導入圖片
	{
		super(mainActivity);
		this.activity = mainActivity;
		this.stage = stage;
		holder = getHolder();// *SurfaceView必須，畫面控制用
		holder.addCallback(this);// *SurfaceView必須，畫面控制用
		this.setKeepScreenOn(true);// 使畫面一直亮著

		// 從資料庫中取得IP
		ip = activity.dbhelper.getIp();

		// 將檔案中的圖片路徑傳給各Bitmap

		// 背景
		bg = BitmapFactory.decodeResource(getResources(), R.drawable.bg03);
		bg1_1_1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.bg1_1_1);
		bg1_1_0 = BitmapFactory.decodeResource(getResources(),
				R.drawable.bg1_1_0);

		// 背景循環圖，把背景的一個循環全部存成分開圖片
		for (int i = 0, j = 0; i <= 356; i = i + 3, j++) {
			Bitmap downbg = Bitmap.createBitmap(bg1_1_1, 0, 356 - i, 1074,
					320 - bgy); // 原圖,開始x,開始y,範圍x,範圍y;
			bgBit[j] = changeShape(downbg, (float) 0.6, 1, 1);
		}

		// 玩家
		player01 = BitmapFactory.decodeResource(getResources(),
				R.drawable.player02);
		player01_i = BitmapFactory.decodeResource(getResources(),
				R.drawable.player02_i);
		// 畫面效果
		lock = BitmapFactory.decodeResource(getResources(), R.drawable.lock);
		lock2 = BitmapFactory.decodeResource(getResources(), R.drawable.lock2);
		boom = BitmapFactory.decodeResource(getResources(), R.drawable.boom);
		// 武器
		weaponBit[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.w01_handgun);
		weaponBit_s[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.w01_handgun_s);
		weaponBit[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.w02_assaultgun);
		weaponBit_s[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.w02_assaultgun_s);
		weaponBit[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.w03_blade);
		weaponBit_s[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.w03_blade_s);
		weaponBit[4] = BitmapFactory.decodeResource(getResources(),
				R.drawable.w04_chargegun);
		weaponBit_s[4] = BitmapFactory.decodeResource(getResources(),
				R.drawable.w04_chargegun_s);
		// 特殊武器圖
		spBit[0] = BitmapFactory.decodeResource(getResources(),
				R.drawable.sp1_a);
		spBit[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.sp1_b);
		spBit[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.sp1_c);
		spBit[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.sp2_a);
		spBit[4] = BitmapFactory.decodeResource(getResources(),
				R.drawable.sp2_b);
		spBit[5] = BitmapFactory.decodeResource(getResources(),
				R.drawable.sp2_c);
		// 子彈特效
		bEffectBit[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.b01_gun01);
		bEffectBit[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.b02_blade3);
		bEffectBit[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.b03_charge01);
		bEffectBit[4] = BitmapFactory.decodeResource(getResources(),
				R.drawable.b04_charge02);
		bEffectBit[5] = BitmapFactory.decodeResource(getResources(),
				R.drawable.b05_boom1);
		bEffectBit[6] = BitmapFactory.decodeResource(getResources(),
				R.drawable.b06_sp1boom);
		bEffectBit[7] = BitmapFactory.decodeResource(getResources(),
				R.drawable.b07_sp1boom_1);
		bEffectBit[8] = BitmapFactory.decodeResource(getResources(),
				R.drawable.b08_sp2beam);
		bEffectBit[9] = BitmapFactory.decodeResource(getResources(),
				R.drawable.b09_boom2);

		// 敵人
		enemyBit[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e01_chiken);
		enemyBit_i[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e01_chiken_i);
		enemyBit[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e02_dragon);
		enemyBit_i[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e02_dragon_i);
		enemyBit[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e03_crowraven);
		enemyBit_i[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e03_crowraven_i);
		enemyBit[4] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e04_bee);
		enemyBit_i[4] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e04_bee_i);
		enemyBit[5] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e05_bbird);
		enemyBit_i[5] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e05_bbird_i);
		enemyBit[6] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e06_egg);
		enemyBit_i[6] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e06_egg_i);
		enemyBit[7] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e07_bat);
		enemyBit_i[7] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e07_bat_i);
		enemyBit[8] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e08_sbat);
		enemyBit_i[8] = BitmapFactory.decodeResource(getResources(),
				R.drawable.e08_sbat_i);

		// 建立特殊武器
		spWeapon = new SpWeapon(spBit);
		// 設置畫筆
		setPaint();
	}

	public void setPaint()// 設置Paint(建構子內用)
	{
		bgpaint = new Paint();// 背景畫筆
		paintScore = new Paint();// 分數文字畫筆
		paintScore.setAntiAlias(true);
		paintScore.setColor(Color.RED);
		paintScore.setTextSize(15);
		paintScore.setTypeface(Typeface.SERIF);// 標楷體?

		paintTscore = new Paint();// 總分文字畫筆
		paintTscore.setAntiAlias(true);
		paintTscore.setColor(Color.WHITE);
		paintTscore.setTextSize(36);

		paintClear = new Paint();
		paintClear.setAntiAlias(true);
		paintClear.setColor(Color.RED);
		paintClear.setTextSize(48);
		paintClear.setTypeface(Typeface.DEFAULT_BOLD);// 有點粗
		paintClear.setAlpha(0);

		paintBlack = new Paint();// 黑色畫筆
		paintBlack.setColor(Color.BLACK);

		paintBlack2 = new Paint();// 黑色畫筆
		paintBlack2.setColor(Color.BLACK);
		paintBlack2.setAlpha(100);

		paintRed = new Paint();// 紅色畫筆
		paintRed.setColor(Color.RED);

		paintDanger = new Paint();// 紅色畫筆2
		paintDanger.setColor(Color.RED);
		paintDanger.setAntiAlias(true);
		paintDanger.setTextSize(20);
		paintDanger.setTypeface(Typeface.DEFAULT_BOLD);// 有點粗

		paintLife = new Paint();// 敵方血條的畫筆
		paintLife.setColor(Color.RED); // 設置畫筆顏色
		paintLife.setStrokeWidth((float) 8.0); // 設置線寬

		paintWeapon = new Paint();// 彈藥條1的畫筆
		paintWeapon.setColor(Color.CYAN); // 設置畫筆顏色
		paintWeapon.setStrokeWidth((float) 12.0); // 設置線寬

		paintWeapon2 = new Paint();// 彈藥條2的畫筆
		paintWeapon2.setColor(Color.CYAN); // 設置畫筆顏色
		paintWeapon2.setStrokeWidth((float) 6.0); // 設置線寬
	}

	// 功能型函式
	public void popEnemy(int enemyType, float x, float y, float x2, float y2,
			int dis, float speed, float hpbuf)// 產生敵人，自動塞進enemy矩陣的空位
	{
		switch (enemyType)// 依照接收到的編號產生不同的敵人
		{
		case 1:// 雞
		{
			for (int i = 0; i < MAX_ENEMY; i++) {
				if (enemy[i].exist == false)// 判斷enemy[]是否有空間
				{
					// Random ran = new Random(); // 建立隨機數
					// 產生敵人
					enemy[i].creatEnemy(x, y, (int) (300 * hpbuf), dis, 0,
							enemyBit[1], enemyBit_i[1], boom, lock, 3, 2);
					i = MAX_ENEMY;// 跳出迴圈
				}
			}
			break;
		}

		case 2:// BOSS1 鳥
		{
			for (int i = 0; i < MAX_ENEMY; i++) {
				if (enemy[i].exist == false) {
					// Random ran = new Random(); // 建立隨機數
					// 產生敵人
					// enemy[i].creatEnemy(x,y,(int)
					// (600*hpbuf),dis,0,enemyBit[5],enemyBit_i[5],boom,lock,100,3);
					enemy[i].creatEnemy(x, y, (int) (600 * hpbuf), dis, 0,
							enemyBit[5], enemyBit_i[5], boom, lock, 100, 3);

					i = MAX_ENEMY;// 跳出迴圈
				}
			}
			break;
		}
		/*
		 * case 3://鎖定玩家的閃電 { for(int i=0;i<MAX_ENEMY;i++) {
		 * if(enemy[i].exist==false) { //產生敵人
		 * enemy[i].creatEnemy(player.x,230,500000
		 * ,130,Thunder01,Thunder01,Thunder02,lock,2); i=MAX_ENEMY;//跳出迴圈 } }
		 * break; }
		 */
		case 4:// 龍
		{
			for (int i = 0; i < MAX_ENEMY; i++) {
				if (enemy[i].exist == false) {
					// Random ran = new Random(); //建立隨機數
					// 產生敵人
					enemy[i].creatEnemy(160, 100, 400, 400, 0, enemyBit[2],
							enemyBit_i[2], boom, lock, 3, 1);
					i = MAX_ENEMY;// 跳出迴圈
				}
			}
			break;
		}
		case 5:// 烏鴉 正面飛行
		{
			for (int i = 0; i < MAX_ENEMY; i++) {
				if (enemy[i].exist == false) {
					// 產生敵人
					enemy[i].creatEnemy(x, y, 15, 600, 0, enemyBit[3],
							enemyBit_i[3], boom, lock, 1, 2);
					i = MAX_ENEMY;// 跳出迴圈
				}
			}
			break;
		}
		case 6:// 蝙蝠 側飛後正面
		{
			for (int i = 0; i < MAX_ENEMY; i++) {
				if (enemy[i].exist == false) {
					// Random ran = new Random(); // 建立隨機數
					// 產生敵人
					enemy[i].creatEnemy(x, y, 45, 500, 0, enemyBit[7],
							enemyBit_i[7], boom, lock, 4, 2);
					enemy[i].movex = x2;
					enemy[i].movey = y2;
					i = MAX_ENEMY;// 跳出迴圈
				}
			}
			break;
		}
		case 7:// 蜜蜂 正面 隨機移動
		{
			for (int i = 0; i < MAX_ENEMY; i++) {
				if (enemy[i].exist == false) {
					// Random ran = new Random(); // 建立隨機數
					// 產生敵人
					enemy[i].creatEnemy(x, y, 30, 600, 0, enemyBit[4],
							enemyBit_i[4], boom, lock, 6, 2);
					i = MAX_ENEMY;// 跳出迴圈
				}
			}
			break;
		}
		case 8:// 蛋
		{
			for (int i = 0; i < MAX_ENEMY; i++) {
				if (enemy[i].exist == false) {
					// Random ran = new Random(); // 建立隨機數
					// 產生敵人
					enemy[i].creatEnemy(x, y, 1, dis, 0, enemyBit[6],
							enemyBit_i[6], enemyBit_i[6], lock, 2, 5);
					i = MAX_ENEMY;// 跳出迴圈
				}
			}
			break;
		}
		case 9:// SP蝙蝠
		{
			for (int i = 0; i < MAX_ENEMY; i++) {
				if (enemy[i].exist == false) {
					Random ran = new Random(); // 建立隨機數
					// 產生敵人
					enemy[i].creatEnemy(ran.nextInt(370) + 80,
							ran.nextInt(130) + 50, 20, dis, 0, enemyBit[8],
							enemyBit_i[8], enemyBit_i[6], lock2, 7, 2);
					i = MAX_ENEMY;// 跳出迴圈
				}
			}
			break;
		}
		}
	}

	public void popBEffect(float x, float y, int dis, int effectType,
			float degree)// 產生子彈效果，自動塞進bEffect矩陣的空位
	{
		switch (effectType)// 依照接收到的編號產生不同的敵人
		{
		case 1:// 手槍、步槍
		{
			for (int i = 0; i < MAX_BEFFECT; i++) {
				if (bEffect[i].exist == false) {
					// 產生效果
					bEffect[i].creatEffect(x, y, dis, bEffectBit[1],
							effectType, degree);
					i = MAX_BEFFECT;// 跳出迴圈
				}
			}
			break;
		}
		case 2:// 刀
		{
			for (int i = 0; i < MAX_BEFFECT; i++) {
				if (bEffect[i].exist == false) {
					// 產生效果
					bEffect[i].creatEffect(x, y, dis, bEffectBit[2],
							effectType, degree);
					i = MAX_BEFFECT;// 跳出迴圈
				}
			}
			break;
		}
		case 3:// 蓄力
		{
			for (int i = 0; i < MAX_BEFFECT; i++) {
				if (bEffect[i].exist == false) {
					// 產生效果
					bEffect[i].creatEffect(x, y, dis, bEffectBit[3],
							effectType, degree);
					i = MAX_BEFFECT;// 跳出迴圈
				}
			}
			break;
		}
		case 4:// 蓄力2
		{
			for (int i = 0; i < MAX_BEFFECT; i++) {
				if (bEffect[i].exist == false) {
					// 產生效果
					bEffect[i].creatEffect(x, y, dis, bEffectBit[4],
							effectType, degree);
					i = MAX_BEFFECT;// 跳出迴圈
				}
			}
			break;
		}
		case 5:// 爆炸1
		{
			for (int i = 0; i < MAX_BEFFECT; i++) {
				if (bEffect[i].exist == false) {
					// 產生效果
					bEffect[i].creatEffect(x, y, dis, bEffectBit[5],
							effectType, degree);
					i = MAX_BEFFECT;// 跳出迴圈
				}
			}
			break;
		}
		case 6:// SP1爆炸
		{
			for (int i = 0; i < MAX_BEFFECT; i++) {
				if (bEffect[i].exist == false) {
					// 產生效果
					bEffect[i].creatEffect(x, y, dis, bEffectBit[6],
							effectType, degree);
					i = MAX_BEFFECT;// 跳出迴圈
				}
			}
			break;
		}
		case 7:// SP1炸彈
		{
			for (int i = 0; i < MAX_BEFFECT; i++) {
				if (bEffect[i].exist == false) {
					// 產生效果
					bEffect[i].creatEffect(x, y, dis, bEffectBit[7],
							effectType, degree);
					i = MAX_BEFFECT;// 跳出迴圈
				}
			}
			break;
		}
		case 8:// SP2光束
		{
			for (int i = 0; i < MAX_BEFFECT; i++) {
				if (bEffect[i].exist == false) {
					// 產生效果
					bEffect[i].creatEffect(x, y, dis, bEffectBit[8],
							effectType, degree);
					i = MAX_BEFFECT;// 跳出迴圈
				}
			}
			break;
		}
		case 9:// SP2擊中
		{
			for (int i = 0; i < MAX_BEFFECT; i++) {
				if (bEffect[i].exist == false) {
					// 產生效果
					bEffect[i].creatEffect(x, y, dis, bEffectBit[4],
							effectType, degree);
					i = MAX_BEFFECT;// 跳出迴圈
				}
			}
			break;
		}
		case 10:// BOOM2
		{
			for (int i = 0; i < MAX_BEFFECT; i++) {
				if (bEffect[i].exist == false) {
					// 產生效果
					bEffect[i].creatEffect(x, y, 400, bEffectBit[9],
							effectType, degree);
					i = MAX_BEFFECT;// 跳出迴圈
				}
			}
			break;
		}
		}
	}

	public void sortEnemy()// 用泡沫排序enemy，讓距離遠的排到前面，才能先畫遠的再畫近的
	{
		for (int i = 0; i < MAX_ENEMY - 1; i++) {
			for (int j = 0; j < MAX_ENEMY - i - 1; j++) {
				if (enemy[j + 1].dis > enemy[j].dis) {
					Enemy temp = enemy[j + 1]; // 交換陣列元素
					enemy[j + 1] = enemy[j];
					enemy[j] = temp;
				}
			}
		}
	}

	public int getAngle(float downX, float downY, float lastDownX,
			float lastDownY)// 計算兩點之間的角度(滑砍武器要用)
	{
		int x = (int) (downX - lastDownX);
		int y = (int) (downY - lastDownY);
		double z = Math.sqrt(x * x + y * y);
		int angle = Math.round((float) (Math.asin(y / z) / Math.PI * 180));// 最终角度
		if (x < 0) {
			return -angle;
		} else {
			return angle;
		}

	}

	public Bitmap changeShape(Bitmap picture, float upSize, float downSize,
			float heightSize)// 要縮放的圖,縮放的基本倍率,旋轉角度
	{
		int width = picture.getWidth(); // 獲得圖片的寬高
		int height = picture.getHeight();
		float[] src = new float[] { 0, 0, // 左上
				width, 0,// 右上
				width, height,// 右下
				0, height // 左下
		};
		float[] dst = new float[] { -((upSize - 1) * width) / 2, 0, // 左上
				width + ((upSize - 1) * width) / 2, 0,// 右上
				width + ((downSize - 1) * width) / 2, height * heightSize,// 右下
				-((downSize - 1) * width) / 2, height * heightSize // 左下
		};
		Matrix matrix = new Matrix();
		// matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
		matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
		Bitmap newbit = Bitmap.createBitmap(picture, 0, 0, width, height,
				matrix, true); // 得到縮放後的新圖片
		Bitmap newbit2 = Bitmap.createBitmap(newbit,
				(int) (width - (width * upSize)) / 2, 0,
				(int) (width + (width * upSize)) / 2, height, null, true); // 得到縮放後的新圖片

		return newbit2;
	}

	// 直接在drawCanvas內使用的函式，照使用順序排列
	public void coldWeapon()// 設置武器回復彈藥和冷卻時間
	{
		for (int i = 0; i < 2; i++) {
			if (weapon[i].bullet < 100) {
				weapon[i].bullet = weapon[i].bullet + weapon[i].reloadSpeed;

				if (weapon[i].bullet > 100) {
					weapon[i].bullet = 100;
				}
			}
			if (weapon[i].cd > 0) {
				weapon[i].cd = weapon[i].cd - 1;
			}
		}
		if (spWeapon.bullet < 100) {
			spWeapon.bullet = spWeapon.bullet + spWeapon.reloadSpeed;

			if (spWeapon.bullet > 100) {
				spWeapon.bullet = 100;
			}
		}
	}

	public void drawBg(Canvas canvas)// 刷新背景
	{

		// 上背景
		Bitmap upbg = Bitmap.createBitmap(bg1_1_0, 100, 0 + bgloop2, 580, bgy);
		canvas.drawBitmap(upbg, 0, 0, null);
		if (bgloop2 < 80 && totalcount % 30 == 0) {
			bgloop2++;
		}

		// 下背景
		if (bgloop >= 116) {
			bgloop = 0;
		}
		bgloop = bgloop + 1;
		canvas.drawBitmap(bgBit[bgloop], 0, bgy, null);
	}

	public void creatEnemyByStage()// 依照關卡時間產生敵人
	{
		switch (stage % 100) {
		case 0:// 測試用
		{/*
		 * 
		 * break;
		 */
		}
		case 1:// 1-1
		{
			Random ran = new Random(); // 建立隨機數

			if (totalcount == 250 || totalcount == 450 || totalcount == 650) {
				popEnemy(5, ran.nextInt(390) + 50, ran.nextInt(130) + 50, 0, 0,
						600, 1, 1);// 烏鴉 正面飛行
			}
			if (totalcount == 900 || totalcount == 1200) {
				popEnemy(6, -30, 120, 300, 120, 500, 1, 1);// 蝙蝠 側飛後正面
			}
			if (totalcount == 1000 || totalcount == 1300) {
				popEnemy(6, 600, 120, 100, 120, 500, 1, 1);// 蝙蝠 側飛後正面
			}
			if (totalcount == 1600 || totalcount == 2250) {
				popEnemy(1, 266, 120, 0, 0, 600, 1, 1);// 雞 正面到定位 發射蛋
			}
			if (totalcount == 2800 || totalcount == 2900 || totalcount == 3000) {
				popEnemy(7, 126, 120, 0, 0, 600, 1, 1);// 蜜蜂 正面 隨機位移
			}
			if (totalcount == 3100 || totalcount == 3200 || totalcount == 3300) {
				popEnemy(7, 406, 120, 0, 0, 600, 1, 1);// 蜜蜂 正面 隨機位移
			}
			if (totalcount == 3500) {
				popEnemy(6, 600, 120, 100, 120, 500, 1, 1);// 蝙蝠 側飛後正面
				popEnemy(6, -30, 120, 400, 120, 500, 1, 1);// 蝙蝠 側飛後正面
			}
			if (totalcount == 3800) {
				popEnemy(5, ran.nextInt(390) + 50, ran.nextInt(130) + 50, 0, 0,
						700, 1, 1);// 烏鴉 正面飛行
				popEnemy(5, ran.nextInt(390) + 50, ran.nextInt(130) + 50, 0, 0,
						700, 1, 1);// 烏鴉 正面飛行
			}
			if (totalcount == 3850) {
				popEnemy(5, ran.nextInt(390) + 50, ran.nextInt(130) + 50, 0, 0,
						600, 1, 1);// 烏鴉 正面飛行
				popEnemy(5, ran.nextInt(390) + 50, ran.nextInt(130) + 50, 0, 0,
						600, 1, 1);// 烏鴉 正面飛行
				popEnemy(5, ran.nextInt(390) + 50, ran.nextInt(130) + 50, 0, 0,
						600, 1, 1);// 烏鴉 正面飛行
			}
			if (totalcount == 4300) {
				popEnemy(2, 266, 120, 0, 0, 600, 1, 1);// BOSS1
			}
			// 多人關卡時
			if (stage > 100) {
				s_dead = damage;// 自身死亡數=受傷數
				s_time = (int) totalcount * 100 / 4300;// 自身進度=關卡時間百分比

				if (totalcount < 4000)// 王之前才會生特殊怪
				{
					if (totalcount % 800 == 400) {
						popEnemy(9, ran.nextInt(390) + 50,
								ran.nextInt(130) + 50, 0, 0, 600, 1, 1);// SP蝙蝠
																		// 正面飛行
					}
					if (e_tkill > 0 && e_tkill < 500) {
						e_tkill--;
						popEnemy(9, ran.nextInt(390) + 50,
								ran.nextInt(130) + 50, 0, 0, 600, 1, 1);// SP蝙蝠
																		// 正面飛行
					}
				}

				// 自身死亡數=受傷數

				if (clearcount != 0)// 通關時
				{
					s_time = 100;
					s_kill = kill;
				} else if (s_time >= 90)// 沒過關時最大90%
				{
					s_time = 90;
				}
				
				//對手通關時
				if(e_time==100&&clearcount2==0)
				{
					clearcount2=totalcount;
				}
			}
			break;
		}
		case 2:// 1-2
		{
			Random ran = new Random(); // 建立隨機數
			if (totalcount == 250 || totalcount == 260 || totalcount == 270) {
				popEnemy(7, 126, 120, 0, 0, 600, 1, 1);// 蜜蜂 正面 隨機位移
			}
			if (totalcount == 520 || totalcount == 540 || totalcount == 560|| totalcount == 580) {
				popEnemy(5, ran.nextInt(390) + 50, ran.nextInt(130) + 50, 0, 0,
						600, 1, 1);// 烏鴉 正面飛行
			}
			if (totalcount == 700 || totalcount == 800) {
				popEnemy(6, -30, 120, 300, 140, 500, 1, 1);// 蝙蝠 側飛後正面
			}
			if (totalcount == 700 || totalcount == 800) {
				popEnemy(6, 600, 120, 100, 140, 500, 1, 1);// 蝙蝠 側飛後正面
			}
			if (totalcount == 1000 || totalcount == 1010 || totalcount == 1020) {
				popEnemy(7, 406, 120, 0, 0, 600, 1, 1);// 蜜蜂 正面 隨機位移
			}
			if (totalcount == 400 || totalcount == 750) {
				popEnemy(1, 266, 120, 0, 0, 600, 1, 1);// 雞 正面到定位 發射蛋
			}
			if (totalcount == 1200 || totalcount == 1220 || totalcount == 1240|| totalcount == 1260) {
				popEnemy(5, ran.nextInt(390) + 50, ran.nextInt(130) + 50, 0, 0,
						600, 1, 1);// 烏鴉 正面飛行
			}
			if (totalcount == 1400 || totalcount == 1450 || totalcount == 1500) {
				popEnemy(7, 126, 120, 0, 0, 600, 1, 1);// 蜜蜂 正面 隨機位移
			}
			
			if (totalcount == 1800 || totalcount == 2000) {
				popEnemy(6, -30, 120, 300, 120, 500, 1, 1);// 蝙蝠 側飛後正面
			}
			if (totalcount == 1900 || totalcount == 2100) {
				popEnemy(6, 600, 120, 100, 120, 500, 1, 1);// 蝙蝠 側飛後正面
			}
			if (totalcount == 2400) {
				popEnemy(1, 266, 110, 0, 0, 600, 1, 1);// 雞 正面到定位 發射蛋
				popEnemy(1, 206, 120, 0, 0, 600, 1, 1);// 雞 正面到定位 發射蛋
				popEnemy(1, 146, 130, 0, 0, 600, 1, 1);// 雞 正面到定位 發射蛋
			}
			if (totalcount == 2800 || totalcount == 2900 || totalcount == 3000) {
				popEnemy(7, 126, 120, 0, 0, 600, 1, 1);// 蜜蜂 正面 隨機位移
				popEnemy(7, 126, 120, 0, 0, 600, 1, 1);// 蜜蜂 正面 隨機位移
			}
			if (totalcount == 3100 || totalcount == 3200 || totalcount == 3300) {
				popEnemy(7, 406, 120, 0, 0, 600, 1, 1);// 蜜蜂 正面 隨機位移
				popEnemy(7, 406, 120, 0, 0, 600, 1, 1);// 蜜蜂 正面 隨機位移
			}
			if (totalcount == 3500) {
				popEnemy(6, 600, 120, 100, 120, 500, 1, 1);// 蝙蝠 側飛後正面
				popEnemy(6, -30, 120, 400, 120, 500, 1, 1);// 蝙蝠 側飛後正面
			}
			if (totalcount == 3800) {
				popEnemy(6, 600, 110, 100, 130, 500, 1, 1);// 蝙蝠 側飛後正面
				popEnemy(6, -30, 130, 400, 110, 500, 1, 1);// 蝙蝠 側飛後正面
			}
			if (totalcount == 3800) {
				popEnemy(7, 126, 120, 0, 0, 600, 1, 1);// 蜜蜂 正面 隨機位移
				popEnemy(7, 406, 120, 0, 0, 600, 1, 1);// 蜜蜂 正面 隨機位移
			}
			if (totalcount == 3850) {
				popEnemy(5, ran.nextInt(390) + 50, ran.nextInt(130) + 50, 0, 0,
						600, 1, 1);// 烏鴉 正面飛行
				popEnemy(5, ran.nextInt(390) + 50, ran.nextInt(130) + 50, 0, 0,
						600, 1, 1);// 烏鴉 正面飛行
				popEnemy(5, ran.nextInt(390) + 50, ran.nextInt(130) + 50, 0, 0,
						600, 1, 1);// 烏鴉 正面飛行
			}
			if (totalcount == 4300) {
				popEnemy(2, 266, 120, 0, 0, 600, 3/2, 2);// BOSS1
			}
			// 多人關卡時
			if (stage > 100) {
				s_dead = damage;// 自身死亡數=受傷數
				s_time = (int) totalcount * 100 / 4300;// 自身進度=關卡時間百分比

				if (totalcount < 4000)// 王之前才會生特殊怪
				{
					if (totalcount % 800 == 400) {
						popEnemy(9, ran.nextInt(390) + 50,
								ran.nextInt(130) + 50, 0, 0, 600, 1, 1);// SP蝙蝠
																		// 正面飛行
					}
					if (e_tkill > 0 && e_tkill < 500) {
						e_tkill--;
						popEnemy(9, ran.nextInt(390) + 50,
								ran.nextInt(130) + 50, 0, 0, 600, 1, 1);// SP蝙蝠
																		// 正面飛行
					}
				}

				// 自身死亡數=受傷數

				if (clearcount != 0)// 通關時
				{
					s_time = 100;
					s_kill = kill;
				} else if (s_time >= 90)// 沒過關時最大90%
				{
					s_time = 90;
				}
				
				//對手通關時
				if(e_time==100&&clearcount2==0)
				{
					clearcount2=totalcount;
				}
			}
			break;
		}
		}

		sortEnemy();// 用泡沫排序enemy，讓距離遠的排到前面，才能先畫遠的再畫近的
	}

	public void touchAreaLogic()// 觸碰畫面的邏輯
	{

		if (firstTouching && isTouching && clearcount < totalcount - 200
				&& clearcount != 0) {
			activity.setUiNow(0);
			activity.sendMessage(0);
			myThread.setRun(false);
		}
		if (firstTouching && isTouching
				&& spWeapon.isInrange(downX, downY) == 1
				&& spWeapon.bullet >= 100) {
			spWeapon.attack(1);
			spWeapon.bullet = 0;
		} else if (firstTouching && isTouching
				&& spWeapon.isInrange(downX, downY) == 2
				&& spWeapon.bullet >= 100) {
			spWeapon.attack(2);
			spWeapon.bullet = 0;
		}
		// 是否觸碰未選擇的武器
		else if (firstTouching && isTouching && !isMoving
				&& weapon[1 - selectWeapon].isInrange(downX, downY)) {
			firstTouching = false; // 第一次觸碰判設為false，讓接下來判斷為按著不放
			isSelecting = true; // 判斷為選武器中
			weapon[1 - selectWeapon].drag = true;// 讓未選擇武器(就是現在按住的)，設為被拖動中
			weapon[selectWeapon].otherDrag = true;// 讓選擇中武器，得知另一個武器被拖動中
		}
		// 是否觸碰選擇中的武器
		else if (firstTouching && isTouching && !isMoving
				&& weapon[selectWeapon].isInrange(downX, downY)) {
			firstTouching = false;
			isSelecting = true;
			weapon[selectWeapon].drag = true;
			weapon[1 - selectWeapon].otherDrag = true;
		}
		// 彈藥不足的情況
		else if (weapon[selectWeapon].bullet < weapon[selectWeapon].bulletCost) {
			isAttacking = false;// 重置攻擊狀態
		}
		// 觸碰其他區域(攻擊判定)
		else if (downY < 220 && !isMoving && !isSelecting) {
			isAttacking = false;// 重置攻擊狀態
			switch (weapon[selectWeapon].weaponType) // 依照武器種類判定是否攻擊
			{
			case 1:// 點放型
			{
				if (isTouching && firstTouching) {
					isAttacking = true;
					firstTouching = false;
					weapon[selectWeapon].bullet = weapon[selectWeapon].bullet
							- weapon[selectWeapon].bulletCost;
					// 產生子彈擊中效果
					popBEffect(downX, downY, 200, 1, 0);
				}
				break;
			}
			case 2:// 長按連射型
			{
				if (isTouching && weapon[selectWeapon].cd == 0) {
					isAttacking = true;
					weapon[selectWeapon].bullet = weapon[selectWeapon].bullet
							- weapon[selectWeapon].bulletCost;
					weapon[selectWeapon].cd = weapon[selectWeapon].cd
							+ weapon[selectWeapon].shootCd;
					firstTouching = false;
					// 產生子彈擊中效果
					popBEffect(downX, downY, 400, 1, 0);
				}
				break;
			}
			case 3:// 滑砍型
			{
				if (isTouching && weapon[selectWeapon].cd == 0) {
					isAttacking = true;
					firstTouching = false;
				}
				break;
			}
			case 4:// 蓄力型
			{
				if (isTouching && firstTouching) {
					isAttacking = true;
					firstTouching = false;
					// 產生子彈擊中效果
					popBEffect(downX, downY, 400, 3, 0);
					// 蓄力型第一次碰觸畫面減少3倍彈藥
					weapon[selectWeapon].bullet = weapon[selectWeapon].bullet
							- weapon[selectWeapon].bulletCost * 3;

					// 蓄力中的彈藥減少寫在繪製效果那邊
				}
				break;
			}
			}
		} else {
			isAttacking = false;// 重置攻擊狀態
		}
	}

	public void spLogic(Canvas canvas) {// 特殊武器的邏輯判斷
		// SP1
		if (spWeapon.exist1 == 20) {// 依照sp存在時間產生不同的動畫
			popBEffect(player.x, player.y, 200, 7, 2);
		} else if (spWeapon.exist1 == 16) {
			popBEffect(player.x, player.y, 200, 7, 1);
		} else if (spWeapon.exist1 == 14) {
			popBEffect(player.x, player.y, 200, 7, 3);
		} else if (spWeapon.exist1 == 2) {
			popBEffect(270, 120, 400, 6, 0);
		}
		if (spWeapon.exist1 != 0) {// 隨迴圈次數減少sp存在時間
			spWeapon.exist1--;
		}

		// SP2
		if (spWeapon.exist2 == 200) {
			popBEffect(player.x, player.y, 200, 8, 0);
		}
		if (spWeapon.exist2 != 0) {
			spWeapon.exist2--;
		}

		// ENEMY SP

		for (int i = 0; i < MAX_ENEMY; i++) {// 如果存在的敵人SP攻擊，隨SP時間顯示警告標示

			if (enemySP[i].exist != 0) {

				if (255 - enemySP[i].exist > 0) {
					paintDanger.setAlpha(255 - enemySP[i].exist);
				} else {
					paintDanger.setAlpha(0);
				}
				canvas.drawRect(enemySP[i].x - enemySP[i].width, 320,
						enemySP[i].x + enemySP[i].width, 310, paintDanger);
				canvas.drawRect(enemySP[i].x - enemySP[i].width, 0,
						enemySP[i].x + enemySP[i].width, 10, paintDanger);
				canvas.drawText("DANGER", enemySP[i].x - 40, 35, paintDanger);
			}
			switch (enemySP[i].type) {// 在SP時間倒數結束時，依照攻擊類型產生動畫特效
			case 1: {
				if (enemySP[i].exist == 100) {// 讓BOSS位置改變成玩家位置
					for (int j = 0; j < MAX_ENEMY; j++) {
						if (enemy[j].deadType == 3)// 用死亡模式來確定是BOSS
						{
							enemy[j].x = enemySP[i].x;
						}
					}
				}
				if (enemySP[i].exist == 1) {// 產生動畫，如果玩家在範圍內對玩家造成傷害
					popBEffect(enemySP[i].x + 20, 150, 200, 2, 90);
					popBEffect(enemySP[i].x - 20, 150, 200, 2, 90);
					if (enemySP[i].isInrange(player.x)) {
						damage++;// 玩家受傷
						activity.sendMessage(-1);// 震動
						player.injured = 5;
					}
				}
			}
			}

			if (enemySP[i].exist != 0) {
				enemySP[i].exist--;
			}
		}
	}

	public void drawEnemy(Canvas canvas)// 繪出敵人
	{
		for (int i = 0; i < MAX_ENEMY; i++) {
			if (enemy[i].exist == true)// 當敵人存在時，做傷害判定、重繪自身
			{
				if (enemy[i].dis <= weapon[selectWeapon].dis && enemy[i].hp > 0)// 武器射程
				{
					enemy[i].lockedTime++;// 累計鎖定時間
				} else {
					enemy[i].lockedTime = 0;// 重製鎖定時間
				}
				switch (enemy[i].sp) // 產生敵人的特殊攻擊
				{
				case 1:// 生蛋
				{
					enemy[i].sp = 0;
					popEnemy(8, enemy[i].x, enemy[i].y + 20, 0, 0,
							enemy[i].dis, 1, 1);
					break;
				}
				case 2:// BOSS1的攻擊
				{
					enemy[i].sp = 0;
					for (int j = 0; j < MAX_ENEMY; j++) {
						if (enemySP[j].exist == 0) {
							enemySP[j].creatEnemySP(player.x, 40, 245, 1);
							j = MAX_ENEMY;// 跳出迴圈
						}
					}
					break;
				}
				}
				switch (weapon[selectWeapon].weaponType) // 依照武器種類判定受傷害的模式
				{
				case 1:// 點放型
				{
					// 如果玩家正在攻擊、敵人被鎖定、碰觸螢幕在敵人的範圍內
					if (isAttacking && enemy[i].lockedTime > 0
							&& enemy[i].isInrange(downX, downY, 0)) {
						enemy[i].injured = weapon[selectWeapon].atk; // 敵人受傷害

						// 產生子彈擊中效果
						popBEffect(downX, downY, enemy[i].dis, 1, 0);
					}
					break;
				}
				case 2:// 長按連射型
				{
					// 如果玩家正在攻擊、敵人被鎖定、碰觸螢幕在敵人的範圍內
					if (isAttacking && enemy[i].lockedTime > 0
							&& enemy[i].isInrange(downX, downY, 0)) {
						enemy[i].injured = weapon[selectWeapon].atk; // 敵人受傷害
					}
					break;
				}
				case 3:// 滑砍
				{
					// 如果玩家不是第一次碰觸、敵人被鎖定、碰觸螢幕在敵人的範圍內、敵人沒被刀切入
					if (isAttacking && enemy[i].lockedTime > 0
							&& enemy[i].isInrange(downX, downY, 0)
							&& enemy[i].bladeIn == 0) {

						enemy[i].injured = weapon[selectWeapon].atk; // 敵人受傷害

						// 產生斬擊效果
						if (lastDownX == -20 && lastDownY == -20) {
							popBEffect(downX, downY, enemy[i].dis * 2, 2, 45);
							popBEffect(downX, downY, enemy[i].dis * 2, 2, -45);
						} else {
							popBEffect(
									downX,
									downY,
									enemy[i].dis,
									2,
									getAngle(downX, downY, lastDownX, lastDownY));// getAngle計算兩點之間的角度
						}

						enemy[i].bladeIn = 1;
						weapon[selectWeapon].bullet = weapon[selectWeapon].bullet
								- weapon[selectWeapon].bulletCost;
						weapon[selectWeapon].cd = weapon[selectWeapon].cd
								+ weapon[selectWeapon].shootCd;

					} else if (enemy[i].bladeIn == 1
							&& !enemy[i].isInrange(downX, downY, 0)
							|| !isAttacking) {
						enemy[i].bladeIn = 0;
					}
					break;
				}
				case 4:// 蓄力型
				{
					// 如果敵人被鎖定、放開瞬間在敵人的範圍內
					if (enemy[i].lockedTime > 0
							&& enemy[i].isInrange(chargeX, chargeY,
									(chargeTime - 1) * 10)) {
						enemy[i].injured = weapon[selectWeapon].atk * chargeAtk; // 敵人受傷害
					}
					break;
				}
				}
				// 計算SP1傷害
				if (spWeapon.exist1 == 1) {
					enemy[i].injured = 100;
				}
				// 計算SP2傷害
				if (spWeapon.exist2 > 10 && enemy[i].hp > 0
						&& enemy[i].x > player.x - 80
						&& enemy[i].x < player.x + 80) {
					if (totalcount % 5 == 0) {
						enemy[i].injured = 10;
						popBEffect(enemy[i].x, enemy[i].y, enemy[i].dis - 100,
								9, 0);
					}
				}
				enemy[i].drawSelf(canvas); // 重繪自身

				if (enemy[i].attack == 1)// 判斷敵人有沒有攻擊到玩家
				{
					damage++;// 玩家受傷
					activity.sendMessage(-1);// 震動
					player.injured = 5;
					enemy[i].attack = 0;
				} else if (enemy[i].attack == 2) {
					if (player.x > enemy[i].x - 60
							&& player.x < enemy[i].x + 60) {
						damage++;// 玩家受傷
						activity.sendMessage(-1);// 震動
						player.injured = 5;
						enemy[i].attack = 0;
					}
				} else if (enemy[i].deadTime == 1)// 判斷敵人有沒有被玩家擊殺
				{
					// 產生子彈擊中效果
					if (enemy[i].deadType == 3) {
						popBEffect(enemy[i].x, enemy[i].y, enemy[i].dis / 2,
								10, 0);
						clearcount = totalcount;
					} else if (spWeapon.exist2 > 10
							&& enemy[i].x > player.x - 80
							&& enemy[i].x < player.x + 80)// SP2中不顯示死亡動畫
					{
					} else if (enemy[i].deadType == 2) {
						popBEffect(enemy[i].x, enemy[i].y, enemy[i].dis, 5, 0);
					} else if (enemy[i].deadType == 4) {

					}
					if (enemy[i].n_sp == 1)// 網路用，如果殺掉特殊敵人，s_kill累加
					{
						s_kill++;
					}
					kill = kill + enemy[i].maxhp;
				}
			}
		}// for迴圈結束
	}

	public void drawBEffect(Canvas canvas)// 繪出子彈效果
	{
		// 重製蓄力子彈的位置，不然下次碰觸螢幕瞬間會有判定
		chargeX = -100;
		chargeY = -100;

		for (int i = 0; i < MAX_BEFFECT; i++) {
			if (bEffect[i].exist)// 如果效果存在
			{
				if (bEffect[i].effectType == 3)// 如果效果是3蓄力型
				{
					if (!isTouching)// 放開時依照蓄力時間產生效果
					{
						bEffect[i].exist = false;
						chargeX = downX;
						chargeY = downY;
						if (bEffect[i].existTime < 30) {
							chargeTime = 1;
							chargeAtk = 1;// 各階段的攻擊倍率
						} else if (bEffect[i].existTime < 60) {
							chargeTime = 2;
							chargeAtk = 3;
						} else {
							chargeTime = 4;
							chargeAtk = 9;
						}
						popBEffect(chargeX, chargeY, 400, 4, chargeTime);
					} else if (weapon[selectWeapon].bullet < weapon[selectWeapon].bulletCost)// 如果彈藥用盡，自動放開
					{
						isTouching = false;
					} else if (isTouching && weapon[selectWeapon].cd == 0)// 除此之外減少彈藥
					{
						weapon[selectWeapon].bullet = weapon[selectWeapon].bullet
								- weapon[selectWeapon].bulletCost;
					}
				}
				if (bEffect[i].effectType == 8)// 如果效果是8 SP2
				{
					bEffect[i].drawSelf(canvas, player.x, player.y);// 畫出來
				} else {
					bEffect[i].drawSelf(canvas, downX, downY);// 畫出來
				}

			}
		}
	}

	public void drawPlayer(Canvas canvas)// 玩家的移動處理、繪出
	{
		if (firstTouching && isTouching && !isAttacking && downY > 220
				&& downX < 480 && isSelecting == false)// 移動玩家
		{
			isMoving = true;
			firstTouching = false;
			player.move(downX, downY);
		} else if (isMoving)// 移動玩家
		{
			player.move(downX, downY);
		}
		player.drawSelf(canvas); // 重繪玩家
	}

	public void drawWeapon(Canvas canvas)// 繪出武器
	{
		weapon[0].move(downX, downY);
		weapon[1].move(downX, downY);
		if (weapon[0].drag) {
			weapon[1].drawSelf(canvas);
			weapon[0].drawSelf(canvas);
		} else {
			weapon[0].drawSelf(canvas);
			weapon[1].drawSelf(canvas);
		}
		spWeapon.drawSelf(canvas);
	}

	public void drawStatus(Canvas canvas)// 繪出血量、彈藥、狀態等畫面
	{
		// 顯示介面文字
		paintBlack.setAlpha(100);
		canvas.drawRect(440, 0, screenW, 60, paintBlack);
		paintScore.setColor(Color.WHITE);
		canvas.drawText("SCORE:" + kill, 445, 20, paintScore);
		canvas.drawText("TIME:" + totalcount / 100 + "." + totalcount % 100,
				445, 32, paintScore);
		canvas.drawText("DAMAGE:" + damage, 445, 44, paintScore);

		// 顯示多人介面
		if (stage > 100) {
			paintBlack.setAlpha(100);
			canvas.drawRect(440, 260, screenW, 320, paintBlack);
			paintScore.setColor(Color.WHITE);
			canvas.drawText("PLAYER2:", 445, 20 + 260, paintScore);
			canvas.drawText("TIME:" + e_time + "%", 445, 32 + 260, paintScore);
			canvas.drawText("DAMAGE:" + e_dead, 445, 44 + 260, paintScore);
		}
		// 顯示敵方血條
		for (int i = 0; i < MAX_ENEMY; i++) {
			if (enemy[i].hp > 1 && enemy[i].deadType == 3) {
				canvas.drawLine(20, 15,
						20 + 180 * enemy[i].hp / enemy[i].maxhp, 15, paintLife);// 畫直線
			}
		}
		// 顯示武器彈藥條
		canvas.drawLine(505, 158, 505,
				(float) (158 - weapon[selectWeapon].bullet * 0.8), paintWeapon);// 畫直線
		canvas.drawLine(475, 241, 475,
				(float) (241 - weapon[1 - selectWeapon].bullet * 0.4),
				paintWeapon2);// 畫直線

		// START
		paintBlack.setAlpha(120);
		if (totalcount > 10 && totalcount < 30) {
			canvas.drawRect(0, 160 - (totalcount - 10) * 2, screenW,
					160 + (totalcount - 10) * 2, paintBlack);
		} else if (totalcount >= 30 && totalcount < 100) {
			paintTscore.setColor(Color.WHITE);
			paintTscore.setTextSize(25);
			// paintTscore.setTypeface(Typeface.DEFAULT_BOLD);//有點粗
			paintTscore.setTypeface(Typeface.SANS_SERIF);
			paintTscore.setTypeface(Typeface.SERIF);// 標楷體?

			if (totalcount < 39) {
				paintTscore.setAlpha((totalcount - 30) * 30);
			} else {
				paintTscore.setAlpha(255);
			}
			canvas.drawRect(0, 120, screenW, 200, paintBlack);
			canvas.drawText("STAGE", 223, 150, paintTscore);
			canvas.drawText(stage % 100 / 4 + 1 + "-" + stage % 4, 243, 180,
					paintTscore);
		} else if (totalcount >= 100 && totalcount < 150) {
			if (totalcount < 107) {
				paintTscore.setAlpha((totalcount - 100) * 40);
			} else {
				paintTscore.setAlpha(255);
			}
			paintTscore.setTextSize(40);
			canvas.drawRect(0, 120, screenW, 200, paintBlack);
			canvas.drawText("START!", 207, 180, paintTscore);
		} else if (totalcount >= 150 && totalcount < 160) {
			paintTscore.setAlpha(255 - (totalcount - 150) * 25);
			canvas.drawRect(0, 120 + (totalcount - 150) * 4, screenW,
					200 - (totalcount - 150) * 4, paintBlack);
			canvas.drawText("START!", 207, 178, paintTscore);
		}
		// WARNING
		paintRed.setAlpha(180);
		if (totalcount - 4000 > 10 && totalcount - 4000 < 30) {
			canvas.drawRect(0, 160 - (totalcount - 4000 - 10) * 2, screenW,
					160 + (totalcount - 4000 - 10) * 2, paintRed);
		} else if (totalcount - 4000 >= 30 && totalcount - 4000 < 150) {
			paintTscore.setColor(Color.WHITE);
			paintTscore.setTextSize(25);
			// paintTscore.setTypeface(Typeface.DEFAULT_BOLD);//有點粗
			paintTscore.setTypeface(Typeface.SANS_SERIF);
			paintTscore.setTypeface(Typeface.SERIF);// 標楷體?

			if (totalcount - 4000 < 39) {
				paintTscore.setAlpha((totalcount - 4000 - 30) * 30);
			} else {
				paintTscore.setAlpha(255);
			}
			paintTscore.setTextSize(40);
			canvas.drawRect(0, 120, screenW, 200, paintRed);
			canvas.drawText("WARNING", 177, 180, paintTscore);
		} else if (totalcount - 4000 >= 150 && totalcount - 4000 < 160) {
			paintTscore.setAlpha(255 - (totalcount - 4000 - 150) * 25);
			canvas.drawRect(0, 120 + (totalcount - 4000 - 150) * 4, screenW,
					200 - (totalcount - 4000 - 150) * 4, paintRed);
			canvas.drawText("WARNING", 177, 178, paintTscore);
		}

		// 結算
		if (clearcount != 0) {
			if (paintClear.getAlpha() < 250) {
				paintClear.setAlpha((totalcount - clearcount) * 25);
			}
			canvas.drawText("STAGE CLEAR", 110, 70, paintClear);

			if (20 < totalcount - clearcount && totalcount - clearcount < 31) {
				canvas.drawRect(60,
						190 + (totalcount - clearcount - 20) * 19 / 2, 472,
						190 - (totalcount - clearcount - 20) * 19 / 2,
						paintBlack2);

			} else if (totalcount - clearcount >= 31) {
				paintTscore.setColor(Color.WHITE);
				paintTscore.setTextSize(30);
				paintTscore.setAlpha((totalcount - clearcount - 31) * 10);
				if (totalcount - clearcount - 31 > 25) {
					paintTscore.setAlpha(255);
				}
				canvas.drawRect(60, 310, 472, 80, paintBlack2);
				paintTscore.setTypeface(Typeface.DEFAULT_BOLD);

				canvas.drawText("SCORE", 120, 120, paintTscore);
				canvas.drawText("" + kill, 360, 120, paintTscore);
				canvas.drawText("TIME", 120, 150, paintTscore);
				canvas.drawText("" + clearcount / 100 + "." + clearcount % 100,
						360, 150, paintTscore);
				canvas.drawText("MISS", 120, 180, paintTscore);
				canvas.drawText("" + damage, 360, 180, paintTscore);
				paintTscore.setTextSize(45);
				canvas.drawText("RANK", 120, 220, paintTscore);
				if (stage % 100 == 1||stage % 100 == 2||stage % 100 == 3) {
					int rank = 5;
					if (kill < 500) {
						rank--;
					}
					if (clearcount > 6000) {
						rank--;
					}
					if (damage > 0) {
						rank = rank - damage;
					}
					if (rank == 5) {
						paintTscore.setColor(Color.YELLOW);
						canvas.drawText("S", 360, 220, paintTscore);
					} else if (rank == 4) {
						paintTscore.setColor(Color.RED);
						canvas.drawText("A", 360, 220, paintTscore);
					} else if (rank == 3) {
						paintTscore.setColor(Color.CYAN);
						canvas.drawText("B", 360, 220, paintTscore);
					} else if (rank == 2) {
						paintTscore.setColor(Color.GREEN);
						canvas.drawText("C", 360, 220, paintTscore);
					} else {
						paintTscore.setColor(Color.MAGENTA);
						canvas.drawText("D", 360, 220, paintTscore);
					}
				}
				if(stage>100)
				{
					paintTscore.setColor(Color.WHITE);
					canvas.drawText("P2", 120, 260, paintTscore);	
					int rank = 5;
					
					if (clearcount2 > 6000) {
						rank--;
					}
					if (e_dead > 0) {
						rank = rank - e_dead;
					}
					if(clearcount2==0){
						paintTscore.setTextSize(20);
						canvas.drawText("PLAYING", 360, 260, paintTscore);
					} else if (rank == 5) {
						paintTscore.setColor(Color.YELLOW);
						canvas.drawText("S", 360, 260, paintTscore);
					} else if (rank == 4) {
						paintTscore.setColor(Color.RED);
						canvas.drawText("A", 360, 260, paintTscore);
					} else if (rank == 3) {
						paintTscore.setColor(Color.CYAN);
						canvas.drawText("B", 360, 260, paintTscore);
					} else if (rank == 2) {
						paintTscore.setColor(Color.GREEN);
						canvas.drawText("C", 360, 260, paintTscore);
					} else {
						paintTscore.setColor(Color.MAGENTA);
						canvas.drawText("D", 360, 260, paintTscore);
					}
					if(clearcount2!=0){
						if(e_dead>damage||(e_dead==damage)&&(clearcount2>clearcount))
						{
							paintTscore.setColor(Color.YELLOW);
							canvas.drawText("WIN", 120, 300, paintTscore);	
						}
						else
						{
							paintTscore.setColor(Color.CYAN);
							canvas.drawText("LOSE", 120, 300, paintTscore);
						}
					}
				}
			}
		}
	}

	// 繪製畫面的主函式
	public void drawCanvas(Canvas canvas)// 繪製畫面
	{
		Log.i("net", "s_kill: " + s_kill + " s_time " + s_time + " s_dead "
				+ s_dead);
		Log.i("net", "e_kill: " + e_kill + " e_time " + e_time + " e_dead "
				+ e_dead);
		if(e_time==-1&&stage>100)
		{			
			// 上背景
			Bitmap upbg = Bitmap.createBitmap(bg1_1_0, 100, 0 + bgloop2, 580, bgy);
			canvas.drawBitmap(upbg, 0, 0, null);
			// 下背景
			if (bgloop >= 116) {
				bgloop = 0;
			}
			bgloop = bgloop + 1;
			canvas.drawBitmap(bgBit[bgloop], 0, bgy, null);
			paintBlack.setAlpha(180);
			paintTscore.setTextSize(28);
			canvas.drawRect(150, 120, 402, 200, paintBlack);
			canvas.drawText("尋找對手中...", 193, 175, paintTscore);
		}
		else
		{
			totalcount++;// 時間經過，1 totalcount=0.01秒
			coldWeapon();// 武器冷卻
			drawBg(canvas);// 刷新背景
			creatEnemyByStage();// 依照關卡時間產生敵人
			touchAreaLogic();// 觸碰畫面相關的邏輯
			spLogic(canvas);// 特殊武器邏輯
			drawEnemy(canvas);// 處理敵人的狀態，畫出來(敵人撞到玩家的判斷也在裡面)
			drawBEffect(canvas);// 繪出子彈效果
			drawPlayer(canvas);// 移動並繪製玩家
			drawWeapon(canvas);// 移動並繪製武器圖片
			drawStatus(canvas);// 繪出血量、彈藥、狀態等畫面
		}

		// holder.unlockCanvasAndPost(canvas); // *Surface必須，畫面控制用(解鎖畫布並顯示到螢幕上)

	}

	// 主要執行緒，每過10ms執行一次drawCanvas，畫面有延遲則調整等待時間
	private class MyThread extends Thread {
		long startTime, endTime, diffTime;// 開始時間、結束時間、相差時間
		private boolean run;
		int weapon1, weapon2;
		WeaponData weaponDate1, weaponDate2;

		PlayerData pData = new PlayerData();// 接收資料庫用

		public MyThread() {
			run = true;
			// 匯入裝備的武器

			// 資料庫 將現在裝備中的武器編號傳入weapon1 weapon2
			pData = activity.dbhelper.getPlayerData();
			weapon1 = pData.weapon1;
			weapon2 = pData.weapon2;
			weaponDate1 = activity.dbhelper.getWeaponData(weapon1);
			weaponDate2 = activity.dbhelper.getWeaponData(weapon2);

			for (int i = 0; i < MAX_ENEMY; i++)// 建立Enemy陣列裡的每個Enemy
			{
				enemy[i] = new Enemy();
				enemySP[i] = new EnemySP();
			}
			for (int i = 0; i < 2; i++)// 建立Weapon陣列裡的每個Weapon
			{
				weapon[i] = new Weapon();
			}
			for (int i = 0; i < MAX_BEFFECT; i++)// 建立BulletEffect陣列裡的每個BulletEffect
			{
				bEffect[i] = new BulletEffect();
			}

			weapon[0].creatWeapon(weaponDate1.mode, true, weaponBit[weaponDate1.mode],
					weaponBit_s[weaponDate1.mode], (int) weaponDate1.atk,
					weaponDate1.dis, weaponDate1.cd, weaponDate1.bulletCost,
					weaponDate1.reloadSpeed,weaponDate1.lvBase+weaponDate1.lvMoney);// 建立武器1
			weapon[1].creatWeapon(weaponDate2.mode, false, weaponBit[weaponDate2.mode],
					weaponBit_s[weaponDate2.mode], (int) weaponDate2.atk,
					weaponDate2.dis, weaponDate2.cd, weaponDate2.bulletCost,
					weaponDate2.reloadSpeed,weaponDate2.lvBase+weaponDate2.lvMoney);// 建立武器1
			weapon[0].num = 1;// 裝備上下位置值
			weapon[1].num = 2;
			player.creatPlayer(screenW / 2 - 5, 260, 5, 150, player01,
					player01_i, boom);// 建立玩家到螢幕中下方
		}

		public void setRun(boolean run) {
			this.run = run;
		}

		@Override
		public void run() {

			while (run) // 無限迴圈
			{
				startTime = System.currentTimeMillis();// 儲存開始時間
				SurfaceHolder myholder = GameView.this.getHolder();
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
				endTime = System.currentTimeMillis();// 儲存結束時間
				diffTime = (int) (endTime - startTime);// 算出相差時間
				while (diffTime <= 10) // 如果相差時間比原本要停的時間少，則暫停執行緒
				{
					diffTime = (int) (System.currentTimeMillis() - startTime);
					Thread.yield();// 暫停thread
				}
			}
		}
	}

	// 控制觸碰的函式
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP) {
			// 離開螢幕
			lastDownX = -20;// 重置上一個X
			lastDownY = -20;// 重置上一個Y
			upX = event.getX(); // 離開時的 X 軸位置
			upY = event.getY();// 離開時的 Y 軸位置

			Log.i("info", "upX:" + upX + " upY:" + upY);

			isTouching = false;
			firstTouching = true;
			isSelecting = false;
			isMoving = false;
			weapon[0].drag = false;
			weapon[1].drag = false;
			weapon[0].otherDrag = false;
			weapon[1].otherDrag = false;
			if (weapon[0].num == 1) {
				weapon[0].select = true;
				weapon[1].select = false;
				selectWeapon = 0;
			} else {
				weapon[1].select = true;
				weapon[0].select = false;
				selectWeapon = 1;
			}
		} else {
			LinkedList<HashMap<String, Float>> line;
			if (!isTouching) {
				// 觸摸開始
				lastDownX = -20;// 重置上一個X
				lastDownY = -20;// 重置上一個Y
				downX = event.getX();
				downY = event.getY();
				isTouching = true;
				Log.i("info", "downX:" + downX + " downY:" + downY);

			} else {
				// 開始滑動
				lastDownX = downX;// 存取上一個X
				lastDownY = downY;// 存取上一個Y
				downX = event.getX();
				downY = event.getY();
				Log.i("info", "dragX:" + downX + " dragY:" + downY);
			}
			HashMap<String, Float> point = new HashMap<String, Float>();
			point.put("x", event.getX());
			point.put("y", event.getY());
			postInvalidate();
		}
		return true;
	}

	// surface建立時跑的函式
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		screenW = this.getWidth(); // 只能在視圖創建函數中才能獲得視圖的高寬
		screenH = this.getHeight();

		Log.i("info", "screenW:" + screenW);
		Log.i("info", "screenH:" + screenH);

		thread t = new thread();
		t.start();
		thread2 t2 = new thread2();
		t2.start();

		myThread = new MyThread();// 創立MyThread執行緒，並執行
		myThread.start();

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		myThread.setRun(false);
		myThread.interrupt();
	}

}
// Log.i("info","downX:");
// ctrl+shift+f自動縮排
// 遊戲解析度為532(527)*320