package proxyIp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import com.CrawlerLib;

public class IpPool {
	private static CloseableHttpClient client = CrawlerLib.getInstanceClient(false);

	public static void main(String[] args) {
		long b = System.currentTimeMillis();
		// System.out.println(checkIp(proxy, "https://m.taobao.com/"));
		String[] ss = readSomeIpFromTxt("test.txt", 2, "https://login.tmall.com/");
		for (int i = 0; i < ss.length; i++) {
			System.out.println(ss[i]);
		}
		long j = System.currentTimeMillis();
		System.out.println("用时: " + (j - b));
	}

	/**
	 * 检查是否能连接目标网址 淘宝web和热搜用 https://login.taobao.com/member/login.jhtml
	 * 天猫用:https://login.tmall.com/ 手机淘宝用:https://m.taobao.com/
	 */
	public static boolean checkIp(HttpHost proxy, String purposeUrl) {
		boolean flag = false;
		// String url = "http://www.ip138.com/";
		RequestConfig config = RequestConfig.custom().setConnectTimeout(6000).setSocketTimeout(6000).setProxy(proxy)
				.build();
		HttpGet get = new HttpGet(purposeUrl);
		get.setConfig(config);
		try {
			long i = System.currentTimeMillis();
			HttpResponse response = client.execute(get);
			long j = System.currentTimeMillis();
			System.out.println("execute用时: " + (j - i));
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println(statusCode);
			if (statusCode == 200) {
				System.out.println("checking……");
				flag = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(proxy.getHostName() + " checking failed");
		}
		get.abort();
		return flag;

	}

	/** 从某个txt里面读取i个ip */
	public static String[] readSomeIpFromTxt(String txtUrl, int number, String purposeUrl) {
		String[] result = new String[number];
		StringBuffer buffer = new StringBuffer();
		FileReader reader = null;
		BufferedReader bf = null;
		FileWriter fw = null;
		try {
			reader = new FileReader(new File(txtUrl));
			bf = new BufferedReader(reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String temp = null;
		try {
			int i = 0;
			while ((temp = bf.readLine()) != null) {
				HttpHost proxy = com.Main.ToHost(temp);
				if (i < number) {
					if (checkIp(proxy, purposeUrl)) {
						result[i] = temp;
						i++;
					}
				} else {
					buffer.append(temp + "\r\n");
				}

			}
			fw = new FileWriter(new File(txtUrl), false);
			fw.write(buffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fw.close();
			bf.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;

	}

}
