package me.charleshh.gorpctodo.domain

import domain.Todo
import javax.inject.Inject

data class TodoQuery(
    val filterTodoStatus: Boolean? = null
)

interface TodoRepository : Syncable