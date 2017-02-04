package com.eggsy.rxbus.assist;

/**
 * Created by eggsy on 17-1-31.
 *
 * method's parameters info
 */

public class ProxyParameterInfo {

    /**
     * full class name(include package)
     */
    private String parameterFullName;

    /**
     * type class name
     */
    private String parameterClassName;

    public String getParameterClassName() {
        return parameterClassName;
    }

    public void setParameterClassName(String parameterClassName) {
        this.parameterClassName = parameterClassName;
    }

    public String getParameterFullName() {
        return parameterFullName;
    }

    public void setParameterFullName(String parameterFullName) {
        this.parameterFullName = parameterFullName;
    }
}
