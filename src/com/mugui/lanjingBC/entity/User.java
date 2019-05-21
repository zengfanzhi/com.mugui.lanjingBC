package com.mugui.lanjingBC.entity;

import org.springframework.stereotype.Component;

import com.mugui.Annotation.Module;
import com.mugui.bean.JsonBean;
import com.mugui.bean.JsonBeanDescription;

@Component
@Module(name="user",type=Module.ENTITY)
@JsonBeanDescription(PRIMARY_KEY="user_id",REMOVE_KEY="user_id",SELECT_KEY="user_id",TABLE_NAME="user",UPDATA_KEY="user_id")
public class User extends JsonBean {
	private String user_id;

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	
}
