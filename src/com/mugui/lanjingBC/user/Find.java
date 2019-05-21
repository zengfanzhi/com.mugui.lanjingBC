package com.mugui.lanjingBC.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.mugui.Mugui;
import com.mugui.Annotation.Module;
import com.mugui.lanjingBC.entity.User;
import com.mugui.lanjingBC.user.dao.FindSql;
import com.mugui.net.NetBag;

@Module(name = "user", type = Module.FIND)
@Component
public class Find implements Mugui {
	@Autowired
	private FindSql findSql;
	public Object byUserId(NetBag bag) {
		User user = User.newInstanceBean(User.class, bag.getData());
		return findSql.getJsonBean(user);
	}
}
