package com.github.son_daehyeon.spring_boot_domain_generator.model

data class DomainInfo(
    val domainName: String, val properties: List<EntityProperty>
)

data class EntityProperty(
    val name: String, val type: String, val nullable: Boolean, val simple: Boolean
)
