package de.espend.idea.php.phpunit.utils;

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import com.jetbrains.php.phpunit.PhpUnitRuntimeConfigurationProducer;
import de.espend.idea.php.phpunit.utils.processor.CreateMockMethodReferenceProcessor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpUnitPluginUtil {
    /**
     * Run tests for given element
     *
     * @param psiElement Elements are PhpClass or Method possible context
     */
    public static void executeDebugRunner(@NotNull PsiElement psiElement) {
        ConfigurationFromContext context = RunConfigurationProducer.getInstance(PhpUnitRuntimeConfigurationProducer.class)
            .createConfigurationFromContext(new ConfigurationContext(psiElement));

        if(context != null) {
            ProgramRunnerUtil.executeConfiguration(
                psiElement.getProject(),
                context.getConfigurationSettings(),
                DefaultDebugExecutor.getDebugExecutorInstance()
            );
        }
    }

    /**
     * Check if class is possibly a Test class, we just try to find it in local file scope
     * no index access invoked
     *
     * FooTest or on extends eg PHPUnit\Framework\TestCase
     */
    public static boolean isTestClassWithoutIndexAccess(@NotNull PhpClass phpClass) {
        if(phpClass.getName().endsWith("Test")) {
            return true;
        }

        String superFQN = StringUtils.stripStart(phpClass.getSuperFQN(), "\\");

        return
            "PHPUnit\\Framework\\TestCase".equalsIgnoreCase(superFQN) ||
            "PHPUnit_Framework_TestCase".equalsIgnoreCase(superFQN) ||
            "Symfony\\Bundle\\FrameworkBundle\\Test\\WebTestCase".equalsIgnoreCase(superFQN)
        ;
    }

    /**
     * $foo = $this->createMock('Foobar')
     * $foo->method('<caret>')
     */
    @Nullable
    public static String findCreateMockParameterOnParameterScope(@NotNull StringLiteralExpression psiElement) {
        PsiElement parameterList = psiElement.getParent();
        if(parameterList instanceof ParameterList) {
            PsiElement methodReference = parameterList.getParent();
            if(methodReference instanceof MethodReference && (
                PhpElementsUtil.isMethodReferenceInstanceOf((MethodReference) methodReference, "PHPUnit_Framework_MockObject_MockObject", "method") ||
                PhpElementsUtil.isMethodReferenceInstanceOf((MethodReference) methodReference, "PHPUnit_Framework_MockObject_Builder_InvocationMocker", "method")
                ))
            {
                return CreateMockMethodReferenceProcessor.createParameter((MethodReference) methodReference);
            }
        }

        return null;
    }
}
