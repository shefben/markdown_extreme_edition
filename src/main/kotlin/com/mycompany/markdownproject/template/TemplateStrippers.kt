package com.mycompany.markdownproject.template

object TemplateStrippers {
    private val registry = mutableMapOf<String, TemplateStripper>()

    init {
        register("yaml", YamlTemplateStripper())
        register("yml", YamlTemplateStripper())
        register("json", JsonTemplateStripper())
        register("toml", TomlTemplateStripper())
    }

    fun register(lang: String, stripper: TemplateStripper) {
        registry[lang] = stripper
    }

    fun supports(lang: String): Boolean = registry.containsKey(lang)

    fun strip(lang: String, content: String): String = registry[lang]?.strip(content) ?: content
}
