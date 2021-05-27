package com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories

import com.cgm.experiments.blogapplicationdsl.domain.Repository
import com.cgm.experiments.blogapplicationdsl.domain.model.Article

class InMemoryArticlesRepository(initialValue: List<Article> = emptyList()) : Repository<Article> {
    private val articles = initialValue.toMutableList()

    override fun getAll(): List<Article> = articles

    override fun getOne(id: Int): Article? = articles.firstOrNull { it.id == id }
    override fun save(article: Article): Article {
        val maxId: Int = articles.maxByOrNull { it.id }?.id ?: 0
        return article.copy(id = maxId + 1).apply {
            articles.add(this)
        }
    }

    fun reset(initialValue: List<Article> = emptyList()) = articles.clear().apply {
        articles.addAll(initialValue)
    }
}