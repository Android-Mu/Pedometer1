package com.click369.pedometer.ui.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.click369.pedometer.R;
import com.click369.pedometer.data.db.PedometerDB;
import com.click369.pedometer.data.javabean.Music;
import com.click369.pedometer.data.javabean.Step;
import com.click369.pedometer.service.MusicService;
import com.click369.pedometer.ui.activity.MainActivity;
import com.click369.pedometer.ui.fragment.tools.TimeHelper;
/**
 * 历史统计——回调步数，音乐
 * @author Administrator
 *
 */
public class FragmentHistory extends Fragment implements OnClickListener {
	private AllAnimation ani;
	private View view;
	private ImageView iView;
	private TextView tView;
	private TextView number;
	private TextView ratio,mubiao;
	private ProgressBar progressBar;
	
	private DatePicker dPicker;
	private DatePickerDialog dialog;

	private Calendar calendar;
	private int year;
	private int month;
	private int day;
	private String date;
	private String date1;

	private PedometerDB pedometerDB;
	private Step step;
	private int count;
	private int progress;
	private int ratio1;
	private int mubiao1;
	
	private List<Object> musiclists = new ArrayList<Object>();
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private ImageButton play_pause, stop;
	private ActivityReceiver activityReceiver;
	public static final String CTL_ACTION = "com.click369.action.CTL_ACTION";
	public static final String UPDATE_ACTION = "com.click369.action.UPDATE_ACTION";
	private Intent intentservice;
	
