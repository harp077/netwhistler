package nnm.inet.syslog;

import java.util.Timer;
import java.util.TimerTask;
import nnm.*;

public class startSyslog {
	Timer timer;

	Node sNode;

	boolean change = false;

	public startSyslog(int seconds) {

		timer = new Timer();
		timer.schedule(new RemindTask(), seconds * 1000);
	}

	class RemindTask extends TimerTask {
		public void run() {
			new Syslog();
			timer.cancel(); // Terminate the timer thread
		}
	}
}
