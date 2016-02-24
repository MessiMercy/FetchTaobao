package proxyIp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.CrawlerLib;

public class GetProxyIps {
	static Stack<String> result = new Stack<>();
	private static CloseableHttpClient client = CrawlerLib.getInstanceClient(false);
	// static Stack<String> stack = processIpInStack(getDemo(null));
	static int finishedThread = 0;

	public static void main(String[] args) {
		String[] rrr = new ReadIpFromTxt().readAllIpFromTxt();
		// System.out.println(checkIp(null, ));
		for (int i = 0; i < rrr.length; i++) {
			System.out.println(checkIp(null, rrr[i]));
		}

	}

	public static Entry<String, String> getARandomIp() {
		String html = getDemo(null);
		System.out.println("1111111");
		Map<String, String> ips = processIps(html);
		System.out.println("2222");
		@SuppressWarnings("unchecked")
		Entry<String, String>[] arrayList = ips.entrySet().toArray(new Entry[ips.size()]);
		Random random = new Random();
		boolean flag = true;
		while (arrayList[0] instanceof Map.Entry && flag) {
			int randomCode = random.nextInt(arrayList.length);
			Entry<String, String> entry = arrayList[randomCode];
			System.out.println("checking " + entry.getKey() + " " + entry.getValue());
			if (checkIp(entry, null)) {
				return entry;
			}
		}
		return null;

	}

	public static Map<String, String> getAvailableIp() {
		String html = getDemo(null);
		Map<String, String> ips = processIps(html);
		Iterator<Entry<String, String>> it = ips.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			boolean isAvailable = checkIp(entry, null);
			if (!isAvailable) {
				it.remove();
			}
		}
		return ips;
	}

	private static boolean checkIp(Entry<String, String> entry, String ipInString) {
		boolean flag = false;
		String url = "http://www.ip138.com/ip2city.asp";
		// String url = "http://www.ip138.com/";
		String key = null;
		String value = null;
		if (entry == null) {
			key = ipInString.split(" ")[0];
			value = ipInString.split(" ")[1];
		} else {
			key = entry.getKey();
			value = entry.getValue();
		}
		HttpHost proxy = new HttpHost(key, Integer.valueOf(value));
		RequestConfig config = RequestConfig.custom().setConnectTimeout(6000).setSocketTimeout(6000).setProxy(proxy)
				.build();
		HttpGet get = new HttpGet(url);
		get.setConfig(config);
		try {
			HttpResponse response = client.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println(statusCode);
			if (statusCode == 200) {
				System.out.println("checking¡­¡­");
				String responseHtml = EntityUtils.toString(response.getEntity(), "gb2312");
				System.out.println(responseHtml);
				flag = responseHtml.contains(key);
			}
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println(key + " failed");
		}
		get.abort();
		return flag;

	}

	public static String getDemo(String url) {
		// url = "http://www.ip138.com/ip2city.asp";
		url = "http://www.xicidaili.com/nn/";
		HttpGet get = new HttpGet(url);
		get.setHeader("Host", "www.xicidaili.com");
		get.setHeader("Upgrade-Insecure-Requests", "1");
		get.setHeader("Referer", "http://www.xicidaili.com/nt/");
		get.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36");
		HttpResponse response = null;
		String html = null;
		try {
			response = client.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();// Á¬½Ó´úÂë
			System.out.println("-----------------------");
			System.out.println("status code is " + statusCode);
			html = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return html;
	}

	public static Map<String, String> processIps(String doc) {
		Map<String, String> map = new HashMap<String, String>();
		Document document = Jsoup.parse(doc);
		Elements elements = document.select("img[alt=Cn]");
		for (Element element : elements) {
			map.put(element.parent().nextElementSibling().text(),
					element.parent().nextElementSibling().nextElementSibling().text());
		}
		return map;

	}

	public static Stack<String> processIpInStack(String doc) {
		Stack<String> stack = new Stack<>();
		Document document = Jsoup.parse(doc);
		Elements elements = document.select("img[alt=Cn]");
		for (Element element : elements) {
			stack.push(element.parent().nextElementSibling().text() + " "
					+ element.parent().nextElementSibling().nextElementSibling().text());
		}
		return stack;

	}

}
