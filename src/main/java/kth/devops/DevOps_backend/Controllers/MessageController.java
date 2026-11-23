package kth.devops.DevOps_backend.Controllers;

import kth.devops.DevOps_backend.Services.MessageService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:5173")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/conversations")
    public ResponseEntity<?> createConversation(@RequestBody CreateConversationRequest request) {

        try {
            Long conversationId = messageService.createConversation(request.userId(), request.recipientUserId(),
                    request.subject(), request.content());

            return ResponseEntity.ok(Map.of("conversationId", conversationId));

        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(@RequestParam Long userId) {

        try {
            return ResponseEntity.ok(messageService.getConversations(userId));

        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<?> getMessages(@PathVariable Long conversationId, @RequestParam Long userId) {

        try {
            return ResponseEntity.ok(messageService.getMessages(userId, conversationId));

        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/conversations/{conversationId}")
    public ResponseEntity<?> sendMessage(@PathVariable Long conversationId, @RequestBody SendMessageRequest request) {

        try {
            messageService.sendMessage(request.userId(), conversationId, request.content());

            return ResponseEntity.ok("Message sent");

        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public record CreateConversationRequest(Long userId, Long recipientUserId, String subject, String content) {}

    public record SendMessageRequest(Long userId, String content) {}
}
