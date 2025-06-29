package com.mycompany.markdownproject.tasks

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.tasks.TaskManager
import com.intellij.openapi.project.Project

@Service
class TaskService(private val project: Project) {
    fun updateTasksFromFile(path: String, text: String) {
        val manager = TaskManager.getManager(project) ?: return
        val regex = Regex("""^- \[( |x)] (.+)""", RegexOption.MULTILINE)
        regex.findAll(text).forEach { m ->
            val summary = m.groupValues[2]
            manager.createLocalTask(summary)
        }
    }

    companion object {
        fun getInstance(project: Project): TaskService = project.service()
    }
}
