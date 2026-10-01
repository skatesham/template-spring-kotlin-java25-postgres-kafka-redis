package com.kotlin.template.customer.infrastructure.config

import com.fasterxml.uuid.Generators
import com.kotlin.template.customer.application.port.CustomerIds
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration(proxyBeanMethods = false)
class CustomerConfiguration {
    @Bean
    fun customerClock(): Clock = Clock.systemUTC()
    @Bean
    fun customerIds(): CustomerIds {
        val generator = Generators.timeBasedEpochGenerator()
        return CustomerIds { generator.generate() }
    }
}
