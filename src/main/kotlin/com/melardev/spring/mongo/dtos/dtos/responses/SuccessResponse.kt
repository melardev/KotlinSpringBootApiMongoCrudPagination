package com.melardev.spring.mongo.dtos.dtos.responses

open class SuccessResponse @JvmOverloads constructor(message: String? = null) : AppResponse(true) {

    init {
        addFullMessage(message)
    }
}
