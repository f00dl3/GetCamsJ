package jASUtils;

import java.io.*;

import jASUtils.StumpJunk;

public class CamWorkerUSB {

	public static void main(String args[]) {
		
		final String camPath = args[0];
		final int testVal = 1;

		while (testVal == testVal) {

			String usbDevice = StumpJunk.runProcessOutVar("ls /dev/video*");
			usbDevice = usbDevice.replaceAll("\\n", "");
			StumpJunk.runProcess("timeout --kill-after=5 5 ffmpeg -f video4linux2 -s 954x540 -i "+usbDevice+" -ss 00:00:00.2 -frames 1 "+camPath+"/Xwebc1-temp-A.jpeg");
			StumpJunk.moveFile(camPath+"/Xwebc1-temp-A.jpeg", camPath+"/Xwebc1-temp.jpeg");

		}

	}

}
