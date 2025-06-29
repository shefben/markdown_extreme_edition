package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor

class InsertCodeBlockAction : AnAction("Insert Code Block") {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR) ?: return
        insertText(editor, "```\n\n```\n")
    }

    private fun insertText(editor: Editor, text: String) {
        WriteCommandAction.runWriteCommandAction(editor.project) {
            val caret = editor.caretModel.offset
            editor.document.insertString(caret, text)
            editor.caretModel.moveToOffset(caret + 4)
        }
    }
}
