package com.Qiydaardev.systemcontrol.repository;

import com.Qiydaardev.systemcontrol.entity.AccountUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<AccountUsers, Integer> {

    Optional<AccountUsers> findByEmail(String email);

}
