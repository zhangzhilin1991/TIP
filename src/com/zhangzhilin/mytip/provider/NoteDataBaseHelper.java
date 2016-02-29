package com.zhangzhilin.mytip.provider;

import com.zhangzhilin.mytip.R.string;
import com.zhangzhilin.mytip.provider.Notes.DataColums;
import com.zhangzhilin.mytip.provider.Notes.DataConstants;
import com.zhangzhilin.mytip.provider.Notes.NoteColoums;

import android.R.mipmap;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NoteDataBaseHelper extends SQLiteOpenHelper {

	private static String TAG = "NoteDataBaseHelper";

	private static final String DBNAME = "note.db";
	private static final int DBVERSION = 1;

	public interface TABLE {
		public static final String NOTE = "note";
		public static final String DATA = "data";
	}

	private static NoteDataBaseHelper mInstance = null;

	static synchronized NoteDataBaseHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new NoteDataBaseHelper(context);
		}
		return mInstance;
	}

	private NoteDataBaseHelper(Context context) {
		super(context, DBNAME, null, DBVERSION);
		// TODO Auto-generated constructor stub
	}

	private static final String CREATE_NOTE_TABLE_SQL = "create table "
			+ TABLE.NOTE + "(" + NoteColoums.ID + "INTEGER PRIMARY KEY,"
			+ NoteColoums.PARENT_ID + "INTEGER NOT NULL DEFAULT 0,"
			+ NoteColoums.ALERTED_DATE + "INTEGER NOT NULL DEFAULT 0,"
			+ NoteColoums.BG_COLOR_ID + "INTEGER NOT NULL DEFAULT 0,"
			+ NoteColoums.CREATE_DATE
			+ "INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),"
			+ NoteColoums.HAS_ATTACHEMENT + "INTEGER NOT NULL DEFAULT 0,"
			+ NoteColoums.MODIFIED_DATE
			+ "INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),"
			+ NoteColoums.NOTES_COUNT + "INTEGER NOT NULL DEFAULT 0,"
			+ NoteColoums.SNIPPET + "TEXT NOT NULL DEFAULT ''"
			+ NoteColoums.TYPE + "INTEGER NOT NULL DEFAULT 0,"
			+ NoteColoums.WIDGET_ID + "INTEGER NOT NULL DEFAULT 0,"
			+ NoteColoums.WIDGET_TYPE + "INTEGER NOT NULL DEFAULT -1,"
			+ NoteColoums.SYNC_ID + "INTEGER NOT NULL DEFAULT 0,"
			+ NoteColoums.LOACAL_MODIFIED + "INTEGER NOT NULL DEFAULT 0,"
			+ NoteColoums.ORIGENIAL_PARENT_ID + "INTEGER NOT NULL DEFAULT 0,"
			+ NoteColoums.GTASK_ID + "TEXT NOT NULL DEFAULT '',"
			+ NoteColoums.VERION + "INTEGER NOT NULL DEFAULT 0," + ")";

	private static final String CREATE_DATA_TABLE_SQL = "create table"
			+ TABLE.DATA + "(" + DataColums.ID + "INTEGER PRIMARY KEY,"
			+ DataColums.MIME_TYPE + "TEXT NOT NULL," + DataColums.NOTE_ID
			+ "INTEGER NOT NULL DEFAULT 0," + DataColums.CREATED_DATE
			+ "INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),"
			+ DataColums.MODIFIED_DATE
			+ "INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),"
			+ DataColums.CONTENT + "TEXT NOT NULL DEFAULT '',"
			+ DataColums.DATA1 + "INTEGER," + DataColums.DATA2 + "INTEGER,"
			+ DataColums.DATA3 + "TEXT NOT NULL DEFAULT ''," + DataColums.DATA4
			+ "TEXT NOT NULL DEFAULT ''," + DataColums.DATA5
			+ "TEXT NOT NULL DEFAULT ''," + ")";

	private static final String CREATE_DATA_NOTE_ID_INDEX_SQL = ""
			+ "CREATE INDEX IF NOT EXISTS note_id_index ON " + TABLE.DATA + "("
			+ DataColums.NOTE_ID + ")";

	/**
	 * Increase folder's note count when move note to the folder
	 */
	private static final String NOTE_INCREASE_FOLDER_COUNT_ON_UPDATE_TRAIGGER = "CREATE TRIGGER increase_folder_count_on_update "
			+ " AFTER UPDATE OF "
			+ NoteColoums.PARENT_ID
			+ " ON "
			+ TABLE.NOTE
			+ ""
			+ " BEGIN "
			+ " UPDATE"
			+ TABLE.NOTE
			+ " SET "
			+ NoteColoums.NOTES_COUNT
			+ " = "
			+ NoteColoums.NOTES_COUNT
			+ " + 1 "
			+ "WHERE"
			+ NoteColoums.ID
			+ " = new."
			+ NoteColoums.PARENT_ID + ";" + " END ";

	/**
	 * Decrease folder's note count when move note from folder
	 */
	private static final String NOTE_DECREASE_FOLDER_COUNT_ON_UPDATE_TRAIGGER = "CREATE TRIGGER decrease_folder_count_on_update "
			+ " AFTER UPDATE OF "
			+ NoteColoums.PARENT_ID
			+ " ON"
			+ TABLE.NOTE
			+ " BEGIN "
			+ " UPDATE"
			+ TABLE.NOTE
			+ " SET "
			+ NoteColoums.NOTES_COUNT
			+ " = "
			+ NoteColoums.NOTES_COUNT
			+ " - 1 "
			+ "WHERE"
			+ NoteColoums.ID
			+ " = old."
			+ NoteColoums.PARENT_ID
			+ " and "
			+ NoteColoums.NOTES_COUNT
			+ ">0"
			+ ";" + " END ";

	private static final String NOTE_INCREASE_FOLDER_COUNT_ON_INSERT_TRAIGGER = "CREATE TRIGGER increase_folder_count_on_insert "
			+ " AFTER INSERT ON "
			+ NoteColoums.PARENT_ID
			+ " ON "
			+ TABLE.NOTE
			+ ""
			+ " BEGIN "
			+ " UPDATE"
			+ TABLE.NOTE
			+ " SET "
			+ NoteColoums.NOTES_COUNT
			+ " = "
			+ NoteColoums.NOTES_COUNT
			+ " + 1 "
			+ "WHERE"
			+ NoteColoums.ID
			+ " = new."
			+ NoteColoums.PARENT_ID + ";" + " END ";

	/**
	 * Decrease folder's note count when move note from folder
	 */
	private static final String NOTE_DECREASE_FOLDER_COUNT_ON_DELETE_TRAIGGER = "CREATE TRIGGER decrease_folder_count_on_update "
			+ " AFTER DELETE ON "
			+ NoteColoums.PARENT_ID
			+ " ON"
			+ TABLE.NOTE
			+ " BEGIN "
			+ " UPDATE"
			+ TABLE.NOTE
			+ " SET "
			+ NoteColoums.NOTES_COUNT
			+ " = "
			+ NoteColoums.NOTES_COUNT
			+ " - 1 "
			+ "WHERE"
			+ NoteColoums.ID
			+ " = old."
			+ NoteColoums.PARENT_ID
			+ " and "
			+ NoteColoums.NOTES_COUNT
			+ ">0"
			+ ";" + " END ";

	/**
	 * Update note's content when insert data with type
	 */
	private static final String DATA_UPDATE_NOTE_CONTENT_ON_INSERT_TRIGGER = "CREATE TRIGGER update_note_content_on_insert"
			+ "AFTER INSERT ON"
			+ TABLE.DATA
			+ " WHEN NEW."
			+ DataColums.MIME_TYPE
			+ " = "
			+ DataConstants.NOTE
			+ "'"
			+ " BEGIN "
			+ " UPDATE "
			+ TABLE.NOTE
			+ " SET "
			+ NoteColoums.SNIPPET
			+ " = NEW."
			+ DataColums.CONTENT
			+ " WHERE "
			+ NoteColoums.ID + " =new." + DataColums.NOTE_ID + ";" + " END ";

	private static final String DATA_UPDATE_NOTE_CONTENT_ON_UPDATE_TRIGGER = "CREATE TRIGGER update_note_content_on_insert"
			+ "AFTER UPDATE ON"
			+ TABLE.DATA
			+ " WHEN OLD."
			+ DataColums.MIME_TYPE
			+ " = "
			+ DataConstants.NOTE
			+ "'"
			+ " BEGIN "
			+ " UPDATE "
			+ TABLE.NOTE
			+ " SET "
			+ NoteColoums.SNIPPET
			+ " = NEW."
			+ DataColums.CONTENT
			+ " WHERE "
			+ NoteColoums.ID + " =new." + DataColums.NOTE_ID + ";" + " END ";

	private static final String DATA_UPDATE_NOTE_CONTENT_ON_DELETE_TRIGGER = "CREATE TRIGGER update_note_content_on_delete"
			+ " AFTER DELETE ON "
			+ TABLE.DATA
			+ "when old."
			+ DataColums.MIME_TYPE
			+ " = "
			+ DataConstants.NOTE
			+ ","
			+ " BEGIN "
			+ " UPDATE "
			+ TABLE.NOTE
			+ " SET "
			+ NoteColoums.SNIPPET
			+ " ='' "
			+ " WHERE "
			+ NoteColoums.ID
			+ " =.old" + DataColums.NOTE_ID + ";" + "end";

	private static final String NOTE_DELETE_DATA_ON_DELETE_TRIGGER = "CREATE TRIGGER delete_data_on_delete "
			+ " AFTER DELETE ON "
			+ TABLE.NOTE
			+ " BEGIN "
			+ " DELETE FROM "
			+ TABLE.DATA
			+ " WHERE "
			+ DataColums.NOTE_ID
			+ "=old."
			+ NoteColoums.ID + ";" + " END ";

	private static final String FOLDER_DELETE_NOTES_ON_DELELET_TRIGGER = "CREATE TRIGGER folder_delete_notes_on_delete"
			+ " AFTER DELETE ON "
			+ TABLE.NOTE
			+ " BEGIN "
			+ " DELETE FROM "
			+ TABLE.NOTE
			+ " WHERE "
			+ NoteColoums.PARENT_ID
			+ " =old."
			+ NoteColoums.ID + ";" + " END ";

	private static final String FOLDER_MOVE_NOTES_ON_TRASH_TRIGGER = "CREATE TRIGGER folder_move_notes_on_trash"
			+ "AFTER UPDATE ON"
			+ TABLE.NOTE
			+ " WHEN new."
			+ NoteColoums.PARENT_ID
			+ " = "
			+ Notes.ID_TRASH_FOLDER
			+ " BEGIN "
			+ " UPDATE "
			+ TABLE.NOTE
			+ " SET "
			+ NoteColoums.PARENT_ID
			+ " = "
			+ Notes.ID_TRASH_FOLDER
			+ " WHERE "
			+ NoteColoums.PARENT_ID
			+ " =old." + NoteColoums.ID + ";" + " end";

	public void createTableNote(SQLiteDatabase db) {
		db.execSQL(CREATE_NOTE_TABLE_SQL);
		reCreateNoteTableTrigger(db);
		createSystemFolder(db);
		Log.d(TAG, " note table has inited");
	}
	
	
	

	private void reCreateNoteTableTrigger(SQLiteDatabase db) {
		// 1.DELETE
		db.execSQL("DROP TRIGGER IF EXISTS increase_folder_count_on_update");
		db.execSQL("DROP TRIGGER IF EXISTS decrease_folder_count_on_update");
		db.execSQL("DROP TRIGGER IF EXISTS decrease_folder_count_on_delete");
		db.execSQL("DROP TRIGGER IF EXISTS delete_data_on_delete");
		db.execSQL("DROP TRIGGER IF EXISTS increase_folder_count_on_insert");
		db.execSQL("DROP TRIGGER IF EXISTS folder_delete_notes_on_delete");
		db.execSQL("DROP TRIGGER IF EXISTS  folder_move_notes_on_trash");

		// 2.create
		db.execSQL(NOTE_INCREASE_FOLDER_COUNT_ON_UPDATE_TRAIGGER);
		db.execSQL(NOTE_DECREASE_FOLDER_COUNT_ON_UPDATE_TRAIGGER);
		db.execSQL(NOTE_DECREASE_FOLDER_COUNT_ON_DELETE_TRAIGGER);
		db.execSQL(NOTE_DELETE_DATA_ON_DELETE_TRIGGER);
		db.execSQL(NOTE_INCREASE_FOLDER_COUNT_ON_INSERT_TRAIGGER);
		db.execSQL(FOLDER_DELETE_NOTES_ON_DELELET_TRIGGER);
		db.execSQL(FOLDER_MOVE_NOTES_ON_TRASH_TRIGGER);

	}

	private void createSystemFolder(SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		
		/**
		 * call record folder
		 */
		values.put(NoteColoums.ID, Notes.ID_CALL_RECORD_FOLDER);
		values.put(NoteColoums.TYPE, Notes.TYPE_STYSTEM);
		db.insert(TABLE.NOTE, null, values);

		/**
		 * tempeory folder
		 */
		values.clear();
		values.put(NoteColoums.ID, Notes.ID_ROOT_FOLDER);
		values.put(NoteColoums.TYPE, Notes.TYPE_STYSTEM);
		db.insert(TABLE.NOTE, null, values);

		/**
		 * root folder
		 */
		values.clear();
		values.put(NoteColoums.ID, Notes.ID_TEMPORY_FOLDER);
		values.put(NoteColoums.TYPE, Notes.TYPE_STYSTEM);
		db.insert(TABLE.NOTE, null, values);

		/**
		 * trash folder
		 */
		values.clear();
		values.put(NoteColoums.ID, Notes.ID_TRASH_FOLDER);
		values.put(NoteColoums.TYPE, Notes.TYPE_STYSTEM);
		db.insert(TABLE.NOTE, null, values);

	}
	
	public void createDataTable(SQLiteDatabase db){
		db.execSQL(CREATE_DATA_TABLE_SQL);
		reCreateDataTableTrigger(db);
		db.execSQL(CREATE_DATA_NOTE_ID_INDEX_SQL);
		Log.d(TAG," data table has ben created!");
	}
	
	private void reCreateDataTableTrigger(SQLiteDatabase db){
		//DELETE
		db.execSQL("DROP TRIGGER IF EXISTS update_note_content_on_delete");
		db.execSQL("DROP TRIGGER IF EXISTS update_note_content_on_insert");
		db.execSQL("DROP TRIGGER IF EXISTS update_note_content_on_update");
		
		//create
		db.execSQL(DATA_UPDATE_NOTE_CONTENT_ON_DELETE_TRIGGER);
		db.execSQL(DATA_UPDATE_NOTE_CONTENT_ON_INSERT_TRIGGER);
		db.execSQL(DATA_UPDATE_NOTE_CONTENT_ON_UPDATE_TRIGGER);
	}
	
	
	
	
	
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createDataTable(db);
		createTableNote(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		boolean reCreateTriggers=false;
		boolean skipV2=false;
		
		if(oldVersion==1){
			upgrateToV2(db);
			skipV2=true;// this upgrade including the upgrade from v2 to v3
			oldVersion++;
		}
		if(oldVersion==2 && !skipV2){
			upgrateToV3(db);
			reCreateTriggers=true;
			oldVersion++;
		}
		if(oldVersion==3){
			upgrateToV4(db);
			oldVersion++;
		}
		
		if(reCreateTriggers){
			reCreateDataTableTrigger(db);
			reCreateNoteTableTrigger(db);
		}
		
		if(oldVersion!=newVersion){
		throw new IllegalStateException("Upgrade notes database to version " + newVersion
                + "fails");
		}
	}
	
	private void upgrateToV2(SQLiteDatabase db){
		//DROP
		db.execSQL("DROP TABLE IF　EXISTS"+TABLE.NOTE);
		db.execSQL("DROP TABLE IF　EXISTS"+TABLE.DATA);
		
		//create
		createDataTable(db);
		createTableNote(db);
		
	}
	
	private void upgrateToV3(SQLiteDatabase db){
		//DROP
		db.execSQL("DROP TRIGGER IF EXISTS update_note_modifity_date_on_delete");
		db.execSQL("DROP TRIGGER IF EXISTS update_note_modifity_date_on_insert");
		db.execSQL("DROP TRIGGER IF EXISTS update_note_modifity_date_on_update");
		
		//ADD COLUM FOR GTASK ID
		db.execSQL("ALTER TABLE"+TABLE.NOTE+" ADD　COLUMN　"+NoteColoums.GTASK_ID+" TEXT　NOT　NULL DEFAULT '' ");
		
		//ADD TRASH SYSTEM FOLDER
		ContentValues values=new ContentValues();
		values.put(NoteColoums.ID,Notes.ID_TRASH_FOLDER);
		values.put(NoteColoums.TYPE,Notes.TYPE_STYSTEM);
		db.insert(TABLE.NOTE,null,values);
	}
	
	private void upgrateToV4(SQLiteDatabase db){
		
		db.execSQL("ALTER TABLE "+TABLE.NOTE+" ADD COLUMN "+NoteColoums.VERION+" INTEGER NOT NULL DEFAULT 0");
		
	}

}
