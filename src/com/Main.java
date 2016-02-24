package com;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.bson.Document;

import interFace.CatchAndFigure;

public class Main {
	static List<Document> list = new ArrayList<Document>();

	/**
	 * @param shopName
	 *            ����
	 * @param keyWord
	 *            �ؼ���
	 * @param purpose
	 *            ����Ŀ����վ,0Ϊ�Ա�,1Ϊ�ֻ��Ա�,2Ϊ��è,3Ϊֱͨ��
	 * @param proxy
	 *            �������proxy��Ϊָ������ģʽ
	 * 
	 */
	public static void main(String[] args) {
		// String shopName = args[0]; // ����
		// String keyWord = args[1]; // �ؼ���
		// String purpose = args[2];
		// HttpHost proxy = null;
		// String proxyStr = null;
		// if (args.length > 3) {
		// proxyStr = args[3]; // ip
		// }
		// if (proxyStr != null) {
		// proxy = ToHost(proxyStr);
		// }
		// int purposeInt = Integer.valueOf(purpose);
		// CatchAndFigure ca = null;
		// switch (purposeInt) {
		// case 0: {
		// ca = new Taobao(shopName, proxy);
		// break;
		// }
		// case 1: {
		// ca = new MTaobao();
		// break;
		// }
		// case 2: {
		// ca = new Tmall();
		// break;
		// }
		// case 3: {
		// ca = new ZTaobao();
		// }
		//
		// default:
		// break;
		// }
		String shopName = "�����ڹ�ר����";
		String shopName2 = "���շ����콢��";
		String keyWord2 = "���޷�";
		String keyWord = "���";
		HttpHost proxy = null;
		CatchAndFigure ca = new Taobao(shopName, proxy);
		CatchAndFigure ca2 = new Taobao(shopName2, proxy);
		// CatchAndFigure ca = new Tmall();
		new Thread(new MultiThread(ca, keyWord, proxy, 0, 50)).start();
		new Thread(new MultiThread(ca2, keyWord2, proxy, 0, 50)).start();
		new Thread(new MultiThread(ca, keyWord, proxy, 51, 100)).start();
		new Thread(new MultiThread(ca2, keyWord2, proxy, 51, 100)).start();
		while (true) {
			if (Thread.activeCount() == 1) {
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// System.out.println("--------------" + list.size());
		for (Document document : list) {
			String hostName = document.get("����").toString();
			// System.out.println(hostName + "-----------------");
			if (hostName.contains(shopName) || hostName.contains(shopName2)) {
				System.out.println(document.toJson());
			}
			CrawlerLib.printResult(document.toJson(), true);
		}

	}

	public static HttpHost ToHost(String ip) {
		if (ip == null) {
			return null;
		}
		String[] str = null;
		if (ip.contains(":")) {
			str = ip.split(":");
		}
		HttpHost proxy = new HttpHost(str[0], Integer.valueOf(str[1]));
		return proxy;

	}

}
