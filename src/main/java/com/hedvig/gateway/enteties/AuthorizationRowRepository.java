package com.hedvig.gateway.enteties;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorizationRowRepository extends CrudRepository<AuthorizationRow, String> {
    AuthorizationRow findTopByMemberIdOrderByCreatedAtDesc(String memberId);
}