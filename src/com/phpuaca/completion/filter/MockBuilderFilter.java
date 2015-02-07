package com.phpuaca.completion.filter;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpModifier;

public class MockBuilderFilter extends Filter {

    public MockBuilderFilter(FilterContext context) {
        super(context);

        allowMethods();
        allowModifier(PhpModifier.PUBLIC_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);

        MethodReference methodReference = context.getMethodReference();
        setClassConstantReference(FilterUtil.findClassConstantReference(methodReference));
        disallowMethods(FilterUtil.findDeclaredMethodNames(methodReference, context.getFilterConfigItem().getParameterNumber()));
    }
}
