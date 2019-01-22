package com.phpuaca.filter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.phpuaca.util.PhpMethodResolver;
import com.phpuaca.util.PhpParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final public class FilterFactory {

    private FilterConfig config;

    private FilterFactory() {
        config = new FilterConfig();
        config
                .add(new FilterConfigItem("\\PHPUnit\\Framework\\MockObject\\MockBuilder", "setMethods", 1, MockBuilderFilter.class))
                .add(new FilterConfigItem("\\PHPUnit\\Framework\\TestCase", "getMock", 2, MockBuilderFilter.class))
                .add(new FilterConfigItem("\\PHPUnit\\Framework\\TestCase", "getMockClass", 2, MockBuilderFilter.class))
                .add(new FilterConfigItem("\\PHPUnit\\Framework\\TestCase", "getMockForAbstractClass", 7, MockBuilderFilter.class))
                .add(new FilterConfigItem("\\PHPUnit\\Framework\\TestCase", "getMockForTrait", 7, MockBuilderFilter.class))
                .add(new FilterConfigItem("\\PHPUnit\\Framework\\MockObject\\Builder\\InvocationMocker", "method", 1, InvocationMockerFilter.class))
                .add(new FilterConfigItem("\\PHPUnit\\Framework\\MockObject\\MockObject", "method", 1, InvocationMockerFilter.class))
                .add(new FilterConfigItem("\\PHPUnit_Framework_MockObject_MockBuilder", "setMethods", 1, MockBuilderFilter.class))
                .add(new FilterConfigItem("\\PHPUnit_Framework_TestCase", "getMock", 2, MockBuilderFilter.class))
                .add(new FilterConfigItem("\\PHPUnit_Framework_TestCase", "getMockClass", 2, MockBuilderFilter.class))
                .add(new FilterConfigItem("\\PHPUnit_Framework_TestCase", "getMockForAbstractClass", 7, MockBuilderFilter.class))
                .add(new FilterConfigItem("\\PHPUnit_Framework_TestCase", "getMockForTrait", 7, MockBuilderFilter.class))
                .add(new FilterConfigItem("\\PHPUnit_Framework_MockObject_Builder_InvocationMocker", "method", 1, InvocationMockerFilter.class))
                .add(new FilterConfigItem("\\PHPUnit_Framework_MockObject_MockObject", "method", 1, InvocationMockerFilter.class))
                .add(new FilterConfigItem("\\MethodMock", "resetMethodCalledStack", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\MethodMock", "getCalledArgs", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\MethodMock", "isMethodCalled", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\MethodMock", "countMethodCalled", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\MethodMock", "revertMethod", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\MethodMock", "interceptMethodByCode", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\MethodMock", "interceptMethod", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\MethodMock", "mockMethodResult", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\MethodMock", "mockMethodResultByMap", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\MethodMock", "revertMethodResult", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\MethodMock", "callProtectedMethod", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\TestLib\\Helper", "getProtectedPropertyValue", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\TestLib\\Helper", "setProtectedPropertyValue", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\TestLib\\Helper", "callProtectedMethod", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\PHPUnit_Helper", "getProtectedPropertyValue", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\PHPUnit_Helper", "setProtectedPropertyValue", 2, MethodMockFilter.class))
                .add(new FilterConfigItem("\\PHPUnit_Helper", "callProtectedMethod", 2, MethodMockFilter.class));
    }

    public static FilterFactory getInstance() {
        return FilterFactoryHolder.INSTANCE;
    }

    @Nullable
    public Filter getFilter(@NotNull PsiElement parameter) {
        PsiElement parentParameter = PsiTreeUtil.getParentOfType(parameter, ArrayCreationExpression.class);
        if (parentParameter != null) {
            parameter = parentParameter;
        }

        MethodReference methodReference = PsiTreeUtil.getParentOfType(parameter, MethodReference.class);
        if (methodReference == null) {
            return null;
        }

        Method resolvedMethod = (new PhpMethodResolver()).resolveByMethodReference(methodReference);
        if (resolvedMethod == null) {
            return null;
        }

        PhpParameter phpParameter = new PhpParameter(parameter);
        PhpClass resolvedClass = resolvedMethod.getContainingClass();
        if (resolvedClass == null) {
            return null;
        }

        String methodName = resolvedMethod.getName();
        int parameterNumber = phpParameter.getNumber();

        do {
            String className = resolvedClass.getFQN();
            FilterConfigItem filterConfigItem = config.getItem(className, methodName);
            if (filterConfigItem != null && filterConfigItem.getParameterNumber() == parameterNumber) {
                Class<?> filterClass = filterConfigItem.getFilterClass();
                FilterContext filterContext = new FilterContext(filterConfigItem, methodReference);
                return getFilter(filterClass, filterContext);
            }
        }
        while ((resolvedClass = resolvedClass.getSuperClass()) != null);

        return null;
    }

    @Nullable
    private Filter getFilter(@NotNull Class<?> filterClass, @NotNull FilterContext filterContext) {
        Filter filter;
        try {
            filter = (Filter) filterClass.getDeclaredConstructor(FilterContext.class).newInstance(filterContext);
        } catch (Exception e) {
            filter = null;
        }
        return filter;
    }

    public FilterConfig getConfig() {
        return new FilterConfig(config);
    }

    private static class FilterFactoryHolder {
        public static final FilterFactory INSTANCE = new FilterFactory();
    }
}
