package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor

class GenerateTocAction : AnAction("Generate TOC") {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR) ?: return
        val file = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE) ?: return
        if (!file.name.equals("README.md", ignoreCase = true)) return
        val text = editor.document.text
        val tocContent = buildString {
            text.lineSequence().forEach { line ->
                val match = Regex("^(#{1,6})\\s+(.*)").find(line)
                if (match != null) {
                    val level = match.groupValues[1].length
                    val heading = match.groupValues[2]
                    append("  ".repeat(level - 1))
                    append("- [${heading}](#${heading.lowercase().replace(' ', '-')})\n")
                }
            }
        }
        val doc = editor.document
        val startMarker = "<!-- TOC -->"
        val endMarker = "<!-- TOC END -->"
        WriteCommandAction.runWriteCommandAction(editor.project) {
            val existingStart = text.indexOf(startMarker)
            val existingEnd = text.indexOf(endMarker)
            val tocBlock = "$startMarker\n$tocContent$endMarker\n"
            if (existingStart != -1 && existingEnd != -1 && existingEnd > existingStart) {
                doc.replaceString(existingStart, existingEnd + endMarker.length, tocBlock)
            } else {
                doc.insertString(0, tocBlock + "\n")
            }
        }
    }
}
