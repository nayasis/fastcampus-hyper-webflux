package dev.fastcampus.elasticsearch.controller

import dev.fastcampus.elasticsearch.model.ArticleDocument
import dev.fastcampus.elasticsearch.repository.PostDocumentRepository
import dev.fastcampus.elasticsearch.repository.PostDocumentRepositoryNative
import dev.fastcampus.elasticsearch.repository.QrySearch
import dev.fastcampus.elasticsearch.repository.ResSearch
import kotlinx.coroutines.flow.last
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/article")
class PostController(
    private val repository: PostDocumentRepository,
    private val repositoryNative: PostDocumentRepositoryNative,
) {

    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: Long): ArticleDocument? {
        return repository.findById(id)
    }

    @PostMapping
    suspend fun save(@RequestBody request: ArticleDocument): ArticleDocument {
        return repository.save(request)
    }

    @PostMapping("/all")
    suspend fun saveAll(@RequestBody requests: List<ArticleDocument>) {
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