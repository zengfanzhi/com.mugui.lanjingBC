package com.mugui.lanjingBC.net;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.mugui.ModelInterface;
import com.mugui.Mugui;
import com.mugui.MuguiApplication;
import com.mugui.Annotation.Module;
import com.mugui.DB.SqlModel;
import com.mugui.bean.JsonBean;
import com.mugui.bean.JsonBeanDescription;
import com.mugui.model.ModelManagerInterface;
import com.mugui.net.NetBag;

import net.sf.json.JSONObject;

public class ModuleMessage implements ModelManagerInterface {

	private HashMap<String, ModelInterface> map = null;

	@Override
	public void init() {
		clearAll();
		map = new HashMap<>();

	}

	public void init(Class<?> name) throws Exception {
		init();
		URL url = name.getClassLoader().getResource(name.getName().replaceAll("[.]", "/") + ".class");
		String type = url.getProtocol();
		List<Class<?>> list = new ArrayList<>();
		if (type.equals("file")) {
			list.addAll(getClassNameByFile(new File(url.getPath()).getParentFile(), null, name.getName()));
		} else if (type.equals("jar")) {
			list.addAll(getClassNameByJar(url.getPath(), name.getName()));
		}
		for (Class<?> class_name : list) {
			if (class_name.isAnnotationPresent(Module.class)) {
				if (!Mugui.class.isAssignableFrom(class_name)) {
					throw new RuntimeException("Module  not implements Mugui of " + class_name.getName());
				}

				Module module = class_name.getAnnotation(Module.class);
				ModelInterface modelInterface = null;
				if ((modelInterface = map.get(module.name())) == null) {
					modelInterface = new ModelInterface() {

						@Override
						public void stop() {

						}

						@Override
						public void start() {

						}

						@Override
						public boolean isrun() {
							return false;
						}

						@Override
						public void init() {

						}

						public void init(Module module, Mugui obj) {
							if (hashMap.get(module.type()) != null) {
								throw new RuntimeException("module type:" + module.type() + " is already defined of "
										+ obj.getClass().getName());
							}
							if (Module.ENTITY.equals(module.type())) {
								if (!obj.getClass().isAnnotationPresent(JsonBeanDescription.class)) {
									throw new RuntimeException("ENITITY class is not @JsonBeanDescription:"
											+ module.type() + " " + obj.getClass().getName());
								}
							}
							hashMap.put(module.type(),
									MuguiApplication.Application.getApplicationContext().getBean(obj.getClass()));

						}

						HashMap<String, Mugui> hashMap = new HashMap<>();
						SqlModel sqlModel = new SqlModel();

						public NetBag runFunc(NetBag bag)
								throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
							String func[] = bag.getFunc().split("[.]");
							if (func[1].length() == 2) {
								if ("get".equals(func[1])) {
									bag.setData(sqlModel.getJsonBean((JsonBean) hashMap.get(Module.ENTITY)));
								} else if ("delete".equals(func[1])) {
									bag.setData(sqlModel.removeJsonBean((JsonBean) hashMap.get(Module.ENTITY)));
								} else {
									bag.setCode(503);
									bag.setData(bag.toString());
								}
								return bag;
							}
							Object data;
							try {
								data = hashMap.get(func[1]).invokeFunction(func[2], bag);
								bag.setCode(200);
								bag.setData(data);
								return bag;
							} catch (Exception e) {
								JSONObject object = new JSONObject();
								object.put("data", bag.toString());
								object.put("message", "该模块内部发生错误");
								bag.setCode(503);
								bag.setData(object);
								return bag;
							}
						}

					};
				}
				modelInterface.invokeFunction("init", module, class_name.newInstance());
				map.put(module.name(), modelInterface);
			}
		}
	}

	/**
	 * 从项目文件获取某包下所有类
	 * 
	 * @param filePath     文件路径
	 * @param className    类名集合
	 * @param string
	 * @param childPackage 是否遍历子包
	 * @return 类的完整名称
	 */
	private static List<Class<?>> getClassNameByFile(File file, List<Class<?>> className, String string) {
		List<Class<?>> myClassName = new ArrayList<Class<?>>();
		File[] childFiles = file.listFiles();
		for (File childFile : childFiles) {
			if (childFile.isDirectory()) {
				myClassName.addAll(getClassNameByFile(childFile, myClassName, string));
			} else {
				String childFilePath = childFile.getPath();
				if (childFilePath.endsWith(".class")) {
					childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9,
							childFilePath.lastIndexOf("."));
					childFilePath = childFilePath.replace("\\", ".");
					try {
						myClassName.add(Class.forName(childFilePath));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return myClassName;
	}

	/**
	 * 从jar获取某包下所有类
	 * 
	 * @param jarPath      jar文件路径
	 * @param string
	 * @param childPackage 是否遍历子包
	 * @return 类的完整名称
	 */
	private static List<Class<?>> getClassNameByJar(String jarPath, String string) {
		List<Class<?>> myClassName = new ArrayList<Class<?>>();
		String[] jarInfo = jarPath.split("!");
		String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
		String packagePath = jarInfo[1].substring(1);
		try {
			JarFile jarFile = new JarFile(jarFilePath);
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry jarEntry = entrys.nextElement();
				String entryName = jarEntry.getName();
				if (entryName.endsWith(".class")) {
					if (entryName.startsWith(packagePath)) {
						entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
						try {
							myClassName.add(Class.forName(entryName));
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}

				}
			}
			jarFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myClassName;
	}

	@Override
	public void clearAll() {
		if (map != null)
			map.clear();
	}

	@Override
	public boolean is(String name) {
		if (map == null) {
			throw new NullPointerException("please run init");
		}
		return !map.isEmpty() && map.get(name) != null;
	}

	@Override
	public ModelInterface del(String name) {
		if (map == null) {
			throw new NullPointerException("please run init");
		}
		return map.remove(name);
	}

	@Override
	public ModelInterface get(String name) {
		if (map == null) {
			throw new NullPointerException("please run init");
		}
		return map.get(name);
	}

	@Override
	public void add(String name, Object object) {
		if (map == null) {
			throw new NullPointerException("please run init");
		}

	}
}
