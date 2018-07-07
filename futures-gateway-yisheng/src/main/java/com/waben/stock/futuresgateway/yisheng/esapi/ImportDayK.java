package com.waben.stock.futuresgateway.yisheng.esapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteDayK;
import com.waben.stock.futuresgateway.yisheng.service.FuturesQuoteDayKService;
import com.waben.stock.futuresgateway.yisheng.util.StringUtil;

@Service
public class ImportDayK {

	@Autowired
	private FuturesQuoteDayKService dayKServcie;

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

}
