package com.phpuaca.completion;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import com.phpuaca.util.PhpClassAdapter;
import com.phpuaca.util.PhpClassResolver;
import com.phpuaca.util.PhpMethodResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ProphecyTypeProvider extends BaseTypeProvider {

    final protected static String TYPE_SEPARATOR = "θ";

    final protected static String METHOD_PROPHESIZE = "prophesize";
    final protected static String METHOD_REVEAL = "reveal";

    final protected static String CLASS_PROPHET = "\\Prophecy\\Prophet";
    final protected static String CLASS_OBJECT_PROPHECY = "\\Prophecy\\Prophecy\\ObjectProphecy";
    final protected static String CLASS_METHOD_PROPHECY = "\\Prophecy\\Prophecy\\MethodProphecy";
    final protected static String CLASS_PHP_UNIT_TEST_CASE = "\\PHPUnit_Framework_TestCase";

    protected HashMap<String, Boolean> mockedMethods = new HashMap<String, Boolean>();

    @Override
    public char getKey() {
        return 'φ';
    }

    @Nullable
    @Override
    public String getType(PsiElement psiElement) {
        if (!isAvailable(psiElement)) {
            return null;
        }

        MethodReference methodReference = (MethodReference) psiElement;
        Method method = (new PhpMethodResolver()).resolveByMethodReference(methodReference);
        if (method == null) {
            return null;
        }

        String signature = methodReference.getSignature();
        String methodName = method.getName();
        if (methodName.equals(METHOD_PROPHESIZE)) {
            PhpClassAdapter phpClassAdapter = getPhpClassAdapterForMethod(method);
            if (phpClassAdapter != null && (phpClassAdapter.isSubclassOf(CLASS_PHP_UNIT_TEST_CASE) || phpClassAdapter.isSubclassOf(CLASS_PROPHET))) {
                return getTypeForProphesize(methodReference);
            }
        } else if (methodName.equals(METHOD_REVEAL) && signature.contains(TYPE_SEPARATOR)) {
            PhpClassAdapter phpClassAdapter = getPhpClassAdapterForMethod(method);
            if (phpClassAdapter != null && phpClassAdapter.isSubclassOf(CLASS_OBJECT_PROPHECY)) {
                return getTypeForReveal(methodReference);
            }
        } else if (isMethodMocked(method) && signature.contains(TYPE_SEPARATOR)) {
            return getTypeForMockedMethod(methodReference);
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Project project) {
        PhpIndex phpIndex = PhpIndex.getInstance(project);
        Collection<PhpClass> collection = new ArrayList<PhpClass>();
        for (String FQN : s.split(TYPE_SEPARATOR)) {
            collection.addAll(phpIndex.getAnyByFQN(FQN));
        }
        return collection;
    }

    @Nullable
    protected String getTypeForProphesize(@NotNull MethodReference methodReference) {
        PhpClass phpClass = (new PhpClassResolver()).resolveByMethodReferenceContainingParameterListWithClassReference(methodReference);
        if (phpClass == null) {
            return null;
        }

        mockMethods(phpClass.getMethods());
        return CLASS_OBJECT_PROPHECY + TYPE_SEPARATOR + phpClass.getFQN();
    }

    protected String getTypeForReveal(@NotNull MethodReference methodReference) {
        String signature = methodReference.getSignature();
        int offsetStart = signature.indexOf(TYPE_SEPARATOR);
        int offsetEnd = signature.indexOf("." + METHOD_REVEAL);
        return signature.substring(offsetStart + TYPE_SEPARATOR.length(), offsetEnd);
    }

    protected String getTypeForMockedMethod(@NotNull MethodReference methodReference) {
        return CLASS_METHOD_PROPHECY;
    }

    protected void mockMethod(@NotNull Method method) {
        String hash = method.getFQN();
        if (!mockedMethods.containsKey(hash)) {
            mockedMethods.put(hash, true);
        }
    }

    protected void mockMethods(Collection<Method> methods) {
        for (Method method : methods) {
            mockMethod(method);
        }
    }

    protected boolean isMethodMocked(Method method) {
        String hash = method.getFQN();
        return mockedMethods.containsKey(hash);
    }
}
