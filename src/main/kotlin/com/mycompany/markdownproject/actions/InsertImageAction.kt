package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor

class InsertImageAction : AnAction("Insert Image") {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR) ?: return
        insertText(editor, "![alt text](image.png)\n")
    }

    private fun insertText(editor: Editor, text: String) {
        WriteCommandAction.runWriteCommandAction(editor.project) {
            editor.document.insertString(editor.caretModel.offset, text)
        }
    }
}
