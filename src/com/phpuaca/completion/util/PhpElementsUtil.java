package com.phpuaca.completion.util;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.PhpLangUtil;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.patterns.PhpPatterns;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PhpElementsUtil {
//
    @Nullable
    static public Method getClassMethod(PhpClass phpClass, String methodName) {
        return phpClass.findMethodByName(methodName);
    }
//
    @Nullable
    static public Method getClassMethod(Project project, String phpClassName, String methodName) {

        // we need here an each; because eg Command is non unique because phar file
        for(PhpClass phpClass: PhpIndex.getInstance(project).getClassesByFQN(phpClassName)) {
            Method method = getClassMethod(phpClass, methodName);
            if(method != null) {
                return method;
            }
        }

        return null;
    }
//
    @Nullable
    public static String getStringValue(@Nullable PsiElement psiElement) {
        return getStringValue(psiElement, 0);
    }
//
    @Nullable
    private static String getStringValue(@Nullable PsiElement psiElement, int depth) {

        if(psiElement == null || ++depth > 5) {
            return null;
        }

        if(psiElement instanceof StringLiteralExpression) {
            String resolvedString = ((StringLiteralExpression) psiElement).getContents();
            if(StringUtils.isEmpty(resolvedString)) {
                return null;
            }

            return resolvedString;
        }

        if(psiElement instanceof Field) {
            return getStringValue(((Field) psiElement).getDefaultValue(), depth);
        }

        if(psiElement instanceof PhpReference) {

            PsiReference psiReference = psiElement.getReference();
            if(psiReference == null) {
                return null;
            }

            PsiElement ref = psiReference.resolve();
            if(ref instanceof PhpReference) {
                return getStringValue(psiElement, depth);
            }

            if(ref instanceof Field) {
                PsiElement resolved = ((Field) ref).getDefaultValue();

                if(resolved instanceof StringLiteralExpression) {
                    return ((StringLiteralExpression) resolved).getContents();
                }
            }

        }

        return null;
    }

    public static boolean isEqualClassName(@NotNull PhpClass phpClass, @NotNull PhpClass compareClassName) {
        return isEqualClassName(phpClass, compareClassName.getPresentableFQN());
    }

    public static boolean isEqualClassName(@Nullable PhpClass phpClass, @Nullable String compareClassName) {

        if(phpClass == null || compareClassName == null) {
            return false;
        }

        String phpClassName = phpClass.getPresentableFQN();
        if(phpClassName == null) {
            return false;
        }

        if(phpClassName.startsWith("\\")) {
            phpClassName = phpClassName.substring(1);
        }

        if(compareClassName.startsWith("\\")) {
            compareClassName = compareClassName.substring(1);
        }

        return phpClassName.equals(compareClassName);
    }
}
