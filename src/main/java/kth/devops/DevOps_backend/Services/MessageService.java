package kth.devops.DevOps_backend.Services;

import kth.devops.DevOps_backend.Entities.*;
import kth.devops.DevOps_backend.Repos.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class MessageService {
    private final ConversationRepo conversationRepo;
    private final MessageRepo messageRepo;
    private final UserRepo userRepo;
    private final PatientRepo patientRepo;
    private final DoctorRepo doctorRepo;
    private final StaffRepo staffRepo;

    public MessageService(ConversationRepo conversationRepo, MessageRepo messageRepo, UserRepo userRepo, PatientRepo patientRepo, DoctorRepo doctorRepo, StaffRepo staffRepo) {
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.staffRepo = staffRepo;
    }

    @Transactional
    public Long createConversation(Long patientUserId, Long recipientUserId, String subject, String content) {
        UserEntity patientUser = getUser(patientUserId);
        UserEntity recipient = getUser(recipientUserId);

        if (patientUser.getUserRole() != UserRole.PATIENT) {
            throw new IllegalArgumentException("Only patients can start conversations.");
        }
        if (!patientRepo.existsByUserId(patientUserId)) {
            throw new IllegalArgumentException("Patient profile not found.");
        }
        if (recipient.getUserRole() != UserRole.DOCTOR && recipient.getUserRole() != UserRole.STAFF) {
            throw new IllegalArgumentException("Recipient must be a doctor or staff member.");
        }
        if (!isValidPractitioner(recipient)) {
            throw new IllegalArgumentException("Practitioner profile not found.");
        }
        if (subject == null || subject.isBlank() || content == null || content.isBlank()) {
            throw new IllegalArgumentException("Subject and message are required.");
        }

        Optional<PatientEntity> patientResult = patientRepo.findByUserId(patientUserId);
        PatientEntity patient;

        if (patientResult.isPresent()) {
            patient = patientResult.get();
        }
        else {
            throw new IllegalArgumentException("Patient not found.");
        }

        ConversationEntity conversation = new ConversationEntity(patient, recipient, subject.trim());
        conversation = conversationRepo.save(conversation);
        MessageEntity message = new MessageEntity(conversation, patientUser, content.trim());
        messageRepo.save(message);

        return conversation.getId();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getConversations(Long userId) {
        UserEntity user = getUser(userId);
        List<ConversationEntity> conversations;

        if (user.getUserRole() == UserRole.PATIENT) {
            Optional<PatientEntity> patientResult = patientRepo.findByUserId(userId);
            if (patientResult.isPresent()) {
                PatientEntity patient = patientResult.get();
                conversations = conversationRepo.findByPatientId(patient.getId());
            }
            else {
                throw new IllegalArgumentException("Patient not found.");
            }
        }
        else if (user.getUserRole() == UserRole.DOCTOR || user.getUserRole() == UserRole.STAFF) {
            conversations = conversationRepo.findByRecipientId(userId);
        }
        else {
            throw new IllegalArgumentException("Invalid user role.");
        }

        List<Map<String, Object>> conversationList = new ArrayList<>();
        for (ConversationEntity conversation : conversations) {

            UserEntity patientUser = conversation.getPatient().getUser();
            UserEntity recipient = conversation.getRecipient();

            Map<String, Object> conversationData = Map.of(
                    "id", conversation.getId(),
                    "subject", conversation.getSubject(),
                    "createdAt", conversation.getCreatedAt(),
                    "patientName", conversation.getPatient().getName(),
                    "patientUserId", patientUser.getId(),
                    "recipientUserId", recipient.getId(),
                    "recipientUsername", recipient.getUsername()
            );
            conversationList.add(conversationData);
        }
        return conversationList;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMessages(Long userId, Long conversationId) {
        UserEntity user = getUser(userId);
        Optional<ConversationEntity> conversationResult = conversationRepo.findById(conversationId);
        ConversationEntity conversation;

        if (conversationResult.isPresent()) {
            conversation = conversationResult.get();
        }
        else {
            throw new IllegalArgumentException("Conversation not found.");
        }

        checkConversationAccess(user, conversation);
        List<MessageEntity> messages = messageRepo.findByConversationIdOrderBySentAtAsc(conversationId);
        List<Map<String, Object>> messageList = new ArrayList<>();

        for (MessageEntity message : messages) {
            Map<String, Object> messageData = Map.of(
                    "id", message.getId(),
                    "senderId", message.getSender().getId(),
                    "senderUsername", message.getSender().getUsername(),
                    "content", message.getContent(),
                    "sentAt", message.getSentAt()
            );
            messageList.add(messageData);
        }
        return messageList;
    }

    @Transactional
    public void sendMessage(Long userId, Long conversationId, String content) {
        UserEntity sender = getUser(userId);
        Optional<ConversationEntity> conversationResult = conversationRepo.findById(conversationId);
        ConversationEntity conversation;

        if (conversationResult.isPresent()) {
            conversation = conversationResult.get();
        }
        else {
            throw new IllegalArgumentException("Conversation not found.");
        }

        checkConversationAccess(sender, conversation);

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty.");
        }

        MessageEntity message = new MessageEntity(conversation, sender, content.trim());
        messageRepo.save(message);
    }

    private UserEntity getUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required.");
        }

        Optional<UserEntity> userResult = userRepo.findById(userId);

        if (userResult.isPresent()) {
            return userResult.get();
        }
        else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    private boolean isValidPractitioner(UserEntity user) {
        if (user.getUserRole() == UserRole.DOCTOR) {
            List<DoctorEntity> doctors = doctorRepo.findAll();
            for (DoctorEntity doctor : doctors) {
                if (doctor.getUser().getId().equals(user.getId())) {
                    return true;
                }
            }
            return false;
        }
        else {
            List<StaffEntity> staffMembers = staffRepo.findAll();
            for (StaffEntity staff : staffMembers) {
                if (staff.getUser().getId().equals(user.getId())) {
                    return true;
                }
            }
            return false;
        }
    }

    private void checkConversationAccess(UserEntity user, ConversationEntity conversation) {
        boolean isPatient = user.getUserRole() == UserRole.PATIENT
                && conversation.getPatient().getUser() != null
                && conversation.getPatient().getUser().getId().equals(user.getId());

        boolean isRecipient = conversation.getRecipient().getId().equals(user.getId());

        if (!isPatient && !isRecipient) {
            throw new IllegalArgumentException("You do not have access to this conversation.");
        }
    }
}
