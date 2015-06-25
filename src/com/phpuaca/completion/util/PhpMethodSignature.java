package com.phpuaca.completion.util;

public class PhpMethodSignature {

    private String className;
    private String methodName;
    private int parameterIndex;

    public PhpMethodSignature(String className, String methodName, int parameterIndex) {
        this.className = className;
        this.methodName = methodName;
        this.parameterIndex = parameterIndex;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setParameterIndex(int parameterIndex) {
        this.parameterIndex = parameterIndex;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getParameterIndex() {
        return parameterIndex;
    }
}
