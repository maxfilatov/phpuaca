package com.phpuaca.completion.filter;

import com.jetbrains.php.lang.psi.elements.PhpModifier;

public class MockBuilderFilter extends Filter {

    public MockBuilderFilter(FilterContext context) {
        super(context);

        allowMethods();
        allowModifier(PhpModifier.PUBLIC_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);

        setClassConstantReference(FilterUtil.findClassConstantReference(context.getMethodReference()));
    }
}
