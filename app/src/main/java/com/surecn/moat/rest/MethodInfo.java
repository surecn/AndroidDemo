package com.surecn.moat.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by surecn on 15/8/4.
 */
/*package*/class MethodInfo {

    /*package*/Annotation[] mAnnotations;
    /*package*/Method mMethod;
    /*package*/Type mReturnType;
    /*package*/String mUrl;
    /*package*/Object [] mArgs;
}
