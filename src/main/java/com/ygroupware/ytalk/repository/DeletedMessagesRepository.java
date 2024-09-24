package com.ygroupware.ytalk.repository;

import com.ygroupware.ytalk.entity.DeletedMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedMessagesRepository extends JpaRepository<DeletedMessages, Long> {

}