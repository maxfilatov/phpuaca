package de.espend.idea.php.phpunit.linemarker;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.util.ConstantFunction;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.phpunit.PhpUnitUtil;
import de.espend.idea.php.phpunit.utils.PatternUtil;
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
            if(!(psiElement instanceof LeafPsiElement)) {
                continue;
            }

            if(PatternUtil.getMethodNamePattern().accepts(psiElement)) {
                // attach Method runner

                PsiElement method = psiElement.getParent();
                if(method instanceof Method && PhpUnitUtil.isTestMethod((Method) method)) {
                    collection.add(createLineMakerInfo((LeafPsiElement) psiElement, AllIcons.RunConfigurations.TestState.Run));
                }
            } else if(PatternUtil.getClassNamePattern().accepts(psiElement)) {
                // attach PhpClass runner

                PsiElement phpClass = psiElement.getParent();
                if(phpClass instanceof PhpClass && PhpUnitUtil.isTestClass((PhpClass) phpClass)) {
                    collection.add(createLineMakerInfo((LeafPsiElement) psiElement, AllIcons.RunConfigurations.TestState.Run_run));
                }
            }
        }
    }

    @NotNull
    private LineMarkerInfo<PsiElement> createLineMakerInfo(@NotNull LeafPsiElement psiElement, @NotNull Icon icon) {
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
