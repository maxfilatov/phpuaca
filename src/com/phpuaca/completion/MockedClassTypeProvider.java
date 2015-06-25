package com.phpuaca.completion;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeProvider2;
import com.phpuaca.completion.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MockedClassTypeProvider implements PhpTypeProvider2
{
    final static char TRIM_KEY = '\u0180';

    @Override
    public char getKey() {
        return '\u0150';
    }

    @Nullable
    @Override
    public String getType(PsiElement element) {

        if (DumbService.getInstance(element.getProject()).isDumb() || !(element instanceof MethodReference)) {
            return null;
        }

        List<PhpMethodSignature> signatures = getSignatures();
        if (signatures.size() == 0) {
            return null;
        }

        MethodReference methodReference = (MethodReference) element;
        String refSignature = methodReference.getSignature();

        if (StringUtil.isEmpty(refSignature)) {
            return null;
        }

        // Get signature from:  $this->getMockBuilder('CLASS')->getMock()
        MethodReference mockBuilderMethodReference = (new PhpMethodChain(methodReference)).findMethodReference("getMockBuilder");

        if (mockBuilderMethodReference != null && "getMock".equals(methodReference.getName())) {
            PsiElement[] mockBuilderParams = mockBuilderMethodReference.getParameters();
            String signature = buildParameterMethodSignature(refSignature, 0, mockBuilderParams);

            if (signature != null) {
                return signature;
            }
        }

        // Set MethodProphecy signature for:  $prophecy->method()
        PsiElement child = methodReference.getFirstChild();
        if (child instanceof Variable) {
            String expression = ((Variable)child).getType().toString();
            // get back our original call
            int endIndex = expression.lastIndexOf(TRIM_KEY);
            if(endIndex != -1) {
                PhpIndex phpIndex = PhpIndex.getInstance(element.getProject());

                MethodReference prophesizeMethod = (new PhpVariable((Variable)child)).findClosestAssignment();

                if (prophesizeMethod != null) {
                    PhpNamedElement prophCall = phpIndex.getBySignature(prophesizeMethod.getSignature()).iterator().next();

                    if(prophCall != null && prophCall.getFQN() != null && prophCall.getFQN().equals("\\PHPUnit_Framework_TestCase.prophesize")) {
                        String prophType = prophCall.getType().toString();
                        if(prophType.equals("\\Prophecy\\Prophecy\\ObjectProphecy")) {
                            if("reveal".equals(methodReference.getName())) {
                                return buildParameterMethodSignature(refSignature, 0, prophesizeMethod.getParameters());
                            } else {
                                // check if methodReference is not in ObjectProphecy (it is a mocked method)
                                PhpMethodResolver resolver = new PhpMethodResolver(methodReference);
                                if(resolver.resolve()) {
                                    String resolvedClass = resolver.getResolvedClass().getFQN();
                                    if(resolvedClass != null && !resolvedClass.equals("\\Prophecy\\Prophecy\\ObjectProphecy")) {
                                        return "\\Prophecy\\Prophecy\\MethodProphecy";
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Get signatures like:  $this->getMock('CLASS') ...
        ArrayList<PhpMethodSignature> matchedSignatures = getMatchedSignatures(methodReference.getName(), signatures);
        if (matchedSignatures.size() == 0) {
            return null;
        }

        PsiElement[] parameters = methodReference.getParameters();
        for (PhpMethodSignature methodSignature: matchedSignatures) {
            String signature = buildParameterMethodSignature(refSignature, methodSignature.getParameterIndex(), parameters);

            if (signature != null) {
                return signature;
            }
        }

        return null;
    }

    @Override
    public Collection<? extends PhpNamedElement> getBySignature(String expression, Project project) {

        PhpIndex phpIndex = PhpIndex.getInstance(project);

        if (expression.equals("\\Prophecy\\Prophecy\\MethodProphecy")) {
            return phpIndex.getClassesByFQN(expression);
        }

        // get back our original call
        int endIndex = expression.lastIndexOf(TRIM_KEY);
        if (endIndex == -1) {
            return null;
        }

        String originalSignature = expression.substring(0, endIndex);
        String parameter = expression.substring(endIndex + 1);

        Collection<? extends PhpNamedElement> phpNamedElementCollections = phpIndex.getBySignature(originalSignature, null, 0);
        if (phpNamedElementCollections.size() == 0) {
            return null;
        }

        // get first matched item
        PhpNamedElement phpNamedElement = phpNamedElementCollections.iterator().next();
        if (!(phpNamedElement instanceof Method)) {
            return null;
        }

        List<PhpMethodSignature> signatures = getSignatures();
        if (signatures.size() == 0) {
            return null;
        }

        ArrayList<PhpNamedElement> phpNamedElements = new ArrayList<PhpNamedElement>();
        phpNamedElements.add(phpNamedElement);

        parameter = PhpTypeProviderUtil.getResolvedParameter(phpIndex, parameter);
        if (parameter == null) {
            return phpNamedElementCollections;
        }

        for (PhpMethodSignature matchedSignature : signatures) {
            if (PhpUtil.isCallTo((Method) phpNamedElement, matchedSignature.getClassName(), matchedSignature.getMethodName())) {
                Collection<? extends PhpNamedElement> namedElements = getByParameter(project, parameter);
                phpNamedElements.addAll(namedElements);
            }
        }

        return new ArrayList<PhpNamedElement>(phpNamedElements);
    }

    private List<PhpMethodSignature> getSignatures() {
        List<PhpMethodSignature> signatures = new ArrayList<PhpMethodSignature>();

        signatures.add(new PhpMethodSignature("\\PHPUnit_Framework_MockObject_MockBuilder", "getMock", 0));
        signatures.add(new PhpMethodSignature("\\PHPUnit_Framework_TestCase", "getMock", 0));
        signatures.add(new PhpMethodSignature("\\PHPUnit_Framework_TestCase", "getMockForAbstractClass", 0));
        signatures.add(new PhpMethodSignature("\\PHPUnit_Framework_TestCase", "getMockForTrait", 0));
        signatures.add(new PhpMethodSignature("\\PHPUnit_Framework_TestCase", "prophesize", 0));
        signatures.add(new PhpMethodSignature("\\Prophecy\\Prophecy\\ObjectProphecy", "reveal", 0));

        return signatures;
    }

    private ArrayList<PhpMethodSignature> getMatchedSignatures(String methodName, List<PhpMethodSignature> methodSignatureSettingList) {

        ArrayList<PhpMethodSignature> matchedSignatures = new ArrayList<PhpMethodSignature>();

        for(PhpMethodSignature methodSignatureSetting: methodSignatureSettingList) {
            if(methodSignatureSetting.getMethodName().equals(methodName)) {
                matchedSignatures.add(methodSignatureSetting);
            }
        }

        return matchedSignatures;
    }

    private String buildParameterMethodSignature(String refSignature, int parameterIndex, PsiElement[] parameters) {
        if (parameters.length - 1 < parameterIndex) {
            return null;
        }
        PsiElement parameter = parameters[parameterIndex];
        if ((parameter instanceof StringLiteralExpression)) {
            String param = ((StringLiteralExpression)parameter).getContents();
            if (StringUtil.isNotEmpty(param)) {
                return refSignature + TRIM_KEY + param;
            }
        }

        if (parameter instanceof PhpReference && (parameter instanceof ClassConstantReference || parameter instanceof FieldReference)) {
            String signature = ((PhpReference) parameter).getSignature();
            if (StringUtil.isNotEmpty(signature)) {
                return refSignature + TRIM_KEY + signature;
            }
        }

        return null;
    }

    @NotNull
    private Collection<? extends PhpNamedElement> getByParameter(Project project, String parameter) {
        return PhpIndex.getInstance(project).getClassesByFQN(parameter.startsWith("\\") ? parameter : "\\" + parameter);
    }

}
