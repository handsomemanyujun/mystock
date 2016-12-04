/*package com.yujun.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.yujun.calculate.TradeOrder;
import com.yujun.calculate.impl.HighAndLowPriceCal;
import com.yujun.calculate.impl.TenPercentAndOther;
import com.yujun.domain.OnlinePriceDO;
import com.yujun.domain.OrderDO;
import com.yujun.domain.StockDO;
import com.yujun.domain.Task;
import com.yujun.util.DateUtil;
import com.yujun.util.Money;

public class Control {
	Logger log = Logger.getLogger(this.getClass());
	
	TradeOrder highAndLowPriceCal = new HighAndLowPriceCal();
	TradeOrder tenPercentAndOther = new TenPercentAndOther();
	static List<Task> taskList = new ArrayList<Task>();
	static boolean isTest = true;
	public void init() { 
		TdxClient tdxClient1 = new TdxClient(1);
		TdxClient tdxClient2 = new TdxClient(2);
		
		taskList.add(new Task(new StockDO("000838",21000,new Money("11.80")),tenPercentAndOther,tdxClient1));
		taskList.add(new Task(new StockDO("600196",10000,new Money("23.39")),highAndLowPriceCal,tdxClient2));
		taskList.add(new Task(new StockDO("600886",21000,new Money("9.03")),highAndLowPriceCal,tdxClient2));
		taskList.add(new Task(new StockDO("600018",39900,new Money("6.83")),highAndLowPriceCal,tdxClient2));
		taskList.add(new Task(new StockDO("300085",7300,new Money("32.3")),tenPercentAndOther,tdxClient2));
		
	}
	
	public void start() throws Exception {
		log.info("program start");
		while(true){
			if(DateUtil.isOpenTradeTime()){
				if(taskList.isEmpty()) {
					init();
				} 
				if(DateUtil.needCancelOrder()) {	// ����ǰ���һ���ӣ��ⵥ
					cancle();
				} else {
					orders();
				}
			} else {
				taskList.clear();
			}
			
			TimeUnit.SECONDS.sleep(60);
		}
	}
	public void orders() {
		log.info("==============orders start======================");
		for(Task task : taskList) {
			try{ 
				log.info("\n\n��ʼ�µ���"+task.getStockDO());
				String zqCode 			= task.getStockDO().getZqCode();
				StockDO hoding 			= task.getTdxClient().queryStockDO(zqCode);
				if(hoding==null || hoding.getAmount() ==0) {
					log.info("�Ѿ������иù�Ʊ��" + task.getStockDO());
					continue;
				}
				
				OnlinePriceDO online 	= task.getTdxClient().queryMarket(zqCode);
				Map<String,StockDO> lh 	= task.getTradeOrder().calculate(task.getStockDO(),hoding, online);
				
				StockDO target = lh.get("low");	// ���������
				if(target !=null) {
					OrderDO orderDO = task.getTdxClient().haveDelegate(true,zqCode);
					if(orderDO !=null) {
						if(!isTest && !orderDO.getPrice().equals(target.getAvaPrice())) {
							task.getTdxClient().cancleOrder(orderDO);
						} 
					} else {
						orderDO = new OrderDO();
						orderDO.setAmount(((Math.abs(target.getAmount()-hoding.getAmount()))/100)*100);
						orderDO.setBuy(true);
						orderDO.setPrice(target.getAvaPrice());
						orderDO.setZqCode(zqCode);
						if(!isTest) {
							task.getTdxClient().crateOrder(orderDO);
						}
					}
				}
				target = lh.get("high");	// ����������
				if(target !=null) {
					OrderDO orderDO = task.getTdxClient().haveDelegate(false,zqCode);
					if(orderDO !=null) {
						if(!isTest && !orderDO.getPrice().equals(target.getAvaPrice())) {
							task.getTdxClient().cancleOrder(orderDO);
						} 
					} else {
						orderDO = new OrderDO();
						orderDO.setAmount(((Math.abs(hoding.getAmount()-target.getAmount()))/100)*100);
						orderDO.setBuy(false);
						orderDO.setPrice(target.getAvaPrice());
						orderDO.setZqCode(zqCode);
						if(!isTest) {
							task.getTdxClient().crateOrder(orderDO);
						}
					}
				}
			} catch(Exception e) {
				log.info("error," + task.getStockDO(),e);
			}
			
		}
		log.info("================orders end====================");
		
	}
	
	public void cancle() {
		log.info("\n\n��ʼ�ⵥ");
		for(Task task : taskList) {
			List<OrderDO> orderDOs = task.getTdxClient().queryDelegate();
			for(OrderDO orderDO : orderDOs) {
				if(orderDO.isBuy() && orderDO.isBuy() && orderDO.getStatus() ==OrderDO.WAITING) {
					task.getTdxClient().cancleOrder(orderDO);
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		PropertyConfigurator.configure("log4j.properties");
		Control control= new Control();
		if(!isTest) {
			control.start();
		} else {
			control.test();
		}
	
	}
	
	public void test() {
		monitorStockList = new HashMap<StockDO,TdxClient>();
		TdxClient tdxClient1 = new TdxClient(1);
		monitorStockList.put(new StockDO("600750",5600,new Money("35.9")),tdxClient1);
		init();
		orders();
	}
}
*/