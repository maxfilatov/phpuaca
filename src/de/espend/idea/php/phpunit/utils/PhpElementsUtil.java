package de.espend.idea.php.phpunit.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpElementsUtil {
    @Nullable
    public static String getStringValue(@Nullable PsiElement psiElement) {
        return getStringValue(psiElement, 0);
    }

    /**
     * Foo::class to its class fqn include namespace
     */
    public static String getClassConstantPhpFqn(@NotNull ClassConstantReference classConstant) {
        PhpExpression classReference = classConstant.getClassReference();
        if(!(classReference instanceof PhpReference)) {
            return null;
        }

        String typeName = ((PhpReference) classReference).getFQN();
        return typeName != null && StringUtils.isNotBlank(typeName) ? StringUtils.stripStart(typeName, "\\") : null;
    }

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
        } else if(psiElement instanceof Field) {
            return getStringValue(((Field) psiElement).getDefaultValue(), depth);
        } else if(psiElement instanceof ClassConstantReference && "class".equals(((ClassConstantReference) psiElement).getName())) {
            // Foobar::class
            return getClassConstantPhpFqn((ClassConstantReference) psiElement);
        } else if(psiElement instanceof PhpReference) {
            PsiReference psiReference = psiElement.getReference();
            if(psiReference == null) {
                return null;
            }

            PsiElement ref = psiReference.resolve();
            if(ref instanceof PhpReference) {
                return getStringValue(psiElement, depth);
            }

            if(ref instanceof Field) {
                return getStringValue(((Field) ref).getDefaultValue());
            }
        }

        return null;
    }

    /**
     * @param subjectClass eg DateTime
     * @param expectedClass eg DateTimeInterface
     */
    public static boolean isInstanceOf(@NotNull PhpClass subjectClass, @NotNull String expectedClass) {
        return new PhpType().add(expectedClass).isConvertibleFrom(new PhpType().add(subjectClass), PhpIndex.getInstance(subjectClass.getProject()));
    }

    /**
     * Resolves MethodReference and compare containing class against implementations instances
     */
    public static boolean isMethodReferenceInstanceOf(@NotNull MethodReference methodReference, @NotNull String expectedClassName) {
        for (ResolveResult resolveResult : methodReference.multiResolve(false)) {
            PsiElement resolve = resolveResult.getElement();

            if(!(resolve instanceof Method)) {
                continue;
            }

            PhpClass containingClass = ((Method) resolve).getContainingClass();
            if(containingClass == null) {
                continue;
            }

            if(!PhpElementsUtil.isInstanceOf(containingClass, expectedClassName)) {
                continue;
            }

            return true;
        }

        return false;
    }

    /**
     * Resolves MethodReference and compare containing class against implementations instances
     */
    public static boolean isMethodReferenceInstanceOf(@NotNull MethodReference methodReference, @NotNull String expectedClassName, @NotNull String methodName) {
        if(!methodName.equals(methodReference.getName())) {
            return false;
        }

        return isMethodReferenceInstanceOf(methodReference, expectedClassName);
    }
}
