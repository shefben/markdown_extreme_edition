package com.mycompany.markdownproject.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor

import com.mycompany.markdownproject.inspections.RemoveTrailingSpaceFix
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile

class MarkdownLintInspection : LocalInspectionTool() {
    override fun getGroupDisplayName(): String = "Markdown"
    override fun getGroupPath(): Array<String> = arrayOf("Markdown")
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                if (file is MarkdownFile) {
                    val doc = file.viewProvider.document ?: return
                    var offset = 0
                    doc.text.split('\n').forEach { line ->
                        if (line.endsWith(" ")) {
                            val range = TextRange(offset + line.length - 1, offset + line.length)
                            holder.registerProblem(file, range, "Trailing whitespace", RemoveTrailingSpaceFix())
                        }
                        offset += line.length + 1
                    }
                }
            }
        }
    }
}
