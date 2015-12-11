package me.qinchao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Util {
	public static void writerFile(String fileName, String string, boolean append) {
		File file = new File( fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(file, append);
			fileWriter.write(string);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static String readerFile(String fileName) {

		File file = new File(fileName);

		try {
			if (file.exists()) {
				FileReader fileReader = new FileReader(file);
				char[] cs = new char[1024];
				while (fileReader.read(cs) != -1) {
				}
				fileReader.close();
				return new String(cs);
			} else {
				file.createNewFile();
				return "";
			}

		} catch (FileNotFoundException e1) {
			throw new RuntimeException(e1);
		} catch (IOException e2) {
			throw new RuntimeException(e2);
		}
	}
}
