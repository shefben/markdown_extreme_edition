package com.mycompany.markdownproject.draft

import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.fileEditor.FileDocumentManager

class DraftListener : EditorFactoryListener {
    override fun editorReleased(event: EditorFactoryEvent) {
        val editor = event.editor
        val file = FileDocumentManager.getInstance().getFile(editor.document) ?: return
        if (file.fileType.defaultExtension == "md") {
            DraftService.instance().saveDraft(file.path, editor.document.text)
        }
    }
}
