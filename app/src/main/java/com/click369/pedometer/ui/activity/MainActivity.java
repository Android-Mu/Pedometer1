package com.click369.pedometer.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.click369.pedometer.R;
import com.click369.pedometer.data.db.PedometerDB;
import com.click369.pedometer.ui.fragment.FragmentAdapter;
import com.click369.pedometer.ui.fragment.FragmentAnalysis;
import com.click369.pedometer.ui.fragment.FragmentHistory;
import com.click369.pedometer.ui.fragment.FragmentPK;
import com.click369.pedometer.ui.fragment.FragmentPedometer;
import com.click369.pedometer.ui.fragment.FragmentSet;

/**
 * 主页面
 */
public class MainActivity extends FragmentActivity {
    public static String myObjectId = null;
    private RadioGroup rgs;//用来切换各个页面
    private RadioButton btn1;//如果是用户第一次进入这个app没有进行注册，则会跳转到注册页面
    public List<Fragment> fragments = new ArrayList<Fragment>();//将5个fragment添加到这个list里
    private PedometerDB pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.page_mian);
        btn1 = (RadioButton) findViewById(R.id.btn5);
        pd = PedometerDB.getInstance(this);
        //判断用户是否进行注册过，如果没有进行注册则选中注册页面的radiobutton
        if (pd.loadFirstUser() == null) {
            btn1.setChecked(true);
        } else {
            myObjectId = pd.loadFirstUser().getObjectId();
        }

        rgs = (RadioGroup) findViewById(R.id.radioGroup);//实例化RadioGroup
        fragments.add(new FragmentHistory());
        fragments.add(new FragmentAnalysis());
        fragments.add(new FragmentPedometer());
        fragments.add(new FragmentPK());
        fragments.add(new FragmentSet());
        //自己写的一个fragment的适配器，进行几个页面的逻辑跳转
        new FragmentAdapter(MainActivity.this, fragments, R.id.Fragment, rgs,this);
        
    }
    /**
     * 如果退出程序会提示对话框
     */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			// 创建退出对话框  
			AlertDialog isExit = new AlertDialog.Builder(this).create();  
			// 设置对话框标题  
			isExit.setTitle("系统提示");  
			// 设置对话框消息  
			isExit.setMessage("确定要退出吗");  
			// 添加选择按钮并注册监听  
			isExit.setButton("确定", listener);  
			isExit.setButton2("取消", listener);  
			// 显示对话框  
			isExit.show();  

		}
		return false;
	}
	//监听对话框里面的button点击事件
	 DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			switch(arg1){
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序  
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框  
				break;
				default:
					break;
			}
			
		}
		 
	 };
    

}
