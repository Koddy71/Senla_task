package com.sen.service.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sen.client.UserServiceClient;
import com.sen.dto.internal.UserInternal;
import com.sen.dto.request.CreateDialogRequest;
import com.sen.dto.request.MessageRequest;
import com.sen.dto.response.DialogResponse;
import com.sen.dto.response.MessageResponse;
import com.sen.entity.Dialog;
import com.sen.entity.Message;
import com.sen.exception.DialogAccessDeniedException;
import com.sen.exception.DialogAlreadyExistsException;
import com.sen.exception.DialogNotFoundException;
import com.sen.exception.SelfDialogException;
import com.sen.exception.UserBlockedException;
import com.sen.mapper.DialogMapper;
import com.sen.mapper.MessageMapper;
import com.sen.repository.DialogRepository;
import com.sen.repository.MessageRepository;
import com.sen.service.DialogService;

@Service
@Transactional
public class DialogServiceImpl implements DialogService {

    private static final Logger logger = LoggerFactory.getLogger(DialogServiceImpl.class);

    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final UserServiceClient userServiceClient;
    private final MessageMapper messageMapper;
    private final DialogMapper dialogMapper;

    public DialogServiceImpl(DialogRepository dialogRepository,
            MessageRepository messageRepository,
            UserServiceClient userServiceClient,
            MessageMapper messageMapper,
            DialogMapper dialogMapper) {
        this.dialogRepository = dialogRepository;
        this.messageRepository = messageRepository;
        this.userServiceClient = userServiceClient;
        this.messageMapper = messageMapper;
        this.dialogMapper = dialogMapper;
    }

