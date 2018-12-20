package com.surecn.moat.moat;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-09-01
 * Time: 20:42
 */
public interface ProcessObserver {

    /**
     * 第一个任务流执行前
     */
    public void onPre();


    public void onNext();

    /**
     *任务流最后执行
     */
    public void onFinally();
}
