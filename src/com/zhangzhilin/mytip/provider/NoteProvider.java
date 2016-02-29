package com.zhangzhilin.mytip.provider;

import com.zhangzhilin.mytip.R;
import com.zhangzhilin.mytip.provider.NoteDataBaseHelper.TABLE;
import com.zhangzhilin.mytip.provider.Notes.DataColums;
import com.zhangzhilin.mytip.provider.Notes.NoteColoums;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;

public class NoteProvider extends ContentProvider {

	private static final UriMatcher mMATCHER;

	private static final String TAG = "NoteProvider";

	private NoteDataBaseHelper mHelper;

	private static final int URI_NOTE = 1;
	private static final int URI_NOTE_ITEM = 2;
	private static final int URI_DATA = 3;
	private static final int URI_DATA_ITEM = 4;
	private static final int URI_SEARCH = 5;
	private static final int URI_SEARCH_SUGGEST = 6;

	static {
		mMATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		mMATCHER.addURI(Notes.AUTHOURITY, "note", URI_NOTE);
		mMATCHER.addURI(Notes.AUTHOURITY, "note/#", URI_NOTE_ITEM);
		mMATCHER.addURI(Notes.AUTHOURITY, "data", URI_DATA);
		mMATCHER.addURI(Notes.AUTHOURITY, "data/#", URI_DATA_ITEM);
		mMATCHER.addURI(Notes.AUTHOURITY, SearchManager.SUGGEST_URI_PATH_QUERY,
				URI_SEARCH);
		mMATCHER.addURI(Notes.AUTHOURITY, SearchManager.SUGGEST_URI_PATH_QUERY
				+ "/*", URI_SEARCH_SUGGEST);
	}

	private static final String NOTE_SEARCH_PROJECTION = NoteColoums.ID + ","
			+ NoteColoums.ID + " AS "
			+ SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA + ","
			+ "TRIM(REPLACE(" + NoteColoums.SNIPPET + ",x'oA','')) AS "
			+ SearchManager.SUGGEST_COLUMN_TEXT_1 + "," + "TRIM(REPLACE("
			+ NoteColoums.SNIPPET + ",x'oA','')) AS "
			+ SearchManager.SUGGEST_COLUMN_TEXT_2 + ","
			+ R.drawable.search_result + " AS "
			+ SearchManager.SUGGEST_COLUMN_ICON_1 + " , " + "'"
			+ Intent.ACTION_VIEW + "' AS "
			+ SearchManager.SUGGEST_COLUMN_INTENT_ACTION + " , " + "'"
			+ Notes.TextNote.CONTENT_TYPE + "' AS"
			+ SearchManager.SUGGEST_COLUMN_INTENT_DATA;

