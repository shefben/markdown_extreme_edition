package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.nio.file.Files
import java.nio.file.Path

class ExportHtmlAction : AnAction("Export Document") {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR)
        val project = e.project ?: return
        val parser = Parser.builder().build()
        val renderer = HtmlRenderer.builder().build()
        val html = renderer.render(parser.parse(editor.document.text))
        val descriptor = FileSaverDescriptor("Export", "Choose target", "html", "pdf")
        val dialog = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, project)
        val result = dialog.save(project.baseDir, "document") ?: return
        val path: Path = result.file.toPath()
        if (path.toString().endsWith(".pdf")) {
            Files.newOutputStream(path).use { out ->
                PdfRendererBuilder().withHtmlContent(html, null).toStream(out).run()
            }
        } else {
            Files.write(path, html.toByteArray())
        }
    }
}
