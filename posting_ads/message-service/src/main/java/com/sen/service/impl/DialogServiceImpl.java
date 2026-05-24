// package com.sen.service.impl;

// import java.time.LocalDateTime;
// import java.util.Comparator;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
// import java.util.UUID;
// import java.util.stream.Collectors;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.sen.client.UserServiceClient;
// import com.sen.dto.internal.UserInternal;
// import com.sen.dto.request.CreateDialogRequest;
// import com.sen.dto.request.SendMessageRequest;
// import com.sen.dto.response.DialogResponse;
// import com.sen.dto.response.MessageResponse;
// import com.sen.entity.Dialog;
// import com.sen.entity.Message;
// import com.sen.exception.DialogAccessDeniedException;
// import com.sen.exception.DialogNotFoundException;
// import com.sen.exception.EmptyMessageException;
// import com.sen.exception.SelfDialogException;
// import com.sen.exception.UserBlockedException;
// import com.sen.exception.UserNotFoundException;
// import com.sen.exception.UserServiceException;
// import com.sen.mapper.MessageMapper;
// import com.sen.repository.DialogRepository;
// import com.sen.repository.MessageRepository;
// import com.sen.service.DialogService;

// @Service
// @Transactional
// public class DialogServiceImpl implements DialogServiceService {

//     private final DialogRepository dialogRepository;
//     private final MessageRepository messageRepository;
//     private final UserServiceClient userServiceClient;
//     private final MessageMapper messageMapper;

//     public MessageServiceImpl(DialogRepository dialogRepository,
//             MessageRepository messageRepository,
//             UserServiceClient userServiceClient,
//             MessageMapper messageMapper) {
//         this.dialogRepository = dialogRepository;
//         this.messageRepository = messageRepository;
//         this.userServiceClient = userServiceClient;
//         this.messageMapper = messageMapper;
//     }

//     @Override
//     public DialogResponse createOrGetDialog(String myLogin, CreateDialogRequest request) {
//         UserInternal me = getUserByLogin(myLogin);
//         ensureNotBlocked(me);

//         String participantLogin = normalizeLogin(request.getParticipantLogin());
//         UserInternal participant = getUserByLogin(participantLogin);
//         ensureNotBlocked(participant);

//         if (me.getLogin() != null && me.getLogin().equalsIgnoreCase(participant.getLogin())) {
//             throw new SelfDialogException();
//         }

//         UUID user1Id = first(me.getId(), participant.getId());
//         UUID user2Id = second(me.getId(), participant.getId());

//         Dialog dialog = dialogRepository.findByUser1IdAndUser2Id(user1Id, user2Id)
//                 .orElseGet(() -> {
//                     Dialog created = new Dialog();
//                     created.setUser1Id(user1Id);
//                     created.setUser2Id(user2Id);
//                     created.setCreatedAt(LocalDateTime.now());
//                     return dialogRepository.save(created);
//                 });

//         return messageMapper.toDialogResponse(dialog, me.getLogin(), participant.getLogin());
//     }

//     @Override
//     @Transactional(readOnly = true)
//     public List<DialogResponse> getMyDialogs(String myLogin) {
//         UserInternal me = getUserByLogin(myLogin);
//         ensureNotBlocked(me);

//         List<Dialog> dialogs = dialogRepository.findDialogsByUserId(me.getId());
//         if (dialogs.isEmpty()) {
//             return List.of();
//         }

//         Set<UUID> ids = new HashSet<>();
//         for (Dialog dialog : dialogs) {
//             ids.add(otherUserId(dialog, me.getId()));
//         }

//         Map<UUID, String> logins = loadLogins(ids);

//         return dialogs.stream()
//                 .map(dialog -> {
//                     UUID otherId = otherUserId(dialog, me.getId());
//                     String otherLogin = logins.get(otherId);
//                     if (otherLogin == null) {
//                         throw new UserNotFoundException("Пользователь не найден: " + otherId);
//                     }

//                     return messageMapper.toDialogResponse(
//                             dialog,
//                             loginForUserId(dialog.getUser1Id(), me, logins),
//                             loginForUserId(dialog.getUser2Id(), me, logins));
//                 })
//                 .sorted(Comparator.comparing(DialogResponse::getCreatedAt,
//                         Comparator.nullsLast(Comparator.naturalOrder())).reversed())
//                 .collect(Collectors.toList());
//     }

//     @Override
//     @Transactional(readOnly = true)
//     public List<MessageResponse> getDialog(UUID dialogId, String myLogin) {
//         UserInternal me = getUserByLogin(myLogin);
//         ensureNotBlocked(me);

//         Dialog dialog = findDialogOrThrow(dialogId);
//         checkAccess(dialog, me.getId());

//         List<Message> messages = messageRepository.findByDialogId(dialogId);
//         if (messages.isEmpty()) {
//             return List.of();
//         }

//         messageRepository.findByDialogId(dialogId).stream()
//                 .filter(m -> !me.getId().equals(m.getSenderId()))
//                 .forEach(m -> {
//                     // no-op, just keeps method behavior explicit for read marking below
//                 });

//         // Отмечаем как прочитанные только входящие сообщения
//         messages.stream()
//                 .filter(m -> !me.getId().equals(m.getSenderId()) && !m.isRead())
//                 .forEach(m -> {
//                     m.setRead(true);
//                     messageRepository.save(m);
//                 });

//         Set<UUID> senderIds = messages.stream()
//                 .map(Message::getSenderId)
//                 .collect(Collectors.toSet());

//         Map<UUID, String> logins = loadLogins(senderIds);

