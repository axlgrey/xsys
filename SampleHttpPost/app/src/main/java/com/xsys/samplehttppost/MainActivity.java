package com.xsys.samplehttppost;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xsys.api.CfgMain;
import com.xsys.api.LibNetHttp;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
	public static Activity lastCreated;

	private String httpreq_url = "http://192.168.137.1:80/xsys/posttest.php";
	private String httpreq_postkey = "exec_cmd";
	private String httpreq_postval = "testval_01";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialization
		CfgMain.readConfig(this);
		lastCreated = this;

		// Start-up Routine
		EditText et_url = (EditText) findViewById(R.id.txt_posturl);
		EditText et_postkey = (EditText) findViewById(R.id.txt_postkey);
		EditText et_postval = (EditText) findViewById(R.id.txt_postval);
		et_url.setText(httpreq_url);
		et_postkey.setText(httpreq_postkey);
		et_postval.setText(httpreq_postval);

		Button btn_post = (Button) findViewById(R.id.btnPost);
		btn_post.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				EditText et_url = (EditText) findViewById(R.id.txt_posturl);
				EditText et_postkey = (EditText) findViewById(R.id.txt_postkey);
				EditText et_postval = (EditText) findViewById(R.id.txt_postval);
				httpreq_url = et_url.getText().toString();
				httpreq_postkey = et_postkey.getText().toString();
				httpreq_postval = et_postval.getText().toString();

				getPostResultFromServer();
			}
		});
	}

	private void getPostResultFromServer() {
		try {
			LibNetHttp libnetobj = new LibNetHttp();
			//String url = libnetobj.GetServerUrl();
			String url = httpreq_url;

			String[] param = {httpreq_postkey, httpreq_postval};
			List<String[]> lparam = new ArrayList<String[]>();
			lparam.add(param);
			libnetobj.MakeHttpReq(MainHandler, this, url, lparam);
		} catch (Exception ex) {
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}
	}

	private Handler MainHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				super.handleMessage(msg);
				switch (msg.what) {

					case LibNetHttp.HTTP_REQUEST_COMPLETED:
						JSONObject jsonObj = (JSONObject) msg.obj;
						String retcommand = jsonObj.getString("command");
						Integer retresult = jsonObj.getInt("result");

						TextView tv_msg = (TextView) findViewById(R.id.tvMessage);
						//tv_msg.setText(jsonObj.toString());
						tv_msg.setText(msg.obj.toString());
						break;
				}

			} catch (Exception ex) {
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_close) {
			this.finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
