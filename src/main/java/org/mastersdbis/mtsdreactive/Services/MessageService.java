package org.mastersdbis.mtsdreactive.Services;

import lombok.RequiredArgsConstructor;
import org.mastersdbis.mtsdreactive.Entities.Message;
import org.mastersdbis.mtsdreactive.Repositories.MessageRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Mono<Message> sendMessage(Message message) {
        message.setDateCreated(LocalDateTime.now());
        message.setDateUpdated(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public Flux<Message> getMessagesBetweenUsers(Integer senderId, Integer receiverId) {
        return messageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
    }

    public Flux<Message> getMessagesBySender(Integer senderId) {
        return messageRepository.findBySenderId(senderId);
    }

    public Flux<Message> getMessagesByReceiver(Integer receiverId) {
        return messageRepository.findByReceiverId(receiverId);
    }
}