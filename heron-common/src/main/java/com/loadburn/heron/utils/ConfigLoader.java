package com.loadburn.heron.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loadburn.heron.enums.CharsetEnum;
import com.loadburn.heron.exceptions.ConfigLoaderException;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.Arrays;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
public class ConfigLoader {

    private ServletContext context;
    private ObjectMapper mapper = new ObjectMapper();
    private static ConfigLoader ourInstance = null;

    private static synchronized void syncInit(ServletContext context) {
        if (ourInstance == null) {
            ourInstance = context == null ? new ConfigLoader(null) : new ConfigLoader(context);
        }
    }

    public static ConfigLoader newInstance(ServletContext context) {
        if (ourInstance == null) {
            syncInit(context);
        }
        return ourInstance;
    }

    public static ConfigLoader newInstance() {
        if (ourInstance == null) {
            syncInit(null);
        }
        return ourInstance;
    }

    public ConfigLoader(ServletContext context) {
        this.context = context;
    }

    public String loadConfig(String path) {
        InputStream stream = loadConfigOfStream(path);
        try {
            return read(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转换json字符串为对象
     *
     * @param json  字符串
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> T convertJsonToTObject(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }

    // ==============================================================

    /**
     * @param path
     * @return
     */
    public InputStream loadConfigOfStream(String path) {
        InputStream stream = getClass().getResourceAsStream("/" + path);

        // 在web上下文中查找模板
        if (null == stream && null != context) {
            stream = open(path, context);
        }

        // 在web-inf下面查找
        if (null == stream && null != context) {
            stream = openWebInf(path, context);
        }

        // 在servlet context下面查找
        if (null == stream && null != context) {
            stream = context.getResourceAsStream(path);
        }

        if (null == stream) {
            String message = String.format(
                    "在包根目录或者WEB-INF/中均不存在[%s]的配置文件", path);
            throw new ConfigLoaderException(message);
        }
        return stream;
    }

    /**
     * 打开文件
     *
     * @param templateName 文件名
     * @param context      web上下文对象
     * @return 输入流
     */
    private static InputStream open(String templateName, ServletContext context) {
        try {
            String path = context.getRealPath(templateName);
            return path == null ? null : new FileInputStream(path);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * 在web-inf下面打开
     *
     * @param templateName 模板名称
     * @param context      web上下文对象
     * @return 输入流{@link InputStream}
     */
    private static InputStream openWebInf(String templateName, ServletContext context) {
        return open("/WEB-INF/" + templateName, context);
    }

    /**
     * 从输入流中读取模板文件内容
     *
     * @param stream 输入流 {@link java.io.InputStream}
     * @return 模板内容
     * @throws java.io.IOException
     */
    private static String read(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, CharsetEnum.UTF8.getText()));
        StringBuilder builder = new StringBuilder();
        try {
            while (reader.ready()) {
                builder.append(reader.readLine());
                builder.append("\n");
            }
        } finally {
            stream.close();
        }
        return builder.toString();
    }

}
