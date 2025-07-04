package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.actionSystem.CommonDataKeys

class InsertHorizontalRuleAction : AnAction("Insert Horizontal Rule") {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        insertText(editor, "\n---\n")
    }

    private fun insertText(editor: Editor, text: String) {
        WriteCommandAction.runWriteCommandAction(editor.project) {
            val offset = editor.caretModel.offset
            editor.document.insertString(offset, text)
        }
    }
}
