package com.waben.stock.futuresgateway.yisheng.esapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesCommodityDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesContractDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteMinuteKDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteMinuteKMultipleDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesCommodity;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteDayK;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteK;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteKGroup;
import com.waben.stock.futuresgateway.yisheng.entity.MongoFuturesQuoteMinuteK;
import com.waben.stock.futuresgateway.yisheng.entity.MongoFuturesQuoteMinuteKMultiple;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqProducer;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.message.EsDeleteQuoteMessage;
import com.waben.stock.futuresgateway.yisheng.service.FuturesQuoteDayKService;
import com.waben.stock.futuresgateway.yisheng.service.FuturesQuoteMinuteKGroupService;
import com.waben.stock.futuresgateway.yisheng.service.FuturesQuoteMinuteKService;
import com.waben.stock.futuresgateway.yisheng.util.CopyBeanUtils;
import com.waben.stock.futuresgateway.yisheng.util.JacksonUtil;
import com.waben.stock.futuresgateway.yisheng.util.StringUtil;

@Service
public class ImportDayK {
	
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesContractDao contractDao;

	@Autowired
	private FuturesQuoteDayKService dayKServcie;

	@Autowired
	private FuturesQuoteMinuteKDao minuteKDao;
	
	@Autowired
	private FuturesQuoteMinuteKMultipleDao minuteKMultipleDao;

	@Autowired
	private FuturesQuoteMinuteKService minuteKService;
	
	@Autowired
	private FuturesCommodityDao commodityDao;

	@Autowired
	private FuturesQuoteMinuteKGroupService minuteKGroupServcie;
	
	@Autowired
	private RabbitmqProducer producer;

	private static RestTemplate restTemplate = new RestTemplate();

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat minSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String toFullNumber(String number) {
		if (number.trim().length() < 2) {
			return "0" + number;
		} else {
			return number;
		}
	}

