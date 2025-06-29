package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction

class InsertFrontMatterAction : AnAction("Insert Front Matter") {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)
        WriteCommandAction.runWriteCommandAction(editor.project) {
            editor.document.insertString(0, "---\ntitle: \n---\n\n")
        }
    }
}
