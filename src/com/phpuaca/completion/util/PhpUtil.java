package com.phpuaca.completion.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PhpUtil {

//
    protected static boolean isCallTo(Method e, Method[] expectedMethods) {

        PhpClass methodClass = e.getContainingClass();
        if(methodClass == null) {
            return false;
        }

        for (Method expectedMethod : expectedMethods) {

            // @TODO: its stuff from beginning times :)
            if(expectedMethod == null) {
                continue;
            }

            PhpClass containingClass = expectedMethod.getContainingClass();
            if (containingClass != null && expectedMethod.getName().equals(e.getName()) && isInstanceOf(methodClass, containingClass)) {
                return true;
            }
        }

        return false;
    }
//
    @Nullable
    protected static Method getInterfaceMethod(Project project, String interfaceFQN, String methodName) {

        Collection<PhpClass> interfaces = PhpIndex.getInstance(project).getInterfacesByFQN(interfaceFQN);

        if (interfaces.size() < 1) {
            return null;
        }

        return findClassMethodByName(interfaces.iterator().next(), methodName);
    }
//
    @Nullable
    protected static Method getClassMethod(Project project, String classFQN, String methodName) {
        return PhpElementsUtil.getClassMethod(project, classFQN, methodName);
    }
//
    @Nullable
    protected static Method findClassMethodByName(PhpClass phpClass, String methodName) {
        return PhpElementsUtil.getClassMethod(phpClass, methodName);
    }

    protected static boolean isImplementationOfInterface(PhpClass phpClass, PhpClass phpInterface) {
        if (phpClass == phpInterface) {
            return true;
        }

        for (PhpClass implementedInterface : phpClass.getImplementedInterfaces()) {
            if (isImplementationOfInterface(implementedInterface, phpInterface)) {
                return true;
            }
        }

        if (null == phpClass.getSuperClass()) {
            return false;
        }

        return isImplementationOfInterface(phpClass.getSuperClass(), phpInterface);
    }

    public static boolean isInstanceOf(@NotNull PhpClass subjectClass, @NotNull PhpClass expectedClass) {

        // we have equal class instance, on non multiple classes with same name fallback to namespace and classname
        if (subjectClass == expectedClass || PhpElementsUtil.isEqualClassName(subjectClass, expectedClass)) {
            return true;
        }

        if (expectedClass.isInterface()) {
            return isImplementationOfInterface(subjectClass, expectedClass);
        }

        if (null == subjectClass.getSuperClass()) {
            return false;
        }

        return isInstanceOf(subjectClass.getSuperClass(), expectedClass);
    }
//
    public static boolean isCallTo(Method e, String ClassInterfaceName, String methodName) {

        // we need a full fqn name
        if(ClassInterfaceName.contains("\\") && !ClassInterfaceName.startsWith("\\")) {
            ClassInterfaceName = "\\" + ClassInterfaceName;
        }

        return isCallTo(e, new Method[] {
                getInterfaceMethod(e.getProject(), ClassInterfaceName, methodName),
                getClassMethod(e.getProject(), ClassInterfaceName, methodName),
        });
    }
}