//         return messages.stream()
//                 .map(message -> {
//                     String senderLogin = logins.get(message.getSenderId());
//                     if (senderLogin == null) {
//                         throw new UserNotFoundException("Пользователь не найден: " + message.getSenderId());
//                     }
//                     return messageMapper.toMessageResponse(message, senderLogin);
//                 })
//                 .collect(Collectors.toList());
//     }

//     @Override
//     public MessageResponse sendMessage(UUID dialogId, String myLogin, SendMessageRequest request) {
//         UserInternal me = getUserByLogin(myLogin);
//         ensureNotBlocked(me);

//         Dialog dialog = findDialogOrThrow(dialogId);
//         checkAccess(dialog, me.getId());

//         String text = request.getText() == null ? "" : request.getText().trim();
//         if (text.isBlank()) {
//             throw new EmptyMessageException();
//         }

//         Message message = new Message();
//         message.setDialogId(dialogId);
//         message.setSenderId(me.getId());
//         message.setText(text);
//         message.setSentAt(LocalDateTime.now());
//         message.setRead(false);

//         Message saved = messageRepository.save(message);
//         return messageMapper.toMessageResponse(saved, me.getLogin());
//     }

//     @Override
//     public void deleteDialog(UUID dialogId, String myLogin) {
//         UserInternal me = getUserByLogin(myLogin);
//         ensureNotBlocked(me);

//         Dialog dialog = findDialogOrThrow(dialogId);
//         checkAccess(dialog, me.getId());

//         dialogRepository.deleteById(dialogId);
//     }

//     @Override
//     public void deleteMessage(UUID messageId, String myLogin) {
//         UserInternal me = getUserByLogin(myLogin);
//         ensureNotBlocked(me);

//         Message message = messageRepository.findById(messageId)
//                 .orElseThrow(() -> new DialogNotFoundException("Сообщение не найдено: " + messageId));

//         if (!me.getId().equals(message.getSenderId())) {
//             throw new DialogAccessDeniedException("Удалять сообщение может только его отправитель");
//         }

//         boolean deleted = messageRepository.deleteById(messageId);
//         if (!deleted) {
//             throw new DialogNotFoundException("Сообщение не найдено: " + messageId);
//         }
//     }

//     private UserInternal getUserByLogin(String login) {
//         try {
//             UserInternal user = userServiceClient.getByLogin(login);
//             if (user == null || user.getId() == null) {
//                 throw new UserNotFoundException("Пользователь не найден: " + login);
//             }
//             return user;
//         } catch (UserServiceException ex) {
//             throw ex;
//         } catch (RuntimeException ex) {
//             throw new UserServiceException("Ошибка получения пользователя по логину: " + login, ex);
//         }
//     }

//     private Map<UUID, String> loadLogins(Set<UUID> ids) {
//         if (ids == null || ids.isEmpty()) {
//             return Map.of();
//         }

//         try {
//             List<UserInternal> users = userServiceClient.getByIds(ids);
//             if (users == null) {
//                 throw new UserServiceException("User service returned empty response");
//             }

//             Map<UUID, String> result = new HashMap<>();
//             for (UserInternal user : users) {
//                 if (user != null && user.getId() != null && user.getLogin() != null) {
//                     result.put(user.getId(), user.getLogin());
//                 }
//             }

//             for (UUID id : ids) {
//                 if (!result.containsKey(id)) {
//                     throw new UserNotFoundException("Пользователь не найден: " + id);
//                 }
//             }

//             return result;
//         } catch (UserServiceException ex) {
//             throw ex;
//         } catch (RuntimeException ex) {
//             throw new UserServiceException("Ошибка пакетного получения пользователей", ex);
//         }
//     }

//     private void ensureNotBlocked(UserInternal user) {
//         if (Boolean.TRUE.equals(user.getBlocked())) {
//             throw new UserBlockedException("Пользователь заблокирован: " + user.getLogin());
//         }
//     }

//     private Dialog findDialogOrThrow(UUID dialogId) {
//         return dialogRepository.findById(dialogId)
//                 .orElseThrow(() -> new DialogNotFoundException(dialogId));
//     }

//     private void checkAccess(Dialog dialog, UUID myUserId) {
//         boolean allowed = myUserId.equals(dialog.getUser1Id()) || myUserId.equals(dialog.getUser2Id());
//         if (!allowed) {
//             throw new DialogAccessDeniedException();
//         }
//     }

//     private UUID otherUserId(Dialog dialog, UUID myUserId) {
//         if (myUserId.equals(dialog.getUser1Id())) {
//             return dialog.getUser2Id();
//         }
//         if (myUserId.equals(dialog.getUser2Id())) {
//             return dialog.getUser1Id();
//         }
//         throw new DialogAccessDeniedException();
//     }

//     private UUID first(UUID a, UUID b) {
//         return a.compareTo(b) <= 0 ? a : b;
//     }

//     private UUID second(UUID a, UUID b) {
//         return a.compareTo(b) <= 0 ? b : a;
//     }

//     private String normalizeLogin(String login) {
//         if (login == null) {
//             return null;
//         }
//         String value = login.trim();
//         if (value.isBlank()) {
//             throw new UserNotFoundException("Логин участника не может быть пустым");
//         }
//         return value;
//     }

//     private String loginForUserId(UUID userId, UserInternal me, Map<UUID, String> logins) {
//         if (me.getId().equals(userId)) {
//             return me.getLogin();
//         }
//         String login = logins.get(userId);
//         if (login == null) {
//             throw new UserNotFoundException("Пользователь не найден: " + userId);
//         }
//         return login;
//     }
// }