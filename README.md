# Markdown Project Creator

<!-- Plugin description -->
Markdown Project Creator is an IntelliJ IDEA plugin for quickly starting Markdown based projects. It also converts formatted website content from the clipboard into clean Markdown when you paste.
<!-- Plugin description end -->

## Features

- New project wizard entry with templates for docs, blogs and API docs
- Markdown Project type available in the New Project dialog
- `.editorconfig` added with UTF-8 encoding
- Paste handler that converts HTML clipboard content to clean Markdown on paste
- Images pasted or dropped are optimized, saved locally and referenced in Markdown
- Custom tag mapping and exclusion rules for paste conversion
- Keeps website formatting such as headings, bold, italics, bullet and numbered lists
- Quick actions to insert tables, code blocks and images
- "Copy as Template" action for YAML, JSON and TOML code blocks
- Convert selected HTML to Markdown via **Convert HTML Selection** action
- Convert highlighted HTML to Markdown from the editor context menu
- "Fix Markdown Formatting" action to reformat the current document
- Auto-saves Markdown drafts across IDE sessions
- Generate or update a Table of Contents for README files using `<!-- TOC -->` markers
- Drag and drop images into Markdown files
- Lint inspection with quick fix for trailing spaces
- Snippet library with reusable blocks and front matter insertion; placeholders update across documents
- Export Markdown to HTML or PDF
- "Paste from Browser" action converts copied web content to Markdown
- Setting under **Tools | Markdown Paste** to disable the auto-convert behaviour
- Navigation tool window to browse headings, code blocks and anchors
- Drag-and-drop heading reordering from the navigation tool window
- Keyboard shortcuts for bold, italic and headings
- Autocomplete for internal Markdown links and frequently used URLs
- Broken link detection with quick fix
- Tasks are created from checklist items on save
- Insert citations as numbered footnotes
- Spellcheck ignores code blocks
- Generate release notes from recent Git commits
- Insert horizontal rules with one click
- Blockquote selected lines
- Toggle strikethrough formatting
- Convert clipboard URLs into Markdown links

## Building

Run the Gradle wrapper to build the plugin:

```bash
./gradlew buildPlugin -x signPlugin --no-daemon
```

The resulting plugin zip will be in `build/distributions`.
