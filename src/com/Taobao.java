package com;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import interFace.CatchAndFigure;

public class Taobao implements CatchAndFigure {
	static CloseableHttpClient client = CrawlerLib.getInstanceClient(false);
	static CookieStore store = new BasicCookieStore();
	String shopName = null;
	HttpHost proxy = null;

	public Taobao(String shopName, HttpHost proxy) {
		this.shopName = shopName;
		this.proxy = proxy;
	}

	public static void main(String[] args) {
		// for (int i = 0; i < 5; i++) {
		// new Thread(new MultiThreadForTaobao("林志玲", i * 20, (i + 1) *
		// 20)).start();
		// new Thread(new MultiThreadForTaobao("范冰冰", i * 20, (i + 1) *
		// 20)).start();
		// new Thread(new MultiThreadForTaobao("高圆圆", i * 20, (i + 1) *
		// 20)).start();
		// new Thread(new MultiThreadForTaobao("林允儿", i * 20, (i + 1) *
		// 20)).start();
		// }
		// HttpHost proxy = new HttpHost("119.188.94.145", 80);
		// RequestConfig config =
		// RequestConfig.custom().setProxy(proxy).build();
		// String url = "https://www.taobao.com/";
		// HttpGet get = new HttpGet(url);
		// get.setConfig(config);
		// try {
		// HttpResponse response = client.execute(get);
		// CrawlerLib.printResult(EntityUtils.toString(response.getEntity()),
		// true);
		// } catch (ClientProtocolException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	public static String getUrl(String url, HttpHost proxy) {
		HttpUriRequest request = CrawlerLib.getResponse("https:" + url, null, null, proxy);
		HttpResponse response = null;
		String html = null;
		try {
			response = client.execute(request);
			html = EntityUtils.toString(response.getEntity(), "gbk");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return html;
	}

	/**
	 * 找到淘宝商品详情页上下架时间,详情页编码为gbk,
	 * 
	 * @param endOrStart
	 *            true代表得到结束时间,false代表得到上架时间
	 */
	public static String getStartAndEndTime(String html, boolean endOrStart) {
		String endTime = null;
		long i = 0;
		String result = null;
		String regex = endOrStart ? "ends=\\d+" : "starts=\\d+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		if (matcher.find()) {
			result = matcher.group();
		}
		int offset = endOrStart ? 5 : 7;
		i = Long.valueOf(result.substring(offset));
		Date date = new Date(i);
		SimpleDateFormat spf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		endTime = spf.format(date);
		return endTime;

	}

