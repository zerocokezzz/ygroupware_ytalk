package com.ygroupware.ytalk.repository;

import com.ygroupware.ytalk.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembersRepository extends JpaRepository<Members, String> {

}
