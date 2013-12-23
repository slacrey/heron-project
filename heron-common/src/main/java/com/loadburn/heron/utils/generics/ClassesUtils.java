package com.loadburn.heron.utils.generics;

import com.google.inject.matcher.Matcher;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-16
 */
public class ClassesUtils {

    private final Matcher<? super Class<?>> matcher;

    ClassesUtils(Matcher<? super Class<?>> matcher) {
        this.matcher = matcher;
    }

    public static ClassesUtils matching(Matcher<? super Class<?>> matcher) {
        return new ClassesUtils(matcher);
    }

    public Set<Class<?>> in(Package pack) {
        String packageName = pack.getName();
        String packageOnly = pack.getName();

        final boolean recursive = true;

        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        String packageDirName = packageOnly.replace('.', '/');

        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);

        } catch (IOException e) {
            throw new ScannerPackageException("包不可读: " + packageDirName, e);
        }

        while (dirs.hasMoreElements()) {
            URL url = dirs.nextElement();
            String protocol = url.getProtocol();

            if ("file".equals(protocol)) {
                findClassesInDirPackage(packageOnly, convertToPath(url), recursive, classes);
            } else if ("jar".equals(protocol)) {
                JarFile jar;

                try {
                    jar = ((JarURLConnection) url.openConnection()).getJarFile();
                } catch (IOException e) {
                    throw new ScannerPackageException("URL 不可读: " + url, e);
                }

                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.charAt(0) == '/') {
                        name = name.substring(1);
                    }
                    if (name.startsWith(packageDirName)) {
                        int idx = name.lastIndexOf('/');
                        if (idx != -1) {
                            packageName = name.substring(0, idx).replace('/', '.');
                        }

                        if ((idx != -1) || recursive) {
                            //it's not inside a deeper dir
                            if (name.endsWith(".class") && !entry.isDirectory()) {
                                String className = name.substring(packageName.length() + 1, name.length() - 6);
                                //include this class in our results
                                if (!"package-info".equalsIgnoreCase(className)) {
                                    addClassToSet(packageName, classes, className);
                                }
                            }
                        }
                    }
                }
            }
        }

        return classes;
    }

    private static String convertToPath(final URL url) {
        String path = url.getPath();
        StringBuilder buf = new StringBuilder();
        for (int i = 0, length = path.length(); i < length; i++) {
            char c = path.charAt(i);
            if ('/' == c) {
                buf.append(File.separatorChar);
            } else if ('%' == c && i < length - 2) {
                buf.append((char) Integer.parseInt(path.substring(++i, ++i + 1), 16));
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    /**
     * 添加类到Set集合
     *
     * @param packageName 包名
     * @param classes     类Set集合
     * @param className   类名
     */
    private void addClassToSet(String packageName, Set<Class<?>> classes, String className) {

        Class<?> clazz = null;
        try {
            clazz = Class.forName(packageName + '.' + className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("不能找到该类", e);
        }
        if (matcher.matches(clazz)) {
            classes.add(clazz);
        }

    }

    /**
     * 在目录或包中查找类
     *
     * @param packageName 包名 String类型
     * @param packagePath 包全路径 String类型
     * @param recursive   是否递归boolean类型
     * @param classes     类Set集合
     */
    private void findClassesInDirPackage(String packageName,
                                         String packagePath,
                                         final boolean recursive,
                                         Set<Class<?>> classes) {
        File dir = new File(packagePath);

        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] dirfiles = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });

        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findClassesInDirPackage(packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                //include class
                addClassToSet(packageName, classes, className);
            }
        }
    }

}
