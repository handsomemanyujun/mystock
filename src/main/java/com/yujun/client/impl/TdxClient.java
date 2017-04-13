package com.yujun.client.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.yujun.client.StockClient;
import com.yujun.domain.FundPoolDO;
import com.yujun.domain.OnlinePriceDO;
import com.yujun.domain.OrderDO;
import com.yujun.domain.StockDO;
import com.yujun.util.Money;
import com.yujun.util.TdxResultUtil;

//@Component
public class TdxClient implements StockClient{
	Logger log = Logger.getLogger(this.getClass());
	public interface TdxLibrary extends Library 
	{
        //基本版函数
		/**
		 * 打开通达信实例
		 */
	    public  void OpenTdx();
	    /**
	     * 闭通达信实例
	     */
	    public  void CloseTdx();
	    
	    /**
	     * 
	     * @param IP	券商交易服务器IP
	     * @param Port	券商交易服务器端口
	     * @param Version	设置通达信客户端的版本号
	     * @param YybID		营业部代码，请到网址 http://www.chaoguwaigua.com/downloads/qszl.htm 查询
	     * @param AccountNo	完整的登录账号，券商一般使用资金帐户或客户号
	     * @param TradeAccount	交易账号，一般与登录帐号相同. 请登录券商通达信软件，查询股东列表，股东列表内的资金帐号就是交易帐号, 具体查询方法请见网站“热点问答”栏目
	     * @param JyPassword	交易密码
	     * @param TxPassword	通讯密码
	     * @param ErrInfo	此API执行返回后，如果出错，保存了错误信息说明。一般要分配256字节的空间。没出错时为空字符串。
	     * @return
	     */
	    public  int Logon(String IP, short  Port, String Version, short  YybID, String AccountNo, String TradeAccount, String JyPassword, String TxPassword, byte[] ErrInfo);
	    public  void Logoff(int ClientID);
	    /**
	     * 查询各种交易数据
	     * @param ClientID 	客户端ID
	     * @param Category	表示查询信息的种类，0资金  1股份   2当日委托  3当日成交     4可撤单   5股东代码  6融资余额   7融券余额  8可融证券
	     * @param Result	此API执行返回后，Result内保存了返回的查询数据, 形式为表格数据，行数据之间通过\n字符分割，列数据之间通过\t分隔。一般要分配1024*1024字节的空间。出错时为空字符串。
	     * @param ErrInfo	同Logon函数的ErrInfo说明
	     */
	    public  void QueryData(int ClientID, int Category,  byte[] Result,  byte[] ErrInfo);
	    /**
	     * 下委托交易证券
	     * @param ClientID	客户端ID
	     * @param Category	表示委托的种类，0买入 1卖出  2融资买入  3融券卖出   4买券还券   5卖券还款  6现券还券
	     * @param PriceType	表示报价方式 0上海限价委托 深圳限价委托 1(市价委托)深圳对方最优价格  2(市价委托)深圳本方最优价格  3(市价委托)深圳即时成交剩余撤销  4(市价委托)上海五档即成剩撤 深圳五档即成剩撤 5(市价委托)深圳全额成交或撤销 6(市价委托)上海五档即成转限价
	     * @param Gddm	股东代码, 交易上海股票填上海的股东代码；交易深圳的股票填入深圳的股东代码
	     * @param Zqdm	证券代码
	     * @param Price	委托价格
	     * @param Quantity	委托数量
	     * @param Result	同上,其中含有委托编号数据
	     * @param ErrInfo	同上
	     */
	    public  void SendOrder(int ClientID, int Category, int PriceType, String Gddm, String Zqdm, float Price, int Quantity, byte[] Result,  byte[] ErrInfo);
	    
	    /**
	     *  撤委托
	     * @param ClientID	客户端ID
	     * @param ExchangeID	交易所ID， 上海1，深圳0(招商证券普通账户深圳是2)
	     * @param hth	表示要撤的目标委托的编号
	     * @param Result	同上
	     * @param ErrInfo	同上
	     */
	    public  void CancelOrder(int ClientID, String ExchangeID, String hth,  byte[] Result,  byte[] ErrInfo);
	    /**
	     * 获取证券的实时五档行情
	     * @param ClientID 	客户端ID
	     * @param Zqdm		证券代码
	     * @param Result	同上
	     * @param ErrInfo	同上
	     */
	    public  void GetQuote(int ClientID, String Zqdm,  byte[] Result,  byte[] ErrInfo);
	    public  void Repay(int ClientID, String Amount, byte[] Result,  byte[] ErrInfo);
	}
	
	
	private byte[] result = new byte[1024 * 1024];
	private byte[] errInfo = new byte[256];
	private int clientID =0;
	private String[] gddm =null;
	protected TdxLibrary tdxLibrary;

