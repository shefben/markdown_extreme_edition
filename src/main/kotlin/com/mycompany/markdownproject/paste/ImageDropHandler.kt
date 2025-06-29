package com.mycompany.markdownproject.paste

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.EditorEx
import java.awt.datatransfer.DataFlavor
import javax.swing.TransferHandler
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

class ImageDropHandler : TransferHandler() {
    override fun importData(support: TransferHandler.TransferSupport): Boolean {
        val editor = support.component as? EditorEx ?: return false
        val file = FileDocumentManager.getInstance().getFile(editor.document) ?: return false
        if (file.fileType.defaultExtension != "md") return false
        val t = support.transferable
        if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            val files = t.getTransferData(DataFlavor.javaFileListFlavor) as java.util.List<*>
            val f = files.firstOrNull() as? java.io.File ?: return false
            val name = f.name
            val dir = file.parent
            val target = dir.findOrCreateChildData(this, name)
            target.setBinaryContent(f.readBytes())
            insertLink(editor, name)
            return true
        }
        if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            val image = t.getTransferData(DataFlavor.imageFlavor) as? BufferedImage ?: return false
            val name = "dropped_${System.currentTimeMillis()}.png"
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
            editor.document.insertString(editor.caretModel.offset, "![image](${name})")
        }
    }

    override fun canImport(support: TransferHandler.TransferSupport): Boolean {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || support.isDataFlavorSupported(DataFlavor.imageFlavor)
    }
}
