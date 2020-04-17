package de.espend.idea.php.phpunit.utils;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PatternUtil {
    @NotNull
    public static PsiElementPattern.Capture<PsiElement> getMethodReferenceWithParameterInsideTokenStringPattern() {
        return PlatformPatterns.psiElement().withParent(getMethodReferenceWithParameterPattern());
    }

    @NotNull
    public static PsiElementPattern.Capture<StringLiteralExpression> getMethodReferenceWithParameterPattern() {
        return PlatformPatterns.psiElement(StringLiteralExpression.class)
            .withParent(PlatformPatterns.psiElement(ParameterList.class)
                .withParent(MethodReference.class)
            );
    }
}
