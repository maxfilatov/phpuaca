package com.phpuaca.completion.filter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.refactoring.PhpNameUtil;
import com.phpuaca.completion.util.PhpElementUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    @NotNull
    public static List<String> findDeclaredMethodNames(@Nullable ParameterList parameterList, int parameterNumber)
    {
        List<String> methodNames = new ArrayList<String>();
        if (parameterList != null) {
            PsiElement[] parameters = parameterList.getParameters();
            int position = parameterNumber - 1;
            if (position < parameters.length && parameters[position] instanceof ArrayCreationExpression) {
                ArrayCreationExpression arrayCreationExpression = (ArrayCreationExpression) parameters[position];
                for (PsiElement child : arrayCreationExpression.getChildren()) {
                    methodNames.add(PhpNameUtil.unquote(child.getText()));
                }
            }
        }
        return methodNames;
    }

    @NotNull
    public static List<String> findDeclaredMethodNames(@Nullable MethodReference methodReference, int parameterNumber)
    {
        ParameterList parameterList = methodReference == null ? null : methodReference.getParameterList();
        return findDeclaredMethodNames(parameterList, parameterNumber);
    }
}
