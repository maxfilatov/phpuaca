package com.phpuaca.completion.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import org.jetbrains.annotations.NotNull;

final public class PhpParameter {

    private PsiElement parameter;

    public PhpParameter(@NotNull PsiElement parameter)
    {
        this.parameter = parameter;
    }

    public int getNumber()
    {
        ParameterList parameterList = PsiTreeUtil.getParentOfType(parameter, ParameterList.class);
        if (parameterList != null) {
            int i = 1;
            for (PsiElement p : parameterList.getParameters()) {
                if (p.equals(parameter)) {
                    return i;
                }

                i++;
            }
        }

        return -1;
    }
}
