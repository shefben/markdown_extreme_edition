package com.mycompany.markdownproject.template

class YamlTemplateStripper : TemplateStripper {
    override fun strip(content: String): String {
        return content.lines().joinToString("\n") { line ->
            stripYamlLine(line)
        }
    }

    private fun stripYamlLine(line: String): String {
        val pair = line.split(":", limit = 2)
        if (pair.size != 2) return line
        val key = pair[0]
        val valuePart = pair[1].trim()
        return when {
            valuePart.startsWith(">") -> "$key: >\n  \u2022"
            valuePart.startsWith("|") -> "$key: |\n  \u2022"
            valuePart.startsWith("-") -> line.replace(Regex("-\\s+.*"), "- ")
            valuePart.startsWith("[") -> "$key: []"
            else -> "$key: \"\""
        }
    }
}
