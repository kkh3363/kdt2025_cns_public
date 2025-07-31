package client_test;

import java.util.Timer;
import java.util.TimerTask;

//좀 더 간편하게 쓰려고 만든 타이머
public class STimer {
	private Timer timer = null;
	private Runnable task = null; 
	
	public STimer(Runnable task) {
		timer = new Timer();
		this.task = task; 
	}
	
	public void start(int sec) {
		if(timer!=null) {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					task.run();
					timer.cancel();
				}
			}, sec*1000);
		}
	}
	
	public void delete() {
		if(timer!=null) {
			timer.cancel();
		}
	}
}