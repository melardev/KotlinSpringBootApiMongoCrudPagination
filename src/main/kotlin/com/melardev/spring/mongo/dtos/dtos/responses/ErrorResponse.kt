package com.melardev.spring.paginatedrestcrud.dtos.responses

import com.melardev.spring.mongo.dtos.dtos.responses.AppResponse

class ErrorResponse(errorMessage: String) : AppResponse(false) {

    init {
        addFullMessage(errorMessage)
    }

}
