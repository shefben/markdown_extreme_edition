package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDocumentManager
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile

class InsertCitationAction : AnAction("Insert Citation") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR) ?: return
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) as? MarkdownFile ?: return
        val doc = editor.document
        val citation = Messages.showInputDialog(project, "Citation text", "Insert Citation", null) ?: return
        val footnoteRegex = Regex("\\n\\[\\^(\\d+)]:")
        val existingNumbers = footnoteRegex.findAll(doc.text).map { it.groupValues[1].toInt() }.toList()
        val next = (existingNumbers.maxOrNull() ?: 0) + 1
        val insertText = "[^$next]"
        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.insertString(editor.caretModel.offset, insertText)
            val footnote = "\n[^$next]: $citation"
            doc.insertString(doc.textLength, footnote)
            PsiDocumentManager.getInstance(project).commitDocument(doc)
        }
    }
}
