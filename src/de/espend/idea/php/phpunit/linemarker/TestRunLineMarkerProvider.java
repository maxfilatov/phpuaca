package de.espend.idea.php.phpunit.linemarker;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.util.ConstantFunction;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.phpunit.PhpUnitUtil;
import de.espend.idea.php.phpunit.utils.PhpUnitPluginUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * For full example see. we provide direct call without
 * @see com.intellij.testIntegration.TestRunLineMarkerProvider
 */
public class TestRunLineMarkerProvider implements LineMarkerProvider {
    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psiElement) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> psiElements, @NotNull Collection<LineMarkerInfo> collection) {
        for (PsiElement psiElement : psiElements) {
            if(psiElement instanceof Method && PhpUnitUtil.isTestMethod((Method) psiElement)) {
                collection.add(createLineMakerInfo(psiElement, AllIcons.RunConfigurations.TestState.Run));
            } else if(psiElement instanceof PhpClass && PhpUnitUtil.isTestClass((PhpClass) psiElement)) {
                collection.add(createLineMakerInfo(psiElement, AllIcons.RunConfigurations.TestState.Run_run));
            }
        }
    }

    @NotNull
    private LineMarkerInfo<PsiElement> createLineMakerInfo(@NotNull PsiElement psiElement, @NotNull Icon icon) {
        return new LineMarkerInfo<>(
            psiElement,
            psiElement.getTextRange(),
            icon,
            6,
            new ConstantFunction<>("Run Test"),
            new MyProgramRunnerGutterIconNavigationHandler(),
            GutterIconRenderer.Alignment.LEFT
        );
    }

    private static class MyProgramRunnerGutterIconNavigationHandler implements GutterIconNavigationHandler<PsiElement> {
        @Override
        public void navigate(MouseEvent mouseEvent, PsiElement psiElement) {
            PhpUnitPluginUtil.executeDebugRunner(psiElement);
        }
    }
}
