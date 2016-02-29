package com.zhangzhilin.mytip.ui;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.zhangzhilin.mytip.R;

public class NoteEditActivity extends AppCompatActivity{
	
	
	private Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_edit);
		toolbar=(Toolbar) findViewById(R.id.toolbar);
		toolbar.setLogo(R.drawable.icon_app);
		toolbar.setTitle(R.string.edit);
		setSupportActionBar(toolbar);
		
	}
}
