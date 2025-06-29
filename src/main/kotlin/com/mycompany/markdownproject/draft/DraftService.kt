package com.mycompany.markdownproject.draft

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@State(name = "MarkdownDraftService", storages = [Storage("markdownDrafts.xml")])
class DraftService : PersistentStateComponent<DraftService.State> {
    data class State(var drafts: MutableMap<String, String> = mutableMapOf())
    private var myState = State()
    override fun getState(): State = myState
    override fun loadState(state: State) { myState = state }

    fun saveDraft(path: String, text: String) {
        myState.drafts[path] = text
    }

    fun getDraft(path: String): String? = myState.drafts[path]

    companion object {
        fun instance(): DraftService = service()
    }
}
