package com.joymeng.common.util.dynamicjava;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import com.joymeng.log.GameLog;

public class DynamicClassEngine {
	private static DynamicClassEngine instance = new DynamicClassEngine();

	public static DynamicClassEngine getInstance() {
		return instance;
	}

	private URLClassLoader parentClassLoader;
	private String classpath;

	private DynamicClassEngine() {
		this.parentClassLoader = (URLClassLoader) this.getClass().getClassLoader();
		this.buildClassPath();
	}

	public Class<?> loadFromJavaFile(String className, String fileName) throws Exception {
		File file = new File(fileName);
		if (!file.exists()) {
			throw new FileNotFoundException(fileName);
		}
		if (className == null) {
			className = file.getName().substring(0, file.getName().indexOf('.'));
		}
		return loadFromJavaFile(className, file);
	}
	
	public Class<?> loadFromJavaFile(String className, File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		byte[] bytes = new byte[(int) file.length()];
		fis.read(bytes);
		fis.close();
		String str = new String(bytes);
		return javaCodeToObject(className, str);
	}
	private void buildClassPath() {
		classpath = null;
		StringBuilder sb = new StringBuilder();
		URL[] urls = parentClassLoader.getURLs();
		for (int i = 0 ; i < urls.length ; i++) {
			String p = urls[i].getFile();
			sb.append(p).append(File.pathSeparator);
		}
		classpath = sb.toString();
	}

	public Class<?> javaCodeToObject(String fullClassName, String javaCode) throws Exception {
		long start = System.currentTimeMillis();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		ClassFileManager fileManager = new ClassFileManager(
				compiler.getStandardFileManager(diagnostics, null, null));

		List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
		jfiles.add(new CharSequenceJavaFileObject(fullClassName, javaCode));

		List<String> options = new ArrayList<String>();
		options.add("-encoding");
		options.add("UTF-8");
		options.add("-classpath");
		options.add(this.classpath);

		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
				diagnostics, options, null, jfiles);
		boolean success = task.call();
		// same classloader must load a class once
		@SuppressWarnings("resource")
		DynamicClassLoader classLoader = new DynamicClassLoader(parentClassLoader);
		if (success) {
			JavaClassObject jco = fileManager.getMainJavaClassObject();
			List<JavaClassObject> innerClassJcos = fileManager
					.getInnerClassJavaClassObject();
			if (innerClassJcos != null && innerClassJcos.size() > 0) {
				for (int i = 0 ; i < innerClassJcos.size() ; i++){
					JavaClassObject inner = innerClassJcos.get(i);
					String name = inner.getName();
					name = name.substring(1, name.length() - 6);
					classLoader.loadClass(name, inner);
				}
			}
			Class<?> clazz = classLoader.loadClass(fullClassName, jco);
			if (clazz != null) {
				long end = System.currentTimeMillis();
				GameLog.info(fullClassName + "\tjavaCodeToObject use:" + (end - start) + "ms");
				return clazz;
			}
		} else {
			StringBuffer error = new StringBuffer();
			List<Diagnostic<? extends JavaFileObject>> temp = diagnostics.getDiagnostics();
			for (int i = 0 ; i < temp.size() ; i++){
				Diagnostic<? extends JavaFileObject> diagnostic = temp.get(i);
				error.append(compilePrint(diagnostic));
			}
			GameLog.info("Exception in compile " + fullClassName + "\t" + error);
		}
		return null;
	}

	private String compilePrint(Diagnostic<?> diagnostic) {
		StringBuffer res = new StringBuffer();
		res.append("Code:[" + diagnostic.getCode() + "]\n");
		res.append("Kind:[" + diagnostic.getKind() + "]\n");
		res.append("Position:[" + diagnostic.getPosition() + "]\n");
		res.append("Start Position:[" + diagnostic.getStartPosition() + "]\n");
		res.append("End Position:[" + diagnostic.getEndPosition() + "]\n");
		res.append("Source:[" + diagnostic.getSource() + "]\n");
		res.append("Message:[" + diagnostic.getMessage(null) + "]\n");
		res.append("LineNumber:[" + diagnostic.getLineNumber() + "]\n");
		res.append("ColumnNumber:[" + diagnostic.getColumnNumber() + "]\n");
		return res.toString();
	}
}