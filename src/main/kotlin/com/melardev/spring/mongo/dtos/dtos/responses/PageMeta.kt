package com.melardev.spring.mongo.dtos.dtos.responses


import org.springframework.data.domain.Page

class PageMeta {
    var isHasNextPage: Boolean = false
        private set
    var hasPrevPage: Boolean = false
    var currentPageNumber: Int = 0
    var totalItemsCount: Long = 0 // total cartItems in total
    var requestedPageSize: Int = 0 // max cartItems per page
    var currentItemsCount: Int = 0 // cartItems in this page
    var numberOfPages: Int = 0 // number of pages
    var offset: Long = 0
    var nextPageNumber: Int = 0
    var prevPageNumber: Int = 0
        private set
    var nextPageUrl: String? = null
    var prevPageUrl: String? = null

    fun isHasPrevPage(): Boolean {
        return hasPrevPage
    }

    companion object {
        fun build(resourcePage: Page<*>, basePath: String): PageMeta {
            val pageMeta = PageMeta()
            val pageable = resourcePage.pageable

            pageMeta.totalItemsCount = resourcePage.totalElements
            pageMeta.offset = pageable.offset
            pageMeta.requestedPageSize = pageable.pageSize
            pageMeta.currentItemsCount = resourcePage.content.size
            pageMeta.numberOfPages = resourcePage.totalPages

            pageMeta.currentPageNumber = resourcePage.number + 1

            pageMeta.isHasNextPage = resourcePage.hasNext()
            pageMeta.hasPrevPage = resourcePage.hasPrevious()

            if (resourcePage.hasNext()) {
                pageMeta.nextPageNumber = resourcePage.pageable.next().pageNumber + 1
                pageMeta.nextPageUrl = String.format("%s?page_size=%d&page=%d",
                        basePath, pageMeta.requestedPageSize, pageMeta.nextPageNumber)
            } else {
                pageMeta.nextPageNumber = pageMeta.numberOfPages
                pageMeta.nextPageUrl = String.format("%s?page_size=%d&page=%d",
                        basePath, pageMeta.requestedPageSize, pageMeta.nextPageNumber)
            }
            if (resourcePage.hasPrevious()) {
                pageMeta.prevPageNumber = resourcePage.pageable.previousOrFirst().pageNumber + 1

                pageMeta.prevPageUrl = String.format("%s?page_size=%d&page=%d",
                        basePath, pageMeta.requestedPageSize,
                        pageMeta.prevPageNumber)
            } else {
                pageMeta.prevPageNumber = 1
                pageMeta.prevPageUrl = String.format("%s?page_size=%d&page=%d",
                        basePath, pageMeta.requestedPageSize, pageMeta.prevPageNumber)
            }

            return pageMeta
        }
    }
}