	// 定义音乐的播放状态 ，0X11 代表停止 ，0x12代表播放,0x13代表暂停
	private int status = 0x11;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.history, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	@SuppressLint("SimpleDateFormat")
	private void init() {
		iView = (ImageView) view.findViewById(R.id.date_image);
		tView = (TextView) view.findViewById(R.id.date_text);
		number = (TextView) view.findViewById(R.id.history_number);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		ratio = (TextView) view.findViewById(R.id.ratio);
		mubiao= (TextView) view.findViewById(R.id.history_mubiao);
		ani = new AllAnimation();
		ani.setDuration(2000);
		iView.setOnClickListener(this);

		calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		pedometerDB = PedometerDB.getInstance(getActivity());

		date1 = TimeHelper.gettimeString();

		step = pedometerDB.loadSteps(MainActivity.myObjectId, date1);
		if (step == null) {
			step = new Step();
		}
		view.startAnimation(ani);
		number.setText(count + "");
		progressBar.setProgress(progress);
		ratio.setText(ratio1 + "%");
		//初始化音乐空间
		play_pause = (ImageButton) view.findViewById(R.id.play_button1);
		stop = (ImageButton) view.findViewById(R.id.stop_button2);
		play_pause.setOnClickListener(this);
		stop.setOnClickListener(this);
		musicList();
		activityReceiver = new ActivityReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATE_ACTION);
		getActivity().registerReceiver(activityReceiver, filter);
		intentservice = new Intent(getActivity(), MusicService.class);
		getActivity().startService(intentservice);
	}
	//广播内部类
	public class ActivityReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取Intent中的update消息，update代表播放状态
		 	int update = intent.getIntExtra("update", -1);
			switch (update) {
			case 0x11: {
				play_pause.setImageResource(R.drawable.star);
				status = 0x11;
				break;
			}
			// 控制系统进入播放状态
			case 0x12: {
				// 播放状态下设置使用按钮
				play_pause.setImageResource(R.drawable.pause);
				// 设置当前状态
				status = 0x12;
				break;
			}
			// 控制系统进入暂停状态
			case 0x13: {
				play_pause.setImageResource(R.drawable.star);
				status = 0x13;
				break;
			}
			}
		}

	}
	//播放列表
	private void musicList() {
		// 取得指定位置的文件设置显示到播放列表
		String[] music = new String[] { Media._ID, Media.DISPLAY_NAME,
				Media.TITLE, Media.DURATION, Media.ARTIST, Media.DATA };
		Cursor cursor = getActivity().getContentResolver().query(Media.EXTERNAL_CONTENT_URI,
				music, null, null, null);
		while (cursor.moveToNext()) {
			Music temp = new Music();
			temp.setFilename(cursor.getString(1));
			temp.setTitle(cursor.getString(2));
			temp.setDuration(cursor.getInt(3));
			temp.setArtist(cursor.getString(4));
			temp.setData(cursor.getString(5));
			musiclists.add(temp);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", cursor.getString(1));
			map.put("artist", cursor.getString(4));
			list.add(map);
		}

		ListView listview = (ListView) view.findViewById(R.id.musics_listview);
		SimpleAdapter adapter = new SimpleAdapter(
				getActivity(), 
				list,
				R.layout.musicsshow, 
				new String[] { "name", "artist" },
				new int[] { R.id.music_name, R.id.music_artist });
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int current, long id) {

				Intent intent=new Intent(CTL_ACTION);
				intent.putExtra("control", 5);
				intent.putExtra("current", current);
				getActivity().sendBroadcast(intent);
			}
		});
	}

	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(CTL_ACTION);
		switch (arg0.getId()) {
		case R.id.play_button1: {
			intent.putExtra("control", 1);
			break;
		}
		case R.id.stop_button2: {
			intent.putExtra("control", 2);
			break;
		}
		case R.id.xiayiqu_button1: {
			intent.putExtra("control", 3);
			break;
		}
		case R.id.shangyiqu_button1: {
			intent.putExtra("control", 4);
			break;
		}
		
		case R.id.date_image:{
		
		//日历对话框
		dialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {

			public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

				if (arg3 < 10 && arg2 < 10) {
					date = arg1 + "" + "0" + (arg2 + 1) + "0" + arg3;
				} else if (arg3 < 10) {
					date = arg1 + "" + (arg2 + 1) + "0" + arg3;
				} else if (arg2 < 10) {
					date = arg1 + "" + "0" + (arg2 + 1) + arg3;
				} else {
					date = arg1 + "" + (arg2 + 1) + "" + arg3;
				}
				if (date.equals(date1)) {
					tView.setText("今天");
				} else {
					tView.setText(arg1 + "/" + (arg2 + 1) + "/" + arg3);
				}
				queryStep();
			}
		}, year, month, day);
		dPicker = dialog.getDatePicker();
		dPicker.setSpinnersShown(false);
		dPicker.setCalendarViewShown(true);
		dPicker.setMaxDate(System.currentTimeMillis());
		dialog.show();
		break;}
		}
		getActivity().sendBroadcast(intent);
	}

	/**
	 * 查询选择日期所走的步数
	 */
	private void queryStep() {
		step = pedometerDB.loadSteps(MainActivity.myObjectId, date);
		if (step != null) {
			progressBar.setProgress(0);
			number.setText(count + "");
			view.startAnimation(ani);
			mubiao.setText(""+step.getMubiao());
		} else {
			progressBar.setProgress(0);
			number.setText(0 + "");
			ratio.setText("0%");
			mubiao.setText("0");
		}
	}

	private class AllAnimation extends Animation {
		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				count = (int) (step.getNumber() * interpolatedTime);
				progress = (int) ((step.getNumber() / (double) step.getMubiao())
						* progressBar.getMax() * interpolatedTime);
				ratio1 = (int) ((step.getNumber() / (double) step.getMubiao()) * 100 * interpolatedTime);
				
			} else {
				count = step.getNumber();
				progress = (int) ((step.getNumber() / (double) step.getMubiao()) * progressBar
						.getMax());
				ratio1 = (int) ((step.getNumber() / (double) step.getMubiao()) * 100);
			}
			view.postInvalidate();
			progressBar.setProgress(progress);
			number.setText(count + "");
			ratio.setText(ratio1 + "%");
			mubiao.setText(""+step.getMubiao());
		}
	}
}
