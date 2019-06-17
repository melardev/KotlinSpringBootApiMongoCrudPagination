package com.melardev.spring.mongo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MongoCrudApplication

fun main(args: Array<String>) {
    runApplication<MongoCrudApplication>(*args)
}
