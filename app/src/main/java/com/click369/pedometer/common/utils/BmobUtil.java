package com.click369.pedometer.common.utils;

import java.util.List;

import com.click369.pedometer.data.javabean.User;

import android.content.Context;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
	
/**
 * 比目云操作帮助类
 * @author Administrator
 */
public class BmobUtil {

	public static void saveBmob(final User user,
			final BmobSaveAndUpdataListener listener, final Context context) {

		new Thread(new Runnable() {
			public void run() {
				Bmob.initialize(context, "106ac81e4141bdd3bc930fb1f01777d1");
				if (user.getObjectId().equals("1")) {
					user.setObjectId(null);
//					Toast.makeText(context, "正在存储", Toast.LENGTH_LONG).show();
					user.save(context, new SaveListener() {

						@Override
						public void onSuccess() {
							listener.onFinishedSave(user);
						}

						@Override
						public void onFailure(int arg0, String arg1) {
							listener.onFailure(arg1);
						}
					});

				} else {
//					Toast.makeText(context, "我在更新", Toast.LENGTH_LONG).show();
					user.update(context,new UpdateListener() {
						
						public void onSuccess() {
							listener.onFinishedupdata(user);
						}
						
						public void onFailure(int arg0, String arg1) {
							listener.onFailure(arg1);
							
						}
					});
				}

			}
		}).start();
	}

	public static void queryBmob(final BmobQueryListener listener,
			final Context context) {

		new Thread(new Runnable() {
			public void run() {
				Bmob.initialize(context, "106ac81e4141bdd3bc930fb1f01777d1");
				BmobQuery<User> query = new BmobQuery<User>();
				query.findObjects(context, new FindListener<User>() {

					@Override
					public void onSuccess(List<User> user_list) {
						listener.onQuerySuccess(user_list);

					}

					@Override
					public void onError(int arg0, String arg1) {
						listener.onFailure(arg1);

					}
				});

			}
		}).start();

	}
	
	public interface BmobSaveAndUpdataListener{
		void onFinishedSave(User user);
		void onFailure(String str);
		void onFinishedupdata(User user);
		
	}
	
	public interface BmobQueryListener{
		void onQuerySuccess(List<User> users);
		void onFailure(String str);
	}
}
