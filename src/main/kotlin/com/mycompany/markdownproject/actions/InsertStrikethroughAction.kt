package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.actionSystem.CommonDataKeys

class InsertStrikethroughAction : AnAction("Insert Strikethrough") {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selectionModel = editor.selectionModel
        WriteCommandAction.runWriteCommandAction(editor.project) {
            if (selectionModel.hasSelection()) {
                val start = selectionModel.selectionStart
                val end = selectionModel.selectionEnd
                editor.document.insertString(end, "~~")
                editor.document.insertString(start, "~~")
            } else {
                val offset = editor.caretModel.offset
                editor.document.insertString(offset, "~~~~")
                editor.caretModel.moveToOffset(offset + 2)
            }
        }
    }
}
