package com.cgm.experiments.blogapplicationdsl.doors.outbound.repositories.exposed

import com.cgm.experiments.blogapplicationdsl.domain.Repository
import com.cgm.experiments.blogapplicationdsl.domain.model.Article
import com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.exposed.ArticleDao
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedArticlesRepository : Repository<Article> {
    override fun getAll(): List<Article> = transaction {
        ArticleDao.all()
            .map { Article(it.id.value, it.title, it.body) }
    }

    override fun getOne(id: Int): Article? {
        TODO("Not yet implemented")
    }

    override fun save(article: Article): Article {
        TODO("Not yet implemented")
    }

}