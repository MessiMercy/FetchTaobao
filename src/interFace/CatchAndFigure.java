package interFace;

import java.util.List;

import org.apache.http.HttpHost;
import org.bson.Document;

public interface CatchAndFigure {
	/**
	 * 从某个网站抓下需要的内容
	 * 
	 * @param keyWord
	 *            代表所需搜索关键词
	 * @param index
	 *            代表第几页
	 * @param proxy
	 *            代表所需要的代理
	 */
	public String catchContent(String keyWord, int index, HttpHost proxy);

	/**
	 * 将抓下来的内容解析成document,用list放
	 * 
	 * @param html
	 *            为抓下来的内容
	 * @param index
	 *            代表第几页,用于写入排名
	 */
	public List<Document> figureContent(String html, int index);

}
