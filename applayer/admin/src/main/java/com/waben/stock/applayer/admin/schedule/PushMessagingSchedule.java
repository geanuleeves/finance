package com.waben.stock.applayer.admin.schedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.waben.stock.applayer.admin.rabittmq.RabbitmqProducer;
import com.waben.stock.interfaces.dto.message.MessagingDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.message.OutsideMessage;
import com.waben.stock.interfaces.service.message.MessagingInterface;
import com.waben.stock.interfaces.service.message.OutsideMessageInterface;

import net.sf.json.JSONObject;


@Component
public class PushMessagingSchedule {

	/**
	 * 推送间隔
	 */
	public static final long Push_Interval = 120 * 1000*100;

	@Autowired
	private OutsideMessageInterface reference;

	@Autowired
	private RabbitmqProducer producer;

	@Autowired
	private MessagingInterface msgReference;

	@PostConstruct
	public void initTask() {
		Timer timer = new Timer();
		timer.schedule(new RetriveTask(), Push_Interval);
	}

	private class RetriveTask extends TimerTask {
		@Override
		public void run() {
			try {
				Response<List<MessagingDto>> response = msgReference.retrieveOutsideMsgType();
				List<MessagingDto> list = response.getResult();
				for (MessagingDto msg : list) {
					OutsideMessage message = new OutsideMessage();
					message.setContent(msg.getContent());
					message.setTitle(msg.getTitle());
					msgReference.sendAll(msg);
					msg.setHasRead(true);

					Map<String, String> map = new HashMap<String, String>();
					map.put("title", msg.getTitle());
					map.put("content", msg.getContent());
					JSONObject json = JSONObject.fromObject(map);
					producer.sendAll(json.toString());

					msgReference.modifyMessaging(msg);
				}
			} finally {
				initTask();
			}
		}
	}
}
