package com;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.bson.Document;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import interFace.CatchAndFigure;

public class MTaobao implements CatchAndFigure {
	static CloseableHttpClient client = CrawlerLib.getInstanceClient(false);
	int abtestInt = 0;

	public static void main(String[] args) {
		int abtest = 5;
		for (int i = 0; i < 100; i++) {
			String html = getContent("键盘", i, null, abtest);
			List<Document> document = figureJson(html, i);
			for (Document document2 : document) {
				CrawlerLib.printResult(document2.toJson(), true);
			}
		}
		// Taobao.printResult(html, false);
		// figureJson(html);
	}

	/**
	 * 找到头一步请求,并准备从其中解析出搜索界面的部分参数
	 */
	public String getFirstQuery(String keyWord, HttpHost proxy) {
		String html = null;
		String url = null;
		try {
			url = "http://s.m.taobao.com/h5?q=" + URLEncoder.encode(keyWord, "utf-8") + "&search=%E6%8F%90%E4%BA%A4";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (url == null) {
			return null;
		}
		HttpUriRequest request = CrawlerLib.getResponse(url, null, null, proxy);
		request.setHeader("host", "s.m.taobao.com");
		request.setHeader("Referer", url);
		HttpResponse response = null;
		int statusCode = 0;
		try {
			response = client.execute(request);
			statusCode = response.getStatusLine().getStatusCode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (response != null && statusCode > 199 && statusCode < 300) {
			try {
				html = EntityUtils.toString(response.getEntity(), "utf-8");
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		}
		return html;

	}

	public static List<Document> figureJson(String json, int index) {
		JsonReader reader = null;
		JsonElement element = null;
		List<Document> result = new ArrayList<Document>();
		// element = new JsonParser().parse(json.trim());
		// Gson gson = new Gson();
		// element = gson.toJsonTree(json, String.class);
		reader = new JsonReader(new StringReader(json));
		reader.setLenient(true);
		element = new JsonParser().parse(reader);
		JsonArray array = element.getAsJsonObject().get("listItem").getAsJsonArray();
		for (int i = 0; i < array.size(); i++) {
			Document document = new Document();
			JsonObject jsonObject = array.get(i).getAsJsonObject();
			if (jsonObject == null) {
				continue;
			}
			document.append("排名", index + "." + i);
			document.append("商品名", jsonObject.get("title").getAsString());
			document.append("价格", jsonObject.get("price").getAsString());
			document.append("店名", jsonObject.get("nick").getAsString());
			document.append("销量", jsonObject.get("sold").getAsString());
			document.append("链接", jsonObject.get("auctionURL").getAsString());
			result.add(document);
		}
		return result;

	}

	/**
	 * 延迟最佳设定为平均500左右
	 */
	public static String getContent(String keyWord, int index, HttpHost proxy, int abtest) {
		String url = null;
		String result = null;
		CloseableHttpResponse response = null;
		try {
			if (index == 0) {
				url = "http://s.m.taobao.com/search?" + "event_submit_do_new_search_auction=1"
						+ "&_input_charset=utf-8&topSearch=1&atype=b"
						+ "&searchfrom=1&action=home%3Aredirect_app_action" + "&from=1&q="
						+ URLEncoder.encode(keyWord, "utf-8") + "&sst=1" + "&n=20&buying=buyitnow&m=api4h5&abtest=3"
						+ "&wlsort=3" + "&page=" + (index + 1);
			} else {
				url = "http://s.m.taobao.com/search?" + "event_submit_do_new_search_auction=1"
						+ "&_input_charset=utf-8&topSearch=1&atype=b"
						+ "&searchfrom=1&action=home%3Aredirect_app_action" + "&from=1&q="
						+ URLEncoder.encode(keyWord, "utf-8") + "&sst=1" + "&n=20&buying=buyitnow&m=api4h5&abtest="
						+ abtest + "&wlsort=" + abtest + "&style=list" + "&closeModues=nav%2Cselecthot%2Conesearch"
						+ "&page=" + (index + 1);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpUriRequest request = CrawlerLib.getResponse(url, null, null, proxy);
		if (url != null) {
			try {
				response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode > 199 && statusCode < 300) {
					result = EntityUtils.toString(response.getEntity(), "utf-8");
				} else {
					result = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(new Random().nextInt(500) + 200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (request != null && !request.isAborted()) {
			request.abort();
		}
		return result;

	}

	public int setAbtestAndWlsort(HttpHost host) {
		String url = "http://s.m.taobao.com/h5?search-btn=&event_submit_do_new_search_auction=1&_input_charset=utf-8&topSearch=1&atype=b&searchfrom=1&action=home%3Aredirect_app_action&from=1";
		HttpUriRequest request = CrawlerLib.getResponse(url, null, null, host);
		String html = null;
		int result = 0;
		CloseableHttpResponse response = null;
		try {
			response = client.execute(request);
			html = EntityUtils.toString(response.getEntity(), "utf-8");
			// Taobao.printResult(html, true);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String abtest = null;
		String wlsort = null;
		if (html != null) {
			String regex = "abtest\\:'\\w+'";
			String regex0 = "wlsort\\:'\\w+'";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher0 = Pattern.compile(regex0).matcher(html);
			Matcher matcher = pattern.matcher(html);
			while (matcher.find() && matcher0.find()) {
				// System.out.println(matcher.group() + "--" +
				// matcher0.group());
				abtest = matcher.group();
				wlsort = matcher0.group();
			}
			String number = null;
			if (wlsort.length() == abtest.length()) {
				number = abtest == null ? null : abtest.substring(8, abtest.length() - 1);
			}
			result = Integer.valueOf(number);
			System.out.println(Integer.valueOf(number));
		} else {
			result = new Random().nextInt(8) + 1;
		}
		abtestInt = result;
		return result;

	}

	@Override
	public String catchContent(String keyWord, int index, HttpHost proxy) {
		String str = getContent(keyWord, index, proxy, abtestInt);
		return str;
	}

	@Override
	public List<Document> figureContent(String html, int index) {
		List<Document> list = figureJson(html, index);
		return list;
	}

}
