package com.zhangzhilin.mytip.ui;

import java.io.IOException;
import java.io.InputStream;

import com.zhangzhilin.mytip.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

public class NoteListActivity extends AppCompatActivity {
	
	
	public static int REQUEST_NEW_NOTE=0x100;
	public static int REQUEST_OPEN_NOTE=0x101;
	
	private Toolbar toolbar;
	private RecyclerView recyclerView;
	private SharedPreferences preferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_list);
		toolbar=(Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.tittle);
		toolbar.setLogo(R.drawable.icon_app);
		setSupportActionBar(toolbar);
		recyclerView=(RecyclerView) findViewById(R.id.notelist);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.search:
			
			break;
			
		case R.id.create_new_node:
			
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 创建新便签
	 */
	private void createNewNote(){
		Intent intent=new Intent();
		
	}
	
	
	
	/**
	 * app默认数据设置
	 */
	private void setDefaultAppinfo(){
		PreferenceManager.setDefaultValues(this,R.xml.default_setting,false);
		preferences=PreferenceManager.getDefaultSharedPreferences(this);		
		if(!preferences.getBoolean(getResources().getString(R.string.preference_key_init),false)){
			//加载初始数据
	        InputStream is=getResources().openRawResource(R.raw.init);
	        try {
	        	byte[] bs=new byte[is.available()];
				is.read(bs);
				//设置初始化数据
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//设置初始化状态为true
			preferences.edit().putBoolean(getResources().getString(R.string.preference_key_init),true).commit();
		}
	}
	
	
	class listAdapter extends RecyclerView.Adapter<ViewHolder>{

		@Override
		public int getItemCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void onBindViewHolder(ViewHolder arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	
}