	private static final String NOTES_SIAPPET_SEARCH_QUERY = "SELECT"
			+ NOTE_SEARCH_PROJECTION + " FROM " + TABLE.NOTE + " WHERE "
			+ NoteColoums.SNIPPET + " like ? " + " AND "
			+ NoteColoums.PARENT_ID + "<>" + Notes.ID_TRASH_FOLDER + " AND "
			+ NoteColoums.TYPE + " = " + Notes.TYPE_NOTE;

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mHelper = NoteDataBaseHelper.getInstance(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		Cursor c = null;
		SQLiteDatabase db = mHelper.getReadableDatabase();
		String id = null;
		switch (mMATCHER.match(uri)) {
		case URI_NOTE:
			c = db.query(TABLE.NOTE, projection, selection, selectionArgs,
					null, null, sortOrder);
			break;
		case URI_NOTE_ITEM:
			id = uri.getPathSegments().get(1);
			c = db.query(TABLE.NOTE, projection, NoteColoums.ID + " = " + id
					+ parseSelection(selection), selectionArgs, null, null,
					sortOrder);

			break;

		case URI_DATA:
			c = db.query(TABLE.DATA, projection, selection, selectionArgs,
					null, null, sortOrder);
			break;

		case URI_DATA_ITEM:
			id = uri.getPathSegments().get(1);
			c = db.query(TABLE.DATA, projection, DataColums.ID + " = " + id
					+ parseSelection(selection), selectionArgs, null, null,
					sortOrder);

			break;

		case URI_SEARCH:
		case URI_SEARCH_SUGGEST:
			if (sortOrder != null && projection != null) {
				throw new IllegalArgumentException(
						"do not specify sortOrder ,Selection, "
								+ ",SelectionArgs,or Projection with this query ");
			}
			String searchString = null;
			if (mMATCHER.match(uri) == URI_SEARCH_SUGGEST) {
				if (uri.getPathSegments().size() > 1) {
					searchString = uri.getPathSegments().get(1);
				}
			} else {
				searchString = uri.getQueryParameter("pattern");
			}
			if (TextUtils.isEmpty(searchString)) {
				return null;
			}
			try {
				searchString = String.format("%%%S%%", searchString);
				c = db.rawQuery(NOTES_SIAPPET_SEARCH_QUERY,
						new String[] { searchString });
			} catch (Exception e) {
				// TODO: handle exception
				Log.e(TAG, "get error:" + e.getMessage());
			}
			break;

		default:
			throw new IllegalArgumentException("unknow uri" + uri);

		}
		if (c != null) {
			c.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return c;
	}

	private String parseSelection(String selection) {
		return (!TextUtils.isEmpty(selection)) ? " AND (" + selection + ")"
				: "";
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = mHelper.getWritableDatabase();
		//
		long dataId = 0, noteId = 0, insertId = 0;
		switch (mMATCHER.match(uri)) {
		case URI_NOTE:
			insertId = noteId = db.insert(TABLE.NOTE, null, values);

			break;

		case URI_DATA:
			if (values.containsKey(DataColums.NOTE_ID)) {
				noteId = values.getAsLong(DataColums.NOTE_ID);
			} else {
				Log.d(TAG, "wrong data format with " + values.toString());
			}
			insertId = dataId = db.insert(TABLE.DATA, null, values);

			break;

		default:
			throw new IllegalArgumentException("unknow uri" + uri);

		}
		// NOTIFITY the note uri
		if (noteId > 0) {
			getContext().getContentResolver().notifyChange(
					ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, noteId),
					null);
		}
		// NOTIDITY THE DATA URI
		if (dataId > 0) {

			getContext().getContentResolver().notifyChange(
					ContentUris.withAppendedId(Notes.CONTENT_DATA_URI, dataId),
					null);
		}
		return ContentUris.withAppendedId(uri, insertId);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int count = 0;
		String id = null;
		SQLiteDatabase db = mHelper.getWritableDatabase();
		boolean deleteData = false;
		switch (mMATCHER.match(uri)) {
		case URI_NOTE:
			selection = "(" + selection + ")AND" + NoteColoums.ID + ">0";
			count = db.delete(TABLE.NOTE, selection, selectionArgs);
			break;

		case URI_NOTE_ITEM:
			id = uri.getPathSegments().get(1);
			long noteId = Long.valueOf(id);
			if (noteId < 0) {
				break;
			}
			count = db.delete(TABLE.NOTE, NoteColoums.ID + "=" + id
					+ parseSelection(selection), selectionArgs);
			break;

		case URI_DATA:
			count = db.delete(TABLE.DATA, selection, selectionArgs);
			deleteData = true;
			break;

		case URI_DATA_ITEM:
			id = uri.getPathSegments().get(1);
			count = db.delete(TABLE.DATA, DataColums.ID + "=" + id + selection,
					selectionArgs);
			deleteData = true;
			break;

		default:
			throw new IllegalArgumentException("unkow uri:" + uri);
		}
		if (count > 0) {
			if (deleteData) {
				getContext().getContentResolver().notifyChange(
						Notes.CONTENT_NOTE_URI, null);
			}
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		int count=0;
		String id=null;
		SQLiteDatabase db=mHelper.getWritableDatabase();
		boolean updateData=false;
		switch (mMATCHER.match(uri)) {
		case URI_NOTE:
			
			
			break;
			
		case URI_NOTE_ITEM:
			
			
			break;
			
		case URI_DATA:
			
			break;
			
		case URI_DATA_ITEM:
			
			break;

		default:
			break;
		}
		
		return 0;
	}
	
	private void increaseNoteVersion(long id,String selection,String[] selectionArgs){
		StringBuilder sql=new StringBuilder(120);
		sql.append("UPDATE ");
		sql.append(TABLE.NOTE);
		sql.append(" SET ");
		sql.append(NoteColoums.VERION);
		sql.append(" = "+NoteColoums.VERION+"+1 ");
		
		if(id>0 || !TextUtils.isEmpty(selection)){
			sql.append(" WHERE ");
		}
		if(id>0){
			sql.append(NoteColoums.ID+" = "+String.valueOf(id));
		}
		if(!TextUtils.isEmpty(selection)){
			
			String selectString=id>0?parseSelection(selection):selection;
			for(String args:selectionArgs){
				selectString=selectString.replaceFirst("\\?",args);
			}
			sql.append(selectString);
		}
		mHelper.getWritableDatabase().execSQL(sql.toString());
	}
	
}
