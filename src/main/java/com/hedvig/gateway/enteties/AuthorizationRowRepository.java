package com.hedvig.gateway.enteties;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizationRowRepository extends CrudRepository<AuthorizationRow, String> {

    //@Cacheable("authorizationRows")
    AuthorizationRow findOne(String id);

}
