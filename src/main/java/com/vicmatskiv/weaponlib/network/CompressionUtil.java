package com.vicmatskiv.weaponlib.network;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressionUtil {
	
	
	public static byte[] compressString(String str) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(bos);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			gos.write(str.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			bos.close();
			gos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return bos.toByteArray();
	}
	
	public static String decompressString(byte[] bytes) {
		String line = "";
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			GZIPInputStream gis = new GZIPInputStream(bis);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(gis));
			
			line = reader.readLine();
			
			gis.close();
			bis.close();
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return line;
	}

}
