package de.espend.idea.php.phpunit.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class ChainVisitorUtil {
    public static void visit(@NotNull MethodReference methodReference, @NotNull ChainProcessorInterface processor) {
        visit(methodReference, processor, 10);
    }

    private static void visit(@NotNull PsiElement psiElement, @NotNull ChainProcessorInterface processor, int depth) {
        if(depth <= 0) {
            return;
        }

        PsiElement firstChild = psiElement.getFirstChild();
        if(firstChild == null) {
            return;
        }

        if(firstChild instanceof MethodReference) {
            if(!processor.process((MethodReference) firstChild)) {
                return;
            }

            visit(firstChild, processor, --depth);
        } else if(firstChild instanceof FieldReference) {
            PhpPsiElement phpPsiElement = resolveField((FieldReference) firstChild);
            if(phpPsiElement instanceof MethodReference) {
                if(!processor.process((MethodReference) phpPsiElement)) {
                    return;
                }

                visit(phpPsiElement, processor, --depth);
            }
        } else if(firstChild instanceof Variable && !((Variable) firstChild).isDeclaration()) {
            for (PhpPsiElement phpPsiElement : resolveVariable((Variable) firstChild)) {
                if(phpPsiElement instanceof MethodReference && !processor.process((MethodReference) phpPsiElement)) {
                    return;
                }

                visit(phpPsiElement, processor, --depth);
            }
        }
    }

    @NotNull
    private static Collection<PhpPsiElement> resolveVariable(@NotNull Variable variable) {
        String name = variable.getName();

        Method methodScope = PhpPsiUtil.getParentByCondition(variable, Method.INSTANCEOF);
        if(methodScope == null) {
            return Collections.emptyList();
        }

        Collection<PhpPsiElement> psiElements = new ArrayList<>();

        for (Variable variable1 : PsiTreeUtil.collectElementsOfType(methodScope, Variable.class)) {
            if(!variable1.isDeclaration() || !name.equals(variable1.getName())) {
                continue;
            }

            PsiElement parent = variable1.getParent();
            if(!(parent instanceof AssignmentExpression)) {
                continue;
            }

            PhpPsiElement value = ((AssignmentExpression) parent).getValue();
            if(value == null) {
                continue;
            }

            psiElements.add(value);
        }

        return psiElements;
    }

    @Nullable
    private static PhpPsiElement resolveField(@NotNull FieldReference fieldReference) {
        String name = fieldReference.getName();
        if(name != null) {
            // find method scope, we not directly search for class as Method is our parent scope
            Method methodScope = PhpPsiUtil.getParentByCondition(fieldReference, Method.INSTANCEOF);
            if(methodScope != null) {
                PhpClass phpClass = methodScope.getContainingClass();
                if(phpClass != null/* && PhpUnitPluginUtil.isTestClassWithoutIndexAccess(phpClass) */) {
                    Method method = phpClass.findOwnMethodByName("setUp");

                    // "setUp" is our "constructor" for test classes
                    if(method != null) {
                        for (AssignmentExpression assignmentExpression : PsiTreeUtil.collectElementsOfType(method, AssignmentExpression.class)) {
                            PhpPsiElement variable = assignmentExpression.getVariable();

                            // remember or field name and attach is for a later resolve
                            if(variable instanceof FieldReference && name.equals(variable.getName())) {
                                return assignmentExpression.getValue();
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    public interface ChainProcessorInterface {
        boolean process(@NotNull MethodReference methodReference);
    }
}
