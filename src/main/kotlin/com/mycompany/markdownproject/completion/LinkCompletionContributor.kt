package com.mycompany.markdownproject.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.mycompany.markdownproject.history.LinkHistoryService
import org.intellij.plugins.markdown.lang.MarkdownFileType

class LinkCompletionContributor : CompletionContributor() {
    override fun fillCompletionVariants(parameters: CompletionParameters, result: CompletionResultSet) {
        val project = parameters.editor.project ?: return
        if (parameters.completionType != CompletionType.BASIC) return
        val prefix = result.prefixMatcher.prefix
        suggestFiles(project, prefix).forEach { path ->
            result.addElement(LookupElementBuilder.create(path))
        }
        LinkHistoryService.getInstance(project).topMatches(prefix).forEach { url ->
            result.addElement(LookupElementBuilder.create(url).withTypeText("recent"))
        }
    }

    private fun suggestFiles(project: Project, prefix: String): List<String> {
        val result = mutableListOf<String>()
        VfsUtil.iterateChildrenRecursively(project.baseDir, { true }) { file ->
            if (!file.isDirectory && file.fileType == MarkdownFileType.INSTANCE) {
                val rel = VfsUtil.findRelativePath(project.baseDir, file, '/')
                if (rel != null && rel.startsWith(prefix)) result.add(rel)
            }
            true
        }
        return result
    }
}
