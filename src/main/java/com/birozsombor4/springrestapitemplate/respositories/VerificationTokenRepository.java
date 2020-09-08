package com.birozsombor4.springrestapitemplate.respositories;

import com.birozsombor4.springrestapitemplate.models.daos.VerificationToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Integer> {
  Optional<VerificationToken> findByToken(String token);
}
