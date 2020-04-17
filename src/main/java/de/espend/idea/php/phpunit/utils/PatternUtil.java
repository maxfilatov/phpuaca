package de.espend.idea.php.phpunit.utils;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
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

    /**
     * Match on PhpClass identifier leaf item: "class FooBarClass extends"
     */
    static public PsiElementPattern.Capture<PsiElement> getClassNamePattern() {
        return PlatformPatterns
            .psiElement(PhpTokenTypes.IDENTIFIER)
            .afterLeafSkipping(
                PlatformPatterns.psiElement(PsiWhiteSpace.class),
                PlatformPatterns.psiElement(PhpTokenTypes.kwCLASS)
            )
            .withParent(PhpClass.class)
            .withLanguage(PhpLanguage.INSTANCE);
    }

    /**
     * Match on "Method" identifier leaf item: "function test() {}"
     */
    static public PsiElementPattern.Capture<PsiElement> getMethodNamePattern() {
        return PlatformPatterns
            .psiElement(PhpTokenTypes.IDENTIFIER)
            .afterLeafSkipping(
                PlatformPatterns.psiElement(PsiWhiteSpace.class),
                PlatformPatterns.psiElement(PhpTokenTypes.kwFUNCTION)
            )
            .withParent(Method.class)
            .withLanguage(PhpLanguage.INSTANCE);
    }
}
