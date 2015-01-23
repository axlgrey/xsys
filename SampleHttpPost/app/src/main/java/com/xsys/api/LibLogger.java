package com.xsys.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LibLogger {
	private static final String fdir = CfgMain.applicationdir + "/log/";
	private static final String fext = ".txt";

	public static final String ID_MESSAGE = "MSG";
	public static final String ID_ALERT = "ALT";
	public static final String ID_ERROR = "ERR";

	public static void writeLog(String key, String msg) {
		try {
			File flogdir = new File(fdir);
			flogdir.mkdir();

			String flogpath = fdir + getCurrentDateStr() + fext;
			File flog = new File(flogpath);
			FileOutputStream fout = new FileOutputStream(flog, true);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fout);

			DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormatter.setLenient(false);
			Date today = new Date();
			String timestamp = dateFormatter.format(today);

			myOutWriter.append(timestamp + " #" + key + "# " + msg + "\r\n");
			myOutWriter.close();
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getCurrentDateStr() {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		dateFormatter.setLenient(false);
		Date today = new Date();
		String str = dateFormatter.format(today);
		return str;
	}
}
