package com.click369.pedometer.ui.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.click369.pedometer.R;
import com.click369.pedometer.service.StepDetector;
import com.click369.pedometer.service.StepService;

/**
 * 欢迎页面
 */
public class WelcomeActivity extends Activity {
	private ImageView imageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//页面设置为无标题
		//如果StepServer是启动的话或者CURRENT_SETP不为0，则直接进入到主页面，否则进入欢迎页面
		if (StepService.flag || StepDetector.CURRENT_SETP > 0) {
			Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
			startActivity(intent);
			this.finish();
		} else {
			setContentView(R.layout.welcome);
//			//动画效果，可以自己设置
//			imageView=(ImageView) findViewById(R.id.welcome);
//			Animation animation=AnimationUtils.loadAnimation(this, R.anim.animation_welcome);
			
			//设置一个计时器，在此页面上停留3秒然后跳转到主页面,
			 (new Timer()).schedule(new TimerTask() {
			 public void run() {
			 Intent intent = new Intent(WelcomeActivity.this,
			 MainActivity.class);
			 startActivity(intent);
			 finish();
			 }
			 }, 3000);
//			 imageView.startAnimation(animation);
		}
	}

}
