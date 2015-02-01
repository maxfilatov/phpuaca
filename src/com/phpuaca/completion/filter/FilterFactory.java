package com.phpuaca.completion.filter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.phpuaca.completion.util.PhpElementUtil;
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
        PsiElement parentParameter = PsiTreeUtil.getParentOfType(parameter, ArrayCreationExpression.class);
        if (parentParameter != null) {
            parameter = parentParameter;
        }

        MethodReference methodReference = PsiTreeUtil.getParentOfType(parameter, MethodReference.class);
        Method method = PhpElementUtil.resolveMethod(methodReference);
        if (method == null) {
            return null;
        }

        PhpClass phpClass = (PhpClass) method.getParent();
        String methodName = method.getName();
        int parameterNumber = PhpElementUtil.getParameterNumber(parameter);

        while (true) {
            String className = phpClass.getName();
            Class<?> filterClass = config.getFilterClass(className, methodName, parameterNumber);
            if (filterClass != null) {
                FilterContext filterContext = new FilterContext(methodReference, className, methodName, parameterNumber);
                return getFilter(filterClass, filterContext);
            }

            phpClass = phpClass.getSuperClass();
            if (phpClass == null) {
                break;
            }
        }

        return null;
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
