package com;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import interFace.CatchAndFigure;

public class Tmall implements CatchAndFigure {
	static CloseableHttpClient client = CrawlerLib.getInstanceClient(false);
	static String spmCode = null;

	public String getSpmCode() {
		return spmCode;
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		HttpHost proxy = new HttpHost("121.43.193.135", 20151);
		for (int i = 0; i < 10; i++) {
			String html = fetchHtml("ȡů��", i, proxy);
			List<Document> document = configureHtml(html, i);
			for (Document document2 : document) {
				CrawlerLib.printResult(document2.toJson(), true);
			}
		}
		// String html = fetchHtml("��������", 1, proxy);
		// Document document = configureHtml(html, 5);
		// Taobao.printResult(html, true);
		long end = System.currentTimeMillis();
		System.out.println("���ƺ�ʱ" + (end - start) + "ms");
	}

	static public String fetchHtml(String keyWord, int index, HttpHost proxy) {
		String html = null;
		String url = null;
		try {
			if (index == 0) {
				url = "https://list.tmall.com/search_product.htm?q=" + URLEncoder.encode(keyWord, "gbk")
						+ "&type=p&vmarket=&from=.list.pc_1_searchbutton";
			} else if (spmCode != null) {
				url = "https://list.tmall.com/search_product.htm?" + "spm=" + spmCode + "&s=" + (index + 1) * 60 + "&q="
						+ URLEncoder.encode(keyWord, "gbk") + "&sort=s&style=g&tmhkmain=0&type=pc#J_Filter";
			} else {
				url = "https://list.tmall.com/search_product.htm?" + "&s=" + (index + 1) * 60 + "&q="
						+ URLEncoder.encode(keyWord, "gbk") + "&sort=s&style=g&tmhkmain=0&type=pc#J_Filter";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpUriRequest request = CrawlerLib.getResponse(url, null, null, proxy);
		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println(statusCode);
		if (statusCode >= 200 && statusCode < 300) {
			try {
				html = EntityUtils.toString(response.getEntity(), "gbk");
				System.out.print(html.length());
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		int wait = new Random().nextInt(100) + 100;
		try {
			Thread.sleep(wait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (request != null && !request.isAborted()) {
			request.abort();
		}
		return html;
	}

	/**
	 * ��html��ȡ��������Ʒ��Ϣ,indexΪ�ڼ�ҳ��Ʒ,����documentΪ������Ʒdocument�ļ���
	 */
	static public List<Document> configureHtml(String html, int index) {
		if (index == 0) {
			int index1 = html.indexOf("spm=");
			int index2 = html.indexOf("&amp");
			spmCode = ((index2 - index1) < 30 && (index2 > index1)) ? html.substring(index1, index2) : null;
		}
		List<Document> result = new ArrayList<Document>();
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		// ������ȡ
		Elements element1 = doc.select("p.productTitle");// ��Ʒ����
		Elements element2 = doc.select("a.productShop-name");// ����
		Elements element3 = doc.select("p.productPrice");// �۸�
		Elements element4 = doc.select("p.productStatus");// һ�����ڽ�����
		// ��ȡ��ʽ:
		for (int i = 0; i < element1.size(); i++) {
			String number = index + "." + i;// ����ڼ�ҳ�ڼ�����Ʒ
			String itemName = element1.get(i).text();
			String bossName = element2.get(i).text();
			String price = element3.get(i).text();
			String count = element4.get(i).getElementsByTag("em").text();
			Document temp = new Document().append("����", number).append("��Ʒ��", itemName).append("����", bossName)
					.append("�۸�", price).append("����", count);
			result.add(temp);
		}
		return result;

	}

	@Override
	public String catchContent(String keyWord, int index, HttpHost proxy) {
		String str = fetchHtml(keyWord, index, proxy);
		return str;
	}

	@Override
	public List<Document> figureContent(String html, int index) {
		List<Document> list = configureHtml(html, index);
		return list;
	}

}
