package com.melardev.spring.mongo.controllers

import com.melardev.spring.mongo.dtos.dtos.responses.AppResponse
import com.melardev.spring.mongo.dtos.dtos.responses.PageMeta
import com.melardev.spring.mongo.dtos.dtos.responses.TodoListResponse
import com.melardev.spring.mongo.dtos.dtos.responses.TodoSummaryDto
import com.melardev.spring.mongo.entities.Todo
import com.melardev.spring.mongo.repositories.TodosRepository
import com.melardev.spring.paginatedrestcrud.dtos.responses.ErrorResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.ZoneId
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@CrossOrigin
@RestController
@RequestMapping("/todos")
class TodosController(@Autowired
                      private val todosRepository: TodosRepository) {

    @GetMapping
    fun index(@RequestParam(value = "page", defaultValue = "1") page: Int,
              @RequestParam(value = "page_size", defaultValue = "10") pageSize: Int,
              request: HttpServletRequest): AppResponse {

        val pageable = getPageable(page, pageSize)
        val todos = this.todosRepository.findAll(pageable)
        val todoDtos = buildTodoDtos(todos)
        return TodoListResponse(PageMeta.build(todos, request.requestURI), todoDtos)
    }

    @GetMapping("/pending")
    fun getNotCompletedTodos(@RequestParam(value = "page", defaultValue = "1") page: Int,
                             @RequestParam(value = "page_size", defaultValue = "10") pageSize: Int,
                             request: HttpServletRequest): AppResponse {
        val pageable = getPageable(page - 1, pageSize)
        val todos = this.todosRepository.findByCompletedFalse(pageable)
        return TodoListResponse(PageMeta.build(todos, request.requestURI), buildTodoDtos(todos))
    }

    @GetMapping("/completed")
    fun getCompletedTodos(@RequestParam(value = "page", defaultValue = "1") page: Int,
                          @RequestParam(value = "page_size", defaultValue = "10") pageSize: Int,
                          request: HttpServletRequest): AppResponse {
        val todosPage = todosRepository.findByCompletedIsTrue(getPageable(page, pageSize))
        return TodoListResponse(PageMeta.build(todosPage, request.requestURI), buildTodoDtos(todosPage))
    }


    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: String): ResponseEntity<*> {
        val todo = this.todosRepository.findById(id)

        return when {
            todo.isPresent -> ResponseEntity(todo.get(), HttpStatus.OK)
            else -> ResponseEntity(ErrorResponse("Not Found"), HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping
    fun create(@Valid @RequestBody todo: Todo): ResponseEntity<Todo> {
        return ResponseEntity(todosRepository.save(todo), HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: String,
               @RequestBody todoInput: Todo): ResponseEntity<*> {
        val optionalTodo = todosRepository.findById(id)
        return if (optionalTodo.isPresent()) {
            val todo = optionalTodo.get()
            todo.title = todoInput.title

            val description = todoInput.description
            if (description != null)
                todo.description = description

            todo.completed = todoInput.completed
            ResponseEntity.ok(todosRepository.save(optionalTodo.get()))
        } else {
            ResponseEntity<Any>(ErrorResponse("This todo does not exist"), HttpStatus.NOT_FOUND)
        }
    }


    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: String): ResponseEntity<*> {
        val todo = todosRepository.findById(id)
        return if (todo.isPresent) {
            todosRepository.delete(todo.get())
            ResponseEntity.noContent().build<Any>()
        } else {
            ResponseEntity<Any>(ErrorResponse("This todo does not exist"), HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping
    fun deleteAll(): ResponseEntity<*> {
        todosRepository.deleteAll()
        return ResponseEntity<Any>(HttpStatus.NO_CONTENT)
    }

    @GetMapping(value = ["/after/{date}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getByDateAfter(@PathVariable("date") @DateTimeFormat(pattern = "dd-MM-yyyy") date: Date): List<Todo> {
        val articlesIterable = todosRepository.findByCreatedAtAfter(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
        val articleList = ArrayList<Todo>()
        articlesIterable.forEach(Consumer<Todo> { articleList.add(it) })
        return articleList
    }

    @GetMapping(value = ["/before/{date}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getByDateBefore(@PathVariable("date") @DateTimeFormat(pattern = "dd-MM-yyyy") date: Date): List<Todo> {
        val articlesIterable = todosRepository.findByCreatedAtBefore(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
        val articleList = ArrayList<Todo>()
        articlesIterable.forEach(Consumer<Todo> { articleList.add(it) })
        return articleList
    }

    private fun getPageable(page: Int, pageSize: Int): Pageable {
        var page = page
        var pageSize = pageSize
        if (page <= 0)
            page = 1

        if (pageSize <= 0)
            pageSize = 5

        return PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "createdAt")
    }

    private fun buildTodoDtos(todos: Page<Todo>): List<TodoSummaryDto> {
        return todos.content.stream()
                .map { TodoSummaryDto.build(it) }
                .collect(Collectors.toList())
    }
}
