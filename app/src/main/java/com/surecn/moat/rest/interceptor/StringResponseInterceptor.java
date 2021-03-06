package com.surecn.moat.rest.interceptor;


import com.surecn.moat.rest.IHttpResult;
import java.lang.reflect.Type;

/**
 * Created by surecn on 15/8/20.
 * HttpResponse的数据不做任何处理，直接返回
 */
public class StringResponseInterceptor implements ResponseInterceptor {

    @Override
    public Object intercept(IHttpResult result, Type t) {
        return result.getResponseText();
    }
}
