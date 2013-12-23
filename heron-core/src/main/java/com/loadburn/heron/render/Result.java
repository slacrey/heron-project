package com.loadburn.heron.render;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.loadburn.heron.transport.Transport;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-10-16
 */
public abstract class Result<E> {

    public static final Result<?> NO_RESULT = Result.saying();

    public static final String NO_RESPOND_ATTR = "sb_no_respond";


    /**
     * 301 跳转 永久跳转
     * @param uri
     * @return
     */
    public abstract Result<E> seeOther(String uri);

    /**
     * 3XX 跳转
     * @param uri 指定uri
     * @param statusCode http代码
     * @return
     */
    public abstract Result<E> seeOther(String uri, int statusCode);

    /**
     *  媒体类型 例如：application/json
     * @param mediaType 媒体类型
     * @return
     */
    public abstract Result<E> type(String mediaType);

    /**
     * 设置header
     * @param headers
     * @return
     */
    public abstract Result<E> headers(Map<String, String> headers);

    /**
     * 404 错误
     * @return
     */
    public abstract Result<E> notFound();

    /**
     * 401 没有授权错误
     * @return
     */
    public abstract Result<E> unauthorized();

    /**
     * 传输实体到客户端
     * @param transport 继承{@link Transport}的对象
     * @return
     */
    public abstract Result<E> as(Class<? extends Transport> transport);

    /**
     * see {@link #as(Class)}
     * @param transport 继承{@link Transport}的对象
     * @return
     */
    public abstract Result<E> as(Key<? extends Transport> transport);

    /**
     * 302 临时跳转
     */
    public abstract Result<E> redirect(String uri);

    /**
     * 403 禁止访问
     */
    public abstract Result<E> forbidden();

    /**
     * 204 没有内容
     */
    public abstract Result<E> noContent();

    /**
     * 500 通用错误
     */
    public abstract Result<E> error();

    /**
     * 400 错误的请求
     */
    public abstract Result<E> badRequest();

    /**
     *
     */
    public abstract Result<E> template(Class<?> templateKey);

    /**
     * 设置http 状态
     */
    public abstract Result<E> status(int code);

    /**
     * 200 正确请求
     */
    public abstract Result<E> ok();

    /**
     * Used internally by sitebricks. Do NOT call.
     */
    public abstract void render(Injector injector, HttpServletResponse response) throws IOException;

    /**
     * 用于跳转例如： return Respond.saying().redirect("/other");
     */
    public static <E> Result<E> saying() {
        return new ResultBuilder<E>(null);
    }

    /**
     * Returns a reply with an entity that is sent back to the client via the specified
     * transport.
     *
     * @param entity An entity to send back for which a valid transport exists (see
     *   {@link #as(Class)}).
     */
    public static <E> Result<E> with(E entity) {
        return new ResultBuilder<E>(entity);
    }

}
