package com.hedvig.gateway.enteties;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public class AuthorizationRow {
    @Id
    public String token;

    public String memberId;

    @CreationTimestamp
    public Instant createdAt;

    @UpdateTimestamp
    public Instant updatedAt;
}
