package interFace;

import java.util.List;

import org.apache.http.HttpHost;
import org.bson.Document;

public interface CatchAndFigure {
	/**
	 * ��ĳ����վץ����Ҫ������
	 * 
	 * @param keyWord
	 *            �������������ؼ���
	 * @param index
	 *            ����ڼ�ҳ
	 * @param proxy
	 *            ��������Ҫ�Ĵ���
	 */
	public String catchContent(String keyWord, int index, HttpHost proxy);

	/**
	 * ��ץ���������ݽ�����document,��list��
	 * 
	 * @param html
	 *            Ϊץ����������
	 * @param index
	 *            ����ڼ�ҳ,����д������
	 */
	public List<Document> figureContent(String html, int index);

}
