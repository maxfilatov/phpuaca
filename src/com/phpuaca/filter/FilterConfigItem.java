package com.phpuaca.filter;

public class FilterConfigItem {

    private String className;
    private String methodName;
    private int parameterNumber;
    private Class filterClass;

    public FilterConfigItem(String className, String methodName, int parameterNumber, Class filterClass) {
        this.className = className;
        this.methodName = methodName;
        this.parameterNumber = parameterNumber;
        this.filterClass = filterClass;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getParameterNumber() {
        return parameterNumber;
    }

    public Class getFilterClass() {
        return filterClass;
    }
}
