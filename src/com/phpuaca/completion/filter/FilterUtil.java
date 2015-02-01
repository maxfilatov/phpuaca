package com.phpuaca.completion.filter;

import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.ClassConstantReference;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.phpuaca.completion.util.PhpElementUtil;
import org.jetbrains.annotations.Nullable;

public class FilterUtil {

    public static ClassConstantReference findClassConstantReference(@Nullable MethodReference methodReference, @Nullable FilterConfigItemHolder filterConfigItemHolder)
    {
        String methodNameToFind = "setMethods";
        MethodReference mockBuilderMethodReference = PhpElementUtil.findMethodReferenceInChain(methodReference, "getMockBuilder");
        if (mockBuilderMethodReference == null && methodReference != null) {
            String methodName = methodReference.getName();
            if (methodName != null && methodName.startsWith("getMock")) {
                mockBuilderMethodReference = methodReference;
                methodNameToFind = methodName;
            }
        }

        if (mockBuilderMethodReference == null) {
            return null;
        }

        if (filterConfigItemHolder != null) {
            FilterConfigItem filterConfigItem = FilterFactory.getInstance().getConfig().getItem(methodNameToFind);
            filterConfigItemHolder.setItem(filterConfigItem);
        }

        ParameterList parameterList = mockBuilderMethodReference.getParameterList();
        return PsiTreeUtil.getChildOfType(parameterList, ClassConstantReference.class);
    }

    @Nullable
    public static ClassConstantReference findClassConstantReference(@Nullable MethodReference methodReference)
    {
        return findClassConstantReference(methodReference, null);
    }

    @Nullable
    public static ClassConstantReference findClassConstantReference(@Nullable Variable variable)
    {
        MethodReference methodReference = PhpElementUtil.findClosestMethodReferenceForVariableAssignment(variable);
        return methodReference == null ? null : findClassConstantReference(methodReference);
    }
}
