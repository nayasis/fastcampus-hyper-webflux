package dev.fastcampus.elasticsearch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories

@SpringBootApplication
@EnableReactiveElasticsearchRepositories
/**
 * 실효성 없음
 *
 * 1. @Id @Generated 작동 안함
 *    - Elasticsearch 에서 id 채번은 string 으로 처리 (_id 필드)
 *    - id 를 string 으로 처리하면, _id 필드값을 매핑해주긴 함
 * 2. Audit 처리는 수동
 *    - Persistable 인터페이스를 상속해 명시적으로 처리해야 함
 *    - update 상태는 isNew overriding 으로 구현
 */
//@EnableElasticsearchAuditing
class ElasticsearchApplication

fun main(args: Array<String>) {
	runApplication<ElasticsearchApplication>(*args)
}
