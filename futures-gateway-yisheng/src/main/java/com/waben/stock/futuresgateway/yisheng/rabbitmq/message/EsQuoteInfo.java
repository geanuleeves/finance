package com.waben.stock.futuresgateway.yisheng.rabbitmq.message;

import com.future.api.es.external.quote.bean.TapAPIQuoteWhole;

public class EsQuoteInfo {

	private TapAPIQuoteWhole info;

	private long quoteIndex;

	public TapAPIQuoteWhole getInfo() {
		return info;
	}

	public void setInfo(TapAPIQuoteWhole info) {
		this.info = info;
	}

	public long getQuoteIndex() {
		return quoteIndex;
	}

	public void setQuoteIndex(long quoteIndex) {
		this.quoteIndex = quoteIndex;
	}

}
