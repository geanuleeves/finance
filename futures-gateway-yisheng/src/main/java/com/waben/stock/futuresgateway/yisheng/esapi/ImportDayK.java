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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesContractDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteDayK;
import com.waben.stock.futuresgateway.yisheng.service.FuturesQuoteDayKService;
import com.waben.stock.futuresgateway.yisheng.util.JacksonUtil;
import com.waben.stock.futuresgateway.yisheng.util.StringUtil;

@Service
public class ImportDayK {

	@Autowired
	private FuturesContractDao contractDao;

	@Autowired
	private FuturesQuoteDayKService dayKServcie;

	private static RestTemplate restTemplate = new RestTemplate();

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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

}
