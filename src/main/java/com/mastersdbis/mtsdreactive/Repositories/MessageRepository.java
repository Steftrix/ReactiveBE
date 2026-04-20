package com.mastersdbis.mtsdreactive.Repositories;

import com.mastersdbis.mtsdreactive.Entities.Message;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveCrudRepository<Message, Integer> {

    Flux<Message> findBySenderId(Integer senderId);

    Flux<Message> findByReceiverId(Integer receiverId);

    @Query("SELECT * FROM messages WHERE sender_user_id = :senderId AND receiver_user_id = :receiverId")
    Flux<Message> findBySenderIdAndReceiverId(Integer senderId, Integer receiverId);
}
