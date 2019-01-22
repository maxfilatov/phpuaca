package com.phpuaca.completion;

import com.intellij.openapi.project.*;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.phpuaca.util.PhpClassAdapter;
import com.phpuaca.util.PhpClassResolver;
import com.phpuaca.util.PhpMethodChain;
import com.phpuaca.util.PhpMethodResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PHPUnitTypeProvider extends BaseTypeProvider {

    protected Map<String, Boolean> mockCreatorMethodMap = new HashMap<String, Boolean>() {{
        put("getMock", true);
        put("getMockClass", true);
        put("getMockForAbstractClass", true);
        put("getMockForTrait", true);
    }};

    @Override
    public char getKey() {
        return '元';
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

        if (!(psiElement instanceof MethodReference)) {
            return null;
        }

        MethodReference methodReference = (MethodReference) psiElement;
        Method method = (new PhpMethodResolver()).resolveByMethodReference(methodReference);
        if (method == null) {
            return null;
        }

        if (isMethodIsMockCreator(method)) {
            PhpClassAdapter phpClassAdapter = getPhpClassAdapterForMethod(method);
            if (phpClassAdapter != null) {
                if (phpClassAdapter.isSubclassOf("\\PHPUnit\\Framework\\MockObject\\MockBuilder") || phpClassAdapter.isSubclassOf("\\PHPUnit_Framework_MockObject_MockBuilder")) {
                    return getTypeForMockBuilder(methodReference);
                }

                if (phpClassAdapter.isSubclassOf("\\PHPUnit\\Framework\\TestCase") || phpClassAdapter.isSubclassOf("\\PHPUnit_Framework_TestCase")) {
                    return getTypeForTestCase(methodReference);
                }
            }
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String s, Project project) {
        if (DumbService.isDumb(project)) {
            return null;
        }

        PhpIndex phpIndex = PhpIndex.getInstance(project);
        Collection<PhpClass> collection = new ArrayList<PhpClass>();
        collection.addAll(phpIndex.getInterfacesByFQN("\\PHPUnit\\Framework\\MockObject\\MockObject"));
        collection.addAll(phpIndex.getInterfacesByFQN("\\PHPUnit_Framework_MockObject_MockObject"));
        collection.addAll(phpIndex.getAnyByFQN(s));
        return collection;
    }

    protected boolean isMethodIsMockCreator(@NotNull Method method) {
        return mockCreatorMethodMap.containsKey(method.getName());
    }

    @Nullable
    protected String getTypeForMockBuilder(@NotNull MethodReference methodReference) {
        MethodReference mockBuilderMethodReference = (new PhpMethodChain(methodReference)).findMethodReference("getMockBuilder");
        if (mockBuilderMethodReference == null) {
            return null;
        }

        PhpClass phpClass = (new PhpClassResolver()).resolveByMethodReferenceContainingParameterListWithClassReference(mockBuilderMethodReference);
        if (phpClass == null) {
            return null;
        }

        return phpClass.getFQN();
    }

    @Nullable
    protected String getTypeForTestCase(@NotNull MethodReference methodReference) {
        PhpClass phpClass = (new PhpClassResolver()).resolveByMethodReferenceContainingParameterListWithClassReference(methodReference);
        if (phpClass == null) {
            return null;
        }

        return phpClass.getFQN();
    }
}
