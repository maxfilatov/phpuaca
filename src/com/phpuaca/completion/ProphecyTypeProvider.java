package com.phpuaca.completion;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import com.phpuaca.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ProphecyTypeProvider implements PhpTypeProvider2 {

    final protected static String METHOD_PROPHESIZE = "prophesize";
    final protected static String METHOD_REVEAL = "reveal";

    final protected static String CLASS_PROPHET = "\\Prophecy\\Prophet";
    final protected static String CLASS_OBJECT_PROPHECY = "\\Prophecy\\Prophecy\\ObjectProphecy";
    final protected static String CLASS_METHOD_PROPHECY = "\\Prophecy\\Prophecy\\MethodProphecy";
    final protected static String CLASS_PHP_UNIT_TEST_CASE = "\\PHPUnit_Framework_TestCase";

    final protected static String KEY_NONE = "PROPHECY_TYPE_PROVIDER_NONE:";
    final protected static String KEY_OBJECT_PROPHECY = "PROPHECY_TYPE_PROVIDER_OBJECT:";
    final protected static String KEY_METHOD_PROPHECY = "PROPHECY_TYPE_PROVIDER_METHOD:";

    protected HashMap<String, Boolean> mockedMethods = new HashMap<String, Boolean>();

    @Override
    public char getKey() {
        return 'φ';
    }

    @Nullable
    @Override
    public String getType(PsiElement psiElement) {
        if (!(psiElement instanceof MethodReference)) {
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
                ParameterList parameterList = methodReference.getParameterList();
                PhpClass phpClass = (new PhpClassResolver()).resolveByParameterListContainingClassReference(parameterList);
                if (phpClass != null) {
                    mockMethods(phpClass.getMethods());
                    return KEY_OBJECT_PROPHECY + phpClass.getFQN();
                }
            }
        } else if (methodName.equals(METHOD_REVEAL) && signature.contains(KEY_OBJECT_PROPHECY)) {
            PhpClassAdapter phpClassAdapter = getPhpClassAdapterForMethod(method);
            if (phpClassAdapter != null && phpClassAdapter.isSubclassOf(CLASS_OBJECT_PROPHECY)) {
                int offsetStart = signature.indexOf(KEY_OBJECT_PROPHECY);
                int offsetEnd = signature.indexOf("." + METHOD_REVEAL);
                String className = signature.substring(offsetStart + KEY_OBJECT_PROPHECY.length(), offsetEnd);
                return KEY_NONE + className;
            }
        } else if (isMethodMocked(method) && signature.contains(KEY_OBJECT_PROPHECY)) {
            return KEY_METHOD_PROPHECY;
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Project project) {
        if (s.equals(KEY_METHOD_PROPHECY)) {
            return getMethodProphecyCollection(project);
        }

        if (s.startsWith(KEY_OBJECT_PROPHECY)) {
            String className = s.substring(KEY_OBJECT_PROPHECY.length());
            Collection<PhpClass> phpClassCollection = new ArrayList<PhpClass>();
            phpClassCollection.addAll(getObjectProphecyCollection(project));
            phpClassCollection.addAll(getPhpClassCollection(project, className));
            return phpClassCollection;
        }

        if (s.startsWith(KEY_NONE)) {
            String className = s.substring(KEY_NONE.length());
            return getPhpClassCollection(project, className);
        }

        return null;
    }

    protected void mockMethod(@NotNull Method method)
    {
        String hash = method.getFQN();
        if (!mockedMethods.containsKey(hash)) {
            mockedMethods.put(hash, true);
        }
    }

    protected void mockMethods(Collection<Method> methods)
    {
        for (Method method : methods) {
            mockMethod(method);
        }
    }

    protected boolean isMethodMocked(Method method)
    {
        String hash = method.getFQN();
        return mockedMethods.containsKey(hash);
    }

    protected Collection<PhpClass> getObjectProphecyCollection(@NotNull Project project)
    {
        return getPhpClassCollection(project, CLASS_OBJECT_PROPHECY);
    }

    protected Collection<PhpClass> getMethodProphecyCollection(@NotNull Project project)
    {
        return getPhpClassCollection(project, CLASS_METHOD_PROPHECY);
    }

    protected Collection<PhpClass> getPhpClassCollection(@NotNull Project project, String className)
    {
        return PhpIndex.getInstance(project).getClassesByFQN(className);
    }

    @Nullable
    protected PhpClassAdapter getPhpClassAdapterForMethod(@NotNull Method method)
    {
        PhpClass phpClass = method.getContainingClass();
        if (phpClass == null) {
            return null;
        }

        return new PhpClassAdapter(phpClass);
    }
}
