package com.mycompany.markdownproject.paste

import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.vladsch.flexmark.formatter.Formatter
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.parser.Parser
import org.jsoup.Jsoup
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import javax.imageio.ImageIO
import java.awt.Graphics2D
import java.io.ByteArrayOutputStream

class MarkdownPasteHandler : EditorActionHandler() {
    // Use the editor specific paste action so the returned handler is an
    // EditorActionHandler. Using IdeActions.ACTION_PASTE causes a
    // ClassCastException because that ID refers to a generic AnAction.
    private val original = EditorActionManager.getInstance()
        .getActionHandler(IdeActions.ACTION_EDITOR_PASTE)
    private val htmlConverter = FlexmarkHtmlConverter.builder().build()
    private val parser = Parser.builder().build()
    private val formatter = Formatter.builder().build()

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

    private fun extractImage(transferable: Transferable?): Image? {
        if (transferable == null) return null
        if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            return transferable.getTransferData(DataFlavor.imageFlavor) as? Image
        }
        return null
    }

    private fun optimize(image: BufferedImage, maxWidth: Int): BufferedImage {
        if (image.width <= maxWidth) return image
        val ratio = maxWidth.toDouble() / image.width
        val height = (image.height * ratio).toInt()
        val scaled = BufferedImage(maxWidth, height, BufferedImage.TYPE_INT_ARGB)
        val g: Graphics2D = scaled.createGraphics()
        g.drawImage(image, 0, 0, maxWidth, height, null)
        g.dispose()
        return scaled
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


    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
        val project = editor.project
        val psiFile = project?.let { PsiDocumentManager.getInstance(it).getPsiFile(editor.document) }
        if (psiFile == null || psiFile.fileType.defaultExtension != "md") {
            original.execute(editor, caret, dataContext)
            return
        }
        val settings = com.mycompany.markdownproject.settings.MarkdownPasteSettings.instance()
        val state = settings.state
        if (!state.autoConvert) {
            original.execute(editor, caret, dataContext)
            return
        }
        val clipboard = CopyPasteManager.getInstance().contents
        val html = extractHtml(clipboard)
        val image = extractImage(clipboard)
        if (image != null) {
            val psiFile = PsiDocumentManager.getInstance(project!!).getPsiFile(editor.document) ?: return
            val vFile = psiFile.virtualFile
            val dir = vFile.parent
            val name = "pasted_${System.currentTimeMillis()}.png"
            val target = dir.findOrCreateChildData(this, name)
            val buffered = image as? java.awt.image.BufferedImage ?: return
            val optimized = optimize(buffered, state.maxImageWidth)
            ByteArrayOutputStream().use { out ->
                ImageIO.write(optimized, "png", out)
                target.setBinaryContent(out.toByteArray())
            }
            val markdown = "![image](${name})"
            WriteCommandAction.runWriteCommandAction(project) {
                val model = editor.selectionModel
                val start = model.selectionStart
                val end = model.selectionEnd
                editor.document.replaceString(start, end, markdown)
                PsiDocumentManager.getInstance(project).commitDocument(editor.document)
            }
        } else if (html != null) {
            val cleaned = sanitizeHtml(html, state)
            val markdown = htmlConverter.convert(cleaned)
            val formatted = formatter.render(parser.parse(markdown))
            WriteCommandAction.runWriteCommandAction(project) {
                val model = editor.selectionModel
                val start = model.selectionStart
                val end = model.selectionEnd
                editor.document.replaceString(start, end, formatted)
                PsiDocumentManager.getInstance(project!!).commitDocument(editor.document)
            }
        } else {
            original.execute(editor, caret, dataContext)
        }
    }
}
