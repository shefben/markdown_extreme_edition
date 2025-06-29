package com.mycompany.markdownproject.toolwindow

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.components.BorderLayoutPanel
import com.intellij.openapi.fileEditor.FileEditorManager
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile
import javax.swing.DefaultListModel
import javax.swing.DropMode
import javax.swing.TransferHandler
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import com.intellij.openapi.command.WriteCommandAction

class NavigationToolWindowFactory : ToolWindowFactory {
    data class Section(val text: String, val start: Int, val end: Int, val content: String)

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = BorderLayoutPanel()
        val model = DefaultListModel<Section>()
        val list = JBList(model)
        list.dropMode = DropMode.INSERT
        list.dragEnabled = true
        val search = JBTextField()
        panel.addToTop(search)
        panel.addToCenter(list)
        val content = toolWindow.contentManager.factory.createContent(panel, "Navigation", false)
        toolWindow.contentManager.addContent(content)

        list.transferHandler = object : TransferHandler() {
            override fun getSourceActions(c: javax.swing.JComponent) = MOVE
            override fun createTransferable(c: javax.swing.JComponent) = StringSelection(list.selectedIndex.toString())
            override fun importData(support: TransferSupport): Boolean {
                val from = support.transferable.getTransferData(DataFlavor.stringFlavor).toString().toInt()
                val to = (support.dropLocation as? JList.DropLocation)?.index ?: return false
                if (from == to) return false
                val doc = FileEditorManager.getInstance(project).selectedTextEditor?.document ?: return false
                val sections = (0 until model.size).map { model.getElementAt(it) }.toMutableList()
                val moved = sections.removeAt(from)
                sections.add(if (from < to) to - 1 else to, moved)
                WriteCommandAction.runWriteCommandAction(project) {
                    val prefix = doc.text.substring(0, sections.first().start)
                    val newText = StringBuilder(prefix)
                    sections.forEach { newText.append(it.content) }
                    doc.setText(newText.toString())
                }
                model.removeAllElements()
                sections.forEach { model.addElement(it) }
                return true
            }
        }

        project.messageBus.connect().subscribe(FileDocumentManagerListener.TOPIC, object : FileDocumentManagerListener {
            override fun beforeDocumentSaving(document: com.intellij.openapi.editor.Document) {
                val file = FileDocumentManager.getInstance().getFile(document) ?: return
                if (file.fileType.defaultExtension == "md") {
                    val sections = mutableListOf<Section>()
                    var offset = 0
                    var currentStart = -1
                    var currentText = ""
                    var builder = StringBuilder()
                    document.text.lines().forEach { line ->
                        if (line.startsWith("#")) {
                            if (currentStart != -1) {
                                val sectionText = builder.toString()
                                sections.add(Section(currentText, currentStart, offset, sectionText))
                                builder = StringBuilder()
                            }
                            currentStart = offset
                            currentText = line.trim()
                        }
                        builder.append(line).append('\n')
                        offset += line.length + 1
                    }
                    if (currentStart != -1) {
                        val sectionText = builder.toString()
                        sections.add(Section(currentText, currentStart, offset, sectionText))
                    }
                    model.removeAllElements()
                    val filtered = sections.filter { it.text.contains(search.text, ignoreCase = true) }
                    filtered.forEach { model.addElement(it) }
                }
            }
        })

        list.addListSelectionListener {
            val entry = list.selectedValue ?: return@addListSelectionListener
            val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return@addListSelectionListener
            editor.caretModel.moveToOffset(entry.start)
            editor.scrollingModel.scrollToCaret(com.intellij.openapi.editor.ScrollType.CENTER)
        }
    }
}
