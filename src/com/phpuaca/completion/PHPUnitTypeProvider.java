package com.phpuaca.completion;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
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

    final protected static String CLASS_PHP_UNIT_TEST_CASE = "\\PHPUnit_Framework_TestCase";
    final protected static String CLASS_PHP_UNIT_MOCK_BUILDER = "\\PHPUnit_Framework_MockObject_MockBuilder";
    final protected static String CLASS_PHP_UNIT_MOCK_OBJECT = "\\PHPUnit_Framework_MockObject_MockObject";

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
        Project project = psiElement.getProject();
        if (DumbService.isDumb(project)) {
            return null;
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
                if (phpClassAdapter.isSubclassOf(CLASS_PHP_UNIT_MOCK_BUILDER)) {
                    return getTypeForMockBuilder(methodReference);
                }

                if (phpClassAdapter.isSubclassOf(CLASS_PHP_UNIT_TEST_CASE)) {
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
        collection.addAll(phpIndex.getInterfacesByFQN(CLASS_PHP_UNIT_MOCK_OBJECT));
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
