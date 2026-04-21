package org.mastersdbis.mtsdreactive.Controllers;

import lombok.RequiredArgsConstructor;
import org.mastersdbis.mtsdreactive.DTO.MessageDTO;
import org.mastersdbis.mtsdreactive.Services.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public Mono<ResponseEntity<String>> sendMessage(@RequestBody MessageDTO dto) {
        return messageService.sendMessage(dto.toMessage())
            .map(m -> ResponseEntity.ok("Message sent successfully."))
            .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @GetMapping("/between/{senderId}/{receiverId}")
    public Mono<ResponseEntity<List<MessageDTO>>> getBetween(
            @PathVariable Integer senderId,
            @PathVariable Integer receiverId) {
        return messageService.getMessagesBetweenUsers(senderId, receiverId)
            .map(MessageDTO::fromMessage)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/sender/{senderId}")
    public Mono<ResponseEntity<List<MessageDTO>>> getBySender(@PathVariable Integer senderId) {
        return messageService.getMessagesBySender(senderId)
            .map(MessageDTO::fromMessage)
            .collectList()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/receiver/{receiverId}")
    public Mono<ResponseEntity<List<MessageDTO>>> getByReceiver(@PathVariable Integer receiverId) {
        return messageService.getMessagesByReceiver(receiverId)
            .map(MessageDTO::fromMessage)
            .collectList()
            .map(ResponseEntity::ok);
    }
}