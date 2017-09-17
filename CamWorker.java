package jASUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import jASUtils.Mailer;
import jASUtils.StumpJunk;

public class CamWorker {

	public static void main(String args[]) {

		final int testVal = 1;

		DateFormat dateOverlayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		DateFormat dateFileFormat = new SimpleDateFormat("yyMMdd-HHmmss-SSS");

		final String camPath = args[0];
		final String asCell = args[1];
		final double capWait = 0.8;
		final String ipCam1 = "Cam1 IP:Port";
		final String ipCam1Alt = "Cam1 Alt IP:Port";
		final String ipCam2 = "Cam2 IP:Port";
		final String piCam1 = "Raspberry Pi IP:Port";
		final String ipCamUser = "Cam User";
		final String ipCamPass = "Cam Pass";
		final int mTrigger = 50;

		final File lastUPSStatus = new File(camPath+"/LUStatus.txt");
		final File lastCaseTemp = new File(camPath+"/CaseTF.txt");	
		final File lastCPUTemp = new File(camPath+"/CPUTF.txt");
		final File ua1Log = new File(camPath+"/OffCam1.txt");
		final File ua2Log = new File(camPath+"/OffCam2.txt");
		final File ua3Log = new File(camPath+"/OffCam3.txt");
		final File ua4Log = new File(camPath+"/OffCam4.txt");
		final File ua1tLog = new File(camPath+"/OffCam1T.txt");
		final File ua2tLog = new File(camPath+"/OffCam2T.txt");
		final File ua3tLog = new File(camPath+"/OffCam3T.txt");
		final File ua4tLog = new File(camPath+"/OffCam4T.txt");

		File xWebC1File = new File(camPath+"/Xwebc1-temp.jpeg");
		File xWebC2File = new File(camPath+"/Xwebc2-temp.jpeg");
		File xWebC3File = new File(camPath+"/Xwebc3-temp.jpeg");
		File xWebC4File = new File(camPath+"/Xwebc4-temp.jpeg");
		File webcXaFile = new File(camPath+"/webcXa-temp.jpeg");
		File webcXbFile = new File(camPath+"/webcXb-temp.jpeg");

		while (testVal == testVal) {

			Date date = new Date();
			String camTimestamp = dateOverlayFormat.format(date);
			String fileTimestamp = dateFileFormat.format(date);

			xWebC2File.delete();
			xWebC3File.delete();
			xWebC4File.delete();
			webcXaFile.delete();
			webcXbFile.delete();

			Scanner caseScanner = null; int tempCase = 0; try { caseScanner = new Scanner(lastCaseTemp); while(caseScanner.hasNext()) { tempCase = Integer.parseInt(caseScanner.nextLine()); } } catch (FileNotFoundException e) { e.printStackTrace(); }
			Scanner cpuScanner = null; int tempCPU = 0; try { cpuScanner = new Scanner(lastCPUTemp); while(cpuScanner.hasNext()) { tempCPU = Integer.parseInt(cpuScanner.nextLine()); } } catch (FileNotFoundException e) { e.printStackTrace(); }
			Scanner ua1Scanner = null; int offCam1 = 0; try { ua1Scanner = new Scanner(ua1Log); offCam1 = Integer.parseInt(ua1Scanner.nextLine()); } catch (FileNotFoundException e) { e.printStackTrace(); }
			Scanner ua2Scanner = null; int offCam2 = 0; try { ua2Scanner = new Scanner(ua2Log); offCam2 = Integer.parseInt(ua2Scanner.nextLine()); } catch (FileNotFoundException e) { e.printStackTrace(); }
			Scanner ua3Scanner = null; int offCam3 = 0; try { ua3Scanner = new Scanner(ua3Log); offCam3 = Integer.parseInt(ua3Scanner.nextLine()); } catch (FileNotFoundException e) { e.printStackTrace(); }
			Scanner ua4Scanner = null; int offCam4 = 0; try { ua4Scanner = new Scanner(ua4Log); offCam4 = Integer.parseInt(ua4Scanner.nextLine()); } catch (FileNotFoundException e) { e.printStackTrace(); }
			Scanner ua1tScanner = null; int offCam1t = 0; try { ua1tScanner = new Scanner(ua1tLog); offCam1t = Integer.parseInt(ua1tScanner.nextLine()); } catch (FileNotFoundException e) { e.printStackTrace(); }
			Scanner ua2tScanner = null; int offCam2t = 0; try { ua2tScanner = new Scanner(ua2tLog); offCam2t = Integer.parseInt(ua2tScanner.nextLine()); } catch (FileNotFoundException e) { e.printStackTrace(); }
			Scanner ua3tScanner = null; int offCam3t = 0; try { ua3tScanner = new Scanner(ua3tLog); offCam3t = Integer.parseInt(ua3tScanner.nextLine()); } catch (FileNotFoundException e) { e.printStackTrace(); }
			Scanner ua4tScanner = null; int offCam4t = 0; try { ua4tScanner = new Scanner(ua4tLog); offCam4t = Integer.parseInt(ua4tScanner.nextLine()); } catch (FileNotFoundException e) { e.printStackTrace(); }
			Scanner upsScanner = null; String upsStatus = null; try { upsScanner = new Scanner(lastUPSStatus); while(upsScanner.hasNext()) { upsStatus = upsScanner.nextLine(); } } catch (FileNotFoundException e) { e.printStackTrace(); }

			final String camCaption = "jASCams - "+camTimestamp+" -- IN "+tempCase+"F -- CPU "+tempCPU+"F -- "+upsStatus;

			Thread ca1a = new Thread(new Runnable() { public void run() { StumpJunk.runProcess("ffmpeg -i \"rtsp://"+piCam1+":8554/unicast\" -ss 00:00:00.2 -frames 1 "+xWebC4File.getPath()); }});
			Thread ca1b = new Thread(new Runnable() { public void run() { StumpJunk.jsoupOutBinary("http://"+ipCam1+"/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr="+ipCamUser+"&pwd="+ipCamPass, xWebC3File, capWait); }});
			Thread ca1c = new Thread(new Runnable() { public void run() { StumpJunk.jsoupOutBinary("http://"+ipCam2+"/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr="+ipCamUser+"&pwd="+ipCamPass, xWebC2File, capWait); }});
			Thread thList1[] = { ca1a, ca1b, ca1c };
			for (Thread thread : thList1) { thread.start(); } 
			for (int i = 0; i < thList1.length; i++) { try { thList1[i].join(); } catch (InterruptedException nx) { nx.printStackTrace(); } }
		
			if(xWebC3File.length() == 0) { StumpJunk.jsoupOutBinary("http://"+ipCam1Alt+"/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr="+ipCamUser+"&pwd="+ipCamPass, xWebC3File, capWait); }

			if(!xWebC1File.exists()) { offCam1++; offCam1t++;
				if(offCam1 == mTrigger) { String offMsg1 = "Camera 1 offline more than "+mTrigger+" cycles!"; System.out.println(offMsg1); Mailer.sendMail(asCell, "jASUtils.CamWorker Alert", offMsg1); }
				StumpJunk.runProcess("convert -size 954x540 -gravity center -annotate 0 \"Cam1 temporarily unavailable!\n Cycles: "+offCam1+"\" -pointsize 48 -fill Yellow xc:navy "+camPath+"/Xwebc1-temp.jpeg");
			} else { offCam1 = 0; }

			if(!xWebC2File.exists()) { offCam2++; offCam2t++;
				if(offCam1 == mTrigger) { String offMsg2 = "Camera 2 offline more than "+mTrigger+" cycles!"; System.out.println(offMsg2); Mailer.sendMail(asCell, "jASUtils.CamWorker Alert", offMsg2); }
				StumpJunk.runProcess("convert -size 954x540 -gravity center -annotate 0 \"Cam2 temporarily unavailable!\n Cycles: "+offCam2+"\" -pointsize 48 -fill Yellow xc:navy "+camPath+"/Xwebc2-temp.jpeg");
			} else { offCam2 = 0; }

			if(!xWebC3File.exists()) { offCam3++; offCam3t++;
				if(offCam1 == mTrigger) { String offMsg3 = "Camera 3 offline more than "+mTrigger+" cycles!"; System.out.println(offMsg3); Mailer.sendMail(asCell, "jASUtils.CamWorker Alert", offMsg3); }
				StumpJunk.runProcess("convert -size 954x540 -gravity center -annotate 0 \"Cam3 temporarily unavailable!\n Cycles: "+offCam3+"\" -pointsize 48 -fill Yellow xc:navy "+camPath+"/Xwebc3-temp.jpeg");
			} else { offCam3 = 0; }

			if(!xWebC4File.exists()) { offCam4++; offCam4t++;
				if(offCam1 == mTrigger) { String offMsg4 = "Camera 4 offline more than "+mTrigger+" cycles!"; System.out.println(offMsg4); Mailer.sendMail(asCell, "jASUtils.CamWorker Alert", offMsg4); }
				StumpJunk.runProcess("convert -size 954x540 -gravity center -annotate 0 \"Cam4 temporarily unavailable!\n Cycles: "+offCam4+"\" -pointsize 48 -fill Yellow xc:navy "+camPath+"/Xwebc4-temp.jpeg");
			} else { offCam4 = 0; }

			String convertA = "convert \\( "+camPath+"/Xwebc4-temp.jpeg -resize 954x540! "+camPath+"/Xwebc1-temp.jpeg -resize 954x540! +append \\)"
				+ " -background Black -append "+camPath+"/webcXa-temp.jpeg";

			String convertB = "convert \\( "+camPath+"/Xwebc2-temp.jpeg -resize 954x540! "+camPath+"/Xwebc3-temp.jpeg -resize 954x540! +append \\)"
				+ " \\( -gravity south -background Black -pointsize 36 -fill Yellow label:\""+camCaption+"\" +append \\)"
				+ " -background Black -append "+camPath+"/webcXb-temp.jpeg";

			Thread ca2a = new Thread(new Runnable() { public void run() { StumpJunk.runProcess(convertA); }});
			Thread ca2b = new Thread(new Runnable() { public void run() { StumpJunk.runProcess(convertB); }});
			Thread thList2[] = { ca2a, ca2b };
			for (Thread thread : thList2) { thread.start(); } 
			for (int i = 0; i < thList2.length; i++) { try { thList2[i].join(); } catch (InterruptedException nx) { nx.printStackTrace(); } }

			String convertC = "convert \\( "+camPath+"/webcXa-temp.jpeg +append \\)"
				+ " \\( "+camPath+"/webcXb-temp.jpeg +append \\)"
				+ " -background Black -append -resize 1090x810! "+camPath+"/webcX-temp.jpeg";

			StumpJunk.runProcess(convertC);

			StumpJunk.moveFile(camPath+"/webcX-temp.jpeg", camPath+"/PushTmp/"+fileTimestamp+".jpeg");

			try { StumpJunk.varToFile(Integer.toString(offCam1), ua1Log, false); } catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
			try { StumpJunk.varToFile(Integer.toString(offCam2), ua2Log, false); } catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
			try { StumpJunk.varToFile(Integer.toString(offCam3), ua3Log, false); } catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
			try { StumpJunk.varToFile(Integer.toString(offCam4), ua4Log, false); } catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
			try { StumpJunk.varToFile(Integer.toString(offCam1t), ua1tLog, false); } catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
			try { StumpJunk.varToFile(Integer.toString(offCam2t), ua2tLog, false); } catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
			try { StumpJunk.varToFile(Integer.toString(offCam3t), ua3tLog, false); } catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
			try { StumpJunk.varToFile(Integer.toString(offCam4t), ua4tLog, false); } catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
		
		}

	}

}
