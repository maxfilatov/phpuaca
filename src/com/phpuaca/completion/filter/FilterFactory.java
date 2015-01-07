package com.phpuaca.completion.filter;

import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

final public class FilterFactory {

    private Map<String, List<String>> config;

    public FilterFactory()
    {
        config = new HashMap<String, List<String>>();
        config.put("PHPUnit_Framework_MockObject_MockBuilder", Arrays.asList(
                "setMethods"
        ));
        config.put("PHPUnit_Framework_MockObject_Builder_InvocationMocker", Arrays.asList(
                "method"
        ));
        config.put("MethodMock", Arrays.asList(
                "resetMethodCalledStack",
                "getCalledArgs",
                "getCalledArgs",
                "isMethodCalled",
                "countMethodCalled",
                "revertMethod",
                "interceptMethodByCode",
                "interceptMethod",
                "mockMethodResult",
                "mockMethodResultByMap",
                "revertMethodResult",
                "callProtectedMethod"
        ));
        config.put("PHPUnit_Helper", Arrays.asList(
                "getProtectedPropertyValue",
                "setProtectedPropertyValue",
                "callProtectedMethod"
        ));
    }

    public static FilterFactory create()
    {
        return new FilterFactory();
    }

    @Nullable
    public Filter getFilter(@NotNull MethodReference entryPoint)
    {
        Method method = (Method) entryPoint.resolveGlobal(true).iterator().next();
        PhpClass phpClass = (PhpClass) method.getParent();

        String methodName = method.getName();
        String className = phpClass.getName();

        if (!config.containsKey(className) || !config.get(className).contains(methodName)) {
            return null;
        }

        Filter filter = null;
        if (className.equals("PHPUnit_Framework_MockObject_MockBuilder")) {
            filter = new MockBuilderFilter(entryPoint);
        } else if (className.equals("PHPUnit_Framework_MockObject_Builder_InvocationMocker")) {
            filter = new InvocationMockerFilter(entryPoint);
        } else if (className.equals("MethodMock") || className.equals("PHPUnit_Helper")) {
            filter = new MethodMockFilter(entryPoint);
            if (methodName.equals("callProtectedMethod")) {
                filter.allowMethods();
                filter.allowModifier(PhpModifier.PUBLIC_FINAL_DYNAMIC);
                filter.allowModifier(PhpModifier.PUBLIC_FINAL_STATIC);
                filter.allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
                filter.allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_STATIC);
                filter.allowModifier(PhpModifier.PROTECTED_FINAL_DYNAMIC);
                filter.allowModifier(PhpModifier.PROTECTED_FINAL_STATIC);
                filter.allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);
                filter.allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_STATIC);
            } else if (methodName.endsWith("ProtectedPropertyValue")) {
                filter.allowFields();
                filter.allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
                filter.allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_STATIC);
                filter.allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);
                filter.allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_STATIC);
            } else {
                filter.allowMethods();
            }
        }

        return filter;
    }
}
