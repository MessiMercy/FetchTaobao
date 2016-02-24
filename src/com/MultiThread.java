package com;

import java.util.List;

import org.apache.http.HttpHost;
import org.bson.Document;

import interFace.CatchAndFigure;

public class MultiThread implements Runnable {
	CatchAndFigure catchAndFigure = null;
	String keyWord = null;
	HttpHost host = null;
	int startIndex = 0;
	int endIndex = 0;

	public MultiThread(CatchAndFigure ca, String keyWord, HttpHost proxy, int startIndex, int endIndex) {
		catchAndFigure = ca;
		this.keyWord = keyWord;
		this.host = proxy;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	@Override
	public void run() {

		for (int i = startIndex; i < endIndex; i++) {
			String html = catchAndFigure.catchContent(keyWord, i, host);
			List<Document> list = catchAndFigure.figureContent(html, i);
			Main.list.addAll(list);
		}

	}

}
