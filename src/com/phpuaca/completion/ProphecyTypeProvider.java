package com.phpuaca.completion;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.phpuaca.util.PhpClassAdapter;
import com.phpuaca.util.PhpClassResolver;
import com.phpuaca.util.PhpMethodResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

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
        return '言';
    }

    @Nullable
    @Override
    public String getType(PsiElement psiElement) {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        for (Project project : projects) {
            if (DumbService.isDumb(project)) {
                return null;
            }
        }

        Project project = psiElement.getProject();

        if (psiElement instanceof FieldReference) {
            FieldReference fieldReference = (FieldReference) psiElement;

            return getTypeForField(fieldReference, project);
        }

        if (psiElement instanceof Variable) {
            Variable variable = (Variable) psiElement;

            return getTypeForVariable(variable, project);
        }

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

        PhpType phpType = method.getType();

        return getTypeForPhpType(phpType, project);
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Project project) {
        if (DumbService.isDumb(project)) {
            return null;
        }

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

        return getTypeForPhpClass(phpClass);
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

    @Nullable
    private String getTypeForField(@NotNull FieldReference fieldReference, Project project) {
        PhpType phpType = fieldReference.resolveLocalType();

        return getTypeForPhpType(phpType, project);
    }

    private String getTypeForPhpClass(@NotNull PhpClass phpClass) {
        mockMethods(phpClass.getMethods());

        return CLASS_OBJECT_PROPHECY + TYPE_SEPARATOR + phpClass.getFQN();
    }

    @Nullable
    private String getTypeForPhpType(PhpType phpType, Project project) {
        Set<String> typeNames = phpType.getTypes();

        if (typeNames.size() == 2 && typeNames.contains(CLASS_OBJECT_PROPHECY)) {
            for (String typeName : typeNames) {
                if (typeName.equals(CLASS_OBJECT_PROPHECY)) {
                    continue;
                }

                Collection<PhpClass> phpClasses = PhpIndex.getInstance(project).getAnyByFQN(typeName);

                return getTypeForPhpClass(phpClasses.iterator().next());
            }
        }

        return null;
    }

    @Nullable
    private String getTypeForVariable(@NotNull Variable variable, Project project) {
        PsiElement parameterCandidate = variable.resolve();

        if (!(parameterCandidate instanceof Parameter)) {
            return null;
        }

        Parameter parameter = (Parameter) parameterCandidate;
        PhpType phpType = parameter.getType().global(project);

        return getTypeForPhpType(phpType, project);
    }
}
