package com.mycompany.markdownproject.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.util.ui.UIUtil
import org.intellij.plugins.markdown.lang.MarkdownLanguage
import org.intellij.plugins.markdown.lang.psi.MarkdownElementTypes
import org.intellij.plugins.markdown.lang.psi.MarkdownPsiElement
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkDestinationImpl
import java.net.HttpURLConnection
import java.net.URL

class BrokenLinkInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiRecursiveElementWalkingVisitor() {
            override fun visitFile(file: PsiFile) {
                if (file !is MarkdownFile) return
                super.visitFile(file)
            }

            override fun visitElement(element: com.intellij.psi.PsiElement) {
                if (element is MarkdownLinkDestinationImpl) {
                    val url = element.text
                    if (!url.startsWith("http")) {
                        val vf = element.containingFile.virtualFile.parent?.findFileByRelativePath(url)
                        if (vf == null) {
                            holder.registerProblem(element, "Broken link", ProblemHighlightType.GENERIC_ERROR_OR_WARNING, RemoveLinkFix())
                        }
                    } else {
                        try {
                            val conn = URL(url).openConnection() as HttpURLConnection
                            conn.requestMethod = "HEAD"
                            conn.connectTimeout = 2000
                            val code = conn.responseCode
                            if (code >= 400) {
                                holder.registerProblem(element, "Unreachable link", ProblemHighlightType.GENERIC_ERROR_OR_WARNING, RemoveLinkFix())
                            }
                        } catch (e: Exception) {
                            holder.registerProblem(element, "Unreachable link", ProblemHighlightType.GENERIC_ERROR_OR_WARNING, RemoveLinkFix())
                        }
                    }
                }
            }
        }
    }

    private class RemoveLinkFix : LocalQuickFix {
        override fun getName() = "Remove link"
        override fun getFamilyName() = name
        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val element = descriptor.psiElement as? MarkdownPsiElement ?: return
            val doc = element.containingFile.viewProvider.document ?: return
            val range = element.textRange
            com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction(project) {
                doc.deleteString(range.startOffset, range.endOffset)
            }
        }
    }
}
