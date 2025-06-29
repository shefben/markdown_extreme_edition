package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ide.CopyPasteManager
import com.mycompany.markdownproject.template.TemplateStrippers
import com.mycompany.markdownproject.template.TemplateStrippers.strip
import com.mycompany.markdownproject.template.TemplateStrippers.supports
import com.intellij.openapi.util.TextRange
import java.awt.datatransfer.StringSelection

class CopyCodeBlockAsTemplateAction : AnAction("Copy as Template") {

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val block = editor?.let { detectBlock(it) }
        e.presentation.isEnabledAndVisible = block != null && supports(block.lang)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val block = detectBlock(editor) ?: return
        val template = strip(block.lang, block.content)
        val text = "```${block.lang}\n$template\n```"
        CopyPasteManager.getInstance().setContents(StringSelection(text))
    }

    data class Block(val lang: String, val content: String)

    private fun detectBlock(editor: Editor): Block? {
        val sel = editor.selectionModel
        if (!sel.hasSelection()) return null

        val doc = editor.document
        val start = sel.selectionStart
        val end = sel.selectionEnd

        val startLine = doc.getLineNumber(start)
        val endLine = doc.getLineNumber(end)
        val openPattern = Regex("^```(\\w+)?\\s*$")
        val closePattern = Regex("^```\\s*$")

        fun lineText(line: Int): String {
            val range = TextRange(doc.getLineStartOffset(line), doc.getLineEndOffset(line))
            return doc.getText(range).trim()
        }

        var openLine = startLine
        while (openLine >= 0) {
            val text = lineText(openLine)
            if (closePattern.matches(text)) return null
            val m = openPattern.matchEntire(text)
            if (m != null) break
            openLine--
        }
        if (openLine < 0) return null
        val lang = openPattern.matchEntire(lineText(openLine))!!.groupValues[1]
        var closeLine = openLine + 1
        while (closeLine < doc.lineCount) {
            val t = lineText(closeLine)
            if (openPattern.matches(t)) return null
            if (closePattern.matches(t)) break
            closeLine++
        }
        if (closeLine >= doc.lineCount) return null

        val contentStart = doc.getLineStartOffset(openLine + 1)
        val contentEnd = doc.getLineStartOffset(closeLine)
        if (start < contentStart || end > contentEnd) return null

        val content = doc.getText(TextRange(contentStart, contentEnd))
        return Block(lang.ifEmpty { "" }, content)
    }
}
