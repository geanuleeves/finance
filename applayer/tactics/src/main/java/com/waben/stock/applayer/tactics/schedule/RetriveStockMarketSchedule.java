package com.waben.stock.applayer.tactics.schedule;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.applayer.tactics.retrivestock.bean.StockMarket;
import com.waben.stock.applayer.tactics.service.RedisCache;
import com.waben.stock.applayer.tactics.service.StockMarketService;
import com.waben.stock.interfaces.enums.RedisCacheKeyType;
import com.waben.stock.interfaces.util.JacksonUtil;

/**
 * 定时获取股票行情作业
 * 
 * @author luomengan
 *
 */
@Component
public class RetriveStockMarketSchedule {

	/**
	 * 获取间隔间隔
	 */
	public static final long Retrive_Interval = 3 * 60 * 1000;

	@Autowired
	private StockMarketService stockMarketService;
	
	@Autowired
	private RedisCache cache;

	@PostConstruct
	public void initTask() {
		Timer timer = new Timer();
		timer.schedule(new RetriveTask(), Retrive_Interval);
	}

	private class RetriveTask extends TimerTask {
		@Override
		public void run() {
			try {
				// 4609	深证成数
				List<String> shenCodes1 = Arrays.asList("002115,002706,002646,002660,002516,000502,000011,002172,002639,002361,000757,000779,002558,002250,002537,002396,002461,002010,002612,002883,002035,002573,002429,000030,600933,000017,000800,002679,002251,002240,000876,002800,603813,002071,002824,002066,002653,000670,002273,000758,000768,000852,000662,002456,002595,000068,002666,002571,002765,002885,000509,000403,000913,000626,000726,002428,002314,002790,002911,002821,002027,002544,002718,000565,002292,002779,000722,000609,002406,002148,000925,002864,000736,002288,002868,002152,002685,603970,000430,002905,002094,002825,000413,002462,002633,002277,000612,002838,002099,002659,002128,002574,002778,000698,002510,002863,000777,002636,000875,002709,002785,002100,002879,002803,002912,002208,002771,000707,002777,002427,002812,002899,002095,002276,002062,000767,002181,002362,002060,002382,002213,000990,000544,000926,002389,000558,000692,002063,002360,002401,000525,002160,002322,002458,002434,000666,002121,002457,002487,000881,002635,603106,002270,002215,002324,603110,000836,002073,002873,002876,002489,000576,000600,000978,000652,002072,002605,000885,002090,002683,002552,002661,000676,000821,000936,002826,002164,002689,002351,000401,002030,002042,002067,000719,002629,002665,002228,002237,002608,000753,002900,002443,002116,002405,000912,002580,002098,002697,002707,000657,002702,000028,002423,002596,002331,002372,603507,002492,002809,000697,002593,000830,002585,002144,603136,002529,002672,000590,000951,000655,002416,002108,601108,000532,002888,002258,000796,002430,002139,002325,002860,000682,002799,002058,002645,000633,000025,000908,002521,002367,002220,002205,002157,002432,000738,002588,000831,002587,000791,002347,002054,002836,000016,000701,000833,603683,002045,002005,002092,000987,002463,002365,002411,603063,002738,002070,002130,002472,002547,002374,002150,000096,603367,603730,000029,002192,002421,002490,002617,002285,000751,002581,002909,002080,002140,002422,002419,002760,002378,002450,002221,002386,000752,000005,002275,000760,000903,002304,002196,002271,002486,002577,002853,002798,000038,002179,000425,002359,000586,000878,002168,000419,002120,002310,603856,000089,002841,002018,002029,000892,002584,002872,002640,002015,002124,002719,002734,002668,002830,603458,002524,002695,603912,002269,002711,002146,002852,002350,000792,603659,000526,002686,000607,002460,002044,002787,000983,002311,603127,002086,002802,000507,002300,002415,002512,002749,002357,000759,000563,000887,000504,002318,002696,002515,002009,002857,002775,002338,002395,603527,000589,002887,002352,000673,002344,000056,002195,002846,002479,603500,002642,002545,002560,002032,002093,002149,002231,002859,002424,002473,002442,000595,002187,000958,000725,002225,000880,000545,002052,002618,000918,002064,002851,002183,002647,002805,001896,002282,002758,002459,000737,000938,002502,002483,002203,002654,000948,002469,000601,002081,002166,002565,000980,002156,002113,603978,000523,000524,002118,000046,000616,002881,002619,000708,002262,002527,002678,002739,002878,000786,000417,000418,002001,002871,000100,000702,002377,002656,000625,000020,002254,000909,002603,002024,002480,002559,002770,002670,000421,002083,000937,002123,002575,002806,603557,000668,002796,002638,002135,000042,000151,002752,002316,000755,002822,000807,002540,000153,000567,000959,002801,002478,002680,002019,000935,000411,002298,002891,002109,002234,002085,603535,002096,002210,000886,000150,002399,002622,000955,002453,000877,000997,000301,002371,000705,603860,000678,002102,002084,002599".split(","));
				List<String> shenCodes2 = Arrays.asList("002634,002747,002348,002354,002413,002910,002607,000541,000976,002309,000002,000597,000505,000922,002392,002012,002328,000587,000528,603277,000636,000911,002503,002590,000916,000663,002882,002026,002006,002169,002272,002444,002025,002142,002138,002379,002173,002110,002632,002861,002315,002163,002154,002290,002522,002343,002795,000514,002687,002111,000557,000739,000950,002414,002691,000099,002048,002808,000546,000810,000901,002877,002355,002131,000811,002855,002628,600903,002055,002294,002865,000868,000690,002740,000592,000812,002507,000043,000727,603183,000638,002651,002849,603721,002370,002398,002145,000669,000677,000014,002907,002364,002526,000605,002238,603880,002293,002819,000637,000900,002721,002875,000582,002082,002132,002484,000899,002684,002384,002216,002901,002103,002327,000007,000632,000088,000538,002375,002248,000882,000519,002563,000920,002650,002171,000965,002184,000709,002182,002561,002057,000498,000700,002536,000820,002569,000158,000975,000860,002518,000488,002741,000422,002551,000531,001696,603357,002241,002190,002404,000801,002356,002616,000711,002117,002751,002491,002732,000639,002728,000022,000970,000665,002388,002003,002767,002681,000026,002624,603725,000731,002274,000915,002249,002358,000503,002256,002236,000703,002266,000520,000049,002008,000060,000688,002034,002705,000555,000521,002823,002412,000793,002170,002235,002448,000615,603260,002143,002353,000065,000809,000627,002837,002454,002542,002641,000898,002662,002485,002326,002788,000560,002773,000889,002153,002835,002217,002731,002475,002431,000835,002763,603963,000715,002339,000533,002227,002155,002717,000066,002541,002568,002088,000428,002013,000785,002420,002807,002259,002652,000686,002174,002402,002002,002245,002268,002265,000031,002533,002850,000729,002373,000798,000037,002194,000619,002549,002476,002408,002786,002908,002283,002494,000034,002468,000613,000691,002177,002898,603083,002097,000848,002688,002725,002007,002337,002703,002815,002021,002743,002214,002212,002299,000023,000155,000593,000910,002509,002056,002074,002880,000012,002077,002291,603499,000404,002264,002856,002188,000058,000585,000897,603825,000788,000981,000409,002543,002643,002087,002673,000050,002186,002391,002566,000009,000888,000930,002514,000710,000623,002289,002101,002870,002053,002165,002567,000952,002535,002866,002598,603466,002520,002756,002737,000927,002455,002039,000795,000851,000564,000995,002129,000803,000977,002075,002242,002572,000598,002317,002501,002630,002440,000539,002321,002813,000967,603363,002004,002548,002903,002341,002319,000890,002496,603289,603365,002197,002481,000570,603386,002134,002753,000591,002189,002893,000540,002253,002449,000069,000680,000039,002671,000559,000517,002243,000620,002657,002040,002043,002730,002538,002700,002655,002682,002332,002530,002036,002211,002278,000971,002493,002791,002020,002136,000631,002625,000829,000611,002663,000584,000979,002209,002233,002383,002219,000963,002306,000839,002385,002407,002827,002038,002601,002736,002482,000045,000928,002539,002180,002776,000968,002218,000667,002797,000797,002675,000166,000510,000863,002782,000552,002206,002366,002445,000651,000766,002369,002604,002746,000718,000905,002417,000156,002556,000010,002669,000776,002832,000735,002224,002477,002897,000537,002023,002845,002161,000895,002230,002755,002500,002658,000040,000400,000806,002410,002151,002723,002843,000033,002582,002222,002465,000062,000828,000416,002602,002733,002244,002126,002858,000917,002631,000048,002137,002280,002637,002049,002051,002609,000572".split(","));
				List<String> shenCodes3 = Arrays.asList("000683,000061,000566,000813,002112,000721,002011,000790,603233,000716,000960,002699,002831,000687,002329,002667,002840,002554,002890,002267,002766,002564,000782,000004,000838,603602,002199,000536,002198,603661,000599,000713,002335,002664,000921,002127,002202,000815,603321,000953,002611,002839,002307,000581,000635,000756,002207,000693,002692,002713,000685,002345,002451,000610,002229,002644,002534,002078,002320,000622,002191,000883,002201,002818,002133,002403,002028,002141,002433,000672,002232,002712,000858,000822,603396,002297,000568,000929,000720,000919,001979,002735,002281,002204,603648,002517,002902,603157,002223,002334,002862,000018,000543,000750,002041,002724,002869,000989,603378,002047,000717,002336,002555,000554,002532,000973,002104,002892,000671,000732,000530,002381,002889,002811,000410,000859,002426,002296,002418,002466,002162,002497,002439,000617,002312,002488,002050,002246,002340,000679,000861,002610,000985,002594,000628,000630,002847,000551,000982,002193,002578,000553,000712,000778,002176,002159,000021,000429,601086,603278,002828,002550,002677,000957,002583,002759,002867,000819,002716,002279,002390,002528,002562,002519,002393,000035,002570,000423,002589,002376,000988,002033,002906,000407,002255,002037,002615,000966,002158,002750,000159,000923,002553,002792,000761,002091,002895,000420,002016,002505,603359,002105,000063,000606,000402,002648,002817,002781,000656,002252,000996,002592,000933,002513,002380,000659,601326,002833,002069,002185,002286,000762,002046,603055,000333,002452,002579,002757,002606,000733,002896,000681,002436,002546,002313,000032,002302,000993,000548,000573,000789,002330,002260,002591,603079,002349,002441,000603,002247,002511,603129,002474,002175,002303,000999,002810,603722,002820,002323,603882,000059,002446,000426,603776,000802,002523,603937,000518,000932,000799,002789,002261,002729,002178,002167,002600,002714,002147,601949,002762,002059,002748,000783,002022,002627,002425,002698,002694,000902,002305,000571,000837,000547,002017,603916,002506,002793,002065,000826,002722,002726,000027,000008,002464,000550,002438,002308,000893,002333,002745,002620,000090,000534,000850,002295,603103,002106,000608,002613,002780,000408,000629,000723,002435,000157,000856,000415,002122,002342,601019,000506,000998,000823,002400,002284,000862,002076,002368,002674,000728,002470,000869,000780,000695,002715,002626,603605,000561,000661,002301,603533,603976,000939,002597,002107,002394,000825,002119,002742,002768,002772,002886,000513,002447,603619,000001,000816,002125,002471,002014,002239,000516,002089,002614,000078,002816,002701,002727,000036,002287,002774,002842,000931,000338,002263,002114,002495,002576,603181,002623,002397,002586,000969,002346,002690,000511,002467,002200,002363,002068,002621,000906,603922,002884,002437,002676,002531,002761,002499,000070,002226,002829,000529,000006,000501,002508,000949,002557,002848,000818,002504,000972,002693,603829,000650,002769,002708,000962,002079,002409,000019,000055,000596,603076,002061,603607,002031,000961,002387,002783,002498,002649".split(","));
				List<StockMarket> shenStocks1 = stockMarketService.listStockMarket(shenCodes1);
				List<StockMarket> shenStocks2 = stockMarketService.listStockMarket(shenCodes2);
				List<StockMarket> shenStocks3 = stockMarketService.listStockMarket(shenCodes3);
				for(StockMarket market : shenStocks1) {
					cache.hset(String.format(RedisCacheKeyType.StockMarket.getKey(), "4609"), market.getInstrumentId(), JacksonUtil.encode(market));
				}
				for(StockMarket market : shenStocks2) {
					cache.hset(String.format(RedisCacheKeyType.StockMarket.getKey(), "4609"), market.getInstrumentId(), JacksonUtil.encode(market));
				}
				for(StockMarket market : shenStocks3) {
					cache.hset(String.format(RedisCacheKeyType.StockMarket.getKey(), "4609"), market.getInstrumentId(), JacksonUtil.encode(market));
				}
				// 4353	上证指数
				List<String> shangCodes1 = Arrays.asList("603168,600528,600523,600884,601878,600636,600793,600775,601898,601607,600673,603008,600588,601998,603585,600010,600477,600539,600753,601798,600771,600139,600157,603617,601126,601198,603200,600116,603488,600845,600702,600215,600298,600814,600790,603358,600706,600444,600037,600054,600326,601388,603126,600668,603335,601699,600329,603806,601163,603158,600236,600246,600202,600999,601001,600346,600068,601377,600705,600268,603078,603566,600589,601689,601222,600660,600511,601965,600138,601678,603703,600130,600641,600695,601137,603139,600066,600718,601666,600654,600873,600332,603077,600257,600605,600466,600872,600027,600351,601318,600011,600191,603178,600580,600280,601518,600735,600220,600755,600780,601188,600131,600017,600133,600195,600485,600277,600560,600396,603000,600502,600206,600258,600467,600674,601818,600317,600540,600330,600297,601628,600282,603690,600400,601038,603366,603002,603855,603959,600526,603029,600713,600984,600365,603612,600791,600822,600275,601668,600091,600060,601929,600826,600272,600088,600051,600515,600731,600101,600638,600908,601200,600153,600777,600103,600438,600250,600122,600877,600379,600773,603938,603676,600708,600058,603559,600593,600229,603777,600339,600168,600350,601881,603598,600683,600820,600278,601700,603188,600742,603199,600312,603968,600509,600479,601608,600760,603883,600118,600551,600714,603958,601011,600085,600581,600825,600863,600255,600732,600425,600900,603698,600882,600383,600162,600769,600179,601928,601021,600796,600227,600397,600770,603208,600446,603028,600006,600596,600893,600129,600663,603239,600132,603717,601007,603316,601616,600746,600309,600177,603788,600496,600395,603298,600520,601366,600965,601398,603606,600829,600363,600463,601005,600724,601117,603899,600662,603707,600319,600855,603966,603223,600416,601288,600348,603226,603898,600221,600501,600536,600804,600180,600617,601106,600543,600592,603308,601880,601997,600452,603601,600210,603858,600213,600628,603328,603969,603429,603996,600448,600629,603600,600812,600977,600789,603669,601899,600388,600642,601311,600996,600322,603377,600919,600143,603036,603197,600193,600237,603011,600345,600917,603269,603990,603229,600026,603399,603866,600266,600076,600744,603660,600704,600269,600998,600979,600962,603326,600517,601088,600787,600859,600981,600611,603031,600031,600223,601028,601058,600874,603218,600302,600387,601727,601888,603043,600386,600313,600408,600570,600959,600682,601900,603133,603398,600982,603288,600460,600183,603616,600831,601113,603933,600252,600745,600795,601801,603238,603085,600491,603839,603919,600081,600389,600816,600583,601018,603679,600620,603688,603861,600507,603025,603801,600373,603727,600680,603021,600548,603339,600469,603089,600338,603196,603538,603716,600488,600510,600729,600315,601116,603186,600021,600390,600598,603787,601158,603320,600353,600096,600597,601677,603986,600657,601012,600418,600267,600675,600768,600489,601238,601799,600599,600600,603988,600375,601099,600538,603767,603010,603099,600120,600310,600655,600846,600175,600260,600615,600887,603738,600824,600359,600579,600895,603123,600892,601500,600203,600530,603886,600555,601127,603599,603360,600487,600797,601228,600115,600410,601669,600594,600690,603708,601118,600119,600197,600308,601633,600150,603169,600422,600707,603100,600336,603611,603300,603929,600008,601258,603667,600362,600113,600612,600686,600743,600114,603811,600235,600715,603926,603041,601225,601890,600072,600259,600111,600623,600247,600300,600749,603768,603380,600121,600456,600776,600108,600976,600262,600983,603896".split(","));
				List<String> shangCodes2 = Arrays.asList("600819,600415,600781,600975,600378,600566,600856,600240,601139,600127,601918,603116,600449,603569,600074,600056,603630,603030,601208,600161,600961,601788,601020,600265,603701,600211,600867,600004,600493,600761,600868,600711,603385,600602,600717,600608,603578,603505,603656,603900,600222,600621,601988,600036,603159,600765,601789,603086,600738,601375,600649,600784,603989,603906,600320,600117,601800,603823,600585,601177,600767,600533,600703,601919,600165,603558,600547,600055,600249,601368,600082,600284,603331,600360,601601,603305,600226,601808,600834,600609,600890,603266,600969,600171,600216,601339,600077,603012,600366,600166,600182,603009,600584,600109,600273,600722,600432,603879,603920,600821,603007,600740,603822,603299,603686,601519,600483,600679,600419,600559,600712,600248,600070,603778,600482,600601,601857,601872,600331,601992,600684,600864,603658,601886,603881,600075,600287,600567,600104,603118,600803,601002,601952,600741,603337,601579,603108,600774,601100,601199,600802,600243,600328,600521,603027,603887,600053,600506,600881,601179,600230,601211,600862,600936,600518,600078,600196,600421,600097,603018,600794,600184,603580,600971,600883,600879,600178,603808,600019,600815,600316,600086,603833,603803,600839,600966,600575,603718,600372,601233,603311,600470,600028,603336,603518,600176,603665,600667,600367,600853,603636,603160,603496,600099,600894,600403,601218,601098,603689,600242,600062,600098,603889,600279,600595,600399,601618,600671,600461,600480,603228,600016,601313,601111,600847,600561,600886,603878,600806,600305,603826,600356,603798,601989,603696,600462,601908,600093,600361,603303,600661,601567,600167,603039,603577,600428,600476,603797,601688,600978,603677,600871,600169,600828,601717,603069,600135,600212,603355,603579,600459,603789,600522,600766,600558,600692,601777,603026,603177,600429,603019,600807,600136,600285,601003,603729,600433,603022,600723,600778,600439,603383,603779,601939,601006,600759,601069,603517,600549,600141,601766,600100,600836,600837,600381,600067,600658,601619,603421,600385,600552,600355,600747,600792,603757,601016,600084,600750,600652,600048,600639,603758,600033,600696,601996,600426,600057,600306,603258,601101,600160,600107,600997,600960,600290,600368,601168,600468,600633,603520,600716,600495,600289,600619,600556,600727,600875,600126,600630,603113,600200,600281,600817,603101,601566,603067,603222,603005,600185,600145,600756,603268,600880,600239,600571,600860,603678,600369,600401,603663,600370,601336,600007,600577,603166,600059,600681,600865,603128,603567,603903,601588,600782,603536,603319,600325,600071,600335,600233,600343,600151,603333,600079,600535,600693,603001,600645,600677,603586,600939,600186,600603,603818,600283,600527,603609,603817,600562,603318,600694,603737,600156,603637,603167,600112,600699,603979,600676,600568,601882,603799,600475,603322,600870,600963,600069,600207,600218,600228,600291,600624,600018,600276,600909,601212,601968,603615,603020,600064,601216,600035,603015,603766,600809,600897,603444,600137,600818,603038,600106,600478,601229,603991,600391,600869,603888,600039,600209,603588,600015,600569,603088,603306,600371,603286,600823,600333,600110,600513,600958,601369,600398,603980,603555,603589,600288,600626,600728,600844,600392,600590,600757,600850,600992,600995,600754,600073,600190,600736,601015,603040,600985,600094,601390,600405,600159,603608,603050,603225,600691,601999,603232,601595,600725,603626,600838,600061,600299,600748,600201,603037,600301,600529,600785,600436,603309,603819,603987,600926,603111".split(","));
				List<String> shangCodes3 = Arrays.asList("603868,600256,600393,600557,601933,603639,600377,603456,603090,600563,600576,600635,600188,603528,600219,601877,600610,600637,600841,603618,600634,603885,603315,603515,600009,600063,600606,603117,600125,600697,600512,603180,600687,600337,600586,600800,600352,601186,601985,600155,600217,600420,603728,600148,600614,600835,600497,603081,600000,601169,600618,600719,603203,600578,603638,600380,600737,600354,600891,600967,601858,600653,603058,601009,600152,601231,603098,603501,601636,603508,600292,601010,600764,600810,603060,600505,603877,601008,603909,601558,600050,603416,600622,600545,600678,600801,600023,603960,600734,603603,601599,603096,600303,600486,601166,601969,600689,600128,600644,603066,600158,600358,603165,600616,603369,603313,600858,601901,600376,600973,603032,600739,600020,600643,603955,601328,603393,600481,601991,600582,600170,601107,603908,603389,603668,603977,603595,600406,600423,603556,601958,600208,600251,600340,600321,601155,601226,603838,600573,600848,600885,600318,600986,600458,600701,603901,603918,601866,603985,600516,600876,600022,603035,600123,600647,601718,600038,600827,601515,600537,601000,603003,603338,603345,603816,603023,600225,603699,603323,600587,600805,600519,603843,600763,603131,600490,600990,603628,603227,601333,601811,600192,600720,600500,600993,600751,601600,600987,600861,600089,600232,603198,600271,600187,600688,600293,600146,600970,600730,601966,600798,603859,600685,600327,603928,600172,603999,600323,600843,600231,601128,600311,600851,603189,603179,600980,603936,600640,603998,603330,600898,600546,600811,600565,603033,600198,600857,600095,600409,600604,603138,603800,603568,603939,601611,600455,600499,603387,600854,603368,600613,603828,603869,600261,600234,600783,600896,603006,600503,600531,600666,600833,600726,600030,600295,600435,600808,603633,603993,603042,600532,600080,603519,603726,600173,600382,600733,600012,600163,600830,600665,600090,600241,600199,603388,600698,603997,600988,603017,600508,603016,600052,601555,600238,600664,600270,600710,600149,600498,600721,600029,600083,600550,600758,600889,600525,600105,600307,600189,600572,600888,600648,600779,600866,600650,600651".split(","));
				List<StockMarket> shangStocks1 = stockMarketService.listStockMarket(shangCodes1);
				List<StockMarket> shangStocks2 = stockMarketService.listStockMarket(shangCodes2);
				List<StockMarket> shangStocks3 = stockMarketService.listStockMarket(shangCodes3);
				for(StockMarket market : shangStocks1) {
					cache.hset(String.format(RedisCacheKeyType.StockMarket.getKey(), "4353"), market.getInstrumentId(), JacksonUtil.encode(market));
				}
				for(StockMarket market : shangStocks2) {
					cache.hset(String.format(RedisCacheKeyType.StockMarket.getKey(), "4353"), market.getInstrumentId(), JacksonUtil.encode(market));
				}
				for(StockMarket market : shangStocks3) {
					cache.hset(String.format(RedisCacheKeyType.StockMarket.getKey(), "4353"), market.getInstrumentId(), JacksonUtil.encode(market));
				}
				// 4621	创业板指
				List<String> chuangCodes1 = Arrays.asList("300263,300447,300043,300228,300464,300553,300497,300274,300695,300532,300196,300453,300512,300151,300188,300550,300278,300055,300095,300019,300206,300576,300159,300551,300042,300522,300608,300076,300094,300625,300333,300529,300595,300693,300569,300033,300315,300096,300398,300434,300468,300502,300046,300346,300586,300559,300264,300144,300438,300505,300115,300158,300006,300012,300670,300452,300080,300176,300419,300430,300327,300606,300063,300515,300723,300273,300238,300240,300192,300481,300128,300658,300306,300561,300542,300351,300418,300318,300201,300543,300226,300507,300579,300023,300417,300591,300280,300510,300648,300149,300359,300478,300643,300147,300234,300320,300029,300687,300051,300545,300521,300022,300449,300071,300067,300460,300519,300028,300268,300316,300516,300275,300413,300527,300014,300117,300389,300303,300628,300647,300294,300491,300711,300170,300441,300097,300365,300037,300040,300283,300428,300154,300015,300168,300420,300713,300233,300650,300231,300336,300370,300490,300203,300616,300088,300682,300712,300578,300182,300509,300161,300271,300253,300640,300091,300526,300208,300391,300627,300654,300661,300675,300066,300199,300589,300137,300337,300603,300546,300054,300074,300314,300633,300387,300393,300609,300003,300518,300431,300385,300129,300537,300065,300329,300211,300324,300269,300340,300469,300107,300562,300707,300720,300688,300237,300202,300580,300171,300629,300259,300132,300191,300092,300146,300189,300068,300197,300552,300105,300296,300534,300069,300500,300641,300575,300198,300005,300384,300106,300219,300571,300715,300035,300645,300706,300590,300148,300486,300248,300406,300286,300433,300676,300239,300102,300669,300513,300563,300600,300666,300113,300032,300439,300523,300124,300207,300440,300432,300437,300013,300573,300367,300155,300556,300592,300386,300009,300319,300290,300410,300272,300442,300517,300103,300525,300725,300372,300597,300710,300466,300482,300116,300717,300285,300119,300123,300411,300016,300474,300483,300345,300300,300039,300399,300141,300344,300401,300267,300038,300284,300602,300708,300172,300287,300403,300470,300218,300360,300157,300685,300257,300492,300045,300150,300570,300125,300062,300166,300582,300680,300030,300485,300162,300282,300683,300167,300254,300325,300348,300636,300235,300445,300681,300304,300313,300639,300276,300205,300702,300705,300604,300412,300010,300585,300567,300224,300549,300004,300488,300697,300353,300289,300354,300396,300443,300018,300142,300508,300657,300311,300134,300383,300204,300322,300244,300309,300020,300217,300656,300070,300619,300663,300099,300548,300335,300025,300651,300180,300258,300536,300593,300363,300458,300184,300307,300703,300463,300499,300690,300093,300425,300448,300610,300047,300127,300001,300426,300368,300617,300637,300671,300118,300477,300446,300514,300587,300613,300120,300145,300121,300194,300689,300588,300721,300471,300143,300277,300506,300541,300301,300338,300716,300251,300058,300245,300017,300479,300031,300139,300108,300496,300511,300349,300053,300709,300679,300249,300034,300073,300305,300435,300114,300177,300621,300686,300312,300465,300101,300392,300061,300077,300104,300160,300459,300222,300662,300422,300450,300297,300048,300181,300098,300423,300558,300673,300255,300476,300584,300472,300461,300021,300246,300153,300232,300356,300696,300241,300409,300632,300085,300214,300473,300110,300086,300084,300026,300436,300659,300140,300131,300427,300090,300611,300612,300421,300698,300310,300164,300126,300369,300572,300183,300381,300243,300404,300371,300538,300225,300109,300334".split(","));
				List<String> chuangCodes2 = Arrays.asList("300415,300501,300402,300691,300210,300002,300111,300649,300480,300064,300503,300133,300057,300475,300375,300250,300169,300631,300236,300087,300332,300660,300165,300362,300138,300227,300358,300082,300163,300653,300405,300247,300089,300052,300665,300535,300667,300215,300220,300377,300229,300339,300583,300601,300081,300487,300357,300382,300326,300622,300540,300374,300379,300457,300317,300528,300342,300223,300330,300376,300424,300484,300366,300677,300618,300498,300638,300178,300279,300281,300262,300292,300416,300596,300652,300179,300190,300623,300059,300135,300130,300075,300328,300270,300455,300174,300701,300565,300112,300260,300554,300388,300655,300560,300719,300078,300373,300568,300056,300298,300242,300615,300599,300122,300252,300598,300495,300668,300036,300261,300347,300390,300555,300407,300722,300200,300156,300049,300323,300566,300539,300700,300620,300531,300718,300380,300530,300044,300378,300331,300533,300173,300024,300642,300007,300395,300581,300678,300414,300008,300083,300288,300072,300343,300364,300299,300175,300489,300011,300079,300216,300308,300195,300699,300230,300577,300726,300630,300394,300350,300494,300672,300355,300212,300408,300027,300321,300050,300185,300213,300293,300256,300451,300692,300100,300295,300429,300456,300291,300635,300547,300266,300607,300626,300221,300557,300341,300444,300193,300041,300209,300605,300397,300136,300187,300400,300462,300467,300493,300265,300520,300302,300152,300352".split(","));
				List<StockMarket> chuangStocks1 = stockMarketService.listStockMarket(chuangCodes1);
				List<StockMarket> chuangStocks2 = stockMarketService.listStockMarket(chuangCodes2);
				for(StockMarket market : chuangStocks1) {
					cache.hset(String.format(RedisCacheKeyType.StockMarket.getKey(), "4621"), market.getInstrumentId(), JacksonUtil.encode(market));
				}
				for(StockMarket market : chuangStocks2) {
					cache.hset(String.format(RedisCacheKeyType.StockMarket.getKey(), "4621"), market.getInstrumentId(), JacksonUtil.encode(market));
				}
			} finally {
				initTask();
			}
		}

	}

}
