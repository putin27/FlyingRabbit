package tw.brad.book.mysvtest1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmDialog extends Dialog {
	// Define default width and height
	private Button b_yes, b_no;
	private TextView d_text;

	public ConfirmDialog(final MainActivity activity, final int msg) {
		super(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

		// set content

		setContentView(R.layout.dialog);

		// setCanceledOnTouchOutside(false);
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		// set window params

		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();

		// set width, height by density and gravity

		// params.width = (int) dm.widthPixels / 2;
		// params.height = (int) dm.heightPixels / 2;
		params.width = LayoutParams.WRAP_CONTENT;
		params.height = LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.CENTER;

		window.setAttributes(params);

		b_yes = (Button) findViewById(R.id.b_yes);
		b_yes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.sendMessage(msg);
				dismiss();
			}
		});
		b_no = (Button) findViewById(R.id.b_no);
		b_no.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

	}

	public ConfirmDialog(final MainActivity activity, final int msg, String text) {
		super(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

		// set content

		setContentView(R.layout.dialog);

		// setCanceledOnTouchOutside(false);

		d_text = (TextView) findViewById(R.id.d_text);
		d_text.setText(text);
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		// set window params

		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();

		// set width, height by density and gravity

		// params.width = (int) dm.widthPixels / 2;
		// params.height = (int) dm.heightPixels / 2;
		params.width = LayoutParams.WRAP_CONTENT;
		params.height = LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.CENTER;

		window.setAttributes(params);

		b_yes = (Button) findViewById(R.id.b_yes);
		b_yes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.sendMessage(msg);
				dismiss();
			}
		});
		b_no = (Button) findViewById(R.id.b_no);
		b_no.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

	}
}