package com.mycompany.markdownproject.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

@State(name = "MarkdownPasteSettings", storages = [Storage("markdownPaste.xml")])
class MarkdownPasteSettings : PersistentStateComponent<MarkdownPasteSettings.State> {
    data class State(
        var autoConvert: Boolean = true,
        var tagMappings: MutableMap<String, String> = mutableMapOf(
            "b" to "b",
            "i" to "i"
        ),
        var excludedTags: MutableSet<String> = mutableSetOf(),
        var maxImageWidth: Int = 800
    )
    private var myState = State()
    override fun getState(): State = myState
    override fun loadState(state: State) { myState = state }

    companion object {
        fun instance(): MarkdownPasteSettings = service()
    }
}
