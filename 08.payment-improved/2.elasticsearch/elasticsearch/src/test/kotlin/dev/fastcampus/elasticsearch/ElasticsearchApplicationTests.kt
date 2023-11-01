package dev.fastcampus.elasticsearch

import dev.fastcampus.elasticsearch.config.extension.toLocalDate
import dev.fastcampus.elasticsearch.model.History
import dev.fastcampus.elasticsearch.model.Status
import dev.fastcampus.elasticsearch.repository.HistoryNativeRepository
import dev.fastcampus.elasticsearch.repository.HistoryRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class ElasticsearchApplicationTests(
	@Autowired private val repository: HistoryRepository,
) {

	@Test
	fun contextLoads() {
		runBlocking {
			listOf(
				History( 1 ,  11 ,  "apple"           ,  1000 ,  Status.CAPTURE_REQUEST ,  "2023-01-01".toLocalDate().atStartOfDay()),
				History( 2 ,  11 ,  "mango"           ,  1100 ,  Status.CAPTURE_RETRY   ,  "2023-01-02".toLocalDate().atStartOfDay()),
				History( 3 ,  11 ,  "orange,mango"    ,  1200 ,  Status.CAPTURE_SUCCESS ,  "2023-01-03".toLocalDate().atStartOfDay()),
				History( 4 ,  11 ,  "pineapple,mango" ,  1300 ,  Status.CAPTURE_REQUEST ,  "2023-01-04".toLocalDate().atStartOfDay()),
				History( 5 ,  11 ,  "banana,mango"    ,  1400 ,  Status.CAPTURE_RETRY   ,  "2023-01-05".toLocalDate().atStartOfDay()),
				History( 6 ,  11 ,  "crown"           ,  1500 ,  Status.CAPTURE_SUCCESS ,  "2023-01-06".toLocalDate().atStartOfDay()),
				History( 7 ,  11 ,  "car"             ,  1600 ,  Status.CAPTURE_REQUEST ,  "2023-01-07".toLocalDate().atStartOfDay()),
				History( 8 ,  11 ,  "tomato"          ,  1700 ,  Status.CAPTURE_RETRY   ,  "2023-01-08".toLocalDate().atStartOfDay()),
				History( 9 ,  11 ,  "potato"          ,  1800 ,  Status.CAPTURE_SUCCESS ,  "2023-01-09".toLocalDate().atStartOfDay()),
				History(10 ,  11 ,  "fried egg"       ,  1900 ,  Status.CAPTURE_REQUEST ,  "2023-01-10".toLocalDate().atStartOfDay()),
				History(11 ,  11 ,  "egg scramble"    ,  2000 ,  Status.CAPTURE_RETRY   ,  "2023-01-11".toLocalDate().atStartOfDay()),
				History(12 ,  11 ,  "boiled egg"      ,  2100 ,  Status.CAPTURE_SUCCESS ,  "2023-01-12".toLocalDate().atStartOfDay()),

				History(21 ,  12 ,  "apple"           ,  1000 ,  Status.CAPTURE_REQUEST ,  "2023-02-01".toLocalDate().atStartOfDay()),
				History(23 ,  12 ,  "orange,mango"    ,  1200 ,  Status.CAPTURE_SUCCESS ,  "2023-02-03".toLocalDate().atStartOfDay()),
				History(22 ,  12 ,  "mango"           ,  1100 ,  Status.CAPTURE_RETRY   ,  "2023-02-02".toLocalDate().atStartOfDay()),
				History(24 ,  12 ,  "pineapple,mango" ,  1300 ,  Status.CAPTURE_REQUEST ,  "2023-02-04".toLocalDate().atStartOfDay()),
				History(25 ,  12 ,  "banana,mango"    ,  1400 ,  Status.CAPTURE_RETRY   ,  "2023-02-05".toLocalDate().atStartOfDay()),
				History(26 ,  12 ,  "crown"           ,  1500 ,  Status.CAPTURE_SUCCESS ,  "2023-02-06".toLocalDate().atStartOfDay()),
				History(27 ,  12 ,  "car"             ,  1600 ,  Status.CAPTURE_REQUEST ,  "2023-02-07".toLocalDate().atStartOfDay()),
				History(28 ,  12 ,  "tomato"          ,  1700 ,  Status.CAPTURE_RETRY   ,  "2023-02-08".toLocalDate().atStartOfDay()),
				History(29 ,  12 ,  "potato"          ,  1800 ,  Status.CAPTURE_SUCCESS ,  "2023-02-09".toLocalDate().atStartOfDay()),
				History(30 ,  12 ,  "fried egg"       ,  1900 ,  Status.CAPTURE_REQUEST ,  "2023-02-10".toLocalDate().atStartOfDay()),
				History(31 ,  12 ,  "egg scramble"    ,  2000 ,  Status.CAPTURE_RETRY   ,  "2023-02-11".toLocalDate().atStartOfDay()),
				History(32 ,  12 ,  "boiled egg"      ,  2100 ,  Status.CAPTURE_SUCCESS ,  "2023-02-12".toLocalDate().atStartOfDay()),
			).forEach { repository.save(it) }
		}
	}

}
