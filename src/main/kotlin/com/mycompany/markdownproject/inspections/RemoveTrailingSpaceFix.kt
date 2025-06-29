package com.mycompany.markdownproject.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class RemoveTrailingSpaceFix : LocalQuickFix {
    override fun getFamilyName(): String = "Remove trailing spaces"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val file = descriptor.psiElement.containingFile ?: return
        val document = file.viewProvider.document ?: return
        WriteCommandAction.runWriteCommandAction(project) {
            val cleaned = document.text.lines().joinToString("\n") { it.trimEnd() }
            document.setText(cleaned)
        }
    }
}
