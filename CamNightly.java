package jASUtils;

import java.io.*;
import java.sql.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter; 

import jASUtils.StumpJunk; 

public class CamNightly {

	public static void main(String args[]) {

		final DateTime dtYesterday = new DateTime().minusDays(1);
		final DateTimeFormatter dtFormat = DateTimeFormat.forPattern("yyMMdd");
		final String getYesterday = dtFormat.print(dtYesterday);
		final Path camPath = Paths.get("/var/www/Get/Cams");
		final Path sourceFolder = Paths.get(camPath.toString()+"/Archive");
		final Path unpackFolder = Paths.get("/dev/shm/mp4tmp");
		final Path cListing = Paths.get(unpackFolder.toString()+"/Listing.txt");
		final Path mp4OutFile = Paths.get(camPath+"/MP4/"+getYesterday+"J.mp4");

		try { Files.createDirectories(unpackFolder); } catch (IOException ix) { ix.printStackTrace(); }

		StumpJunk.runProcess("mv "+sourceFolder.toString()+"/* "+unpackFolder.toString());
		StumpJunk.runProcess("bash /dev/shm/Sequence.sh "+unpackFolder.toString()+"/ mp4");
		List<String> camFiles = StumpJunk.fileSorter(unpackFolder, "*.mp4");
		
		try { Files.delete(cListing); }
		catch (IOException ix) { ix.printStackTrace(); }

		for (String thisLoop : camFiles) {
			String fileListStr = "file '"+thisLoop+"'\n"; 
			try { StumpJunk.varToFile(fileListStr, cListing.toFile(), true); } catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
		}

		StumpJunk.runProcess("timeout --kill-after=120 120 ffmpeg -threads 8 -safe 0 -f concat -i "+cListing.toString()+" -c copy "+mp4OutFile.toString()+"  2> "+camPath.toString()+"/MakeMP4_Last.log");

		String camImgQty = "";
		camImgQty = StumpJunk.runProcessOutVar("timeout --kill-after=120 120 ffprobe -v error -count_frames -select_streams v:0 -show_entries stream=nb_read_frames -of default=nokey=1:noprint_wrappers=1 "+mp4OutFile.toString());

		long camMP4Size = 0;
		try { camMP4Size = (Files.size(mp4OutFile)/1024); } catch (IOException ix) { ix.printStackTrace(); }

		String camLogSQL = "INSERT INTO Core.Log_CamsMP4 (Date,ImgCount,MP4Size) VALUES (CURDATE()-1,'"+camImgQty+"',"+camMP4Size+");";

		try ( Connection conn = MyDBConnector.getMyConnection(); Statement stmt = conn.createStatement();) { stmt.executeUpdate(camLogSQL); }
		catch (Exception e) { e.printStackTrace(); }

		StumpJunk.runProcess("(ls "+camPath.toString()+"/MP4/*.mp4 -t | head -n 31; ls "+camPath.toString()+"/MP4/*.mp4)|sort|uniq -u|xargs rm");
		StumpJunk.runProcess("chown -R www-data "+camPath.toString()+"/MP4");

		StumpJunk.deleteDir(unpackFolder.toFile());
	}

}
