package com.waben.stock.futuresgateway.yisheng.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteMinuteKMultipleDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuote;
import com.waben.stock.futuresgateway.yisheng.entity.MongoFuturesQuoteMinuteKMultiple;

/**
 * 行情-多分钟K Dao实现
 * 
 * @author lma
 *
 */
@Repository
public class FuturesQuoteMinuteKMultipleDaoImpl implements FuturesQuoteMinuteKMultipleDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	private static String minuteKMultipleCollectionNamePrefix = "minutekmultiple-";

	@Override
	public MongoFuturesQuoteMinuteKMultiple createFuturesQuoteMinuteKMultiple(
			MongoFuturesQuoteMinuteKMultiple futuresQuoteMinuteKMultiple) {
		futuresQuoteMinuteKMultiple.setId(new ObjectId().toString());
		mongoTemplate.save(futuresQuoteMinuteKMultiple, minuteKMultipleCollectionNamePrefix
				+ futuresQuoteMinuteKMultiple.getCommodityNo() + "-" + futuresQuoteMinuteKMultiple.getContractNo());
		return futuresQuoteMinuteKMultiple;
	}

	@Override
	public void deleteFuturesQuoteMinuteKMultipleById(String commodityNo, String contractNo, String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		mongoTemplate.remove(query, FuturesQuote.class,
				minuteKMultipleCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

	@Override
	public MongoFuturesQuoteMinuteKMultiple retrieveFuturesQuoteMinuteKMultipleById(String commodityNo,
			String contractNo, String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		return mongoTemplate.findOne(query, MongoFuturesQuoteMinuteKMultiple.class,
				minuteKMultipleCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

	@Override
	public Page<MongoFuturesQuoteMinuteKMultiple> pageFuturesQuoteMinuteKMultiple(String commodityNo, String contractNo,
			int page, int limit) {
		Query query = new Query();
		query.skip(page * limit);
		query.limit(limit);
		List<MongoFuturesQuoteMinuteKMultiple> content = mongoTemplate.find(query,
				MongoFuturesQuoteMinuteKMultiple.class,
				minuteKMultipleCollectionNamePrefix + commodityNo + "-" + contractNo);
		long total = mongoTemplate.count(query, FuturesQuote.class,
				minuteKMultipleCollectionNamePrefix + commodityNo + "-" + contractNo);
		Page<MongoFuturesQuoteMinuteKMultiple> result = new PageImpl<>(content, new PageRequest(page, limit), total);
		return result;
	}

	@Override
	public List<MongoFuturesQuoteMinuteKMultiple> listFuturesQuoteMinuteKMultiple(String commodityNo,
			String contractNo) {
		Query query = new Query();
		return mongoTemplate.find(query, MongoFuturesQuoteMinuteKMultiple.class,
				minuteKMultipleCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

	@Override
	public MongoFuturesQuoteMinuteKMultiple retrieveByCommodityNoAndContractNoAndTime(String commodityNo,
			String contractNo, Date time) {
		Query query = new Query();
		query.skip(0);
		query.limit(1);
		query.addCriteria(Criteria.where("time").gte(time));
		return mongoTemplate.findOne(query, MongoFuturesQuoteMinuteKMultiple.class,
				minuteKMultipleCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

	@Override
	public MongoFuturesQuoteMinuteKMultiple retrieveNewestByCommodityNoAndContractNo(String commodityNo,
			String contractNo) {
		Query query = new Query();
		query.skip(0);
		query.limit(1);
		query.with(new Sort(new Sort.Order(Direction.DESC, "time")));
		return mongoTemplate.findOne(query, MongoFuturesQuoteMinuteKMultiple.class,
				minuteKMultipleCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

	@Override
	public List<MongoFuturesQuoteMinuteKMultiple> retriveByCommodityNoAndContractNoAndTimeStrLike(String commodityNo,
			String contractNo, String timeStr) {
		Query query = new Query();
		Pattern pattern = Pattern.compile("^" + timeStr + ".*", Pattern.CASE_INSENSITIVE);
		query.addCriteria(Criteria.where("timeStr").regex(pattern));
		query.with(new Sort(new Sort.Order(Direction.ASC, "time")));
		return mongoTemplate.find(query, MongoFuturesQuoteMinuteKMultiple.class,
				minuteKMultipleCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

	@Override
	public List<MongoFuturesQuoteMinuteKMultiple> retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
			String commodityNo, String contractNo, Date startTime, Date endTime) {
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Query query = new Query();
		query.addCriteria(Criteria.where("timeStr").gte(fullSdf.format(startTime)).lt(fullSdf.format(endTime)));
		query.with(new Sort(new Sort.Order(Direction.ASC, "time")));
		return mongoTemplate.find(query, MongoFuturesQuoteMinuteKMultiple.class,
				minuteKMultipleCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

}
