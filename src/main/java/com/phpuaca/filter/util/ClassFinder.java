package com.phpuaca.filter.util;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.Variable;
import com.phpuaca.filter.FilterConfigItem;
import com.phpuaca.filter.FilterFactory;
import com.phpuaca.util.PhpClassResolver;
import com.phpuaca.util.PhpMethodChain;
import com.phpuaca.util.PhpVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final public class ClassFinder {

    @Nullable
    public Result find(@NotNull MethodReference methodReference) {
        String methodNameToFind = "setMethods";
        MethodReference mockBuilderMethodReference = (new PhpMethodChain(methodReference)).findMethodReference("getMockBuilder");
        if (mockBuilderMethodReference == null) {
            String methodName = methodReference.getName();
            if (methodName != null && (methodName.startsWith("getMock") || methodName.startsWith("createMock"))) {
                mockBuilderMethodReference = methodReference;
                if (methodName.startsWith("createMock")) {
                    methodNameToFind = "getMock";
                } else {
                    methodNameToFind = methodName;
                }
            }
        }

        if (mockBuilderMethodReference == null) {
            return null;
        }

        FilterConfigItem filterConfigItem = FilterFactory.getInstance().getConfig().getItem(methodNameToFind);
        if (filterConfigItem == null) {
            return null;
        }

        PhpClass phpClass = (new PhpClassResolver()).resolveByMethodReferenceContainingParameterListWithClassReference(mockBuilderMethodReference);
        if (phpClass == null) {
            return null;
        }

        return new Result(phpClass, filterConfigItem.getParameterNumber());
    }

    @Nullable
    public Result find(@NotNull Variable variable) {
        MethodReference methodReference = (new PhpVariable(variable)).findClosestAssignment();
        return methodReference == null ? null : find(methodReference);
    }

    public class Result {
        private PhpClass phpClass;
        private int parameterNumber;

        public Result(@NotNull PhpClass phpClass, int parameterNumber) {
            this.phpClass = phpClass;
            this.parameterNumber = parameterNumber;
        }

        public PhpClass getPhpClass() {
            return phpClass;
        }

        public int getParameterNumber() {
            return parameterNumber;
        }
    }
}
