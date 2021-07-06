package com.demo.util;

import org.springframework.stereotype.Component;

/*
 * Author  : baiye <baiye_21@163.com>
   Time    : 2021/06/30
   Function: 线程获取当前系统时间 currentTimeMillis
*/
@Component
public class SysTimeUtil {

	private static long time;

	static {

		// 线程获取当前系统时间的currentTimeMillis
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					long cur = System.currentTimeMillis();
					setTime(cur);
				}
			}
		}).start();
	}

	public static long getTime() {
		return time;
	}

	public static void setTime(long time) {
		SysTimeUtil.time = time;
	}
}
