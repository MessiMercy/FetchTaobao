package com;

public class RequestConfig {
	public String keyWord;// �ؼ���
	public int indexs;
	public String sort = null;// ����
	public boolean asc;// ����
	public boolean baoyou;// ����
	public String[] auction_tag;// 385Ϊ�˻���,1154��Ʒ,4806 7�����˻�
	public boolean support_cod;// ��������
	public boolean globalbuy;// ������Ʒ
	public boolean filterFineness;// ����
	public boolean filter_tianmao;// ��è
	public boolean user_type;// ��Ʒ
	public boolean consign_date; // 24Сʱ����
	public boolean olu;// ��������

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public int getIndexs() {
		return indexs;
	}

	public void setIndexs(int indexs) {
		this.indexs = indexs;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	public boolean isBaoyou() {
		return baoyou;
	}

	public void setBaoyou(boolean baoyou) {
		this.baoyou = baoyou;
	}

	public String[] getAuction_tag() {
		return auction_tag;
	}

	public void setAuction_tag(String[] auction_tag) {
		this.auction_tag = auction_tag;
	}

	public boolean isSupport_cod() {
		return support_cod;
	}

	public void setSupport_cod(boolean support_cod) {
		this.support_cod = support_cod;
	}

	public boolean isGlobalbuy() {
		return globalbuy;
	}

	public void setGlobalbuy(boolean globalbuy) {
		this.globalbuy = globalbuy;
	}

	public boolean isFilterFineness() {
		return filterFineness;
	}

	public void setFilterFineness(boolean filterFineness) {
		this.filterFineness = filterFineness;
	}

	public boolean isFilter_tianmao() {
		return filter_tianmao;
	}

	public void setFilter_tianmao(boolean filter_tianmao) {
		this.filter_tianmao = filter_tianmao;
	}

	public boolean isUser_type() {
		return user_type;
	}

	public void setUser_type(boolean user_type) {
		this.user_type = user_type;
	}

	public boolean isConsign_date() {
		return consign_date;
	}

	public void setConsign_date(boolean consign_date) {
		this.consign_date = consign_date;
	}

	public boolean isOlu() {
		return olu;
	}

	public void setOlu(boolean olu) {
		this.olu = olu;
	}

	public String toString() {
		// String result = null;
		StringBuffer result = new StringBuffer();
		String sortStr = null;
		// String auctionStr = null;
		if (sort != null) {
			sortStr = "&" + sort + "-" + (asc ? "asc" : "desc");
		}
		result.append(sortStr);
		if (baoyou) {
			result.append("&baoyou=1");
		}
		if (auction_tag.length != 0) {
			for (int i = 0; i < auction_tag.length; i++) {
				result.append("&auction_tag[]=" + auction_tag[i]);
			}
		}
		if (support_cod) {
			result.append("&support_cod=1");
		}
		if (globalbuy) {
			result.append("&globalbuy=1");
		}
		if (filterFineness) {
			result.append("&filterFineness=1");
		}
		if (filter_tianmao) {
			result.append("&filter_tianmao=tmall");
		}
		if (user_type) {
			result.append("&user_type=1");
		}
		if (consign_date) {
			result.append("&consign_date=1");
		}
		if (olu) {
			result.append("&olu=yes");
		}
		return result.toString();

	}
}
