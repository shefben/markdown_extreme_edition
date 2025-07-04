package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.actionSystem.CommonDataKeys
import java.awt.datatransfer.DataFlavor
import java.awt.Toolkit

class LinkFromClipboardAction : AnAction("Insert Link from Clipboard") {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val url = clipboard.getData(DataFlavor.stringFlavor) as? String ?: return
        if (!url.startsWith("http")) return
        val selectionModel = editor.selectionModel
        WriteCommandAction.runWriteCommandAction(editor.project) {
            if (selectionModel.hasSelection()) {
                val text = selectionModel.selectedText ?: ""
                editor.document.replaceString(selectionModel.selectionStart, selectionModel.selectionEnd, "[$text]($url)")
            } else {
                val offset = editor.caretModel.offset
                editor.document.insertString(offset, "[$url]($url)")
            }
        }
    }
}
