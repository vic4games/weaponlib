package com.vicmatskiv.weaponlib.jim.util;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressionUtil {
	
	private static final Inflater INFLATER = new Inflater();
	private static final Deflater DEFLATER = new Deflater();
	
	
	public static byte[] compressBytes(byte[] input) {
		DEFLATER.setInput(input);
		DEFLATER.finish();
		
		byte[] result = new byte[(int) DEFLATER.getBytesWritten()];
		DEFLATER.deflate(result);
		
		return result;
		 
	}
	
	public static byte[] decompressBytes(byte[] input) {
		INFLATER.setInput(input);
	
		byte[] output = new byte[input.length];
		try {
			INFLATER.inflate(output);
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return output;
		
	}

}
