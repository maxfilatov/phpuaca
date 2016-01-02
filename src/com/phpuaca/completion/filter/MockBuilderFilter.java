package com.phpuaca.completion.filter;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import com.phpuaca.completion.filter.util.ClassFinder;
import com.phpuaca.completion.util.PhpArrayParameter;

public class MockBuilderFilter extends Filter {

    public MockBuilderFilter(FilterContext context) {
        super(context);

        allowMethods();
        allowModifier(PhpModifier.PUBLIC_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);

        MethodReference methodReference = context.getMethodReference();

        ClassFinder.Result classFinderResult = (new ClassFinder()).find(methodReference);
        if (classFinderResult != null) {
            if (classFinderResult.getClassConstantReference() != null) {
                setClassConstantReference(classFinderResult.getClassConstantReference());
            }
            if (classFinderResult.getClassName() != null) {
                setClassName(classFinderResult.getClassName());
            }
        }

        disallowMethod("__construct");
        disallowMethod("__destruct");

        ParameterList parameterList = methodReference.getParameterList();
        if (parameterList != null) {
            PhpArrayParameter phpArrayParameter = PhpArrayParameter.create(parameterList, context.getFilterConfigItem().getParameterNumber());
            if (phpArrayParameter != null) {
                disallowMethods(phpArrayParameter.getValues());
            }
        }
    }
}
