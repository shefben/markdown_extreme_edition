package com.mycompany.markdownproject.wizard

import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import javax.swing.Icon
import com.intellij.icons.AllIcons

class MarkdownModuleType : ModuleType<MarkdownModuleBuilder>(ID) {
    companion object {
        const val ID = "MARKDOWN_MODULE_TYPE"
        val instance: MarkdownModuleType
            get() = ModuleTypeManager.getInstance().findByID(ID) as MarkdownModuleType
    }

    override fun createModuleBuilder() = MarkdownModuleBuilder()
    override fun getName() = "Markdown Project"
    override fun getDescription() = "Project type for Markdown documentation"
    override fun getNodeIcon(isOpened: Boolean): Icon = AllIcons.FileTypes.Markdown
}
