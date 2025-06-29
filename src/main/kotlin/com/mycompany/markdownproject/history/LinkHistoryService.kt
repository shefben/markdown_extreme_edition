package com.mycompany.markdownproject.history

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@State(name = "LinkHistoryService", storages = [Storage("markdownLinkHistory.xml")])
class LinkHistoryService : PersistentStateComponent<LinkHistoryService.State> {
    data class State(var urls: MutableMap<String, Int> = mutableMapOf())
    private var myState = State()

    override fun getState(): State = myState
    override fun loadState(state: State) { myState = state }

    fun record(url: String) {
        val count = myState.urls[url] ?: 0
        myState.urls[url] = count + 1
    }

    fun topMatches(prefix: String, limit: Int = 5): List<String> {
        return myState.urls
            .filterKeys { it.startsWith(prefix) }
            .toList()
            .sortedByDescending { it.second }
            .map { it.first }
            .take(limit)
    }

    companion object {
        fun getInstance(project: Project): LinkHistoryService = project.service()
    }
}
