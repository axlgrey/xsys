package com.xsys.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DbhConfig extends SQLiteOpenHelper {
	private static final String DB_URL = CfgMain.applicationdir + "/config.db";
	private static final int DB_VERSION = 1;

	private static final String TBL01_NAME = "appconfig";
	private static final Integer TBL01_WIDTH = 5;

	private static final String TBL01_COL01 = "cfg_id";
	private static final String TBL01_COL02 = "httpreq_url";
	private static final String TBL01_COL03 = "httpreq_username";
	private static final String TBL01_COL04 = "httpreq_password";
	private static final String TBL01_COL05 = "admin_password";

	public DbhConfig(Context con) {
		super(con, DB_URL, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + TBL01_NAME + " (";
		sql += TBL01_COL01 + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE ON CONFLICT ABORT, ";
		sql += TBL01_COL02 + " TEXT, ";
		sql += TBL01_COL03 + " TEXT, ";
		sql += TBL01_COL04 + " TEXT, ";
		sql += TBL01_COL05 + " TEXT " + ");";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TBL01_NAME + ";";
		db.execSQL(sql);
		onCreate(db);
	}

	// region CRUD Method Table 01
	public Long tbl01_insert(String col02, String col03, String col04, String col05) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(TBL01_COL02, col02);
		contentValues.put(TBL01_COL03, col03);
		contentValues.put(TBL01_COL04, col04);
		contentValues.put(TBL01_COL05, col05);
		Long n = db.insert(TBL01_NAME, null, contentValues);
		return n;
	}

	public Long tbl01_getsize() {
		SQLiteDatabase db = this.getReadableDatabase();
		Long n = DatabaseUtils.queryNumEntries(db, TBL01_NAME);
		return n;
	}

	public String[] tbl01_readrow(Integer col01) {
		String[] result = new String[TBL01_WIDTH];
		SQLiteDatabase db = this.getReadableDatabase();

		String[] column = { TBL01_COL01, TBL01_COL02, TBL01_COL03, TBL01_COL04, TBL01_COL05 };
		String selection = TBL01_COL01 + " = " + col01.toString() + " LIMIT 1";
		Cursor cursor = db.query(TBL01_NAME, column, selection, null, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (int i = 0; i < TBL01_WIDTH; i++) {
				result[i] = cursor.getString(i);
			}
		}
		return result;
	}

	public ArrayList<String[]> tbl01_readall() {
		ArrayList<String[]> array_list = new ArrayList<String[]>();
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "SELECT * FROM " + TBL01_NAME + " WHERE 1;";
		Cursor res = db.rawQuery(sql, null);
		res.moveToFirst();
		while (res.isAfterLast() == false) {
			String[] tarr = new String[TBL01_WIDTH];
			for (int i = 0; i < TBL01_WIDTH; i++) {
				tarr[i] = res.getString(i);
			}
			array_list.add(tarr);
			res.moveToNext();
		}
		return array_list;
	}

	public Integer tbl01_updaterow(Integer col01, String col02, String col03, String col04, String col05) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(TBL01_COL02, col02);
		contentValues.put(TBL01_COL03, col03);
		contentValues.put(TBL01_COL04, col04);
		contentValues.put(TBL01_COL05, col05);
		int n = db.update(TBL01_NAME, contentValues, TBL01_COL01 + " = ? ", new String[] { Integer.toString(col01) });
		return n;
	}

	public Integer tbl01_deleterow(Integer col01) {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete(TBL01_COL01, TBL01_COL01 + " = ? ", new String[] { Integer.toString(col01) });
	}

	// endregion

	public void tbl01_clear() {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "DROP TABLE IF EXISTS " + TBL01_NAME + ";";
		db.execSQL(sql);
		onCreate(db);
	}

}
