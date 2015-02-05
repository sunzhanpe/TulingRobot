package com.sunzhan.tuling;

//行不行呀，本地的！
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity implements HttpGetDataListener,OnClickListener
{
	private final String  KEY = "892c2243ef4baa95687031de4f701831";
//	private String url = "http://www.tuling123.com/openapi/api?key="+KEY+"&info=从西直门到东直门怎么走";
	private HttpData httpData;
	private ListView lv;
	private EditText sendText;
	private Button send_btn;
	private String content_str;
	private TextAdapter adapter;
	private String[] welcome_array;
	private double currentTime, oldTime = 0;
	
	private List<ListData> lists; 
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	private void initView(){
		lv = (ListView) findViewById(R.id.lv);
		sendText = (EditText) findViewById(R.id.sendText);
		send_btn = (Button) findViewById(R.id.send_btn);
		lists = new ArrayList<ListData>();
		send_btn.setOnClickListener(this);
		adapter = new TextAdapter(lists, this);
		lv.setAdapter(adapter);
		ListData listData;
		listData = new ListData(getRandomWelcomeTips(), ListData.RECEIVER,getTime());
		lists.add(listData);
	}
	
	private String getRandomWelcomeTips(){
		String welcome_tip = null;
		welcome_array = this.getResources().getStringArray(R.array.welcome_tips);
		int index = (int) (Math.random()*(welcome_array.length-1));
		welcome_tip = welcome_array[index];
		return welcome_tip;
	}
	@Override
	public void getDataUrl(String data)
	{
//		System.out.println(data);
		parseText(data);
	}
	
	public void parseText(String str)
	{
		try
		{
			JSONObject jb = new JSONObject(str);
//			System.out.println("code: "+ jb.getString("code"));
//			System.out.println("text: "+jb.getString("text"));
//			System.out.println("url: "+jb.getString("url"));
			ListData listData;
			listData = new ListData(jb.getString("text"),ListData.RECEIVER,getTime());
			lists.add(listData);
			adapter.notifyDataSetChanged();
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v)
	{
		getTime();
		content_str = sendText.getText().toString();
		sendText.setText("");
		String dropk = content_str.replace(" ", "");
		String droph = dropk.replace("\n", "");
		ListData listData;
		listData = new ListData(content_str,ListData.SEND,getTime());
		lists.add(listData);
		
		if(lists.size()>30){
			for(int i =0; i<10; i++){
				lists.remove(i);
			}
		}
		adapter.notifyDataSetChanged();
		httpData = (HttpData) new HttpData("http://www.tuling123.com/openapi/api?key="+KEY+"&info="+droph
				, this).execute();
	}
	
	public String getTime(){
		currentTime = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		Date curDate = new Date();
		String str = format.format(curDate); 
		if (currentTime - oldTime >= 5*60*1000)
		{
			oldTime = currentTime;
			return str;
		}else{
			return "";
		}
		
	}
}
