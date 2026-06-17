package com.example.furima.repository;

import com.example.furima.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE " +
           "((m.sender.id = :userId AND m.receiver.id = :otherUserId) OR " +
           "(m.sender.id = :otherUserId AND m.receiver.id = :userId)) " +
           "AND m.item.id = :itemId ORDER BY m.createdAt ASC")
    List<Message> findMessagesBetweenUsers(@Param("userId") Integer userId,
                                          @Param("otherUserId") Integer otherUserId,
                                          @Param("itemId") Integer itemId);

    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId AND m.status = 2 ORDER BY m.createdAt DESC")
    List<Message> findReportedMessagesForAdmin(@Param("userId") Integer userId);
}
