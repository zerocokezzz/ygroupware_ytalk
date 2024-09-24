package com.ygroupware.ytalk.repository;

import com.ygroupware.ytalk.entity.DeletedChatRooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedChatRoomsRepository extends JpaRepository<DeletedChatRooms, Long> {

}