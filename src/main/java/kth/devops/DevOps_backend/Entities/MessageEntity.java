package kth.devops.DevOps_backend.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    protected MessageEntity() {
    }

    public MessageEntity(ConversationEntity conversation, UserEntity sender, String content) {
        this.conversation = conversation;
        this.sender = sender;
        this.content = content;
        this.sentAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public ConversationEntity getConversation() {
        return conversation;
    }

    public UserEntity getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }
}
