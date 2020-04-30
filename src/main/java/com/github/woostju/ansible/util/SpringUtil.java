package com.github.woostju.ansible.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringUtil {
	
	private static ApplicationContext ac = null;

	public static ApplicationContext getApplicationContext() {
		return ac;
	}

	public static void setApplicationContext(ApplicationContext ac) {
		SpringUtil.ac = ac;
	}

	public static Object createBean(String className) {
		String beanName = "bean-" + className;
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) ac;
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
		if (!beanFactory.containsBean(beanName)) {
			BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(className);
			bdb.setScope("prototype");
			beanFactory.registerBeanDefinition(beanName, bdb.getBeanDefinition());
		}
		return getApplicationContext().getBean(beanName);
	}

	public static String getPropertyAsString(String key) {
		return ac.getEnvironment().getProperty(key);
	}

	public static int getPropertyAsInt(String key) {
		return Integer.valueOf(ac.getEnvironment().getProperty(key));
	}

	public static long getPropertyAsLong(String key) {
		return Long.valueOf(ac.getEnvironment().getProperty(key));
	}

	public static boolean getPropertyAsBoolean(String key) {
		if(ac.getEnvironment().getProperty(key) == null){
			return false;
		}
		return Boolean.valueOf(ac.getEnvironment().getProperty(key));
	}
	
	/**
	 * 
	 * @param packageName
	 * @param recursive
	 * @param consumer
	 *            usage:
	 *            SpringUtil.loadClasses(Attribute.class.getPackage().getName(),
	 *            true, clazz->{ System.out.println(clazz); } );
	 */
	public static void loadClasses(String packageName, boolean recursive, Consumer<Class<?>> consumer) {
		String packageDirName = packageName.replace('.', '/');
		try {
			Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					loadClasses(filePath, packageName, recursive, consumer);
				} else if ("jar".equals(protocol)) {// 如果是jar包文件
					JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
					findClassesByJar(packageName, jar, consumer);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void findClassesByJar(String pkgName, JarFile jar, Consumer<Class<?>> consumer) {
		String pkgDir = pkgName.replace(".", "/");

		Enumeration<JarEntry> entry = jar.entries();

		JarEntry jarEntry;
		String name, className;
		while (entry.hasMoreElements()) {
			jarEntry = entry.nextElement();

			name = jarEntry.getName();
			if (name.charAt(0) == '/') {
				name = name.substring(1);
			}

			if (jarEntry.isDirectory() || !name.startsWith(pkgDir) || !name.endsWith(".class")) {
				// 非指定包路径， 非class文件
				continue;
			}

			// 去掉后面的".class", 将路径转为package格式
			className = name.substring(0, name.length() - 6);
			try {
				Class<?> clazz = Class.forName(className.replaceAll("/", "."));
				consumer.accept(clazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

	private static void loadClasses(String dirPath, String packageName, boolean recursive,
			Consumer<Class<?>> consumer) {
		File dir = new File(dirPath);
		// 以文件的方式扫描整个包下的文件 并添加到集合中
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().endsWith(".class");
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory() && recursive) {
				loadClasses(file.getAbsolutePath(), packageName + "." + file.getName(), recursive, consumer);
			} else {
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					Class<?> clazz = Class.forName(packageName + '.' + className);
					consumer.accept(clazz);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
