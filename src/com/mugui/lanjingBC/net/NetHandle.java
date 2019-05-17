package com.mugui.lanjingBC.net;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@CrossOrigin(value = "*")
public class NetHandle implements com.mugui.net.UdpHandle  {
	@RequestMapping(value = "/**", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String httpHandle(@RequestBody Map<String, Object> param) {
		return null;
	}

	@Override
	public String UdpHandle(byte[] data) {
		return null;
	}

	
	
	
}
