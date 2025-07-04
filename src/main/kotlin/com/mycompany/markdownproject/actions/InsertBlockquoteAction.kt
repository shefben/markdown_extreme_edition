package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.actionSystem.CommonDataKeys

class InsertBlockquoteAction : AnAction("Insert Blockquote") {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val document = editor.document
        val selectionModel = editor.selectionModel
        val start = selectionModel.selectionStart
        val end = selectionModel.selectionEnd
        val startLine = document.getLineNumber(start)
        val endLine = document.getLineNumber(if (selectionModel.hasSelection()) end else start)
        WriteCommandAction.runWriteCommandAction(editor.project) {
            for (line in startLine..endLine) {
                val lineStart = document.getLineStartOffset(line)
                document.insertString(lineStart, "> ")
            }
        }
    }
}