    @Override
    public DialogResponse createDialog(String myLogin, CreateDialogRequest request) {
        logger.info("Запрос на создание диалога от пользователя {} с участником {}", myLogin,
                request.getParticipantLogin());
        UserInternal me = getUserByLogin(myLogin);
        ensureNotBlocked(me);

        UserInternal participant = getUserByLogin(request.getParticipantLogin());
        ensureNotBlocked(participant);

        if (me.getLogin() != null && me.getLogin().equalsIgnoreCase(participant.getLogin())) {
            logger.error("Попытка создать диалог с самим собой от пользователя {}", myLogin);
            throw new SelfDialogException();
        }

        UUID user1Id = me.getId();
        UUID user2Id = participant.getId();

        Optional<Dialog> existing = dialogRepository.findByUser1IdAndUser2Id(user1Id, user2Id);
        if (existing.isPresent()) {
            logger.warn("Диалог между {} и {} уже существует", myLogin, request.getParticipantLogin());
            throw new DialogAlreadyExistsException();
        }

        Dialog dialog = new Dialog();
        dialog.setUser1Id(user1Id);
        dialog.setUser2Id(user2Id);
        dialog.setCreatedAt(LocalDateTime.now());
        Dialog saved = dialogRepository.save(dialog);
        logger.info("Диалог успешно создан, id: {}, между {} и {}", saved.getId(), me.getLogin(),
                participant.getLogin());
        return dialogMapper.toDialogResponse(saved, me.getLogin(), participant.getLogin());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DialogResponse> getMyDialogs(String myLogin) {
        logger.info("Запрос списка диалогов пользователя {}", myLogin);
        UserInternal me = getUserByLogin(myLogin);
        ensureNotBlocked(me);

        List<Dialog> dialogs = dialogRepository.findDialogsByUserId(me.getId());
        logger.debug("Найдено диалогов: {}", dialogs.size());

        Map<UUID, String> logins = resolveDialogLogins(dialogs, me.getId());

        List<DialogResponse> responses = dialogs.stream()
                .map(dialog -> {
                    UUID otherId = otherUserId(dialog, me.getId());
                    return dialogMapper.toDialogResponse(
                            dialog,
                            me.getLogin(),
                            logins.get(otherId));
                })
                .sorted(Comparator.comparing(DialogResponse::getCreatedAt).reversed())
                .collect(Collectors.toList());
        logger.info("Получено {} диалогов для пользователя {}", responses.size(), myLogin);
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getDialog(UUID dialogId, String myLogin) {
        logger.info("Запрос сообщений диалога {} от пользователя {}", dialogId, myLogin);
        UserInternal me = getUserByLogin(myLogin);
        ensureNotBlocked(me);

        Dialog dialog = findDialog(dialogId);
        checkAccess(dialog, me.getId());

        List<Message> messages = messageRepository.findByDialogId(dialogId);
        logger.debug("В диалоге {} найдено сообщений: {}", dialogId, messages.size());

        Map<UUID, String> logins = resolveSenderLogins(messages);

        List<MessageResponse> responses = messages.stream()
                .map(message -> {
                    String senderLogin = logins.get(message.getSenderId());
                    return messageMapper.toMessageResponse(message, senderLogin);
                })
                .collect(Collectors.toList());
        logger.info("Возвращено {} сообщений для диалога {}", responses.size(), dialogId);
        return responses;
    }

    @Override
    public MessageResponse sendMessage(UUID dialogId, String myLogin, MessageRequest request) {
        logger.info("Пользователь {} отправляет сообщение в диалог {}", myLogin, dialogId);
        UserInternal me = getUserByLogin(myLogin);
        ensureNotBlocked(me);

        Dialog dialog = findDialog(dialogId);
        checkAccess(dialog, me.getId());

        Message message = new Message();
        message.setDialogId(dialogId);
        message.setSenderId(me.getId());
        message.setText(request.getText());
        message.setSentAt(LocalDateTime.now());

        Message saved = messageRepository.save(message);
        logger.info("Сообщение успешно отправлено, id: {}, в диалог {}", saved.getId(), dialogId);
        return messageMapper.toMessageResponse(saved, me.getLogin());
    }

    @Override
    public void deleteDialog(UUID dialogId, String myLogin) {
        logger.info("Запрос на удаление диалога {} от пользователя {}", dialogId, myLogin);
        UserInternal me = getUserByLogin(myLogin);
        ensureNotBlocked(me);

        Dialog dialog = findDialog(dialogId);
        checkAccess(dialog, me.getId());

        dialogRepository.deleteById(dialogId);
        logger.info("Диалог {} успешно удалён", dialogId);
    }

    @Override
    public void deleteMessage(UUID messageId, String myLogin) {
        logger.info("Запрос на удаление сообщения {} от пользователя {}", messageId, myLogin);
        UserInternal me = getUserByLogin(myLogin);
        ensureNotBlocked(me);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> {
                    logger.error("Сообщение с id {} не найдено", messageId);
                    return new DialogNotFoundException("Сообщение не найдено: " + messageId);
                });

        if (!me.getId().equals(message.getSenderId())) {
            logger.error("Пользователь {} не является отправителем сообщения {}", myLogin, messageId);
            throw new DialogAccessDeniedException("Удалять сообщение может только его отправитель");
        }

        messageRepository.deleteById(messageId);
        logger.info("Сообщение {} успешно удалено", messageId);
    }

    private UserInternal getUserByLogin(String login) {
        logger.debug("Получение пользователя по логину: {}", login);
        return userServiceClient.getByLogin(login);
    }

    private void ensureNotBlocked(UserInternal user) {
        if (user.isBlocked()) {
            logger.error("Пользователь {} заблокирован, операция невозможна", user.getLogin());
            throw new UserBlockedException(user.getLogin());
        }
    }

    private UUID otherUserId(Dialog dialog, UUID myUserId) {
        if (myUserId.equals(dialog.getUser1Id())) {
            return dialog.getUser2Id();
        } else if (myUserId.equals(dialog.getUser2Id())) {
            return dialog.getUser1Id();
        } else {
            logger.error("Пользователь {} не является участником диалога {}", myUserId, dialog.getId());
            throw new DialogAccessDeniedException();
        }
    }

    private Map<UUID, String> resolveDialogLogins(List<Dialog> dialogs, UUID myId) {
        Set<UUID> otherIds = dialogs.stream()
                .map(d -> otherUserId(d, myId))
                .collect(Collectors.toSet());

        if (otherIds.isEmpty()) {
            return Map.of();
        }
        List<UserInternal> users = userServiceClient.getByIds(otherIds);
        logger.debug("Загружено логинов для {} участников диалогов", users.size());
        return users.stream()
                .collect(Collectors.toMap(UserInternal::getId, UserInternal::getLogin));
    }

    private Map<UUID, String> resolveSenderLogins(List<Message> messages) {
        Set<UUID> senderIds = messages.stream()
                .map(Message::getSenderId)
                .collect(Collectors.toSet());

        if (senderIds.isEmpty()) {
            return Map.of();
        }
        List<UserInternal> users = userServiceClient.getByIds(senderIds);
        logger.debug("Загружено логинов для {} отправителей сообщений", users.size());
        return users.stream()
                .collect(Collectors.toMap(UserInternal::getId, UserInternal::getLogin));
    }

    private Dialog findDialog(UUID dialogId) {
        return dialogRepository.findById(dialogId)
                .orElseThrow(() -> {
                    logger.error("Диалог с id {} не найден", dialogId);
                    return new DialogNotFoundException(dialogId);
                });
    }

    private void checkAccess(Dialog dialog, UUID myUserId) {
        boolean allowed = myUserId.equals(dialog.getUser1Id()) || myUserId.equals(dialog.getUser2Id());
        if (!allowed) {
            logger.error("Пользователь {} не имеет доступа к диалогу {}", myUserId, dialog.getId());
            throw new DialogAccessDeniedException();
        }
    }
}