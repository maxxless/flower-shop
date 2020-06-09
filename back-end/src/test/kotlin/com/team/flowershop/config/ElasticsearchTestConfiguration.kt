package com.team.flowershop.config

import org.assertj.core.util.Files
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchProperties
import org.springframework.context.annotation.Configuration

@Configuration
class ElasticsearchTestConfiguration {
    @Autowired
    fun elasticsearchProperties(elasticsearchProperties: ElasticsearchProperties) {
        val tempdir = Files.newTemporaryFolder()
        elasticsearchProperties.properties["path.home"] = tempdir.absolutePath
    }
}
