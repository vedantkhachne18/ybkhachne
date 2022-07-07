package com.yogesh;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;


public class TextFileReadWrite {
	
	public List<String> readFileIntoList(InputStream fileStream) throws IOException{
		
		List<String> stringList = new ArrayList<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(fileStream,StandardCharsets.UTF_8));
		String lineIn = br.readLine();
		while (lineIn != null) {
			stringList.add(lineIn);
			lineIn = br.readLine();
		}
		br.close();
		return stringList;
	}
	
	
	public List<String> readFileIntoList(String fileName) throws Exception{
		// If Blank just return the empty list	
		if(StringUtils.isBlank(fileName)){
			return new ArrayList<>();
		}
		try(FileInputStream fis = new FileInputStream(fileName)){
			return readFileIntoList(fis);
		
		} catch (IOException ioException) {
			System.out.println("Problem reading file < " + fileName + " readFileIntoList");
			ioException.printStackTrace();
			throw ioException;
		}
	}
	
	public boolean writeTextFileUsingFiles(String strInputTextToWrite,String strTextFilePath) {
		boolean blnFlag = false;
		try {
			List<String> lines = Arrays.asList(strInputTextToWrite);
			Path file = Paths.get(System.getProperty("user.dir")+"/resourceFiles/"+strTextFilePath+".txt");
			Files.write(file, lines, Charset.forName("UTF-8"));
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return blnFlag;
		}

	}
	
	
	
	
	
	
	
	
	
}