	private void cleanResult(){
		result = new byte[1024 * 1024];
		errInfo = new byte[256];
	}
	/**
	 * 查询目前总资金情况
	 * @return
	 * @throws Exception 
	 */
	public FundPoolDO queryFundDO(String userId) {
		FundPoolDO fundPoolDO = null;
		tdxLibrary.QueryData(clientID, 0, result, errInfo);
		String res = Native.toString(result, "GBK");
		cleanResult();
		if(res !=null) {
			String[][] items =TdxResultUtil.parseStr(res);
			fundPoolDO= new FundPoolDO();
			fundPoolDO.setAvailableFunds(new Money(items[1][2]));
			fundPoolDO.setFreezeFunds(new Money(items[1][3]));
			fundPoolDO.setFeatchFunds(new Money(items[1][4]));
			fundPoolDO.setTotalValue(new Money(items[1][5]));
			fundPoolDO.setStockValue(new Money(items[1][6]));
			
		}
		log.info(fundPoolDO);
		return fundPoolDO;
	}
	
	
	/**
	 * 查询目前持有股票情况
	 * @return
	 */
	public List<StockDO> queryStockDO(String userId,String zqdm) {
		List<StockDO> list = new ArrayList<StockDO>();
		tdxLibrary.QueryData(clientID, 1, result, errInfo);
		String res = Native.toString(result, "GBK");
		cleanResult();
		if(res !=null) {
			String[][] items =TdxResultUtil.parseStr(res);
			for (int i = 1; i < items.length; i++) {
				if (!StringUtils.isEmpty(zqdm) && !zqdm.equals(items[i][0])) {
					continue;
				}
				StockDO stockDO = new StockDO();
				stockDO.setZqCode(items[i][0]);
				stockDO.setZqName(items[i][1]);
				Float amout = Float.parseFloat(items[i][2]);
				stockDO.setAmount(amout.longValue());
				amout = Float.parseFloat(items[i][3]);
				stockDO.setSalesAmount(amout.longValue());
				
				stockDO.setAvaPrice(new Money(items[i][4]));
				stockDO.setNowPrice(new Money(items[i][5]));
				stockDO.setNowValue(new Money(items[i][6]));
				stockDO.setFloatValue(new Money(items[i][7]));
				amout = Float.parseFloat(items[i][8]);
				stockDO.setFloatRate(amout);
				log.info("持有股票情况：" + stockDO);
				list.add(stockDO);
			}
		}
		log.info(list);
		return list;
	}
	/**
	 * 是否有委托单
	 * @param isBuy
	 * @param zqdm
	 * @return
	 */
	public OrderDO haveDelegate(String userId,boolean isBuy, String zqdm){
		List<OrderDO> list = queryDelegate(userId,zqdm);
		for(OrderDO orderDO: list) {
			if(orderDO.isBuy() == isBuy && (orderDO.getStatus() == OrderDO.WAITING || orderDO.getStatus() == OrderDO.PART_SUCCESS)) {
				log.info("存在"+ (isBuy ?"买":"卖")+"委托单，委托单是： " + orderDO);
				return orderDO;
			}
		}
		log.info(zqdm +"目前不存在 "+(isBuy ?"买":"卖")+ "委托单");
		return null;
	}
	
	
	/**
	 * 查询委托情况
	 * @return
	 */
	public List<OrderDO> queryDelegate(String userId,String zqdm) {
		List list = new ArrayList<OrderDO>();
		tdxLibrary.QueryData(clientID, 2, result, errInfo);
		String res = Native.toString(result, "GBK");
		cleanResult();
		if(res !=null) {
			String[][] items =TdxResultUtil.parseStr(res);
			for(int i=1 ;i<items.length; i++) {
				if (!StringUtils.isEmpty(zqdm) && !zqdm.equals(items[i][1])) {
					continue;
				}
				OrderDO orderDO = new OrderDO();
				orderDO.setZqCode(items[i][1]);
				orderDO.setZqName(items[i][2]);
				orderDO.setDate(items[i][0]);
				orderDO.setPrice(new Money(items[i][6]));
				orderDO.setOrderId(items[i][12]);
				Float amout = Float.parseFloat(items[i][7]);
				orderDO.setAmount(amout.longValue());
				
				if("买入".equals(items[i][4])) {
					orderDO.setBuy(true);
				}
				orderDO.setStatusDesc(items[i][5]);
				if("已成".equals(items[i][5])|| "废单".equals(items[i][5])) {
					orderDO.setStatus(OrderDO.SUCCESS);
				} else if("已撤".equals(items[i][5])) {
					orderDO.setStatus(OrderDO.CANCLE);
				} else if("部成".equals(items[i][5])) {
					orderDO.setStatus(OrderDO.PART_SUCCESS);
				} else if("部撤".equals(items[i][5])) {
					orderDO.setStatus(OrderDO.PART_CANCLE);
				} else {
					orderDO.setStatus(OrderDO.WAITING);
				}
				list.add(orderDO);
			}
		}
		return list;
	}
	
