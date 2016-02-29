package com.zhangzhilin.mytip.provider;

import android.net.Uri;

public class Notes {
	
	public static final String AUTHOURITY="myTip";
	
	public static final int TYPE_NOTE=0;
	public static final int TYPE_FOLDER=1;
	public static final int TYPE_STYSTEM=2;
	
	public static final int ID_ROOT_FOLDER=0;
	public static final int ID_TEMPORY_FOLDER=-1;
	public static final int ID_CALL_RECORD_FOLDER=-2;
	public static final int ID_TRASH_FOLDER=-3;
	
	public static final String INTENT_EXTRA_ALERT_DATE="net.mytip.notes.alert.date";
	public static final String INTENT_EXTRA_BACKGROUND_ID="net.mytip.notes.background_color_id";
	public static final String INTENT_EXTRA_WIDGET_ID="net.mytip.notes.widget_id";
	public static final String INTENT_EXTRA_WIDGET_TYPE="net.mytip.notes.widget_type";
	public static final String INTENT_EXTRA_FOLDER_ID="net.mytip.notes.folder_id";
	public static final String INTENT_EXTRA_CALL_DATE="net.mytip.notes.call_date";
	
	public static final int TYPE_WIDGET_INVALID=-1;
	public static final int TYPE_WIDGET_2X=0;
	public static final int TYPE_WIDGET_4x=1;
	
	public static class DataConstants{
		public static final String NOTE=TextNote.CONTENT_ITEM_TYPE;
		
		public static final String CALL_NOTE=CallNote.CONTENT_ITEM_TYPE;
		
	}
	
	public static final Uri CONTENT_NOTE_URI=Uri.parse("content://"+AUTHOURITY+"/note");
	
	public static final Uri CONTENT_DATA_URI=Uri.parse("content://"+AUTHOURITY+"/data");
	
	public interface NoteColoums{
		/**
		 * unique id for a row
		 */
		public static final String ID="_id";
		/**
		 * parent id for note or folder
		 */
		public static final String PARENT_ID="parent_id";
		/**
		 * create date for note or folder
		 */
		public static final String CREATE_DATE="created_date";
		/**
		 * latest modifity date
		 */
		public static final String MODIFIED_DATE="modified_date";
		/**
		 * alerm date
		 */
		public static final String ALERTED_DATE="alerted_date";
		/**
		 * folder's name or note content
		 */
		public static final String SNIPPET="snippet";
		/**
		 * note's widget id
		 */
		public static final String WIDGET_ID="widget_id";
		/**
		 * note's widget type
		 */
		public static final String WIDGET_TYPE="widget_type";
		/**
		 * note's background color
		 */
		public static final String BG_COLOR_ID="bg_color_id";
		/**
		 * mutil-media note attachment
		 */
		public static final String HAS_ATTACHEMENT="has_attachment";
		/**
		 * folder's count of notes
		 */
		public static final String NOTES_COUNT="notes_count";
		/**
		 * the file type:folder or note 
		 */
		public static final String TYPE="type";
		/**
		 * the last sync id
		 */
		public static final String SYNC_ID="sync_id";
		/**
		 * sign to local modified or not
		 */
		public static final String LOACAL_MODIFIED="local_mofidified";
		/**
		 * orignal parent id before moving into tempory folder
		 */
		public static final String ORIGENIAL_PARENT_ID="original_parent_id";
		/**
		 * gtask id
		 */
		public static final String GTASK_ID="gtask_id";
		/**
		 * the version code
		 */
		public static final String VERION="version";
		
	}
	
	public interface DataColums {
		/**
		 * unique id
		 */
		public static final String ID="_id";
		/**
		 * mime type
		 */
		public static final String MIME_TYPE="mime_type";
		/**
		 * reference id to data
		 */
		public static final String NOTE_ID="note_id";
		/**
		 * created date
		 */
		public static final String CREATED_DATE="created_date";
		/**
		 * modified date
		 */
		public static final String MODIFIED_DATE="modified_date";
		/**
		 * data content
		 */
		public static final String CONTENT="content";
		/**
		 * generic data colum  ,integer data type
		 */
		public static final String DATA1="data1";
		/**
		 * generic data colum  ,integer data type
		 */
		public static final String DATA2="data2";
		/**
		 * generic data colum  ,text data type
		 */
		public static final String DATA3="data3";
		/**
		 * generic data colum  ,text data type
		 */
		public static final String DATA4="data4";
		/**
		 * generic data colum  ,text data type
		 */
		public static final String DATA5="data5";
	}
	
	public static final class TextNote implements DataColums{
		
		/**
		 * mode to indicate mode in check list or not
		 * 1 :check list mode 0:normal mode
		 */
		public static final String MODE="DATA1";
		
		public static final int MODE_CHECK_LIST=1;
		
		public static final String CONTENT_TYPE="vnd.android.curdor.dir/text_note";
		
		public static final String CONTENT_ITEM_TYPE="vnd.android.curdor.item/text_note";
		
		public static final Uri CONTENT_URI=Uri.parse("content://"+AUTHOURITY+"/text_note");
		
	}
	
	public static final class CallNote implements DataColums{
		/**
		 * call date
		 */
		public static final String CALL_DATE=DATA1;
		
		/**
		 * phone number
		 */
		public static final String PHONE_NUMBER=DATA3;
		
        public static final String CONTENT_TYPE="vnd.android.curdor.dir/call_note";
		
		public static final String CONTENT_ITEM_TYPE="vnd.android.curdor.item/call_note";
		
		public static final Uri CONTENT_URI=Uri.parse("content://"+AUTHOURITY+"/call_note");
		
	}
	
	
	
}
