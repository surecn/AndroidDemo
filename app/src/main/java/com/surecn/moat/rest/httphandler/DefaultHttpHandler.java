package com.surecn.moat.rest.httphandler;


import com.surecn.moat.http.HttpForm;
import com.surecn.moat.http.HttpRequest;
import com.surecn.moat.rest.IHttpResult;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-08-30
 * Time: 15:17
 */
public class DefaultHttpHandler implements IHttpHandler {
    @Override
    public IHttpResult request(HttpForm form) {
        return HttpRequest.request(form);
    }
}
