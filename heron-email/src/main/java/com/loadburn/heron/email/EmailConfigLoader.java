package com.loadburn.heron.email;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.loadburn.heron.utils.StringUtils;
import net.jcip.annotations.Immutable;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-11-24
 */
@Immutable
@Singleton
public final class EmailConfigLoader {

    private final Logger logger = LoggerFactory.getLogger(EmailConfigLoader.class.getName());
    public final static String HOST_NAME = "email.hostName";
    public final static String SMTP_PORT = "email.smtpPort";
    public final static String USER_NAME = "email.username";
    public final static String PASS_WORD = "email.password";
    public final static String FORM_EMAIL = "email.from";
    public final static String SSL_ON_CONNECT = "email.SSLOnConnect";

    private final Provider<ServletContext> context;
    private final String EMAIL_CONFIG = "heron.properties";
    private Properties properties = null;

    @Inject
    public EmailConfigLoader(Provider<ServletContext> context) {
        this.context = context;
    }

    protected void loadEmailConfig() {

        if (context == null) {
            try {
                InputStream inputStream = EmailConfigLoader.class.getResourceAsStream(EMAIL_CONFIG);
                properties = read(inputStream);
            } catch (IOException e) {
                logger.warn(EMAIL_CONFIG + "文件找不到", e);
                throw new NoEmailConfigFileException(EMAIL_CONFIG + "文件找不到", e);
            }
        } else {
            try {
                final ServletContext servletContext = context.get();
                // 在web上下文中查找模板
                InputStream inputStream = open(EMAIL_CONFIG, servletContext);
                // 在web-inf下面查找
                if (null == inputStream) {
                    inputStream = openWebInf(EMAIL_CONFIG, servletContext);
                }
                // 在servlet context下面查找
                if (null == inputStream) {
                    inputStream = servletContext.getResourceAsStream(EMAIL_CONFIG);
                }
                properties = read(inputStream);
            } catch (IOException e) {
                logger.warn("不能加载email配置文件 (i/o error): ", e);
                throw new EmailConfigLoaderException("不能加载email配置文件 (i/o error): ", e);
            }
        }

    }

    /**
     * 打开文件
     *
     * @param emailConfigFile 文件名
     * @param context      web上下文对象
     * @return 输入流
     */
    private static InputStream open(String emailConfigFile, ServletContext context) {
        try {
            String path = context.getRealPath(emailConfigFile);
            return path == null ? null : new FileInputStream(path);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * 在web-inf下面打开
     *
     * @param emailConfigFile 模板名称
     * @param context      web上下文对象
     * @return 输入流{@link InputStream}
     */
    private static InputStream openWebInf(String emailConfigFile, ServletContext context) {
        return open("/WEB-INF/conf/" + emailConfigFile, context);
    }

    /**
     * 从输入流中读取模板文件内容
     *
     * @param stream 输入流 {@link java.io.InputStream}
     * @return 模板内容
     * @throws java.io.IOException
     */
    private static Properties read(InputStream stream) throws IOException {
        Properties properties = new Properties();
        if (stream != null) {
            properties.load(stream);
        }
        return properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    public <T> T instanceEmail(T obj) {
        Email email = (Email) obj;
        if (properties == null) {
            loadEmailConfig();
        }
        if (properties != null) {
            email.setHostName(properties.getProperty(EmailConfigLoader.HOST_NAME));
            email.setAuthentication(properties.getProperty(EmailConfigLoader.USER_NAME),
                    properties.getProperty(EmailConfigLoader.PASS_WORD));
            String smtpPort = properties.getProperty(EmailConfigLoader.SMTP_PORT);
            Integer smtp = StringUtils.empty(smtpPort) ? 465 : Integer.valueOf(smtpPort);
            email.setSmtpPort(smtp);
            String ssl = properties.getProperty(EmailConfigLoader.SSL_ON_CONNECT);
            email.setSSLOnConnect(Boolean.valueOf(StringUtils.empty(ssl) ? "false" : ssl));
            try {
                email.setFrom(properties.getProperty(EmailConfigLoader.FORM_EMAIL));
            } catch (EmailException e) {
                logger.warn("不能加载email配置文件 (i/o error): ", e);
                e.printStackTrace();
            }
        }
        return (T) email;
    }
}
