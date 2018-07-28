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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteMinuteKDao;
import com.waben.stock.futuresgateway.yisheng.dao.impl.jpa.FuturesQuoteMinuteKRepository;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuote;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteK;
import com.waben.stock.futuresgateway.yisheng.entity.MongoFuturesQuoteMinuteK;

/**
 * 行情-分钟K Dao实现
 * 
 * @author lma
 *
 */
@Repository
public class FuturesQuoteMinuteKDaoImpl implements FuturesQuoteMinuteKDao {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private FuturesQuoteMinuteKRepository repository;

	private static String minuteKCollectionNamePrefix = "minutek-";
	
	@Override
	public List<FuturesQuoteMinuteK> listDbMinuteK() {
		return repository.findAll();
	}
	
	@Override
	public Page<FuturesQuoteMinuteK> pageDbMinuteK(int page, int size) {
		Pageable pageable = new PageRequest(page, size);
		return repository.findAll(pageable);
	}

	@Override
	public MongoFuturesQuoteMinuteK createFuturesQuoteMinuteK(MongoFuturesQuoteMinuteK futuresQuoteMinuteK) {
		futuresQuoteMinuteK.setId(new ObjectId().toString());
		mongoTemplate.save(futuresQuoteMinuteK, minuteKCollectionNamePrefix + futuresQuoteMinuteK.getCommodityNo() + "-"
				+ futuresQuoteMinuteK.getContractNo());
		return futuresQuoteMinuteK;
	}

	@Override
	public void deleteFuturesQuoteMinuteKById(String commodityNo, String contractNo, String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		mongoTemplate.remove(query, FuturesQuote.class, minuteKCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

	@Override
	public MongoFuturesQuoteMinuteK retrieveFuturesQuoteMinuteKById(String commodityNo, String contractNo, String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		return mongoTemplate.findOne(query, MongoFuturesQuoteMinuteK.class,
				minuteKCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

	@Override
	public Page<MongoFuturesQuoteMinuteK> pageFuturesQuoteMinuteK(String commodityNo, String contractNo, int page,
			int limit) {
		Query query = new Query();
		query.skip(page * limit);
		query.limit(limit);
		List<MongoFuturesQuoteMinuteK> content = mongoTemplate.find(query, MongoFuturesQuoteMinuteK.class,
				minuteKCollectionNamePrefix + commodityNo + "-" + contractNo);
		long total = mongoTemplate.count(query, FuturesQuote.class,
				minuteKCollectionNamePrefix + commodityNo + "-" + contractNo);
		Page<MongoFuturesQuoteMinuteK> result = new PageImpl<>(content, new PageRequest(page, limit), total);
		return result;
	}

	@Override
	public List<MongoFuturesQuoteMinuteK> listFuturesQuoteMinuteK(String commodityNo, String contractNo) {
		Query query = new Query();
		return mongoTemplate.find(query, MongoFuturesQuoteMinuteK.class,
				minuteKCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

	@Override
	public MongoFuturesQuoteMinuteK retrieveByCommodityNoAndContractNoAndTime(String commodityNo, String contractNo,
			Date time) {
		Query query = new Query();
		query.skip(0);
		query.limit(1);
		query.addCriteria(Criteria.where("time").gte(time));
		return mongoTemplate.findOne(query, MongoFuturesQuoteMinuteK.class,
				minuteKCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

	@Override
	public MongoFuturesQuoteMinuteK retrieveNewestByCommodityNoAndContractNo(String commodityNo, String contractNo) {
		Query query = new Query();
		query.skip(0);
		query.limit(1);
		query.with(new Sort(new Sort.Order(Direction.DESC, "time")));
		return mongoTemplate.findOne(query, MongoFuturesQuoteMinuteK.class,
				minuteKCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

	@Override
	public List<MongoFuturesQuoteMinuteK> retriveByCommodityNoAndContractNoAndTimeStrLike(String commodityNo,
			String contractNo, String timeStr) {
		Query query = new Query();
		Pattern pattern = Pattern.compile("^" + timeStr + ".*", Pattern.CASE_INSENSITIVE);
		query.addCriteria(Criteria.where("timeStr").regex(pattern));
		query.with(new Sort(new Sort.Order(Direction.ASC, "time")));
		return mongoTemplate.find(query, MongoFuturesQuoteMinuteK.class,
				minuteKCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

	@Override
	public List<MongoFuturesQuoteMinuteK> retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
			String commodityNo, String contractNo, Date startTime, Date endTime) {
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Query query = new Query();
		query.addCriteria(Criteria.where("timeStr").gte(fullSdf.format(startTime)).lt(fullSdf.format(endTime)));
		query.with(new Sort(new Sort.Order(Direction.ASC, "time")));
		return mongoTemplate.find(query, MongoFuturesQuoteMinuteK.class,
				minuteKCollectionNamePrefix + commodityNo + "-" + contractNo);
	}

}
