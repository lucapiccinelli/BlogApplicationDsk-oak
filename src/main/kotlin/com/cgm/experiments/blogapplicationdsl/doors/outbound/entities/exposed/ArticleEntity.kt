package com.cgm.experiments.blogapplicationdsl.doors.outbound.entities.exposed

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ArticleEntity: IntIdTable("blog.articles"){
    val title = varchar("title", 50)
    val body = varchar("body", 2000)
}

class ArticleDao(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<ArticleDao>(ArticleEntity)

    var title by ArticleEntity.title
    var body by ArticleEntity.body

}