package com.mycompany.markdownproject.spellcheck

import com.intellij.spellchecker.tokenizer.Tokenizer
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.psi.PsiElement
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownCodeFence

class MarkdownSpellcheckStrategy : SpellcheckingStrategy() {
    override fun getTokenizer(element: PsiElement): Tokenizer<*> {
        if (element is MarkdownCodeFence) return EMPTY_TOKENIZER
        return super.getTokenizer(element)
    }
}
