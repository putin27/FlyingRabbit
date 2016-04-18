package tw.brad.book.mysvtest1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "item_database";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "item_table";
	private final static String FEILD_ID = "_id";
	private final static String FEILD_TEXT = "item_image_id";
	private String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
			+ "(" + FEILD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ FEILD_TEXT + " INT" + ")";
	private String weaponTable = "CREATE TABLE IF NOT EXISTS weaponTable"
			+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,name CHAR,"
			+ "mode INT,dis INT,atk FLOAT,bulletCost FLOAT,reloadSpeed FLOAT,cd INT,"
			+ "lvBase INT,lvMoney INT,star INT)";
	private String playerTable = "CREATE TABLE IF NOT EXISTS playerTable"
			+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,name CHAR,"
			+ "lv INT,exp INT,money INT,parts INT,sparts INT,weapon1 INT,"
			+ "weapon2 INT,cost INT)";
	private String ipTable = "CREATE TABLE IF NOT EXISTS ipTable"
			+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,ip CHAR)";

	/*
	 * private String createTable2 = "CREATE TABLE IF NOT EXISTS" +
	 * " config_table (_id INTEGER PRIMARY KEY AUTOINCREMENT," + "setting INT)";
	 */

	/*
	 * table的第一筆資料 cursor的position從0開始 裡面的_id是第0個欄位 所以資料從1開始取
	 */
	private SQLiteDatabase db;
	private Cursor cursor;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		db = this.getWritableDatabase();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(createTable);
		db.execSQL(weaponTable);
		db.execSQL(playerTable);
		db.execSQL(ipTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		onCreate(db);
	}

	public void update(int id, int weapon_image_id) {
		ContentValues values = new ContentValues();
		values.put(FEILD_TEXT, weapon_image_id);
		db.update(TABLE_NAME, values, FEILD_ID + "=" + id, null);
	}

	public void insertIp(String ip) {
		ContentValues values = new ContentValues();
		values.put("ip", ip);
		db.insert("ipTable", null, values);
	}

	public String getIp() {
		cursor = db.query("ipTable", null, null, null, null, null, null);
		cursor.moveToPosition(0);
		return cursor.getString(1);
	}

	public void updateIp(String ip) {
		ContentValues values = new ContentValues();
		values.put("ip", ip);
		db.update("ipTable", values, "_id = 1", null);
	}

	public void insert(int itemImageId) {
		ContentValues values = new ContentValues();
		values.put(FEILD_TEXT, itemImageId);
		db.insert(TABLE_NAME, null, values);
	}

	public int getWeaponId(int id) {
		cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
		cursor.moveToPosition(id);
		return cursor.getInt(1);
	}

	public void insertPlayerData(PlayerData pd) {
		ContentValues values = new ContentValues();
		values.put("name", pd.name);
		values.put("lv", pd.lv);
		values.put("exp", pd.exp);
		values.put("money", pd.money);
		values.put("parts", pd.parts);
		values.put("sparts", pd.sparts);
		values.put("weapon1", pd.weapon1);
		values.put("weapon2", pd.weapon2);
		values.put("cost", pd.cost);
		db.insert("playerTable", null, values);
	}

	public String getPName() {
		cursor = db.query("playerTable", null, null, null, null, null, null);
		cursor.moveToFirst();
		return cursor.getString(0);
	}

	public PlayerData getPlayerData() {
		cursor = db.query("playerTable", null, null, null, null, null, null);
		cursor.moveToFirst();
		PlayerData pd = new PlayerData();
		pd.name = cursor.getString(1);
		pd.lv = cursor.getInt(2);
		pd.exp = cursor.getInt(3);
		pd.money = cursor.getInt(4);
		pd.parts = cursor.getInt(5);
		pd.sparts = cursor.getInt(6);
		pd.weapon1 = cursor.getInt(7);
		pd.weapon2 = cursor.getInt(8);
		pd.cost = cursor.getInt(9);

		return pd;
	}

	public void updatePlayerData(PlayerData pd) {
		ContentValues values = new ContentValues();
		values.put("name", pd.name);
		values.put("lv", pd.lv);
		values.put("exp", pd.exp);
		values.put("money", pd.money);
		values.put("parts", pd.parts);
		values.put("sparts", pd.sparts);
		values.put("weapon1", pd.weapon1);
		values.put("weapon2", pd.weapon2);
		values.put("cost", pd.cost);
		db.update("playerTable", values, "_id = 1", null);
	}

	public void switchWeapon(int a, int b) {
		int id;
		ContentValues values = new ContentValues();
		if (a == 1) {
			id = 7;
			values.put("weapon1", b);
		} else {
			id = 8;
			values.put("weapon2", b);
		}
		db.update("playerTable", values, "_id = 1", null);
	}

	public void insertWeaponData(WeaponData wd) {
		ContentValues values = new ContentValues();
		values.put("name", wd.name);
		values.put("mode", wd.mode);
		values.put("dis", wd.dis);
		values.put("atk", wd.atk);
		values.put("bulletCost", wd.bulletCost);
		values.put("reloadSpeed", wd.reloadSpeed);
		values.put("cd", wd.cd);
		values.put("lvBase", wd.lvBase);
		values.put("lvMoney", wd.lvMoney);
		values.put("star", wd.star);
		db.insert("weaponTable", null, values);
	}

	public void updateWeaponData(WeaponData wd) {
		ContentValues values = new ContentValues();
		values.put("name", wd.name);
		values.put("mode", wd.mode);
		values.put("dis", wd.dis);
		values.put("atk", wd.atk);
		values.put("bulletCost", wd.bulletCost);
		values.put("reloadSpeed", wd.reloadSpeed);
		values.put("cd", wd.cd);
		values.put("lvBase", wd.lvBase);
		values.put("lvMoney", wd.lvMoney);
		values.put("star", wd.star);
		db.update("weaponTable", values, "_id = " + wd.id, null);
	}

	public void addWeaponLevel(int id) {
		cursor = db.query("weaponTable", null, null, null, null, null, null);
		cursor.moveToPosition(id);
		ContentValues values = new ContentValues();
		values.put("lvMoney", 5);
		db.update("weaponTable", values, "_id = " + id, null);
	}

	public String getWeaponName(int id) {

		cursor = db.query("weaponTable", null, null, null, null, null, null);
		cursor.moveToPosition(id);
		return cursor.getString(1);
	}

	public WeaponData getWeaponData(int id) {
		cursor = db.query("weaponTable", null, null, null, null, null, null);
		cursor.moveToPosition(id);
		WeaponData wd = new WeaponData();
		wd.id = cursor.getInt(0);
		wd.name = cursor.getString(1);
		wd.mode = cursor.getInt(2);
		wd.dis = cursor.getInt(3);
		wd.atk = cursor.getFloat(4);
		wd.bulletCost = cursor.getFloat(5);
		wd.reloadSpeed = cursor.getFloat(6);
		wd.cd = cursor.getInt(7);
		wd.lvBase = cursor.getInt(8);
		wd.lvMoney = cursor.getInt(9);
		wd.star = cursor.getInt(10);

		return wd;
	}

	public boolean isFirst() {
		Cursor cursor = db
				.query(TABLE_NAME, null, null, null, null, null, null);

		// 如果沒有第一筆資料 回傳false
		if (!cursor.moveToFirst()) {
			return true;
		}
		return false;

	}

	public int getSize() {
		Cursor cursor = db
				.query(TABLE_NAME, null, null, null, null, null, null);

		return cursor.getCount();
	}

	public void close() {
		db.close();
	}
}