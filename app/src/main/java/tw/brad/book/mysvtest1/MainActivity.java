package tw.brad.book.mysvtest1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// 主介面&選關介面宣告
	private MenuSurfaceView menuSurface;
	private StartSurfaceView startSurface;
	private MultiStartSurfaceView multistartSurface;
	private GameView gameView;
	// 介面代號
	private final int MENU = 0;
	private final int START = 1;
	private final int START1 = 11;
	private final int MULTI_START = 2;
	private final int EQUIPMENT = 3;
	private final int MAKE_EQUIPMENT = 4;

	// 目前介面
	private int uiNow = 0;

	// 開發介面
	private Button b_n, b_s;
	private TextView name, star;
	private ImageView weapon_image;
	// 玩家資料
	private PlayerData pd;

	// 裝備資料
	private WeaponData weaponData;
	// 裝備介面
	private GridView equipmentView;
	private int[] image, imagePick, imageNotPick;
	private ImageView weapon1, weapon2;
	private TextView weaponName1, weaponName2;
	// 目前武器庫中 現在被選擇的武器 上一個被選擇的武器
	private int now_chose, pre_chose, weapon_to_switch;
	private List<Map<String, Object>> items;
	private SimpleAdapter adapter;
	private Button b_back, b_detail, b_switch_weapon, b_strengthen, b_evo;

	// 資料庫
	public DBHelper dbhelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 全螢幕，隱藏掉手機狀態欄
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉program的title

		// 傳入裝備圖片
		image = new int[] { R.drawable.w01_handgun, R.drawable.w02_assaultgun,
				R.drawable.w03_blade, R.drawable.w04_chargegun,
				R.drawable.w05_handgun, R.drawable.w06_assaultgun,
				R.drawable.w07_blade, R.drawable.w08_chargegun,
				R.drawable.w09_handgun, R.drawable.w10_assaultgun,
				R.drawable.w11_blade, R.drawable.w12_chargegun,
				R.drawable.w00_lock };
		imagePick = new int[] { R.drawable.w01_handgun_s,
				R.drawable.w02_assaultgun_s, R.drawable.w03_blade_s,
				R.drawable.w04_chargegun_s, R.drawable.w05_handgun_s,
				R.drawable.w06_assaultgun_s, R.drawable.w07_blade_s,
				R.drawable.w08_chargegun_s, R.drawable.w09_handgun_s,
				R.drawable.w10_assaultgun_s, R.drawable.w11_blade_s,
				R.drawable.w12_chargegun_s };
		imageNotPick = new int[] { R.drawable.w01_handgun,
				R.drawable.w02_assaultgun, R.drawable.w03_blade,
				R.drawable.w04_chargegun, R.drawable.w05_handgun,
				R.drawable.w06_assaultgun, R.drawable.w07_blade,
				R.drawable.w08_chargegun, R.drawable.w09_handgun,
				R.drawable.w10_assaultgun, R.drawable.w11_blade,
				R.drawable.w12_chargegun };
		// new出資料庫
		dbhelper = new DBHelper(this);
		weaponData = new WeaponData();
		pd = new PlayerData();
		// 檢查資料庫是否為第一次起動
		if (dbhelper.isFirst()) {
			// 第一次起動資料庫匯入初始資料
			dbhelper.insert(0);
			dbhelper.insert(1);
			// 1星
			weaponData.star = 1;
			weaponData.lvBase = 1;
			weaponData.lvMoney = 0;

			weaponData.id = 0;
			weaponData.name = "Handgun I";
			weaponData.mode = 1;
			weaponData.dis = 500;
			weaponData.atk = 10;
			weaponData.bulletCost = 25;
			weaponData.reloadSpeed = 2;
			weaponData.cd = 0;
			dbhelper.insertWeaponData(weaponData);
			weaponData.id = 1;
			weaponData.name = "Rifle I";
			weaponData.mode = 2;
			weaponData.dis = 500;
			weaponData.atk = 5;
			weaponData.bulletCost = 8;
			weaponData.reloadSpeed = 1;
			weaponData.cd = 5;
			dbhelper.insertWeaponData(weaponData);
			weaponData.id = 2;
			weaponData.name = "Blade I";
			weaponData.mode = 3;
			weaponData.dis = 400;
			weaponData.atk = 10;
			weaponData.bulletCost = 15;
			weaponData.reloadSpeed = 2;
			weaponData.cd = 0;
			dbhelper.insertWeaponData(weaponData);
			weaponData.id = 3;
			weaponData.name = "Charge I";
			weaponData.mode = 4;
			weaponData.dis = 500;
			weaponData.atk = 5;
			weaponData.bulletCost = (float) 2.5;
			weaponData.reloadSpeed = 2;
			weaponData.cd = 0;
			dbhelper.insertWeaponData(weaponData);
			// 2星
			weaponData.star = 2;
			weaponData.lvBase = 1;
			weaponData.lvMoney = 0;

			weaponData.id = 4;
			weaponData.name = "Handgun II";
			weaponData.mode = 1;
			weaponData.dis = 500;
			weaponData.atk = 20;
			weaponData.bulletCost = 20;
			weaponData.reloadSpeed = 2;
			weaponData.cd = 0;
			dbhelper.insertWeaponData(weaponData);
			weaponData.id = 5;
			weaponData.name = "Rifle II";
			weaponData.mode = 2;
			weaponData.dis = 500;
			weaponData.atk = 10;
			weaponData.bulletCost = 6;
			weaponData.reloadSpeed = 1;
			weaponData.cd = 5;
			dbhelper.insertWeaponData(weaponData);
			weaponData.id = 6;
			weaponData.name = "Blade II";
			weaponData.mode = 3;
			weaponData.dis = 450;
			weaponData.atk = 20;
			weaponData.bulletCost = 15;
			weaponData.reloadSpeed = 2;
			weaponData.cd = 0;
			dbhelper.insertWeaponData(weaponData);
			weaponData.id = 7;
			weaponData.name = "Charge II";
			weaponData.mode = 4;
			weaponData.dis = 500;
			weaponData.atk = 10;
			weaponData.bulletCost = (float) 2.5;
			weaponData.reloadSpeed = 2;
			weaponData.cd = 0;
			dbhelper.insertWeaponData(weaponData);
			// 3星
			weaponData.star = 3;
			weaponData.lvBase = 0;
			weaponData.lvMoney = 0;

			weaponData.id = 8;
			weaponData.name = "Handgun III";
			weaponData.mode = 1;
			weaponData.dis = 600;
			weaponData.atk = 40;
			weaponData.bulletCost = 15;
			weaponData.reloadSpeed = 2;
			weaponData.cd = 0;
			dbhelper.insertWeaponData(weaponData);
			weaponData.id = 9;
			weaponData.name = "Rifle III";
			weaponData.mode = 2;
			weaponData.dis = 600;
			weaponData.atk = 20;
			weaponData.bulletCost = 5;
			weaponData.reloadSpeed = 1;
			weaponData.cd = 5;
			dbhelper.insertWeaponData(weaponData);
			weaponData.id = 10;
			weaponData.name = "Blade III";
			weaponData.mode = 3;
			weaponData.dis = 500;
			weaponData.atk = 40;
			weaponData.bulletCost = 15;
			weaponData.reloadSpeed = 3;
			weaponData.cd = 0;
			dbhelper.insertWeaponData(weaponData);
			weaponData.id = 11;
			weaponData.name = "Charge III";
			weaponData.mode = 4;
			weaponData.dis = 600;
			weaponData.atk = 20;
			weaponData.bulletCost = (float) 2.2;
			weaponData.reloadSpeed = 2;
			weaponData.cd = 0;
			dbhelper.insertWeaponData(weaponData);
			pd.name = "玩家1";
			pd.lv = 99;
			pd.exp = 9999;
			pd.money = 99999;
			pd.parts = 99;
			pd.sparts = 99;
			pd.weapon1 = 0;
			pd.weapon2 = 1;
			pd.cost = 99;
			dbhelper.insertPlayerData(pd);

			dbhelper.insertIp("192.168.0.1");
		}

		// new出主介面
		menuSurface = new MenuSurfaceView(this); // 設定目前為主介面
		setContentView(menuSurface);

	}

	// handler的監聽事件
	Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 依照介面代號切換介面
			switch (msg.what) {
			case -1:
				VibratorUtil.Vibrate(MainActivity.this, 100);
				break;
			case 0:
				switch (uiNow) {
				case MENU:
					menuSurface = new MenuSurfaceView(MainActivity.this);
					Log.i("info", "MENU");
					setContentView(menuSurface);
					break;
				case START:
					startSurface = new StartSurfaceView(MainActivity.this);
					setContentView(startSurface);
					break;
				case MULTI_START:
					multistartSurface = new MultiStartSurfaceView(
							MainActivity.this);
					setContentView(multistartSurface);
					break;
				case EQUIPMENT:
					gotoEquipmentView();
					break;
				case MAKE_EQUIPMENT:
					gotoMakeEquipmentView();
					break;
				case START1:
					gameView = new GameView(MainActivity.this, 1);
					setContentView(gameView);
					break;
				case 12:
					gameView = new GameView(MainActivity.this, 2);
					setContentView(gameView);
					break;
				case 13:
					Toast.makeText(MainActivity.this, "you chose stage3", 1000)
							.show();
					break;
				case 14:
					Toast.makeText(MainActivity.this, "you chose stage4", 1000)
							.show();
					break;
				case 15:
					Toast.makeText(MainActivity.this, "you chose stage5", 1000)
							.show();
					break;
				case 101:
					gameView = new GameView(MainActivity.this, 101);
					setContentView(gameView);
					break;
				case 31:
					gotoGetEuiqment(0);
					break;
				case 32:
					gotoGetEuiqment(1);
					break;
				}
				break;
			// 取得一般武器
			case 1:
				setUiNow(31);
				MainActivity.this.sendMessage(0);
				break;
			case 2:
				setUiNow(32);
				MainActivity.this.sendMessage(0);
				break;
			// 武器強化
			case 3:
				PlayerData pd = dbhelper.getPlayerData();
				WeaponData wd = dbhelper.getWeaponData(now_chose);
				if (pd.money < wd.star * 100) {
					Toast.makeText(MainActivity.this, "你沒錢了!", 1500).show();
				} else if (wd.lvBase + wd.lvMoney >= 10) {
					Toast.makeText(MainActivity.this, "等級上限!", 1500).show();
				} else {
					wd.lvMoney = wd.lvMoney + 1;
					dbhelper.updateWeaponData(wd);
					pd.money = pd.money - wd.star * 100;
					dbhelper.updatePlayerData(pd);
					new WeaponDetailDialog(MainActivity.this, now_chose).show();
				}
				break;
			// 武器進化
			case 4:
				PlayerData pd2 = dbhelper.getPlayerData();
				WeaponData wd2 = dbhelper.getWeaponData(now_chose);
				if (now_chose > 7) {
					Toast.makeText(MainActivity.this, "武器星數已達最高!", 1500).show();
				} else if (now_chose <= 7) {
					if (pd2.money > wd2.star * 1000) {
						if (wd2.lvBase + wd2.lvMoney >= 10) {
							wd2.lvMoney = 0;
							WeaponData wd3 = dbhelper
									.getWeaponData(now_chose + 4);
							wd3.lvBase = wd3.lvBase + 1;
							dbhelper.updateWeaponData(wd2);
							dbhelper.updateWeaponData(wd3);
							pd2.money = pd2.money - wd2.star * 1000;
							dbhelper.updatePlayerData(pd2);
							new WeaponDetailDialog(MainActivity.this, now_chose)
									.show();
							new WeaponDetailDialog(MainActivity.this,
									now_chose + 4).show();
							
							//更新武器庫圖片
							Map<String, Object> item = new HashMap<String, Object>();
							item = new HashMap<String, Object>();
							item.put("image", image[now_chose+4]);
							item.put("text",
									dbhelper.getWeaponName(now_chose+4));
							items.set(now_chose+4, item);
							adapter.notifyDataSetChanged();
						} else {
							Toast.makeText(MainActivity.this, "等級不足!", 1500)
									.show();
						}
					} else {
						Toast.makeText(MainActivity.this, "你沒錢了!", 1500).show();
					}

				}
				break;
			}
		}

	};

	// 此函式讓其他介面可以呼叫handler來傳遞消息，進而改變介面
	public void sendMessage(int what) {
		Message msg = new Message();
		msg.what = what;
		myHandler.sendMessage(msg);
	}

	public void gotoGetEuiqment(int type) {

		setContentView(R.layout.get_equipment);
		Random r = new Random();
		r.setSeed(System.currentTimeMillis());
		int id;
		PlayerData pd = dbhelper.getPlayerData();
		if (type == 0) {
			id = r.nextInt(4);
			pd.parts = pd.parts - 1;
		} else {
			id = r.nextInt(8) + 4;
			pd.sparts = pd.sparts - 1;
		}
		dbhelper.updatePlayerData(pd);
		WeaponData wd;
		wd = dbhelper.getWeaponData(id);
		name = (TextView) findViewById(R.id.name);
		star = (TextView) findViewById(R.id.star);
		weapon_image = (ImageView) findViewById(R.id.weapon_image);

		name.setText(wd.name);
		String s_temp = "";
		for (int i = 0; i < wd.star; i++) {
			s_temp = s_temp.concat("★");
		}
		star.setText("星數:" + s_temp);
		weapon_image.setImageResource(imageNotPick[id]);
		wd.lvBase = wd.lvBase + 1;
		dbhelper.updateWeaponData(wd);
		b_back = (Button) findViewById(R.id.b_back);
		b_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setUiNow(0);
				sendMessage(0);
			}

		});

	}

	// 執行開發介面
	@SuppressLint("NewApi")
	public void gotoMakeEquipmentView() {
		/*
		 * animation = new AlphaAnimation(1, 0); animation.setDuration(1000);
		 * menuSurface.setAnimation(animation);
		 */
		setContentView(R.layout.make_equipment);

		b_s = (Button) findViewById(R.id.b_s);
		b_s.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new ConfirmDialog(MainActivity.this, 2, "特殊開發需要:1個特殊零件").show();
			}

		});
		b_n = (Button) findViewById(R.id.b_n);
		b_n.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new ConfirmDialog(MainActivity.this, 1, "一般開發需要:1個普通零件").show();
			}

		});
		b_back = (Button) findViewById(R.id.b_back);
		b_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setUiNow(0);
				sendMessage(0);
			}

		});

	}

	// 執行武器介面
	public void gotoEquipmentView() {

		setContentView(R.layout.equipment);
		equipmentView = (GridView) findViewById(R.id.gridview);
		weapon_to_switch = -1;
		// 初始化目前選擇的位置
		now_chose = -1;
		// 返回按鈕
		b_back = (Button) findViewById(R.id.b_back);
		b_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setUiNow(0);
				sendMessage(0);
			}

		});
		// 詳細按鈕
		b_detail = (Button) findViewById(R.id.detail);
		b_detail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (now_chose != -1) {
					new WeaponDetailDialog(MainActivity.this, now_chose).show();
				}
			}

		});
		b_strengthen = (Button) findViewById(R.id.strengthen);
		b_strengthen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (now_chose != -1) {
					WeaponData wd = dbhelper.getWeaponData(now_chose);
					new ConfirmDialog(MainActivity.this, 3, "強化武器需要:" + wd.star
							* 100 + " $").show();
				}
			}

		});
		b_evo = (Button) findViewById(R.id.evo);
		b_evo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (now_chose != -1) {
					WeaponData wd = dbhelper.getWeaponData(now_chose);
					new ConfirmDialog(MainActivity.this, 4, "進化武器需要:" + wd.star
							* 1000 + " $").show();
				}
			}

		});
		b_switch_weapon = (Button) findViewById(R.id.switch_weapon);
		b_switch_weapon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (now_chose != -1) {
					dbhelper.update(weapon_to_switch + 1, now_chose);
					dbhelper.switchWeapon(weapon_to_switch + 1, now_chose);
					Log.i("info", "switch:" + weapon_to_switch + " now_chose:"
							+ now_chose + " id1:" + dbhelper.getWeaponId(0));
					if (weapon_to_switch == 0) {
						weapon1.setImageResource(imagePick[dbhelper
								.getWeaponId(0)]);
						weapon2.setImageResource(imageNotPick[dbhelper
								.getWeaponId(1)]);
						weaponName1.setText(dbhelper.getWeaponName(dbhelper
								.getWeaponId(0)));
						weaponName2.setText(dbhelper.getWeaponName(dbhelper
								.getWeaponId(1)));
					} else if (weapon_to_switch == 1) {
						weapon1.setImageResource(imageNotPick[dbhelper
								.getWeaponId(0)]);
						weapon2.setImageResource(imagePick[dbhelper
								.getWeaponId(1)]);
						weaponName1.setText(dbhelper.getWeaponName(dbhelper
								.getWeaponId(0)));
						weaponName2.setText(dbhelper.getWeaponName(dbhelper
								.getWeaponId(1)));
					}
				}
			}

		});
		// 初始化現在2個裝備的名子
		weaponName1 = (TextView) findViewById(R.id.current_weapon_name1);
		weaponName2 = (TextView) findViewById(R.id.current_weapon_name2);
		weaponName1.setText(dbhelper.getWeaponName(dbhelper.getWeaponId(0)));
		weaponName2.setText(dbhelper.getWeaponName(dbhelper.getWeaponId(1)));
		// 初始化現在2個裝備的圖片
		weapon1 = (ImageView) findViewById(R.id.current_weapon1);
		weapon2 = (ImageView) findViewById(R.id.current_weapon2);
		weapon1.setImageResource(imageNotPick[dbhelper.getWeaponId(0)]);
		weapon2.setImageResource(imageNotPick[dbhelper.getWeaponId(1)]);
		weapon1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				weapon1.setImageResource(imagePick[dbhelper.getWeaponId(0)]);
				weapon2.setImageResource(imageNotPick[dbhelper.getWeaponId(1)]);
				weapon_to_switch = 0;
			}

		});
		weapon2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				weapon1.setImageResource(imageNotPick[dbhelper.getWeaponId(0)]);
				weapon2.setImageResource(imagePick[dbhelper.getWeaponId(1)]);
				weapon_to_switch = 1;
			}

		});

		// 寫入武器圖片位置和名稱

		items = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 12; i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			WeaponData wd = dbhelper.getWeaponData(i);
			if (wd.lvBase == 0) {
				item.put("image", image[12]);
				item.put("text", "???");
			} else {
				item.put("image", image[i]);
				item.put("text", dbhelper.getWeaponName(i));
			}
			items.add(item);
		}
		// 建立adapter(配置器)
		adapter = new SimpleAdapter(this, items, R.layout.grid_equipment,
				new String[] { "image", "text" }, new int[] { R.id.image,
						R.id.text });
		// 設定每排的可顯示數目
		equipmentView.setNumColumns(3);
		// 設定adapter
		equipmentView.setAdapter(adapter);
		// 項目被點選時，回應的動作
		equipmentView
				.setOnItemClickListener(new GridView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView adapterView, View view,
							int position, long id) {
						// 把上一個被選擇的武器 顯示為"不被選擇"
						// 把現在被選擇的武器 顯示為"被選擇"
						// 並重新寫入item裡，在通知adapter資料更改
						WeaponData wd = dbhelper.getWeaponData(position);
						if (wd.lvBase == 0) {
							Toast.makeText(MainActivity.this, "你還沒取得武器", 1500)
									.show();
						} else {
							pre_chose = now_chose;
							now_chose = position;
							if (pre_chose != -1) {
								Map<String, Object> item = new HashMap<String, Object>();
								item.put("image", imageNotPick[pre_chose]);
								item.put("text",
										dbhelper.getWeaponName(pre_chose));
								items.set(pre_chose, item);
								item = new HashMap<String, Object>();
								item.put("image", imagePick[now_chose]);
								item.put("text",
										dbhelper.getWeaponName(now_chose));
								items.set(now_chose, item);
								adapter.notifyDataSetChanged();
							} else {
								Map<String, Object> item = new HashMap<String, Object>();
								item = new HashMap<String, Object>();
								item.put("image", imagePick[now_chose]);
								item.put("text",
										dbhelper.getWeaponName(now_chose));
								items.set(now_chose, item);
								adapter.notifyDataSetChanged();
							}
						}
					}
				});
	}

	public void VibratorUtil(int a) {
		VibratorUtil.Vibrate(MainActivity.this, 100);
	}

	public void setUiNow(int ui) {
		uiNow = ui;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (uiNow == 11 || uiNow == 12 || uiNow == 101) {

			} else {
				setUiNow(0);
				sendMessage(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
