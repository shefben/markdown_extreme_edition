package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory

class InsertTableAction : AnAction("Insert Markdown Table") {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR) ?: return
        insertText(editor, "| Header 1 | Header 2 |\n|---------|---------|\n|         |         |\n")
    }

    private fun insertText(editor: Editor, text: String) {
        val project = editor.project
        WriteCommandAction.runWriteCommandAction(project) {
            val caretModel = editor.caretModel
            editor.document.insertString(caretModel.offset, text)
        }
    }
}
