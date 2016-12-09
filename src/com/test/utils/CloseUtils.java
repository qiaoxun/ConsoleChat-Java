package com.test.utils;

import java.io.Closeable;

public class CloseUtils {
	public static void closeAll(Closeable... ios){
		for(Closeable io : ios){
			try {
				io.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
