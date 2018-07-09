package com.waben.stock.applayer.admin.controller.futures;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.admin.business.futures.FuturesCommodityBusiness;
import com.waben.stock.applayer.admin.util.PoiUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.FuturesCommodityAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeTimeDto;
import com.waben.stock.interfaces.dto.admin.futures.SetSlipPointDto;
import com.waben.stock.interfaces.dto.futures.FuturesCommodityDto;
import com.waben.stock.interfaces.dto.futures.FuturesStopLossOrProfitDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesCommodityAdminQuery;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 期货品种
 * 
 * @author pzl
 *
 */

@RestController
@RequestMapping("/futuresCommodity")
@Api(description = "期货品种")
public class FuturesCommodityController {

	@Autowired
	private FuturesCommodityBusiness business;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@RequestMapping(value = "/lists/{exchangeId}", method = RequestMethod.GET)
	@ApiOperation(value = "根据交易所ID获取品种")
	public Response<List<FuturesCommodityDto>> listByExchangeId(@PathVariable("exchangeId") Long exchangeId) {
		return new Response<>(business.listByExchangeId(exchangeId));
	}

	@RequestMapping(value = "/isCurrency", method = RequestMethod.POST)
	@ApiOperation(value = "上线/下线品种")
	public Response<String> isCurrency(Long commodityId) {
		return business.isCurrency(commodityId);
	}

	@RequestMapping(value = "/tradeTime/saveAndModify", method = RequestMethod.POST)
	@ApiOperation(value = "添加品种交易时间")
	public Response<FuturesCommodityAdminDto> saveAndModify(FuturesTradeTimeDto dto) {
		return business.saveAndModify(dto);
	}
	
	
	@RequestMapping(value = "/setSlipPoint", method = RequestMethod.POST)
	@ApiOperation(value = "设置点差")
	public Response<FuturesCommodityAdminDto> setSlipPoint(SetSlipPointDto dto) {
		return business.setSlipPoint(dto);
	}

	@GetMapping("/tradeTime/queryTradeTime")
	@ApiOperation(value = "查询品种交易时间")
	public Response<FuturesCommodityAdminDto> queryTradeTime(Long commodityId) {
		return business.queryTradeTime(commodityId);
	}

	@GetMapping("/pagesCommodityAdmin")
	@ApiOperation(value = "查询品种")
	public Response<PageInfo<FuturesCommodityAdminDto>> pages(FuturesCommodityAdminQuery query) {
		return business.pagesCommodity(query);
	}

	@RequestMapping(value = "/futuresCommodity/save", method = RequestMethod.POST)
	@ApiOperation(value = "添加品种")
	public Response<FuturesCommodityAdminDto> save(FuturesCommodityAdminDto dto) {
		return business.save(dto);
	}

	@PostMapping("/futuresCommodity/modify")
	@ApiOperation(value = "修改品种")
	public Response<FuturesCommodityAdminDto> modify(FuturesCommodityAdminDto dto) {
		return business.modify(dto);
	}

	@DeleteMapping("/futuresCommodity/delete/{id}")
	@ApiOperation(value = "删除品种")
	public Response<String> delete(@PathVariable("id") Long id) {
		return business.delete(id);
	}

	@PostMapping("/save/lossOrProfit")
	@ApiOperation(value = "设置止损止盈")
	public Response<Integer> addLossOrProfit(@RequestBody List<FuturesStopLossOrProfitDto> lossOrProfitDto) {
		return new Response<>(business.saveLossOrProfit(lossOrProfitDto));
	}

