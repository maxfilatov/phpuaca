package de.espend.idea.php.phpunit.utils;

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.documentation.phpdoc.lexer.PhpDocTokenTypes;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.documentation.phpdoc.psi.tags.PhpDocTag;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.phpunit.PhpUnitRuntimeConfigurationProducer;
import de.espend.idea.php.phpunit.utils.processor.CreateMockMethodReferenceProcessor;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class PhpUnitPluginUtil {
    private static final String[] EXTENDS_TEST_CLASSES = new String[]{
        "\\PHPUnit\\Framework\\TestCase",
        "\\PHPUnit_Framework_TestCase",
        "\\Symfony\\Bundle\\FrameworkBundle\\Test\\WebTestCase",
        "\\Behat\\Behat\\Context\\BehatContext"
    };

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
        String name = phpClass.getName();
        if (name.endsWith("Test") || name.endsWith("Context")) {
           return true;
        }

        // find "extends" classes
        String superFQN = "\\" + StringUtils.stripStart(phpClass.getSuperFQN(), "\\");

        for (String extendsTestClass : EXTENDS_TEST_CLASSES) {
            if (extendsTestClass.equalsIgnoreCase(superFQN)) {
                return true;
            }
        }

        for (String interfaceName : phpClass.getInterfaceNames()) {
            String interfaceNameNormalized = "\\" + StringUtils.stripStart(interfaceName, "\\");
            if (interfaceNameNormalized.equalsIgnoreCase("\\Behat\\Behat\\Context\\Context")) {
                return true;
            }
        }

        // find somehow inside a project test folder
        VirtualFile containingFile1 = phpClass.getContainingFile().getVirtualFile();
        for (VirtualFile contentRoot : ProjectRootManager.getInstance(phpClass.getProject()).getContentRoots()) {
            String relativePath = VfsUtil.getRelativePath(containingFile1, contentRoot, '/');

            if (relativePath == null) {
                continue;
            }

            // PhpUnit and Behat folder structure
            if (relativePath.toLowerCase().contains("/test/") ||
                relativePath.toLowerCase().contains("/tests/")  ||
                relativePath.toLowerCase().contains("/feature/")  ||
                relativePath.toLowerCase().contains("/features/")
            ) {
                return true;
            }
        }

        return false;
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
                PhpElementsUtil.isMethodReferenceInstanceOf((MethodReference) methodReference, "PHPUnit_Framework_MockObject_Builder_InvocationMocker", "method") ||
                PhpElementsUtil.isMethodReferenceInstanceOf((MethodReference) methodReference, "PHPUnit\\Framework\\MockObject\\MockObject", "method") ||
                PhpElementsUtil.isMethodReferenceInstanceOf((MethodReference) methodReference, "PHPUnit\\Framework\\MockObject\\Builder\\InvocationMocker", "method")
                ))
            {
                return CreateMockMethodReferenceProcessor.createParameter((MethodReference) methodReference);
            }
        }

        return null;
    }

    /**
     * Insert "expectException" for given scope (eg method)
     */
    public static void insertExpectedException(@NotNull Document document, @NotNull PhpNamedElement forElement, @NotNull String exceptionClass) {
        PhpDocComment docComment = forElement.getDocComment();

        String tagString = "@expectedException \\" + exceptionClass;

        // we need to update
        if(docComment != null)  {
            PsiElement elementToInsert = PhpPsiElementFactory.createFromText(forElement.getProject(), PhpDocTag.class, "/** " + tagString + " */\\n");
            if(elementToInsert == null) {
                return;
            }

            PsiElement fromText = PhpPsiElementFactory.createFromText(forElement.getProject(), PhpDocTokenTypes.DOC_LEADING_ASTERISK, "/** \n * @var */");
            docComment.addBefore(fromText, docComment.getLastChild());
            docComment.addBefore(elementToInsert, docComment.getLastChild());

            PsiDocumentManager.getInstance(forElement.getProject()).doPostponedOperationsAndUnblockDocument(document);
            PsiDocumentManager.getInstance(forElement.getProject()).commitDocument(document);

            return;
        }

        // new PhpDoc see PhpDocCommentGenerator
        docComment = PhpPsiElementFactory.createFromText(forElement.getProject(), PhpDocComment.class, "/**\n " + tagString + " \n */");
        if(docComment == null) {
            return;
        }

        PsiElement parent = forElement.getParent();
        int atOffset = forElement.getTextRange().getStartOffset() + 1;
        parent.addBefore(docComment, forElement);
        PsiDocumentManager.getInstance(forElement.getProject()).doPostponedOperationsAndUnblockDocument(document);
        PsiElement atElement = forElement.getContainingFile().findElementAt(atOffset);
        if (atElement != null)            {
            PsiElement docParent = PsiTreeUtil.findFirstParent(atElement, true, element -> ((element instanceof PhpDocComment)) || ((element instanceof PhpFile)));
            if ((docParent instanceof PhpDocComment)) {
                CodeStyleManager.getInstance(forElement.getProject()).reformatNewlyAddedElement(docParent.getParent().getNode(), docParent.getNode());
            }
        }

        PsiDocumentManager.getInstance(forElement.getProject()).doPostponedOperationsAndUnblockDocument(document);
        PsiDocumentManager.getInstance(forElement.getProject()).commitDocument(document);
    }
}
