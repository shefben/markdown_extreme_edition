package com.mycompany.markdownproject.wizard

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.vfs.VfsUtil
import java.io.IOException

class MarkdownModuleBuilder : ModuleBuilder() {
    override fun getModuleType(): ModuleType<MarkdownModuleBuilder> = MarkdownModuleType.instance

    override fun setupRootModel(model: ModifiableRootModel) {
        val base = contentEntryPath?.let { VfsUtil.createDirectories(it) } ?: return
        model.addContentEntry(base)
        try {
            val docs = VfsUtil.createDirectoryIfMissing(base, "docs")
            val readme = docs?.findOrCreateChildData(this, "README.md")
            if (readme != null) {
                VfsUtil.saveText(readme, "# ${model.project.name}\n")
            }
        } catch (_: IOException) {
            // ignore IO issues during setup
        }
    }
}
