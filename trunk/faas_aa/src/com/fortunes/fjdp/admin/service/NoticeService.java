package com.fortunes.fjdp.admin.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.model.Notice;
import com.fortunes.fjdp.admin.model.NoticeMessage;
import com.fortunes.fjdp.admin.model.User;

import net.fortunes.core.service.GenericService;

@Component
public class NoticeService extends GenericService<Notice> {
	
	@Resource private NoticeMessageService noticeMessageService;
	
	public void publishNotice(Notice notice, List<User> users) throws Exception {
		getDefDao().add(notice);
		for(User user : users){
			NoticeMessage message = new NoticeMessage();
			message.setUser(user);
			message.setReaded(false);
			message.setNotice(notice);
			noticeMessageService.add(message);
		}
	}
	
}
