package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiDocumentManager
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.formatter.Formatter
import com.vladsch.flexmark.parser.Parser
import org.jsoup.Jsoup
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable

class PasteFromBrowserAction : AnAction("Paste from Browser") {
    private val htmlConverter = FlexmarkHtmlConverter.builder().build()
    private val parser = Parser.builder().build()
    private val formatter = Formatter.builder().build()

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val project = e.project ?: return
        val html = extractHtml(CopyPasteManager.getInstance().contents) ?: return
        val state = com.mycompany.markdownproject.settings.MarkdownPasteSettings.instance().state
        val cleaned = sanitizeHtml(html, state)
        val markdown = htmlConverter.convert(cleaned)
        val formatted = formatter.render(parser.parse(markdown))
        WriteCommandAction.runWriteCommandAction(project) {
            val sel = editor.selectionModel
            editor.document.replaceString(sel.selectionStart, sel.selectionEnd, formatted)
            PsiDocumentManager.getInstance(project).commitDocument(editor.document)
        }
    }

    private fun extractHtml(transferable: Transferable?): String? {
        if (transferable == null) return null
        listOf(DataFlavor.fragmentHtmlFlavor, DataFlavor.allHtmlFlavor).forEach { flavor ->
            if (transferable.isDataFlavorSupported(flavor)) {
                return transferable.getTransferData(flavor) as? String
            }
        }
        if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            val text = transferable.getTransferData(DataFlavor.stringFlavor) as? String
            if (text != null && text.contains('<') && text.contains('>')) {
                return text
            }
        }
        return null
    }

    private fun sanitizeHtml(html: String, settings: com.mycompany.markdownproject.settings.MarkdownPasteSettings.State): String {
        val doc = Jsoup.parse(html)
        doc.select("style,script").remove()
        settings.excludedTags.forEach { tag ->
            doc.select(tag).remove()
        }
        settings.tagMappings.forEach { (from, to) ->
            doc.select(from).forEach { e -> e.tagName(to) }
        }
        doc.select("[style*=font-weight:bold]").forEach { element ->
            element.tagName(settings.tagMappings.getOrDefault("b", "b"))
            element.removeAttr("style")
        }
        doc.select("[style*=font-style:italic]").forEach { element ->
            element.tagName(settings.tagMappings.getOrDefault("i", "i"))
            element.removeAttr("style")
        }
        return doc.body().html()
    }
}
