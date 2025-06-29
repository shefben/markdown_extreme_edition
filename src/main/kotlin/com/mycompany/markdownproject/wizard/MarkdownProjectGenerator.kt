package com.mycompany.markdownproject.wizard

import com.intellij.platform.DirectoryProjectGenerator
import com.intellij.openapi.project.Project
import com.intellij.openapi.module.Module
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.facet.ui.ValidationResult
import javax.swing.*

data class TemplateSettings(var template: Template = Template.DOCS) {
    enum class Template { DOCS, BLOG, API }
}

class MarkdownProjectGenerator : DirectoryProjectGenerator<TemplateSettings> {
    private var settingsPanel: JPanel? = null
    override fun getName(): String = "Markdown Project"
    override fun getDescription(): String = "Create a project containing Markdown documentation"
    override fun getLogo(): Icon? = null

    override fun generateProject(project: Project, baseDir: VirtualFile, settings: TemplateSettings, module: Module) {
        val docs = when (settings.template) {
            TemplateSettings.Template.DOCS -> VfsUtil.createDirectoryIfMissing(baseDir, "docs")
            TemplateSettings.Template.BLOG -> VfsUtil.createDirectoryIfMissing(baseDir, "blog")
            TemplateSettings.Template.API -> VfsUtil.createDirectoryIfMissing(baseDir, "api-docs")
        } ?: return
        val readme = docs.findOrCreateChildData(this, "README.md")
        VfsUtil.saveText(readme, "# ${project.name}\n")
        val editorconfig = baseDir.findOrCreateChildData(this, ".editorconfig")
        VfsUtil.saveText(editorconfig, "root = true\n\n[*]\ncharset = utf-8\n")
    }

    override fun validate(baseDirPath: String): ValidationResult = ValidationResult.OK

    fun showGenerationSettings(baseDir: VirtualFile): JComponent {
        val panel = JPanel()
        val combo = JComboBox(TemplateSettings.Template.values())
        panel.add(JLabel("Template:"))
        panel.add(combo)
        panel.putClientProperty("combo", combo)
        settingsPanel = panel
        return panel
    }

    fun getSettings(): TemplateSettings {
        val panel = settingsPanel
        val combo = panel?.getClientProperty("combo") as? JComboBox<*> ?: return TemplateSettings()
        @Suppress("UNCHECKED_CAST")
        return TemplateSettings(combo.selectedItem as TemplateSettings.Template)
    }
}
