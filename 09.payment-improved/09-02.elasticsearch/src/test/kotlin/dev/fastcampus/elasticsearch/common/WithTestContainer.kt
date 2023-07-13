package dev.fastcampus.elasticsearch.common

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Container

/**
 * [testcontainers](https://github.com/testcontainers/testcontainers-java)
 */
interface WithTestContainer {
    companion object {
        @Container
        @JvmStatic
        val container = ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.15.2").apply {
            addEnv("discovery.type","single-node")
            addEnv("xpack.security.enabled", "false")
            addExposedPorts(9200)
            start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.elasticsearch.uris") {
                "localhost:${container.getMappedPort(9200)}"
            }
        }
    }
}