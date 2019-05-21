package com.mugui.lanjingBC.net;

import java.io.BufferedReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mugui.ModelInterface;
import com.mugui.net.NetBag;
import com.mugui.net.SessionContext;

import net.sf.json.JSONObject;

@RestController
@CrossOrigin(value = "*")
public class NetHandle implements com.mugui.net.UdpHandle {

	private ModuleMessage ModuleMessage = null;

	@RequestMapping(value = { "/*" }, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String httpHandle(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

		try {
			NetBag bag = new NetBag();
			SessionContext.AddSession(session);
			bag.setSession(session.getId());
			bag.setType(NetBag.TYPE_HTTP);
			bag.setCode(200);
			bag.setFrom_host(request.getRemoteHost());
			bag.setFrom_port(request.getRemotePort());
			bag.setHost(request.getServerName());
			bag.setPort(request.getServerPort());
			bag.setTimestamp(System.currentTimeMillis() + "");
			if (request.getParameter("func") != null) {
				bag.setFunc(request.getParameter("func"));
				bag.setData(request.getParameter("data"));
				bag.setHash(request.getParameter("hash"));
			} else {
				BufferedReader reader = request.getReader();
				String string = null;
				String body = "";
				while ((string = reader.readLine()) != null) {
					body += string;
				}
				NetBag temp = NetBag.newInstanceBean(NetBag.class, body);
				bag.setFunc(temp.getFunc());
				bag.setData(temp.getData());
				bag.setHash(temp.getHash());
			}
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
	 * @throws Exception 
	 */
	public NetBag resolveNetBag(NetBag bag, HttpSession session) throws Exception {
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
		if (StringUtils.isBlank(bag.getFunc()) || bag.getFunc().split("[.]").length < 2
				|| StringUtils.isBlank(bag.getHash())) {
			object.put("data", bag.toJsonObject());
			object.put("message", "参数错误");
			bag.setCode(503);
			bag.setData(object);
			return bag;
		}
		String module = bag.getFunc().split("[.]")[0];
		if(ModuleMessage==null) {
			ModuleMessage=(com.mugui.lanjingBC.net.ModuleMessage) System.getProperties().get("ModuleMessage");
		}
		ModelInterface modelInterface = ModuleMessage.get(module);
		if (modelInterface == null) {
			object.put("data", bag.getFunc());
			object.put("message", "模块未找到");
			bag.setCode(503);
			bag.setData(object);
			return bag;
		}
		return (NetBag) modelInterface.invokeFunction("runFunc", bag);

	}
}
