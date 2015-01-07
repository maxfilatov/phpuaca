package com.phpuaca.completion.filter;

import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import com.phpuaca.completion.util.PhpElementUtil;

public class MockBuilderFilter extends Filter {

    public MockBuilderFilter(MethodReference entryPoint) {
        super(entryPoint);

        allowMethods();
        allowModifier(PhpModifier.PUBLIC_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PUBLIC_IMPLEMENTED_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_ABSTRACT_DYNAMIC);
        allowModifier(PhpModifier.PROTECTED_IMPLEMENTED_DYNAMIC);

        setClassConstantReference(PhpElementUtil.findClassConstantReference(entryPoint));
    }
}
