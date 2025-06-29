package com.mycompany.markdownproject.snippets

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@State(name = "SnippetService", storages = [Storage("markdownSnippets.xml")])
class SnippetService : PersistentStateComponent<SnippetService.State> {
    data class Snippet(val name: String, val text: String)
    data class State(var snippets: MutableList<Snippet> = mutableListOf())
    private var myState = State(mutableListOf(Snippet("note", "> **Note:** ")))
    override fun getState(): State = myState
    override fun loadState(state: State) { myState = state }

    companion object {
        fun instance(): SnippetService = service()
    }
}
