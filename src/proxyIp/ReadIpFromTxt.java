package proxyIp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Stack;

public class ReadIpFromTxt {

	public String[] readAllIpFromTxt() {
		Stack<String> result = new Stack<>();
		FileReader reader = null;
		BufferedReader bf = null;
		try {
			reader = new FileReader(new File("test.txt"));
			bf = new BufferedReader(reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String temp = null;
		try {
			while ((temp = bf.readLine()) != null) {
				result.push(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] array = result.toArray(new String[result.size()]);
		return array;
	}

	public String readARandomIp() {
		Random random = new Random();
		String[] ips = readAllIpFromTxt();
		int ran = random.nextInt(ips.length);
		return ips[ran];
	}

}