	public static String fuck(String keyWord, int index, HttpHost proxy) throws Exception {
		HttpUriRequest request = null;
		String url = null;
		String encodeKeyWord = null;
		index++;
		if (index < 1) {
			System.out.println("index illegal");
			throw new Exception();
		}
		try {
			encodeKeyWord = URLEncoder.encode(keyWord, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		Date date = new Date();
		SimpleDateFormat spf = new SimpleDateFormat("yyyyMMdd");
		String temp = spf.format(date);
		if (index == 0) {
			url = "https://s.taobao.com/search?q=" + encodeKeyWord
					+ "&imgfile=&js=1&stats_click=search_radio_all%3A1&initiative_id=staobaoz_" + temp + "&ie=utf8";
		} else {
			url = "https://s.taobao.com/search?q=" + encodeKeyWord
					+ "&imgfile=&commend=all&ssid=s5-e&search_type=item&sourceId=tb.index&spm=a21bo.7724922.8452-taobao-item.1&ie=utf8&initiative_id=tbindexz_"
					+ temp + "&bcoffset=" + (5 - 3 * index) + "&ntoffset=" + (5 - 3 * index)
					+ "p4plefttype=3%2C1&p4pleftnum=1%2C3&s=" + 44 * index;
		}
		request = CrawlerLib.getResponse(url, null, null, proxy);
		request.setHeader("cna", "1234DsBFTHcCAd3tnrhwImeX");
		HttpResponse response = null;
		String html = null;
		Document doc = null;
		String myJson = null;
		try {
			response = client.execute(request);
			System.out.println("正在访问" + url + "访问状态码" + response.getStatusLine().getStatusCode());
			html = EntityUtils.toString(response.getEntity());
			System.out.println(html.length());
			doc = Jsoup.parse(html);
			Elements json = doc.select("script");
			// int i = 0;
			for (Element element : json) {
				// if (i == 4) {
				// myJson = element.html().substring(15);
				// break;
				// }
				if (element.toString().contains("g_srp_loadCss")) {
					myJson = element.html().substring(15);
				}
				// printResult(element.html(), true);
				// printResult("\r\n-----------------------------------------------------------\r\n",
				// true);
				// i++;
			}
			int offset = myJson.indexOf("g_srp_loadCss") - 6;
			try {
				myJson = myJson.substring(0, offset);
			} catch (Exception e) {
				CrawlerLib.printResult(myJson, true);
				System.out.println("找不到特征字符串");
				System.exit(0);
			}
			// printResult(myJson, false);
			// System.out.println(json);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (request != null && !request.isAborted()) {
			request.abort();
		}
		Thread.sleep(new Random().nextInt(200) + 500);
		return myJson;
		// printResult(article, false);
	}

	List<org.bson.Document> figureJson(String json, int index) {
		JsonReader reader = null;
		JsonElement element = null;
		List<org.bson.Document> list = new ArrayList<org.bson.Document>();
		// element = new JsonParser().parse(json.trim());
		// Gson gson = new Gson();
		// element = gson.toJsonTree(json, String.class);
		reader = new JsonReader(new StringReader(json));
		reader.setLenient(true);
		element = new JsonParser().parse(reader);
		JsonArray array = element.getAsJsonObject().get("mods").getAsJsonObject().get("itemlist").getAsJsonObject()
				.get("data").getAsJsonObject().get("auctions").getAsJsonArray();
		System.out.println(array.size());
		for (int i = 0; i < array.size(); i++) {
			JsonElement jsonElement = array.get(i);
			if (jsonElement.toString().contains("activity")) {
				continue;
			}
			String name = jsonElement.getAsJsonObject().get("raw_title").getAsString();
			String host = jsonElement.getAsJsonObject().get("nick").getAsString();
			String url = jsonElement.getAsJsonObject().get("detail_url").getAsString();
			String price = jsonElement.getAsJsonObject().get("view_price").getAsString();
			String view_sales = jsonElement.getAsJsonObject().get("view_sales").getAsString();
			String nid = jsonElement.getAsJsonObject().get("nid").getAsString();
			String endsTime = null;
			org.bson.Document doc = new org.bson.Document("商品名", name);
			if (host.contains(shopName)) {
				// 暂时移除上下架时间
				// String html = getUrl(url, proxy);
				// endsTime = getStartAndEndTime(html, true);
				doc.append("下架时间", "null");
			}
			doc.append("店名", ensureNotEmpty(host));
			doc.append("详情地址", ensureNotEmpty(url));
			doc.append("价格", ensureNotEmpty(price));
			doc.append("销量", ensureNotEmpty(view_sales));
			doc.append("商品id", ensureNotEmpty(nid));
			doc.append("排名", index + "." + i);
			list.add(doc);
			// System.out.println(temp);
		}
		return list;
	}

	private static String ensureNotEmpty(String str) {
		return str.equals("") ? "no value" : str;
	}

	// /** 如果是店名需要的,则加入下架时间 */
	// private String getEndTime(String url, HttpHost proxy) {
	// HttpUriRequest request = CrawlerLib.getResponse(url, null, null, proxy);
	// HttpResponse response = null;
	// String html = null;
	// try {
	// response = client.execute(request);
	// html = EntityUtils.toString(response.getEntity(), "");
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return null;
	//
	// }

	@Override
	public String catchContent(String keyWord, int index, HttpHost proxy) {
		String str = null;
		try {
			str = fuck(keyWord, index, proxy);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	@Override
	public List<org.bson.Document> figureContent(String html, int index) {
		List<org.bson.Document> list = figureJson(html, index);
		return list;
	}

}
