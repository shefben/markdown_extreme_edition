package com.mycompany.markdownproject.settings

import com.intellij.openapi.options.SearchableConfigurable
import javax.swing.*

class MarkdownPasteConfigurable : SearchableConfigurable {
    private val autoBox = JCheckBox("Auto-convert HTML clipboard content to Markdown", true)
    private val mappingField = JTextField(30)
    private val excludedField = JTextField(20)
    private val widthField = JSpinner(SpinnerNumberModel(800, 100, 5000, 50))

    override fun getId(): String = "markdown.paste.settings"
    override fun getDisplayName(): String = "Markdown Paste"

    override fun createComponent(): JComponent {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.add(autoBox)
        panel.add(JLabel("Tag mappings (tag=replacement, ...):"))
        panel.add(mappingField)
        panel.add(JLabel("Excluded tags (comma-separated):"))
        panel.add(excludedField)
        panel.add(JLabel("Max pasted image width:"))
        panel.add(widthField)
        reset()
        return panel
    }

    override fun isModified(): Boolean {
        val settings = MarkdownPasteSettings.instance().state
        return autoBox.isSelected != settings.autoConvert ||
            mappingField.text != settings.tagMappings.entries.joinToString { "${it.key}=${it.value}" } ||
            excludedField.text != settings.excludedTags.joinToString(",") ||
            (widthField.value as Int) != settings.maxImageWidth
    }

    override fun apply() {
        val state = MarkdownPasteSettings.instance().state
        state.autoConvert = autoBox.isSelected
        state.tagMappings = mappingField.text.split(',')
            .mapNotNull { it.split('=')
                .takeIf { parts -> parts.size == 2 }
                ?.let { parts -> parts[0].trim() to parts[1].trim() } }
            .toMap().toMutableMap()
        state.excludedTags = excludedField.text.split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toMutableSet()
        state.maxImageWidth = widthField.value as Int
    }

    override fun reset() {
        val state = MarkdownPasteSettings.instance().state
        autoBox.isSelected = state.autoConvert
        mappingField.text = state.tagMappings.entries.joinToString { "${it.key}=${it.value}" }
        excludedField.text = state.excludedTags.joinToString(",")
        widthField.value = state.maxImageWidth
    }
}
