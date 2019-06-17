package com.melardev.spring.mongo.dtos.dtos.responses

import com.melardev.spring.mongo.entities.Todo
import java.time.LocalDateTime

class TodoDetailsResponse(val id: String?, val title: String?, val description: String?, val completed: Boolean,
                          val createdAt: LocalDateTime?, val updatedAt: LocalDateTime?) : SuccessResponse() {

    @JvmOverloads
    constructor(todo: Todo, message: String? = null)
            : this(todo.id, todo.title, todo.description, todo.completed, todo.createdAt, todo.updatedAt) {
        addFullMessage(message)
    }
}
