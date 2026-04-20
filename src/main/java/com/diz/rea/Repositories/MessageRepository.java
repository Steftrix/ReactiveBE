package com.diz.rea.Repositories;

import com.diz.rea.Entities.Message;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveCrudRepository<Message, Integer> {
    Flux<Message> findBySenderId(Integer senderId);
    Flux<Message> findByReceiverId(Integer receiverId);
    Flux<Message> findBySenderIdAndReceiverId(Integer senderId, Integer receiverId);
}
