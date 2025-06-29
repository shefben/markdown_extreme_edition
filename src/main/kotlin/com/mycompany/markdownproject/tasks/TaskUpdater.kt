package com.mycompany.markdownproject.tasks

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.ProjectLocator
import com.mycompany.markdownproject.snippets.SnippetService
import com.mycompany.markdownproject.history.LinkHistoryService

class TaskUpdater : FileDocumentManagerListener {
    override fun beforeDocumentSaving(document: Document) {
        val file = FileDocumentManager.getInstance().getFile(document) ?: return
        if (file.fileType.defaultExtension != "md") return
        val project = ProjectLocator.getInstance().guessProjectForFile(file) ?: return
        val text = document.text
        TaskService.getInstance(project).updateTasksFromFile(file.path, text)

        // update link history
        val urlRegex = Regex("\\((https?://[^)\\s]+)\\)")
        urlRegex.findAll(text).forEach { m ->
            LinkHistoryService.getInstance(project).record(m.groupValues[1])
        }

        // expand snippet placeholders {{snippet:name}}
        var expanded = text
        val snippetRegex = Regex("\\{\\{snippet:([a-zA-Z0-9_-]+)}}")
        snippetRegex.findAll(text).forEach { m ->
            val name = m.groupValues[1]
            val snippet = SnippetService.instance().state.snippets.find { it.name == name }?.text
            if (snippet != null) {
                expanded = expanded.replace(m.value, snippet)
            }
        }
        if (expanded != text) {
            document.setText(expanded)
        }
    }
}
