package com.hedvig.gateway.enteties;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AuthorizationRow {

  @Id public String token;

  public String memberId;
}
