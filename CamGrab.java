package jASUtils;

import java.io.*;
import java.sql.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;


import jASUtils.StumpJunk; 

public class CamGrab {

	public static void main(String args[]) {

		final Path camPath = Paths.get("/home/astump/Desktop");
		final Path sourceFolder = Paths.get("/var/www/Get/Cams/Archive");
		final Path unpackFolder = Paths.get("/dev/shm/camPull");
		final Path cListing = Paths.get(unpackFolder.toString()+"/Listing.txt");
		final Path mp4OutFile = Paths.get(camPath+"/CamPull.mp4");

		try { Files.createDirectories(unpackFolder); } catch (IOException ix) { ix.printStackTrace(); }

		StumpJunk.runProcess("cp "+sourceFolder.toString()+"/* "+unpackFolder.toString());
		StumpJunk.runProcess("bash /dev/shm/Sequence.sh "+unpackFolder.toString()+"/ mp4");
		List<String> camFiles = StumpJunk.fileSorter(unpackFolder, "*.mp4");
		
		try { Files.delete(cListing); } catch (IOException ix) { ix.printStackTrace(); }

		for (String thisLoop : camFiles) {
			String fileListStr = "file '"+thisLoop+"'\n"; 
			try { StumpJunk.varToFile(fileListStr, cListing.toFile(), true); } catch (FileNotFoundException fnf) { fnf.printStackTrace(); }
		}

		StumpJunk.runProcess("ffmpeg -threads 8 -safe 0 -f concat -i "+cListing.toString()+" -c copy "+mp4OutFile.toString());

		StumpJunk.runProcess("chown astump "+mp4OutFile.toString());

		StumpJunk.deleteDir(unpackFolder.toFile());
	}

}
