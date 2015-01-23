package com.xsys.api;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class LibNetHttp {

	public static final int HTTP_REQUEST_COMPLETED = 8000;
	public static final int HTTP_REQUEST_ERROR = 8100;

	private Handler ParentHandler;
	private Context ParentCon;
	private Boolean ParentIsSvc;

	private Handler ReqHandler = new Handler();
	private Boolean ReqBusy = false;

	private List<String[]> ReqPostArgs;
	private String ReqUrl;
	private String ReqRetText;

	public LibNetHttp() {
		ParentIsSvc = false;
	}

	public LibNetHttp(Boolean svcmode) {
		ParentIsSvc = svcmode;
	}

	public String GetHttpReqApiPath() {
		String url = CfgMain.httpreq_url;
		if (url.endsWith("/")) {
			url += "webapi.sample.php";
		} else {
			url += "/webapi.sample.php";
		}

		return url;
	}

	public void MakeHttpReq(Handler hndl, Context con, String url, List<String[]> postargs) {
		if (!ReqBusy) {
			ReqBusy = true;
			ParentHandler = hndl;
			ParentCon = con;
			ReqUrl = url;
			ReqPostArgs = postargs;
			ReqRetText = "";
			Thread childThread = new Thread(InvokeHttpReq);
			childThread.start();
		}
	}

	// region Using MultipartEntity
	private Runnable InvokeHttpReq = new Runnable() {
		public void run() {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(ReqUrl);

			try {
				MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				multipartEntity.addPart("name", new StringBody("nameText"));

				for (int i = 0; i < ReqPostArgs.size(); i++) {
					String[] postarg = ReqPostArgs.get(i);
					multipartEntity.addPart(postarg[0], new StringBody(postarg[1]));
				}

				// Post as binary data
				//for (int i = 0; i < ReqPostArgs.size(); i++) {
				//	String[] postarg = ReqPostArgs.get(i);
				//	byte[] bytearr = postarg[1].getBytes();
				//	builder.addBinaryBody(postarg[0], bytearr);
				//}

				httppost.setEntity(multipartEntity);
				HttpResponse response = httpclient.execute(httppost);
				ReqRetText = EntityUtils.toString(response.getEntity());
				ReqHandler.post(CompleteHttpReq);
			} catch (ClientProtocolException e) {
				if (!ParentIsSvc) {
					LibLogger.writeLog(LibLogger.ID_ERROR, e.toString());
				}
				ReqHandler.post(InvokeConnectionError);
			} catch (IOException e) {
				if (!ParentIsSvc) {
					LibLogger.writeLog(LibLogger.ID_ERROR, e.toString());
				}
				ReqHandler.post(InvokeConnectionError);
			}
		}
	};

	private Runnable InvokeConnectionError = new Runnable() {
		public void run() {
			ReqBusy = false;

			Message retmessage = new Message();
			retmessage.what = HTTP_REQUEST_ERROR;
			retmessage.obj = ReqPostArgs;
			ParentHandler.sendMessage(retmessage);

			if (!ParentIsSvc) {
				Toast.makeText(ParentCon, "Cannot connect to server.", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private Runnable CompleteHttpReq = new Runnable() {
		public void run() {
			ReqBusy = false;

			//Toast.makeText(ParentCon, ReqRetText, Toast.LENGTH_LONG).show();
			try {
				JSONObject jsonObj = new JSONObject(ReqRetText);
				Message retmessage = new Message();
				retmessage.what = HTTP_REQUEST_COMPLETED;
				retmessage.obj = jsonObj;
				ParentHandler.sendMessage(retmessage);
			} catch (JSONException e) {
				if (!ParentIsSvc) {
					LibLogger.writeLog(LibLogger.ID_ERROR, e.toString());
					Toast.makeText(ParentCon, "Error connecting to server:\r\n" + ReqRetText, Toast.LENGTH_LONG).show();
				}
			}

		}
	};
	// endregion

	// region Using MultipartEntityBuilder
	/*
	private Runnable InvokeHttpReq = new Runnable() {
		public void run() {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(ReqUrl);

			try {
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

				for (int i = 0; i < ReqPostArgs.size(); i++) {
					String[] postarg = ReqPostArgs.get(i);
					builder.addTextBody(postarg[0], postarg[1]);
				}

				// Post as binary data
				//for (int i = 0; i < ReqPostArgs.size(); i++) {
				//	String[] postarg = ReqPostArgs.get(i);
				//	byte[] bytearr = postarg[1].getBytes();
				//	builder.addBinaryBody(postarg[0], bytearr);
				//}

				httppost.setEntity(builder.build());
				HttpResponse response = httpclient.execute(httppost);
				ReqRetText = EntityUtils.toString(response.getEntity());
				ReqHandler.post(CompleteHttpReq);
			} catch (ClientProtocolException e) {
				if (!ParentIsSvc) {
					LibLogger.writeLog(LibLogger.ID_ERROR, e.toString());
				}
				ReqHandler.post(InvokeConnectionError);
			} catch (IOException e) {
				if (!ParentIsSvc) {
					LibLogger.writeLog(LibLogger.ID_ERROR, e.toString());
				}
				ReqHandler.post(InvokeConnectionError);
			}
		}
	};

	private Runnable InvokeConnectionError = new Runnable() {
		public void run() {
			ReqBusy = false;

			Message retmessage = new Message();
			retmessage.what = HTTP_REQUEST_ERROR;
			retmessage.obj = ReqPostArgs;
			ParentHandler.sendMessage(retmessage);

			if (!ParentIsSvc) {
				Toast.makeText(ParentCon, "Cannot connect to server.", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private Runnable CompleteHttpReq = new Runnable() {
		public void run() {
			ReqBusy = false;

			//Toast.makeText(ParentCon, ReqRetText, Toast.LENGTH_LONG).show();
			try {
				JSONObject jsonObj = new JSONObject(ReqRetText);
				Message retmessage = new Message();
				retmessage.what = HTTP_REQUEST_COMPLETED;
				retmessage.obj = jsonObj;
				ParentHandler.sendMessage(retmessage);
			} catch (JSONException e) {
				if (!ParentIsSvc) {
					LibLogger.writeLog(LibLogger.ID_ERROR, e.toString());
					Toast.makeText(ParentCon, "Error connecting to server:\r\n" + ReqRetText, Toast.LENGTH_LONG).show();
				}
			}

		}
	};
	*/
	// endregion

}