	public void importData(String daykImportDir) {
		File baseDir = new File(daykImportDir);
		File[] dirArr = baseDir.listFiles();
		for (File dir : dirArr) {
			if (dir.isDirectory()) {
				String commodityNo = dir.getName();
				// 获取该目录下的所有文件
				File[] dataFileArr = dir.listFiles();
				for (File dataFile : dataFileArr) {
					if (dataFile.getName().endsWith(".csv")) {
						BufferedReader reader = null;
						try {
							reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
							String line = null;
							int i = 0;
							while ((line = reader.readLine()) != null) {
								if (i == 0) {
									i++;
									continue;
								}
								try {
									if (line.split(",").length > 5 && !line.startsWith("合约代码")) {
										String[] splitData = line.split(",");
										String contractNo = splitData[2].substring(2) + toFullNumber(splitData[3]);
										Date time = sdf.parse(splitData[4].trim());
										String openPrice = !StringUtil.isEmpty(splitData[5].trim())
												? splitData[5].trim() : null;
										String highPrice = !StringUtil.isEmpty(splitData[6].trim())
												? splitData[6].trim() : null;
										String lowPrice = !StringUtil.isEmpty(splitData[7].trim()) ? splitData[7].trim()
												: null;
										String closePrice = !StringUtil.isEmpty(splitData[8].trim())
												? splitData[8].trim() : null;
										String totalVolume = !StringUtil.isEmpty(splitData[9].trim())
												? splitData[9].trim() : "0";
										String preSettlePrice = !StringUtil.isEmpty(splitData[10].trim())
												? splitData[10].trim() : "0";
										String volume = !StringUtil.isEmpty(splitData[11].trim()) ? splitData[11].trim()
												: "0";
										if (openPrice != null && highPrice != null && lowPrice != null
												&& closePrice != null) {
											FuturesQuoteDayK dayK = new FuturesQuoteDayK();
											dayK.setClosePrice(new BigDecimal(closePrice));
											dayK.setCommodityNo(commodityNo);
											dayK.setContractNo(contractNo);
											dayK.setHighPrice(new BigDecimal(highPrice));
											dayK.setLowPrice(new BigDecimal(lowPrice));
											dayK.setOpenPrice(new BigDecimal(openPrice));
											dayK.setPreSettlePrice(new BigDecimal(preSettlePrice));
											dayK.setTime(time);
											dayK.setTimeStr(fullSdf.format(time));
											dayK.setTotalVolume(new BigDecimal(totalVolume).longValue());
											dayK.setVolume(new BigDecimal(volume).longValue());

											FuturesQuoteDayK oldDayK = dayKServcie.getByCommodityNoAndContractNoAndTime(
													commodityNo, contractNo, time);
											if (oldDayK != null) {
												dayKServcie.deleteFuturesQuoteDayK(oldDayK.getId());
											}
											dayKServcie.addFuturesQuoteDayK(dayK);
										}
									}
								} catch (Exception e0) {
									e0.printStackTrace();
								}
								i++;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							if (reader != null) {
								try {
									reader.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
	}

	public void importMainMinuteline(String minutekImportDir) {
		File baseDir = new File(minutekImportDir);
		File[] dirArr = baseDir.listFiles();
		for (File dir : dirArr) {
			if (dir.isDirectory()) {
				String commodityNo = dir.getName();
				// 获取该目录下的所有文件
				File[] dataFileArr = dir.listFiles();
				for (File dataFile : dataFileArr) {
					if (dataFile.getName().endsWith(".txt")) {
						BufferedReader reader = null;
						try {
							reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
							String line = null;
							while ((line = reader.readLine()) != null) {
								String[] splitData = line.split(",");
								String contractNo = dataFile.getName().substring(0, dataFile.getName().length() - 4);
								Date time = minSdf.parse(splitData[0].trim());
								String openPrice = !StringUtil.isEmpty(splitData[1].trim()) ? splitData[1].trim()
										: null;
								String highPrice = !StringUtil.isEmpty(splitData[2].trim()) ? splitData[2].trim()
										: null;
								String lowPrice = !StringUtil.isEmpty(splitData[3].trim()) ? splitData[3].trim() : null;
								String closePrice = !StringUtil.isEmpty(splitData[4].trim()) ? splitData[4].trim()
										: null;
								String volume = !StringUtil.isEmpty(splitData[5].trim()) ? splitData[5].trim() : "0";
								String totalVolume = !StringUtil.isEmpty(splitData[6].trim()) ? splitData[6].trim()
										: "0";
								if (openPrice != null && highPrice != null && lowPrice != null && closePrice != null) {
									MongoFuturesQuoteMinuteK minuteK = new MongoFuturesQuoteMinuteK();
									minuteK.setClosePrice(new BigDecimal(closePrice));
									minuteK.setCommodityNo(commodityNo);
									minuteK.setContractNo(contractNo);
									minuteK.setHighPrice(new BigDecimal(highPrice));
									minuteK.setLowPrice(new BigDecimal(lowPrice));
									minuteK.setOpenPrice(new BigDecimal(openPrice));
									minuteK.setTime(time);
									minuteK.setTimeStr(fullSdf.format(time));
									minuteK.setTotalVolume(new BigDecimal(totalVolume).longValue());
									minuteK.setVolume(new BigDecimal(volume).longValue());

									MongoFuturesQuoteMinuteK oldMinuteK = minuteKDao
											.retrieveByCommodityNoAndContractNoAndTime(commodityNo, contractNo, time);
									if (oldMinuteK != null) {
										minuteKDao.deleteFuturesQuoteMinuteKById(commodityNo, contractNo, oldMinuteK.getId());
									}
									minuteKDao.createFuturesQuoteMinuteK(minuteK);
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							if (reader != null) {
								try {
									reader.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void importMainMultipleMinuteline(String multipleMinutekImportDir) {
		File baseDir = new File(multipleMinutekImportDir);
		File[] dirArr = baseDir.listFiles();
		for (File dir : dirArr) {
			if (dir.isDirectory()) {
				String commodityNo = dir.getName();
				// 获取该目录下的所有文件
				File[] dataFileArr = dir.listFiles();
				for (File dataFile : dataFileArr) {
					if (dataFile.getName().endsWith(".txt")) {
						BufferedReader reader = null;
						try {
							reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
							String line = null;
							while ((line = reader.readLine()) != null) {
								String[] splitData = line.split(",");
								String contractNo = dataFile.getName().substring(0, dataFile.getName().length() - 4).split("_")[0];
								Integer mins = Integer.parseInt(dataFile.getName().substring(0, dataFile.getName().length() - 4).split("_")[1]);
								Date time = minSdf.parse(splitData[0].trim());
								String openPrice = !StringUtil.isEmpty(splitData[1].trim()) ? splitData[1].trim()
										: null;
								String highPrice = !StringUtil.isEmpty(splitData[2].trim()) ? splitData[2].trim()
										: null;
								String lowPrice = !StringUtil.isEmpty(splitData[3].trim()) ? splitData[3].trim() : null;
								String closePrice = !StringUtil.isEmpty(splitData[4].trim()) ? splitData[4].trim()
										: null;
								String volume = !StringUtil.isEmpty(splitData[5].trim()) ? splitData[5].trim() : "0";
								String totalVolume = !StringUtil.isEmpty(splitData[6].trim()) ? splitData[6].trim()
										: "0";
								if (openPrice != null && highPrice != null && lowPrice != null && closePrice != null) {
									MongoFuturesQuoteMinuteKMultiple minuteKMultiple = new MongoFuturesQuoteMinuteKMultiple();
									minuteKMultiple.setMins(mins);
									minuteKMultiple.setClosePrice(new BigDecimal(closePrice));
									minuteKMultiple.setCommodityNo(commodityNo);
									minuteKMultiple.setContractNo(contractNo);
									minuteKMultiple.setHighPrice(new BigDecimal(highPrice));
									minuteKMultiple.setLowPrice(new BigDecimal(lowPrice));
									minuteKMultiple.setOpenPrice(new BigDecimal(openPrice));
									minuteKMultiple.setTime(time);
									minuteKMultiple.setTimeStr(fullSdf.format(time));
									minuteKMultiple.setTotalVolume(new BigDecimal(totalVolume).longValue());
									minuteKMultiple.setVolume(new BigDecimal(volume).longValue());

									MongoFuturesQuoteMinuteKMultiple oldMinuteKMultiple = minuteKMultipleDao
											.retrieveByCommodityNoAndContractNoAndTime(commodityNo, contractNo, time);
									if (oldMinuteKMultiple != null) {
										minuteKMultipleDao.deleteFuturesQuoteMinuteKMultipleById(commodityNo, contractNo, oldMinuteKMultiple.getId());
									}
									minuteKMultipleDao.createFuturesQuoteMinuteKMultiple(minuteKMultiple);
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							if (reader != null) {
								try {
									reader.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
	}

	public void importMainDayline(String daykImportDir) {
		File baseDir = new File(daykImportDir);
		File[] dirArr = baseDir.listFiles();
		for (File dir : dirArr) {
			if (dir.isDirectory()) {
				String commodityNo = dir.getName();
				// 获取该目录下的所有文件
				File[] dataFileArr = dir.listFiles();
				for (File dataFile : dataFileArr) {
					if (dataFile.getName().endsWith(".txt")) {
						BufferedReader reader = null;
						try {
							reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
							String line = null;
							while ((line = reader.readLine()) != null) {
								String[] splitData = line.split(",");
								String contractNo = dataFile.getName().substring(0, dataFile.getName().length() - 4);
								Date time = sdf.parse(splitData[0].trim());
								Calendar cal = Calendar.getInstance();
								cal.setTime(time);
								cal.add(Calendar.MINUTE, 1);
								time = cal.getTime();
								String openPrice = !StringUtil.isEmpty(splitData[1].trim()) ? splitData[1].trim()
										: null;
								String highPrice = !StringUtil.isEmpty(splitData[2].trim()) ? splitData[2].trim()
										: null;
								String lowPrice = !StringUtil.isEmpty(splitData[3].trim()) ? splitData[3].trim() : null;
								String closePrice = !StringUtil.isEmpty(splitData[4].trim()) ? splitData[4].trim()
										: null;
								String volume = !StringUtil.isEmpty(splitData[5].trim()) ? splitData[5].trim() : "0";
								String totalVolume = !StringUtil.isEmpty(splitData[6].trim()) ? splitData[6].trim()
										: "0";
								if (openPrice != null && highPrice != null && lowPrice != null && closePrice != null) {
									FuturesQuoteDayK dayK = new FuturesQuoteDayK();
									dayK.setClosePrice(new BigDecimal(closePrice));
									dayK.setCommodityNo(commodityNo);
									dayK.setContractNo(contractNo);
									dayK.setHighPrice(new BigDecimal(highPrice));
									dayK.setLowPrice(new BigDecimal(lowPrice));
									dayK.setOpenPrice(new BigDecimal(openPrice));
									dayK.setTime(time);
									dayK.setTimeStr(fullSdf.format(time));
									dayK.setTotalVolume(new BigDecimal(totalVolume).longValue());
									dayK.setVolume(new BigDecimal(volume).longValue());

									FuturesQuoteDayK oldDayK = dayKServcie
											.getByCommodityNoAndContractNoAndTime(commodityNo, contractNo, time);
									if (oldDayK != null) {
										dayKServcie.deleteFuturesQuoteDayK(oldDayK.getId());
									}
									dayKServcie.addFuturesQuoteDayK(dayK);
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							if (reader != null) {
								try {
									reader.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
	}

	public void setDayKMainContractEndTime(String commodityNo, String contractNo, Date dayKMainContractEndTime) {
		FuturesContract contract = contractDao.retrieveByCommodityNoAndContractNo(commodityNo, contractNo);
		if (contract != null) {
			contract.setDayKMainContractEndTime(dayKMainContractEndTime);
			contractDao.updateFuturesContract(contract);
		}
	}

	public void setMinuteKMainContractEndTime(String commodityNo, String contractNo, Date minuteKMainContractEndTime) {
		FuturesContract contract = contractDao.retrieveByCommodityNoAndContractNo(commodityNo, contractNo);
		if (contract != null) {
			contract.setMinuteKMainContractEndTime(minuteKMainContractEndTime);
			contractDao.updateFuturesContract(contract);
		}
	}

	public void updateMainDayline(String commodityNo) {
		String url = "https://stock2.finance.sina.com.cn/futures/api/json.php/GlobalFuturesService.getGlobalFuturesDailyKLine?symbol="
				+ commodityNo;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -2);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String response = restTemplate.getForObject(url, String.class);
		List<XinLangDayData> list = JacksonUtil.decode(response,
				JacksonUtil.getGenericType(ArrayList.class, XinLangDayData.class));
		for (XinLangDayData data : list) {
			try {
				Date time = sdf.parse(data.getDate());
				if (time.getTime() > cal.getTime().getTime()) {
					FuturesQuoteDayK dayK = dayKServcie.getByCommodityNoAndContractNoAndTime(commodityNo, "main", time);
					if (dayK == null) {
						dayK = new FuturesQuoteDayK();
						dayK.setTime(time);
						dayK.setTimeStr(sdf.format(time));
						dayK.setCommodityNo(commodityNo);
						dayK.setContractNo("main");
						dayK.setOpenPrice(data.getOpen());
						dayK.setHighPrice(data.getHigh());
						dayK.setLowPrice(data.getLow());
						dayK.setClosePrice(data.getClose());
						dayK.setVolume(data.getVolume());
						dayK.setTotalVolume(data.getVolume());
						dayKServcie.addFuturesQuoteDayK(dayK);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public static class XinLangDayData {

		private String date;
		private BigDecimal open;
		private BigDecimal high;
		private BigDecimal low;
		private BigDecimal close;
		private Long volume;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public BigDecimal getOpen() {
			return open;
		}

		public void setOpen(BigDecimal open) {
			this.open = open;
		}

		public BigDecimal getHigh() {
			return high;
		}

		public void setHigh(BigDecimal high) {
			this.high = high;
		}

		public BigDecimal getLow() {
			return low;
		}

		public void setLow(BigDecimal low) {
			this.low = low;
		}

		public BigDecimal getClose() {
			return close;
		}

		public void setClose(BigDecimal close) {
			this.close = close;
		}

		public Long getVolume() {
			return volume;
		}

		public void setVolume(Long volume) {
			this.volume = volume;
		}

	}

	public void computeMainMinuteKGroup(Date startTime) {
		Date date = new Date();
		while(date.getTime() >= startTime.getTime()) {
			computeMinuteKGroup(date);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.HOUR_OF_DAY, -1);
			date = cal.getTime();
		}
	}

	private void computeMinuteKGroup(Date date) {
		// SimpleDateFormat hourSdf = new SimpleDateFormat("yyyy-MM-dd HH:");
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// step 1 : 获取可用的合约
		List<FuturesCommodity> commodityList = commodityDao.retrieveByEnable(true);
		// step 2 : 获取上一小时
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		Date time = cal.getTime();
		cal.set(Calendar.MINUTE, 1);
		cal.add(Calendar.HOUR_OF_DAY, -1);
		Date beforeTime = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date afterTime = cal.getTime();
		// step 3 : 遍历所有合约，计算小时K
		for (FuturesCommodity commodity : commodityList) {
			String commodityNo = commodity.getCommodityNo();
			String contractNo = "main";
			// step 3.1 : 判断之前是否有计算过
			FuturesQuoteMinuteKGroup minuteKGroup = minuteKGroupServcie
					.getByCommodityNoAndContractNoAndTime(commodityNo, contractNo, time);
			if (minuteKGroup != null) {
				continue;
			}
			// step 3.2 : 根据时间获取上一小时的分钟K
			List<MongoFuturesQuoteMinuteK> minuteKList = minuteKService
					.retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(commodityNo, contractNo,
							beforeTime, afterTime);
			if (minuteKList != null && minuteKList.size() > 0) {
				// step 3.3 : 初始化部分数据
				minuteKGroup = new FuturesQuoteMinuteKGroup();
				minuteKGroup.setCommodityNo(commodityNo);
				minuteKGroup.setContractNo(contractNo);
				minuteKGroup.setTime(time);
				minuteKGroup.setTimeStr(fullSdf.format(time));
				minuteKGroup.setTotalVolume(minuteKList.get(minuteKList.size() - 1).getTotalVolume());
				Long startTotalQty = minuteKList.get(0).getStartTotalQty();
				Long endTotalQty = minuteKList.get(minuteKList.size() - 1).getEndTotalQty();
				minuteKGroup.setEndTotalQty(endTotalQty);
				minuteKGroup.setStartTotalQty(startTotalQty);
				if (endTotalQty != null && startTotalQty != null) {
					minuteKGroup.setVolume(endTotalQty - startTotalQty);
				} else {
					minuteKGroup.setVolume(0L);
				}
				minuteKGroup.setOpenPrice(minuteKList.get(0).getOpenPrice());
				minuteKGroup.setClosePrice(minuteKList.get(minuteKList.size() - 1).getClosePrice());
				// step 3.4 : 计算最高价、最低价
				BigDecimal highPrice = minuteKList.get(0).getHighPrice();
				BigDecimal lowPrice = minuteKList.get(0).getLowPrice();
				for (MongoFuturesQuoteMinuteK minuteK : minuteKList) {
					if (minuteK.getHighPrice().compareTo(highPrice) > 0) {
						highPrice = minuteK.getHighPrice();
					}
					if (minuteK.getLowPrice().compareTo(lowPrice) < 0) {
						lowPrice = minuteK.getLowPrice();
					}
				}
				minuteKGroup.setHighPrice(highPrice);
				minuteKGroup.setLowPrice(lowPrice);
				minuteKGroup.setGroupData(JacksonUtil.encode(minuteKList));
				// step 3.5 : 保存计算出来的分K数据
				minuteKGroupServcie.addFuturesQuoteMinuteKGroup(minuteKGroup);
				// step 3.6 : 删除分K的行情数据
				for (MongoFuturesQuoteMinuteK minuteK : minuteKList) {
					EsDeleteQuoteMessage delQuote = new EsDeleteQuoteMessage();
					delQuote.setQuoteId(String.valueOf(minuteK.getId()));
					delQuote.setType(2);
					producer.sendMessage(RabbitmqConfiguration.deleteQuoteQueueName, delQuote);
				}
			}
		}
		logger.info("计算分K组合数据结束:" + fullSdf.format(new Date()));
	}

	public void moveMinuteKToMongo() {
		List<FuturesQuoteMinuteK> dbList = minuteKDao.listDbMinuteK();
		List<FuturesQuoteMinuteKGroup> groupList = minuteKGroupServcie.list();
		// 迁移FuturesQuoteMinuteK
		if(dbList != null && dbList.size() > 0) {
			for(FuturesQuoteMinuteK minuteK : dbList) {
				MongoFuturesQuoteMinuteK check = minuteKDao.retrieveByCommodityNoAndContractNoAndTime(minuteK.getCommodityNo(), minuteK.getContractNo(), minuteK.getTime());
				if(check == null) {
					MongoFuturesQuoteMinuteK mongoMinuteK = CopyBeanUtils.copyBeanProperties(MongoFuturesQuoteMinuteK.class, minuteK, false);
					minuteKDao.createFuturesQuoteMinuteK(mongoMinuteK);
				}
			}
		}
		// 迁移FuturesQuoteMinuteKGroup
		if(groupList != null && groupList.size() > 0) {
			for(FuturesQuoteMinuteKGroup group : groupList) {
				String groupData = group.getGroupData();
				List<MongoFuturesQuoteMinuteK> dataList = JacksonUtil.decode(groupData,
						JacksonUtil.getGenericType(ArrayList.class, MongoFuturesQuoteMinuteK.class));
				for (MongoFuturesQuoteMinuteK data : dataList) {
					if (data.getOpenPrice() != null && data.getOpenPrice().compareTo(BigDecimal.ZERO) > 0) {
						MongoFuturesQuoteMinuteK check = minuteKDao.retrieveByCommodityNoAndContractNoAndTime(data.getCommodityNo(), data.getContractNo(), data.getTime());
						if(check == null) {
							minuteKDao.createFuturesQuoteMinuteK(data);
						}
					}
				}
			}
		}
	}

}