	@GetMapping("/exportCoommodity")
	@ApiOperation(value = "导出品种")
	public void export(FuturesCommodityAdminQuery query, HttpServletResponse svrResponse) {
		query.setPage(0);
		query.setSize(Integer.MAX_VALUE);

		Response<PageInfo<FuturesCommodityAdminDto>> response = business.pagesCommodity(query);
		if ("200".equals(response.getCode()) && response.getResult() != null
				&& response.getResult().getContent() != null) {
			List<FuturesCommodityAdminDto> list = response.getResult().getContent();

			File file = null;
			FileInputStream is = null;

			List<String> columnDescList = null;
			try {
				String fileName = "contract_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				columnDescList = contractList();

				List<List<String>> dataList = dataList(list, query.getQueryType());
				PoiUtil.writeDataToExcel("期权品种数据", file, columnDescList, dataList);

				is = new FileInputStream(file);
				svrResponse.setContentType("application/vnd.ms-excel");
				svrResponse.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
				IOUtils.copy(is, svrResponse.getOutputStream());
				svrResponse.getOutputStream().flush();

			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION, "导出期货品种数据到excel异常：" + e.getMessage());
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
				if (file != null) {
					file.delete();
				}
			}
		}
	}

	private List<List<String>> dataList(List<FuturesCommodityAdminDto> content, Integer type) {
		List<List<String>> result = new ArrayList<>();
		for (FuturesCommodityAdminDto dto : content) {
			Boolean enable = dto.getEnable();
			String enables = "";
			if (enable != null && enable) {
				enables = "已生效";
			} else {
				enables = "未生效";
			}

			String currencyRate = "";
			if (dto.getRate() != null) {
				if (!dto.getRate().equals(0)) {
					currencyRate = "1人民币=" + dto.getRate() + dto.getCurrency();
				}
			}
			String exchangeType = "";
			if (dto.getExchangeType() == 1) {
				exchangeType = "外盘";
			} else {
				exchangeType = "内盘";
			}

			String unwindPoint = "";
			if (dto.getPerUnitUnwindPoint() != null && dto.getUnwindPointType() != null) {
				if (dto.getUnwindPointType() == 1 && dto.getPerUnitUnwindPoint() != null) {
					unwindPoint = dto.getPerUnitUnwindPoint() + "%";
				} else {
					unwindPoint = dto.getPerUnitUnwindPoint().toString();
				}

			}

			List<String> data = new ArrayList<>();
			if (type == 0) {
				String exchangCode = dto.getExchangcode() == null ? "" : dto.getExchangcode();
				String exchangName = dto.getExchangename() == null ? "" : dto.getExchangename();
				data.add(exchangCode + "/" + exchangName);
				data.add(dto.getExchangeType() == null ? "" : exchangeType);
				data.add(dto.getSymbol() == null ? "" : dto.getSymbol());
				data.add(dto.getName() == null ? "" : dto.getName());
				data.add(dto.getProductType() == null ? "" : dto.getProductType());
				data.add(dto.getCurrency() == null ? "" : dto.getCurrency());
				data.add(dto.getRate() == null ? "" : currencyRate);
				data.add(dto.getTradeUnit() == null ? "" : dto.getTradeUnit());
				data.add(dto.getQutoteUnit() == null ? "" : dto.getQutoteUnit());
				data.add(dto.getMinWave() == null ? "" : dto.getMinWave().toString());
				data.add(dto.getPerWaveMoney() == null ? "" : dto.getPerWaveMoney().toString());
				data.add(dto.getPerContractValue() == null ? "" : dto.getPerContractValue().toString());
				data.add(dto.getPerUnitReserveFund() == null ? "" : dto.getPerUnitReserveFund().toString());
				data.add(dto.getPerUnitUnwindPoint() == null ? "" : unwindPoint);
				data.add(dto.getOpenwindServiceFee() == null ? "" : dto.getOpenwindServiceFee().toString());
				data.add(dto.getUnwindServiceFee() == null ? "" : dto.getUnwindServiceFee().toString());
				data.add(dto.getOvernightPerUnitDeferredFee() == null ? ""
						: dto.getOvernightPerUnitDeferredFee().toString());
				data.add(dto.getOvernightPerUnitReserveFund() == null ? ""
						: dto.getOvernightPerUnitReserveFund().toString());
				data.add(dto.getOvernightTime() == null ? "" : dto.getOvernightTime());
				data.add(
						dto.getReturnOvernightReserveFundTime() == null ? "" : dto.getReturnOvernightReserveFundTime());
				data.add(enables);
				data.add(dto.getCreateTime() == null ? "" : sdf.format(dto.getCreateTime()));
			}
			result.add(data);
		}
		return result;
	}

	private List<String> contractList() {
		List<String> result = new ArrayList<>();
		result.add("市场类别/名称");
		result.add("境外标记");
		result.add("交易代码");
		result.add("交易品种");
		result.add("交易分类");
		result.add("货币单位");
		result.add("汇率");
		result.add("交易单位");
		result.add("报价单位");
		result.add("最小波动点数");
		result.add("波动盈亏");
		result.add("1手合约价值");
		result.add("1手保证金");
		result.add("1手强平点");
		result.add("1手开仓手续费");
		result.add("1手平仓手续费");
		result.add("1手递延费");
		result.add("1手隔夜保证金");
		result.add("隔夜收取时间");
		result.add("隔夜返还时间时间");
		result.add("品种状态");
		result.add("创建时间");
		return result;
	}
}
