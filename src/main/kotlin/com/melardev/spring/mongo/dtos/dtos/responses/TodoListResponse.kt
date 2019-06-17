package com.melardev.spring.mongo.dtos.dtos.responses

class TodoListResponse(val pageMeta: PageMeta, val todos: Collection<TodoSummaryDto>) : SuccessResponse()
