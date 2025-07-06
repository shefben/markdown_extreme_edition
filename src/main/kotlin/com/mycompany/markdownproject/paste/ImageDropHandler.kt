package com.mycompany.markdownproject.paste

import com.intellij.openapi.editor.FileDropHandler
import com.intellij.openapi.editor.FileDropEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import java.awt.datatransfer.DataFlavor
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

class ImageDropHandler : FileDropHandler {
    override suspend fun handleDrop(e: FileDropEvent): Boolean {
        val editor = e.editor ?: return false
        val project = e.project
        val file = FileDocumentManager.getInstance().getFile(editor.document) ?: return false
        if (file.fileType.defaultExtension != "md") return false
        if (e.files.isNotEmpty()) {
            val f = e.files.first()
            val name = f.name
            val target = file.parent.findOrCreateChildData(this, name)
            target.setBinaryContent(f.readBytes())
            insertLink(editor, name)
            return true
        }
        val t = e.transferable
        if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            val image = t.getTransferData(DataFlavor.imageFlavor) as? BufferedImage ?: return false
            val name = "dropped_${'$'}{System.currentTimeMillis()}.png"
            val target = file.parent.findOrCreateChildData(this, name)
            ByteArrayOutputStream().use { out ->
                ImageIO.write(image, "png", out)
                target.setBinaryContent(out.toByteArray())
            }
            insertLink(editor, name)
            return true
        }
        return false
    }

    private fun insertLink(editor: Editor, name: String) {
        com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction(editor.project) {
            editor.document.insertString(editor.caretModel.offset, "![image](${'$'}name)")
        }
    }
}
