package com.click369.pedometer.ui.fragment;

import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.click369.pedometer.R;
import com.click369.pedometer.common.utils.HttpUtil;
import com.click369.pedometer.data.db.PedometerDB;
import com.click369.pedometer.data.javabean.Group;
import com.click369.pedometer.data.javabean.Step;
import com.click369.pedometer.data.javabean.User;
import com.click369.pedometer.data.javabean.Weather;
import com.click369.pedometer.service.StepDetector;
import com.click369.pedometer.service.StepService;
import com.click369.pedometer.ui.activity.MainActivity;
import com.click369.pedometer.ui.fragment.tools.TimeHelper;
import com.click369.pedometer.ui.view.CircleBar;
/**
 * 计步器主页——记步，定位，天气，卡路里
 * @author Administrator
 *
 */
public class FragmentPedometer extends Fragment implements OnClickListener {
	private View view;
	private CircleBar circleBar;
	private EditText editText;
	// 定义LocationClient对象
	public LocationClient mLocationClient = null;
	// 定义注册监听函数
	public BDLocationListener myListener = new MyLocationListener();
	private TextView dingwei_text;
	private int total_step = 0;
	private Thread thread;
	private int Type = 1;
	private int calories = 0;
	private ImageView sharekey,mubiao;
	private int step_length = 50;
	private int weight = 70;
	private Step step = null;
	private User user = null;
	private Group group = null;
	private Weather weather;
	private PedometerDB pedometerDB;
	private String test;
	private boolean flag = true;// 来判断第三个页面是否开启动画

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			total_step = StepDetector.CURRENT_SETP;
			if (Type == 1) {
				circleBar.setProgress(total_step, Type);
			} else if (Type == 2) {
				//公式从网上找的，体重*距离*1.036
				calories = (int) (weight * (total_step * step_length * 0.01 * 0.01)*1.036);
				circleBar.setProgress(calories, Type);
			} else if (Type == 3) {
				if (flag) {
					circleBar.startCustomAnimation();
					
					flag = false;
				}
				if (test != null || weather.getWeather() == null) {
					weather.setWeather("正在更新");
					weather.setPtime("");
					weather.setTemp1("");
					weather.setTemp2("");
					circleBar.setWeather(weather);
				} else {
					circleBar.setWeather(weather);
				}
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.pedometer, container, false);
		init();
		mThread();
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		saveDate();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		saveDate();
	}
	

	private void saveDate(){
		user = pedometerDB.loadUser(MainActivity.myObjectId);
		step = pedometerDB.loadSteps(MainActivity.myObjectId, TimeHelper.gettimeString());
		group = pedometerDB.loadGroup(user.getGroupId());
		user.setToday_step(StepDetector.CURRENT_SETP);
		pedometerDB.updateUser(user);
		if(group!=null){
		group.setTotal_number(group.getTotal_number()+(user.getToday_step()-step.getNumber()));
		pedometerDB.updateGroup(group);
		}
		step.setNumber(StepDetector.CURRENT_SETP);
		circleBar.setMax(step.getMubiao());
		pedometerDB.updateStep(step);
	}

	@SuppressLint("SimpleDateFormat")
	private void init() {
		/**
		 * 初始化定位
		 */
		//实例化定位组件
		dingwei_text=(TextView)view.findViewById(R.id.map_dingweidizhi);
		mLocationClient = new LocationClient(getActivity().getApplicationContext());
		// 注册监听函数
		mLocationClient.registerLocationListener(myListener);
		//设置定位参数
		initLocation();
		//开始定位
		mLocationClient.start();
		/**
		 * 初始化计步器
		 */
		pedometerDB = PedometerDB.getInstance(getActivity());
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(getActivity().CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()) {
			
		}else {
			Toast.makeText(getActivity(), "没有网络", Toast.LENGTH_LONG).show();
		}
		
		user = pedometerDB.loadUser(MainActivity.myObjectId);
		step = pedometerDB.loadSteps(MainActivity.myObjectId, TimeHelper.gettimeString());
		if (MainActivity.myObjectId != null) {
			step_length = user.getStep_length();
			weight = user.getWeight();
			StepDetector.SENSITIVITY = user.getSensitivity();
			StepDetector.CURRENT_SETP = user.getToday_step();
		} else {
			Toast.makeText(getActivity(), "this is my", Toast.LENGTH_SHORT)
					.show();
		}
		Intent intent = new Intent(getActivity(), StepService.class);
		getActivity().startService(intent);
		weather = new Weather();
		//设置目标步数
		mubiao= (ImageView) view.findViewById(R.id.set_mubiao);
		circleBar = (CircleBar) view.findViewById(R.id.progress_pedometer);
		
		circleBar.setProgress(StepDetector.CURRENT_SETP, 1);
		circleBar.startCustomAnimation();
		circleBar.setOnClickListener(this);
		mubiao.setOnClickListener(this);
		circleBar.setMax(step.getMubiao());
		//分享
		sharekey = (ImageView) view.findViewById(R.id.title_pedometer);
		ShareSDK.initSDK(getActivity());
		sharekey.setOnClickListener(this);
		
	}
	//配置定位服务参数
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		//每三秒发起一次定位请求
		option.setScanSpan(3000);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation bdLocation) {
			StringBuffer stringBuffer = new StringBuffer();
			// 得到地址
			String address = bdLocation.getAddrStr();
			String info=stringBuffer.append(address).toString();
			dingwei_text.setText(info);
		}
	}
	


	private void mThread() {
		if (thread == null) {

			thread = new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {
							//每隔0.5秒发送一次数据
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (StepService.flag) {
							Message msg = new Message();
							handler.sendMessage(msg);
						}
					}
				}
			});
			thread.start();
		}
	}

	private void queryFromServer(final String address) {
		new Thread(){
			public void run(){
				weather=HttpUtil.LoadWeather(address);
			}
		}.start();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.title_pedometer:
			String imagePath=null;
			OnekeyShare oks = new OnekeyShare();
			//设置截图
			View view=arg0.getRootView(); 
	        view.setDrawingCacheEnabled(true); 
	        view.buildDrawingCache();
	        Bitmap bitmap = view.getDrawingCache(); 
	        imagePath="/sdcard/"+TimeHelper.gettimename()+".png";
	        if(bitmap!= null){
	           try{
	               FileOutputStream out = new FileOutputStream(imagePath); 
	               bitmap.compress(Bitmap.CompressFormat.PNG,100,out); 
	         System.out.println("file"+ imagePath + "outputdone."); 
	             }catch(Exception e) {
	               e.printStackTrace(); 
	             } 
	           }
			oks.setTitle("哈喽，朋友们！");
			oks.setText("我今天已经走了" + total_step + "步");
			
			oks.setImagePath(imagePath);
			oks.setSilent(false);
			oks.disableSSOWhenAuthorize();
			oks.setDialogMode();
			// 显示
			oks.show(getActivity());
			break;
		case R.id.progress_pedometer:
			if (Type == 1) {
				Type = 2;
			} else if (Type == 2) {
				String address = "xian";
				queryFromServer(address);
				flag = true;
				Type = 3;
			} else if (Type == 3) {
				Type = 1;
			}
			break;
		case R.id.set_mubiao:
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			editText = new EditText(getActivity());
            editText.setSingleLine(true);
            editText.setHint("请输入目标步数");
            //设置只能输入数字
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            dialog.setView(editText);
            dialog.setTitle("今日目标");
            dialog.setPositiveButton("确认",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        	circleBar.setMax(Integer.parseInt(editText.getText().toString()));
                        	step.setMubiao(Integer.parseInt(editText.getText().toString()));
                        	circleBar.setProgress(step.getNumber(), Type);
                            pedometerDB.updateStep(step);
                        }
                    });
            dialog.show();
			
			break;
		default:
			break;
		}
	}
	
	

}
