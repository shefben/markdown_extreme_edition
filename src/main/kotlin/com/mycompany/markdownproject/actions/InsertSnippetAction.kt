package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.components.JBList
import com.mycompany.markdownproject.snippets.SnippetService

class InsertSnippetAction : AnAction("Insert Snippet") {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)
        val snippets = SnippetService.instance().state.snippets
        if (snippets.isEmpty()) return
        val list = JBList(snippets.map { it.name })
        JBPopupFactory.getInstance().createListPopupBuilder(list).setTitle("Choose snippet").setItemChoosenCallback {
            val idx = list.selectedIndex
            if (idx >= 0) {
                val text = snippets[idx].text
                WriteCommandAction.runWriteCommandAction(editor.project) {
                    editor.document.insertString(editor.caretModel.offset, text)
                }
            }
        }.createPopup().showInBestPositionFor(e.dataContext)
    }
}
