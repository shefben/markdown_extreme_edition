package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorModificationUtil
import com.vladsch.flexmark.formatter.Formatter
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.parser.Parser
import org.jsoup.Jsoup

class ConvertHtml5SelectionAction : AnAction("Convert HTML5 Selection") {
    private val converter = FlexmarkHtmlConverter.builder().build()
    private val parser = Parser.builder().build()
    private val formatter = Formatter.builder().build()

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val html = editor.selectionModel.selectedText ?: return
        val cleaned = Jsoup.parse(html).body().html()
        val markdown = converter.convert(cleaned)
        val formatted = formatter.render(parser.parse(markdown))
        WriteCommandAction.runWriteCommandAction(e.project) {
            EditorModificationUtil.insertStringAtCaret(editor, formatted, true)
        }
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val hasSelection = editor?.selectionModel?.hasSelection() ?: false
        val isMarkdown = psiFile?.fileType?.defaultExtension == "md"
        e.presentation.isEnabledAndVisible = hasSelection && isMarkdown
    }
}
