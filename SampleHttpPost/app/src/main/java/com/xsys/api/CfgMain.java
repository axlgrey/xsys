package com.xsys.api;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CfgMain {

	public static final int CFG_CALLBACK = 9000;

	// Non-configurable variable
	public static String applicationdir = Environment.getExternalStorageDirectory().getPath() + "/xsys";

	// Configurable variable
	public static String httpreq_url;
	public static String httpreq_username;
	public static String httpreq_password;
	public static String admin_password;

	public static void initDefaultValue() {
		httpreq_url = "http://192.168.1.1:80/";
		httpreq_username = "username";
		httpreq_password = "password";
		admin_password = "password";
	}

	public static void setValue(String ipaddr, String username, String password, String adminpassword) {
		httpreq_url = ipaddr;
		httpreq_username = username;
		httpreq_password = password;
		admin_password = adminpassword;
	}

	public static void readConfig(Context con) {
		try {
			DbhConfig dbhcfg = new DbhConfig(con);
			String[] row = dbhcfg.tbl01_readrow(dbhcfg.tbl01_getsize().intValue());
			httpreq_url = LibEncrypt.Decrypt_256(row[1]);
			httpreq_username = LibEncrypt.Decrypt_256(row[2]);
			httpreq_password = LibEncrypt.Decrypt_256(row[3]);
			admin_password = LibEncrypt.Decrypt_256(row[4]);
		} catch (Exception ex) {
			initDefaultValue();
			writeConfig(con);
		}
	}

	public static void writeConfig(Context con) {
		DbhConfig dbhcfg = new DbhConfig(con);
		dbhcfg.tbl01_clear();
		dbhcfg.tbl01_insert(LibEncrypt.Encrypt_256(httpreq_url), LibEncrypt.Encrypt_256(httpreq_username), LibEncrypt.Encrypt_256(httpreq_password), LibEncrypt.Encrypt_256(admin_password));
	}

	public static void promptAdminPassword(Context con, final Handler pHandler) {
		LinearLayout layout = new LinearLayout(con);
		LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CLIP_VERTICAL);
		layout.setPadding(10, 12, 10, 0);
		layout.setLayoutParams(lparam);

		LinearLayout.LayoutParams lblParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		TextView lblPassword = new TextView(con);
		lblPassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		lblPassword.setPadding(0, 0, 0, 0);
		lblPassword.setText("Enter password: ");
		lblPassword.setLayoutParams(lblParam);

		LinearLayout.LayoutParams txtParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		final EditText txtPassword = new EditText(con);
		txtPassword.setLayoutParams(txtParam);
		txtPassword.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txtPassword.setPadding(10, 0, 10, 0);
		txtPassword.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

		layout.addView(lblPassword);
		layout.addView(txtPassword);

		AlertDialog.Builder builder = new AlertDialog.Builder(con);
		builder.setTitle("Authentication Required");
		builder.setView(layout);

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				if (txtPassword.getText().toString().equals(admin_password)) {
					Message msg = new Message();
					msg.what = CFG_CALLBACK;
					msg.arg1 = 0;
					pHandler.sendMessage(msg);
				} else {
					Message msg = new Message();
					msg.what = CFG_CALLBACK;
					msg.arg1 = -1;
					pHandler.sendMessage(msg);
				}
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Message msg = new Message();
				msg.what = CFG_CALLBACK;
				msg.arg1 = -2;
				pHandler.sendMessage(msg);
			}
		});

		builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
