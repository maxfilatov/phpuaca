package com.phpuaca.completion.filter;

import com.jetbrains.php.lang.psi.elements.MethodReference;

public class FilterContext {
    private MethodReference methodReference;
    private String className;
    private String methodName;
    private int parameterNumber;

    public FilterContext(MethodReference methodReference, String className, String methodName, int parameterNumber)
    {
        this.methodReference = methodReference;
        this.className = className;
        this.methodName = methodName;
        this.parameterNumber = parameterNumber;
    }

    public MethodReference getMethodReference()
    {
        return methodReference;
    }

    public String getClassName()
    {
        return className;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public int getParameterNumber()
    {
        return parameterNumber;
    }
}
