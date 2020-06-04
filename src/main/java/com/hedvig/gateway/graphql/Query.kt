package com.hedvig.gateway.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component

@Component
class Query : GraphQLQueryResolver {
    fun gateway__(): Boolean = true
}
