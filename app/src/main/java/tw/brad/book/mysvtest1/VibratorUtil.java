package tw.brad.book.mysvtest1;


	import android.app.Activity;
	import android.app.Service;
	import android.os.Vibrator;

	 
	/**
	* 手機震動工具類
	* @author Administrator
	*
	*/
	public class VibratorUtil {

	 
	/**
	* final Activity activity ：調用該方法的Activity實例
	* long milliseconds ：震動的時長，單位是毫秒
	* long[] pattern ：自定義震動模式 。數組中數字的含義依次是[靜止時長，震動時長，靜止時長，震動時長。。。]時長的單位是毫秒
	* boolean isRepeat ： 是否反復震動，如果是true，反復震動，如果是false，只震動一次
	*/

	 
	public static void Vibrate(final Activity activity, long milliseconds) {
	Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
	vib.vibrate(milliseconds);
	}
	public static void Vibrate(final Activity activity, long[] pattern,boolean isRepeat) {
	Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
	vib.vibrate(pattern, isRepeat ? 1 : -1);
	}

	 
	}

