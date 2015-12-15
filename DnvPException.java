package com.buzzai.dnvp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2015</p>
 * @author Nguyen Van Do
 * @version 1.0
 */

class DnvPException {
	
	private static Exception exc = null;
	private static FileWriter fw = null;
	private static BufferedWriter bw = null;
	private static SimpleDateFormat formatDate = new SimpleDateFormat("dd-MM mm:hh:ss");
	
	public static void DnvPExceptionStatic(String message, Throwable cause) {
		if(exc == null) {
			exc = new Exception(cause);
		} else {
			exc.addSuppressed(cause);
		}
		try {
			if(bw == null) {
				File file = new File("./report/error.txt");
				if (!file.exists()) {
					file.createNewFile();
				}
				fw = new FileWriter(file,true);
				bw = new BufferedWriter(fw);
			}
			bw.newLine();
			bw.write( formatDate.format(new Date()) + "  " + message + ", " + exc.getMessage());
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
