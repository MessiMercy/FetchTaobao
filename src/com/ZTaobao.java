package com;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import interFace.CatchAndFigure;

public class ZTaobao implements CatchAndFigure {
	static CloseableHttpClient client = CrawlerLib.getInstanceClient(false);
	static BasicCookieStore cookieStore = new BasicCookieStore();
	// static CloseableHttpClient client =
	// HttpClients.custom().setDefaultCookieStore(cookieStore).build();

	public static void main(String[] args) {
		testCatchContent();
	}

	public static void testCatchContent() {
		for (int i = 0; i < 100; i++) {
			String html = getContent("钱包", i, null);
			if (html == null) {
				continue;
			}
			List<org.bson.Document> documents = figureHtml(html, i);
			for (org.bson.Document document : documents) {
				CrawlerLib.printResult(document.toJson(), true);
			}
		}
	}

	/**
	 * 每一页200个商品
	 */
	public static String getContent(String keyWord, int index, HttpHost proxy) {
		String url = null;
		String result = null;
		HttpResponse response = null;
		String encodedKeyWord = null;
		try {
			encodedKeyWord = URLEncoder.encode(keyWord, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		url = "https://re.taobao.com/search?clk1=" + "&p4pTags=&ismall=&refpos=" + "&refpid=420434_1006" + "&keyword="
				+ encodedKeyWord + "&_input_charset=utf-8&page=" + index;
		HttpUriRequest request = CrawlerLib.getResponse(url, null, null, proxy);
		String refererUrl = "https://re.taobao.com/search?clk1=" + "&p4pTags=&ismall=&refpos=" + "&refpid=420434_1006"
				+ "&keyword=" + encodedKeyWord + "&_input_charset=utf-8&page=" + (index - 1);
		if (index != 0) {
			request.setHeader("referer", refererUrl);
		}
		request.setHeader(":version", "HTTP/1.1");
		request.setHeader(":scheme", "https");
		request.setHeader(":host", "re.taobao.com");
		request.setHeader(":method", "GET");
		request.setHeader(":path", url.substring(url.indexOf("/sear")));
		request.setHeader("upgrade-insecure-requests", "1");
		request.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		request.setHeader("accept-encoding", "gzip, deflate, sdch");
		request.setHeader("accept-language", "zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4");
		request.setHeader("cookie", "cna=" + "12345usQQ2QCAabcKtn1kP8A");// 需要加入此cookie以伪装正常访问.值只要非空就行
		// 经测试能承受住6个线程跑总共240页,完成一次完整查询没问题,此处代理ip消耗较少,淘宝,天猫直通车,手机淘宝相同ip反爬互相独立
		// request.setHeader("cookie",
		// "cookie2=1ea66e9ac5862059afd88344021b672c");
		// request.setHeader("cookie",
		// "cookie2=1ea66e9ac5862059afd88344021b672c");
		if (url != null) {
			try {
				response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode > 199 && statusCode < 300) {
					result = EntityUtils.toString(response.getEntity(), "utf-8");
				} else {
					result = null;
				}
				System.out.println("statusCode: " + statusCode + " length: " + result.length());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// try {
		// Thread.sleep(new Random().nextInt(200) + 800);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		if (request != null && !request.isAborted()) {
			request.abort();
		}
		return result;

	}

	/**
	 * 每页商品只有200个
	 */
	public static List<org.bson.Document> figureHtml(String html, int index) {
		Document doc = null;
		if (html != null) {
			doc = Jsoup.parse(html);
		} else {
			return null;
		}
		List<org.bson.Document> documents = new ArrayList<>();
		Elements itemName = doc.select("div.item").select("a.title"); // 商品名
		Elements itemPrice = doc.select("strong");// 价格
		Elements shopName = doc.select("a.shopNick");// 店名
		Elements payNum = doc.select("a.payNum");// 销量
		int itemCounts = itemName.size() >= 200 ? 200 : payNum.size();
		for (int i = 0; i < itemCounts; i++) {
			org.bson.Document temp = new org.bson.Document();
			String title = itemName.get(i).text();
			String price = itemPrice.get(i).text();
			String shop = shopName.get(i).text();
			String sold = payNum.get(i).text();
			temp.append("排名", index + "." + i);
			temp.append("商品名", title);
			temp.append("价格", price);
			temp.append("店名", shop);
			temp.append("销量", sold);
			documents.add(temp);
		}
		return documents;

	}

	@Override
	public String catchContent(String keyWord, int index, HttpHost proxy) {
		String str = getContent(keyWord, index, proxy);
		return str;
	}

	@Override
	public List<org.bson.Document> figureContent(String html, int index) {
		List<org.bson.Document> list = figureHtml(html, index);
		return list;
	}

}
