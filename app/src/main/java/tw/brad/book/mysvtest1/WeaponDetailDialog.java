package tw.brad.book.mysvtest1;

import java.math.BigDecimal;

import android.app.Dialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class WeaponDetailDialog extends Dialog {

	private TextView t_name, t_star, t_lv, t_atk, t_bCost, t_reload;
	private TextView text[] = new TextView[6];
	private WeaponData wd;
	private String s_temp;
	private View mainView;

	public WeaponDetailDialog(final MainActivity context, int id) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

		setContentView(R.layout.weapon_detail_dialog);
		setCancelable(true);
		setCanceledOnTouchOutside(true);

		// 取得最外面的layout 並設置點擊取消Dialog
		mainView = this.findViewById(R.id.outside_layout);
		mainView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		wd = context.dbhelper.getWeaponData(id);
		t_name = (TextView) findViewById(R.id.t_name);
		t_star = (TextView) findViewById(R.id.t_star);
		t_lv = (TextView) findViewById(R.id.t_lv);
		t_atk = (TextView) findViewById(R.id.t_atk);
		t_bCost = (TextView) findViewById(R.id.t_bCost);
		t_reload = (TextView) findViewById(R.id.t_reload);
		t_name.setText("武器名稱:" + wd.name);
		s_temp = "";
		for (int i = 0; i < wd.star; i++) {
			s_temp = s_temp.concat("★");
		}
		t_star.setText("星數:" + s_temp);
		t_lv.setText("基本+強化等級:" + wd.lvBase + "+" + wd.lvMoney);
		t_atk.setText("攻擊力:" + wd.atk * (1 + (wd.lvBase + wd.lvMoney) * 0.1));
		t_bCost.setText("子彈消耗:" + String.valueOf(wd.bulletCost));
		t_reload.setText("子彈回覆:" + String.valueOf(wd.reloadSpeed));

		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);

		// set window params

		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();

		// set width, height by density and gravity

		params.width = (int) dm.widthPixels / 7 * 5;
		params.height = (int) dm.heightPixels / 7 * 5;
		params.gravity = Gravity.CENTER;

		window.setAttributes(params);

	}
}
