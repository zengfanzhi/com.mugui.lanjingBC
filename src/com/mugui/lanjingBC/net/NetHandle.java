package com.mugui.lanjingBC.net;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mugui.net.NetBag;
import com.mugui.net.SessionContext;

import net.sf.json.JSONObject;

@RestController
@CrossOrigin(value = "*")
public class NetHandle implements com.mugui.net.UdpHandle {
	
	@Autowired
	private ModuleMessage ModuleMessage=null;
	
	
	@RequestMapping(value = { "/**", "**/**", "/**.htm", "/**.html" }, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String httpHandle(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		try {
			SessionContext.AddSession(session);
			NetBag bag = new NetBag();
			bag.setSession(session.getId());
			bag.setType(NetBag.TYPE_HTTP);
			bag.setFunc(request.getParameter("func"));
			bag.setCode(200);
			bag.setData(request.getParameter("data"));
			bag.setFrom_host(request.getRemoteHost());
			bag.setFrom_port(request.getRemotePort());
			bag.setHost(request.getServerName());
			bag.setPort(request.getServerPort());
			bag.setHash(request.getParameter("hash"));
			bag.setTimestamp(System.currentTimeMillis() + "");
			bag = resolveNetBag(bag, session);
			return bag.toString();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String UdpHandle(NetBag bag) {
		try {

			bag.setType(NetBag.TYPE_UDP);
			HttpSession session = SessionContext.getSession(bag.getSession());
			bag = resolveNetBag(bag, session);

			return bag == null ? null : bag.toJsonObject().toString();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析NetBag 到相应的处理位置
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public NetBag resolveNetBag(NetBag bag, HttpSession session) throws UnsupportedEncodingException {
		if (bag == null) {
			bag = new NetBag();
		}
		JSONObject object = new JSONObject();
		if (session == null) {
			object.put("message", "用户登录状态已过期");
			bag.setCode(503);
			bag.setData(object);
			return bag;
		}
		if (StringUtils.isBlank(bag.getFunc())||StringUtils.isBlank(bag.getHash())) {
			object.put("data", bag.toJsonObject());
			object.put("message", "参数错误");
			bag.setCode(503);
			bag.setData(object);
			return bag;
		}
		String str[]= bag.getFunc().split("[.]");
		
		return bag;
	}
}
