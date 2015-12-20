package org.smartcity.util;

import java.io.File;

public class FileUtils {

	public static Boolean isValidFile(String path){
		try {
			File file = new File(path);
			if (file.isFile()){
				return true ;
			}
		} catch (Exception e) {
		}
		return false ;
	}
	
}
