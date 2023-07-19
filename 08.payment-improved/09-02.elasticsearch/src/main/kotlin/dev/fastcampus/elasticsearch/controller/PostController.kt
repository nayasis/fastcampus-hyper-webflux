package dev.fastcampus.elasticsearch.controller

import dev.fastcampus.elasticsearch.model.PostDocument
import dev.fastcampus.elasticsearch.repository.PostDocumentRepository
import dev.fastcampus.elasticsearch.repository.PostDocumentRepositoryNative
import dev.fastcampus.elasticsearch.repository.QrySearch
import dev.fastcampus.elasticsearch.repository.ResSearch
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/post")
class PostController(
    private val repository: PostDocumentRepository,
    private val repositoryNative: PostDocumentRepositoryNative,
) {

    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: Long): PostDocument? {
        return repository.findById(id)
    }

    @PostMapping
    suspend fun save(@RequestBody request: PostDocument): PostDocument {
        return repository.save(request)
    }

    @PostMapping("/all")
    suspend fun saveAll(@RequestBody requests: List<PostDocument>) {
        // 마지막까지 기다려줘야 데이터가 모두 정상 입력됨
        repository.saveAll(requests).last()
    }

    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: Long) {
        repository.deleteById(id)
    }

    @DeleteMapping("/all")
    suspend fun deleteAll() {
        repository.deleteAll()
    }

    @GetMapping("/all")
    suspend fun getAll(
        request: QrySearch,
    ): ResSearch {
        return repositoryNative.search(request)
    }

}