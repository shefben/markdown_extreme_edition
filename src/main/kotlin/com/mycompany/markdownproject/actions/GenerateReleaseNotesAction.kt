package com.mycompany.markdownproject.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vcs.VcsException
import git4idea.history.GitHistoryUtils
import git4idea.repo.GitRepositoryManager
import git4idea.GitCommit
import com.intellij.testFramework.LightVirtualFile

class GenerateReleaseNotesAction : AnAction("Generate Release Notes") {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val repo = GitRepositoryManager.getInstance(project).repositories.firstOrNull() ?: return
        val commits: List<GitCommit> = try {
            GitHistoryUtils.history(project, repo.root, "HEAD", "--max-count=20")
        } catch (ex: VcsException) {
            emptyList()
        }
        val builder = StringBuilder("# Release Notes\n\n")
        commits.forEach { builder.append("- ").append(it.subject).append("\n") }
        val file = LightVirtualFile("RELEASE_NOTES.md", builder.toString())
        FileEditorManager.getInstance(project).openFile(file, true)
    }
}
