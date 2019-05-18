package org.polkadot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 自动扫描包下面的Class
 */
public class PackageScanner {

    private static final Logger logger = LoggerFactory.getLogger(PackageScanner.class);

    private PackageScanner() {
    }

    public static Set<Class<?>> scan(String packageName, boolean recursive) {
        String packageDirName = packageName.replace('.', '/');
        try {
            Set<Class<?>> classSet = new LinkedHashSet<>();
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                // 如果是以文件形式保存在服务器上
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");

                    classSet.addAll(findClassesInPackageByFile(packageName, filePath, recursive));
                } else if ("jar".equals(protocol)) {
                    classSet.addAll(findClassesInPackageByJar(packageName, url, recursive));
                }
            }
            return classSet;
        } catch (IOException e) {
            logger.error("error happen while scanning package", e);
        }

        return Collections.emptySet();
    }

    private static Set<Class<?>> findClassesInPackageByFile(String packageName, String packagePath, boolean recursive) {
        Set<Class<?>> classSet = new LinkedHashSet<>();
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return classSet;
        }

        File[] dirfiles = dir.listFiles(file -> (recursive && file.isDirectory()) || file.getName().endsWith(".class"));
        if (dirfiles == null) {
            return classSet;
        }
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                Set<Class<?>> subClassSet = findClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive);

                classSet.addAll(subClassSet);
            } else {
                String className = file.getName().substring(0, file.getName().length() - ".class".length());
                Class<?> clazz = loadClass(packageName + "." + className);
                if (clazz != null) {
                    classSet.add(clazz);
                }
            }
        }

        return classSet;
    }

    private static Set<Class<?>> findClassesInPackageByJar(String packageName, URL url, boolean recursive) {
        Set<Class<?>> classSet = new LinkedHashSet<>();
        String packageDirName = packageName.replace('.', '/');
        try {
            JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();

            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.charAt(0) == '/') {
                    name = name.substring(1);
                }
                if (name.startsWith(packageDirName)) {
                    int idx = name.lastIndexOf('/');
                    // 如果是以“/”结尾，是一个包
                    if (idx != -1) {
                        packageName = name.substring(0, idx).replace('/', '.');
                    }

                    if (idx != -1 || recursive) {
                        if (name.endsWith(".class") && !entry.isDirectory()) {
                            String className = name.substring(packageName.length() + 1, name.length() - ".class".length());
                            Class<?> clazz = loadClass(packageName + "." + className);
                            if (clazz != null) {
                                classSet.add(clazz);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("error happen while scanning package", e);
        }

        return classSet;
    }

    private static Class<?> loadClass(String fullClassName) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(fullClassName);
        } catch (ClassNotFoundException e) {
            logger.error("error happen while scanning package", e);
            return null;
        }
    }
}