	/**
	 * 查询目标股票价格
	 * @param zqdm
	 * @return
	 */
	public OnlinePriceDO queryMarket(String userId,String zqdm) {
		tdxLibrary.GetQuote(clientID, zqdm, result, errInfo);
		String res = Native.toString(result, "GBK");
		cleanResult();
		OnlinePriceDO market = null;
		if(res !=null) {
			String[][] items =TdxResultUtil.parseStr(res);
			market = new OnlinePriceDO();
			market.setZqCode(items[1][0]);
			market.setZqName(items[1][1]);
			market.setyPrice(new Money(items[1][2]));
			market.setNsPrice(new Money(items[1][3]));
			market.setNowPrice(new Money(items[1][5]));
		}
		log.info("股票:" +market.getZqName()+ " ，当前的价格是" + market);
		return market;
	}
	/**
	 * 撤单订单
	 * @param zqdm
	 */
	public void cancleOrder(String userId,OrderDO orderDO) {
		log.info("放弃一个买订单" + orderDO);
		if(TdxResultUtil.isSHCode(orderDO.getZqCode())) {	// 上证
			tdxLibrary.CancelOrder(clientID, "1", orderDO.getOrderId(), result, errInfo);
		} else {
			tdxLibrary.CancelOrder(clientID, "0",  orderDO.getOrderId(), result, errInfo);
		}
		cleanResult();
	}
	
	/**
	 * 是否可以下单
	 * @param isBuy
	 * @param zqdm
	 * @return
	 */
	private boolean canOrder(String userId,boolean isBuy, String zqdm){
		int sum =0;
		List<OrderDO> list = queryDelegate(userId,zqdm);
		for(OrderDO orderDO: list) {
			if(orderDO.getStatus() == OrderDO.SUCCESS) {
				if(orderDO.isBuy()) {
					sum ++;
				} else {
					sum --;
				}
			}
		}
		
		sum += isBuy? 1:-1;
		
		return Math.abs(sum) <=2;
	}
	
	/**
	 * 下单
	 * @param zqdm
	 * @param money
	 * @param amount
	 * @param isBuy
	 */
	public void crateOrder(String userId, OrderDO orderDO) {
		if (!canOrder(userId,orderDO.isBuy(), orderDO.getZqCode())) {
			log.info("当天该类型单已经成交超过两次，不在下单" + orderDO);
			return;
		}
		if (orderDO.getAmount() * orderDO.getPrice().getCent() < 20000 * 100) {
			log.info("订单金额小于2w 不考虑买入" + orderDO);
			return;
		}

		log.info("下单，" + orderDO);
		tdxLibrary.SendOrder(clientID, orderDO.isBuy() ? 0
				: 1, 0, TdxResultUtil.isSHCode(orderDO.getZqCode()) ? gddm[0]
				: gddm[1], orderDO.getZqCode(), Float.parseFloat(orderDO
				.getPrice().toString()), (int) orderDO.getAmount(), result,
				errInfo);
		String res = Native.toString(errInfo, "GBK");
		cleanResult();
	}
	
	
	public boolean login(String userId,String passWord,String gddm) {
		// DLL是32位的,因此必须使用jdk32位开发,才能调用DLL;
		// 必须把Trade.dll等4个DLL复制到java工程目录下;
		// java工程必须添加引用 jna.jar, 在 https://github.com/twall/jna 下载 jna.jar
		// 无论用什么语言编程，都必须仔细阅读VC版内的关于DLL导出函数的功能和参数含义说明，不仔细阅读完就提出问题者因时间精力所限，恕不解答。
		tdxLibrary = (TdxLibrary) Native.loadLibrary("trade", TdxLibrary.class);
		tdxLibrary.OpenTdx();
		this.gddm = gddm.split(",");
		// 登录
		clientID = tdxLibrary.Logon("140.207.225.74", (short) 7708, "7.02", (short) 1, userId,
				userId, passWord, "", errInfo);
		if (clientID == -1) {
			System.out.println(Native.toString(errInfo, "GBK"));
			return false;
		} 
		log.error("AccountNo "+userId +"login sucess");
		cleanResult();
		return true;
	}
	
	public void close() {
		tdxLibrary.Logoff(clientID);
		tdxLibrary.CloseTdx();
	}
	
}