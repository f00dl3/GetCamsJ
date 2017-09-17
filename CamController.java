package jASUtils;

import java.io.*;

import jASUtils.StumpJunk;
import jASUtils.CamWorker;
import jASUtils.CamWorkerHF;
import jASUtils.CamWorkerUSB;

public class CamController {

	public static void main(String args[]) {

		final File camPath = new File("/dev/shm/GetCamsJ");
		final File pushTemp = new File(camPath.getPath()+"/PushTmp");
		final String asCell = "CellNumber!";

		final String[] argsToPass = { camPath.getPath(), asCell };
		final String[] argsToPassHF = { camPath.getPath(), "500", "Y" };
		final String[] argsToPassHFb = { camPath.getPath(), "1000", "Z" };
		final String[] argsToPassHFc = { camPath.getPath(), "1500", "A" };
		int tester = 1;

		if (!pushTemp.exists()) {
			camPath.mkdirs();
			pushTemp.mkdirs();
		}

		while (tester == tester) {
			Thread cc1 = new Thread(new Runnable() { public void run() { CamWorkerUSB.main(argsToPass); }});
			Thread cc2 = new Thread(new Runnable() { public void run() { CamWorker.main(argsToPass); }});
			Thread cc3 = new Thread(new Runnable() { public void run() { CamWorkerHF.main(argsToPassHF); }});
			Thread cc4 = new Thread(new Runnable() { public void run() { CamWorkerHF.main(argsToPassHFb); }}); 
			/* Thread cc5 = new Thread(new Runnable() { public void run() { CamWorkerHF.main(argsToPassHFc); }});  */
			Thread cams[] = { cc1, cc2, cc3, cc4 /* , cc5 */ };
			for (Thread thread : cams) { thread.start(); } 
			for (int i = 0; i < cams.length; i++) { try { cams[i].join(); } catch (InterruptedException nx) { nx.printStackTrace(); } }
		}
		
	}

}
