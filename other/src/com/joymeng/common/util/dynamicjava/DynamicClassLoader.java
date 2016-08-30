
package com.joymeng.common.util.dynamicjava;
 
import java.net.URLClassLoader;
import java.net.URL;
 
public class DynamicClassLoader extends URLClassLoader {
    public DynamicClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }
 
    public Class<?> loadClass(String fullName, JavaClassObject jco) {
        byte[] classData = jco.getBytes();
        Class<?> clazz = this.defineClass(fullName, classData, 0, classData.length);
		return clazz;
    }
}