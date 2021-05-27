package com.cgm.experiments.blogapplicationdsl.domain

interface Repository<T> {
    fun getAll(): List<T>
    fun getOne(id: Int): T?
    fun save(article: T): T
}