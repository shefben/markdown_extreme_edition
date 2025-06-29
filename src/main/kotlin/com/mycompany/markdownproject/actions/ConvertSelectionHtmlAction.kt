package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorModificationUtil
import com.vladsch.flexmark.formatter.Formatter
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.parser.Parser
import org.jsoup.Jsoup

class ConvertSelectionHtmlAction : AnAction("Convert HTML Selection") {
    private val converter = FlexmarkHtmlConverter.builder().build()
    private val parser = Parser.builder().build()
    private val formatter = Formatter.builder().build()

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR) ?: return
        val selection = editor.selectionModel
        val html = selection.selectedText ?: return
        val cleaned = Jsoup.parse(html).apply { select("style,script").remove() }.body().html()
        val markdown = converter.convert(cleaned)
        val formatted = formatter.render(parser.parse(markdown))
        WriteCommandAction.runWriteCommandAction(e.project) {
            EditorModificationUtil.insertStringAtCaret(editor, formatted, true)
        }
    }
}
