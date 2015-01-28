package com.phpuaca.completion.filter;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

final public class FilterFactory {

    private FilterConfig config;

    public FilterFactory()
    {
        config = new FilterConfig();
        config
                .add("PHPUnit_Framework_MockObject_MockBuilder", "setMethods", 1, MockBuilderFilter.class)
                .add("PHPUnit_Framework_TestCase", "getMock", 2, MockBuilderFilter.class)
                .add("PHPUnit_Framework_MockObject_Builder_InvocationMocker", "method", 1, InvocationMockerFilter.class)
                .add("MethodMock", "resetMethodCalledStack", 2, MethodMockFilter.class)
                .add("MethodMock", "getCalledArgs", 2, MethodMockFilter.class)
                .add("MethodMock", "isMethodCalled", 2, MethodMockFilter.class)
                .add("MethodMock", "countMethodCalled", 2, MethodMockFilter.class)
                .add("MethodMock", "revertMethod", 2, MethodMockFilter.class)
                .add("MethodMock", "interceptMethodByCode", 2, MethodMockFilter.class)
                .add("MethodMock", "interceptMethod", 2, MethodMockFilter.class)
                .add("MethodMock", "mockMethodResult", 2, MethodMockFilter.class)
                .add("MethodMock", "mockMethodResultByMap", 2, MethodMockFilter.class)
                .add("MethodMock", "revertMethodResult", 2, MethodMockFilter.class)
                .add("MethodMock", "callProtectedMethod", 2, MethodMockFilter.class)
                .add("PHPUnit_Helper", "getProtectedPropertyValue", 2, MethodMockFilter.class)
                .add("PHPUnit_Helper", "setProtectedPropertyValue", 2, MethodMockFilter.class)
                .add("PHPUnit_Helper", "callProtectedMethod", 2, MethodMockFilter.class);
    }

    @Nullable
    public Filter getFilter(@NotNull PsiElement parameter)
    {
        FilterContext filterContext = new FilterContext(parameter);
        String className = filterContext.getClassName();
        String methodName = filterContext.getMethodName();
        int parameterNumber = filterContext.getParameterNumber();

        Class<?> filterClass = config.getFilterClass(className, methodName, parameterNumber);
        return filterClass == null ? null : getFilter(filterClass, filterContext);
    }

    @Nullable
    private Filter getFilter(@NotNull Class<?> filterClass, @NotNull FilterContext filterContext)
    {
        Filter filter;
        try {
            filter = (Filter) filterClass.getDeclaredConstructor(FilterContext.class).newInstance(filterContext);
        }
        catch (Exception e) {
            filter = null;
        }
        return filter;
    }

    private class FilterConfig {
        Map<String, Class> config;

        public FilterConfig()
        {
            config = new HashMap<String, Class>();
        }

        public FilterConfig add(String className, String methodName, int parameterNumber, Class filterClass)
        {
            String hash = createHash(className, methodName, parameterNumber);
            config.put(hash, filterClass);
            return this;
        }

        @Nullable
        public Class getFilterClass(String className, String methodName, int parameterNumber)
        {
            String hash = createHash(className, methodName, parameterNumber);
            return config.get(hash);
        }

        private String createHash(String className, String methodName, int parameterNumber)
        {
            return className + "::" + methodName + "/" + parameterNumber;
        }
    }
}
