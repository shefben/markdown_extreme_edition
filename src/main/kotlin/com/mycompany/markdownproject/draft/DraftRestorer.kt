package com.mycompany.markdownproject.draft

import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.fileEditor.FileDocumentManager

class DraftRestorer : EditorFactoryListener {
    override fun editorCreated(event: EditorFactoryEvent) {
        val editor = event.editor
        val file = FileDocumentManager.getInstance().getFile(editor.document) ?: return
        if (file.fileType.defaultExtension == "md") {
            val draft = DraftService.instance().getDraft(file.path) ?: return
            editor.document.setText(draft)
        }
    }
}
