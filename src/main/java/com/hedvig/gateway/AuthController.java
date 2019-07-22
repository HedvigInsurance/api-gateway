package com.hedvig.gateway;

import com.hedvig.gateway.dto.ReassignMemberRequest;
import com.hedvig.gateway.enteties.AuthorizationRow;
import com.hedvig.gateway.enteties.AuthorizationRowRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthorizationRowRepository repo;
    private final String token;

    public AuthController(AuthorizationRowRepository repo, @Value("${member-service.token}") String token) {
        this.repo = repo;
        this.token = token;
    }
    @PostMapping("/_/reassign")
    public ResponseEntity<Void> reassignMember(@RequestHeader("token") String memberServiceToken, @RequestBody ReassignMemberRequest request) {
        if (!memberServiceToken.equals(token)) {
            return ResponseEntity.status(401).build();
        }

        AuthorizationRow row = repo.findByMemberId(request.getOldMemberId());
        if (row == null) {
            return ResponseEntity.notFound().build();
        }
        row.memberId = request.getNewMemberId();

        repo.save(row);
        return ResponseEntity.ok().build();
    }
}
