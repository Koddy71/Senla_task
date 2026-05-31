package com.sen.service;

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
import com.sen.service.impl.DialogServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DialogServiceImplTest {

    @Mock
    private DialogRepository dialogRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private DialogMapper dialogMapper;

    @InjectMocks
    private DialogServiceImpl dialogService;

    private static final String MY_LOGIN = "currentUser";
    private static final String OTHER_LOGIN = "otherUser";
    private UUID myId;
    private UUID otherId;
    private UUID dialogId;
    private UUID messageId;
    private UserInternal me;
    private UserInternal other;

    @BeforeEach
    void setUp() {
        myId = UUID.randomUUID();
        otherId = UUID.randomUUID();
        dialogId = UUID.randomUUID();
        messageId = UUID.randomUUID();

        me = new UserInternal();
        me.setId(myId);
        me.setLogin(MY_LOGIN);
        me.setBlocked(false);

        other = new UserInternal();
        other.setId(otherId);
        other.setLogin(OTHER_LOGIN);
        other.setBlocked(false);
    }

    //  CREATE DIALOG 

    @Test
    void createDialog_shouldCreateNewDialog() {
        CreateDialogRequest request = new CreateDialogRequest();
        request.setParticipantLogin(OTHER_LOGIN);

        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(userServiceClient.getByLogin(OTHER_LOGIN)).thenReturn(other);
        when(dialogRepository.findByUser1IdAndUser2Id(myId, otherId)).thenReturn(Optional.empty());

        Dialog savedDialog = new Dialog();
        savedDialog.setId(dialogId);
        savedDialog.setUser1Id(myId);
        savedDialog.setUser2Id(otherId);
        savedDialog.setCreatedAt(LocalDateTime.now());
        when(dialogRepository.save(any(Dialog.class))).thenReturn(savedDialog);

        DialogResponse expectedResponse = new DialogResponse();
        expectedResponse.setId(dialogId);
        expectedResponse.setUserLogin1(MY_LOGIN);
        expectedResponse.setUserLogin2(OTHER_LOGIN);
        expectedResponse.setCreatedAt(savedDialog.getCreatedAt());
        when(dialogMapper.toDialogResponse(any(Dialog.class), eq(MY_LOGIN), eq(OTHER_LOGIN)))
                .thenReturn(expectedResponse);

        DialogResponse response = dialogService.createDialog(MY_LOGIN, request);

        assertNotNull(response);
        assertEquals(dialogId, response.getId());
        assertEquals(MY_LOGIN, response.getUserLogin1());
        assertEquals(OTHER_LOGIN, response.getUserLogin2());

        ArgumentCaptor<Dialog> captor = ArgumentCaptor.forClass(Dialog.class);
        verify(dialogRepository).save(captor.capture());
        assertEquals(myId, captor.getValue().getUser1Id());
        assertEquals(otherId, captor.getValue().getUser2Id());
    }

    @Test
    void createDialog_shouldThrowWhenSelfDialog() {
        CreateDialogRequest request = new CreateDialogRequest();
        request.setParticipantLogin(MY_LOGIN);

        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me); // same user

        assertThrows(SelfDialogException.class,
                () -> dialogService.createDialog(MY_LOGIN, request));
        verify(dialogRepository, never()).save(any());
    }

    @Test
    void createDialog_shouldThrowWhenDialogAlreadyExists() {
        CreateDialogRequest request = new CreateDialogRequest();
        request.setParticipantLogin(OTHER_LOGIN);

        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(userServiceClient.getByLogin(OTHER_LOGIN)).thenReturn(other);
        when(dialogRepository.findByUser1IdAndUser2Id(myId, otherId))
                .thenReturn(Optional.of(new Dialog()));

        assertThrows(DialogAlreadyExistsException.class,
                () -> dialogService.createDialog(MY_LOGIN, request));
        verify(dialogRepository, never()).save(any());
    }

    @Test
    void createDialog_shouldThrowWhenUserBlocked() {
        me.setBlocked(true);
        CreateDialogRequest request = new CreateDialogRequest();
        request.setParticipantLogin(OTHER_LOGIN);

        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me, me);

        assertThrows(UserBlockedException.class,
                () -> dialogService.createDialog(MY_LOGIN, request));
        verify(userServiceClient, never()).getByLogin(OTHER_LOGIN);
        verify(dialogRepository, never()).save(any());
    }

    //  GET MY DIALOGS 

    @Test
    void getMyDialogs_shouldReturnSortedList() {
        Dialog dialog1 = new Dialog();
        dialog1.setId(UUID.randomUUID());
        dialog1.setUser1Id(myId);
        dialog1.setUser2Id(otherId);
        dialog1.setCreatedAt(LocalDateTime.now().minusDays(1));

        Dialog dialog2 = new Dialog();
        dialog2.setId(UUID.randomUUID());
        dialog2.setUser1Id(myId);
        dialog2.setUser2Id(UUID.randomUUID());
        dialog2.setCreatedAt(LocalDateTime.now());

        List<Dialog> dialogs = List.of(dialog1, dialog2);
        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(dialogRepository.findDialogsByUserId(myId)).thenReturn(dialogs);

        UserInternal otherParticipant = new UserInternal();
        otherParticipant.setId(otherId);
        otherParticipant.setLogin(OTHER_LOGIN);
        UUID thirdId = dialog2.getUser2Id();
        UserInternal thirdUser = new UserInternal();
        thirdUser.setId(thirdId);
        thirdUser.setLogin("third");

        when(userServiceClient.getByIds(Set.of(otherId, thirdId)))
                .thenReturn(List.of(otherParticipant, thirdUser));

        DialogResponse resp1 = new DialogResponse();
        resp1.setId(dialog1.getId());
        resp1.setUserLogin1(MY_LOGIN);
        resp1.setUserLogin2(OTHER_LOGIN);
        resp1.setCreatedAt(dialog1.getCreatedAt());
        DialogResponse resp2 = new DialogResponse();
        resp2.setId(dialog2.getId());
        resp2.setUserLogin1(MY_LOGIN);
        resp2.setUserLogin2("third");
        resp2.setCreatedAt(dialog2.getCreatedAt());

        when(dialogMapper.toDialogResponse(eq(dialog1), eq(MY_LOGIN), eq(OTHER_LOGIN)))
                .thenReturn(resp1);
        when(dialogMapper.toDialogResponse(eq(dialog2), eq(MY_LOGIN), eq("third")))
                .thenReturn(resp2);

        List<DialogResponse> result = dialogService.getMyDialogs(MY_LOGIN);

        assertEquals(2, result.size());
       
        assertEquals(dialog2.getId(), result.get(0).getId());
        assertEquals(dialog1.getId(), result.get(1).getId());
        verify(dialogRepository).findDialogsByUserId(myId);
    }

    @Test
    void getMyDialogs_shouldThrowWhenUserBlocked() {
        me.setBlocked(true);
        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        assertThrows(UserBlockedException.class, () -> dialogService.getMyDialogs(MY_LOGIN));
        verify(dialogRepository, never()).findDialogsByUserId(any());
    }

    //  GET DIALOG MESSAGES 

    @Test
    void getDialog_shouldReturnMessages() {
        Dialog dialog = new Dialog();
        dialog.setId(dialogId);
        dialog.setUser1Id(myId);
        dialog.setUser2Id(otherId);

        Message msg1 = new Message();
        msg1.setId(UUID.randomUUID());
        msg1.setDialogId(dialogId);
        msg1.setSenderId(myId);
        msg1.setText("Hello");
        msg1.setSentAt(LocalDateTime.now());

        Message msg2 = new Message();
        msg2.setId(UUID.randomUUID());
        msg2.setDialogId(dialogId);
        msg2.setSenderId(otherId);
        msg2.setText("Hi");
        msg2.setSentAt(LocalDateTime.now());

        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(dialogRepository.findById(dialogId)).thenReturn(Optional.of(dialog));
        when(messageRepository.findByDialogId(dialogId)).thenReturn(List.of(msg1, msg2));

        // Мок resolveSenderLogins
        when(userServiceClient.getByIds(Set.of(myId, otherId)))
                .thenReturn(List.of(me, other));

        MessageResponse resp1 = new MessageResponse();
        resp1.setSenderLogin(MY_LOGIN);
        MessageResponse resp2 = new MessageResponse();
        resp2.setSenderLogin(OTHER_LOGIN);
        when(messageMapper.toMessageResponse(msg1, MY_LOGIN)).thenReturn(resp1);
        when(messageMapper.toMessageResponse(msg2, OTHER_LOGIN)).thenReturn(resp2);

        List<MessageResponse> result = dialogService.getDialog(dialogId, MY_LOGIN);

        assertEquals(2, result.size());
        verify(messageRepository).findByDialogId(dialogId);
    }

    @Test
    void getDialog_shouldThrowWhenDialogNotFound() {
        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(dialogRepository.findById(dialogId)).thenReturn(Optional.empty());
        assertThrows(DialogNotFoundException.class,
                () -> dialogService.getDialog(dialogId, MY_LOGIN));
    }

    @Test
    void getDialog_shouldThrowWhenAccessDenied() {
        Dialog dialog = new Dialog();
        dialog.setId(dialogId);
        dialog.setUser1Id(UUID.randomUUID()); // чужой
        dialog.setUser2Id(UUID.randomUUID());

        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(dialogRepository.findById(dialogId)).thenReturn(Optional.of(dialog));
        assertThrows(DialogAccessDeniedException.class,
                () -> dialogService.getDialog(dialogId, MY_LOGIN));
    }

    //  SEND MESSAGE 

    @Test
    void sendMessage_shouldSaveAndReturn() {
        Dialog dialog = new Dialog();
        dialog.setId(dialogId);
        dialog.setUser1Id(myId);
        dialog.setUser2Id(otherId);

        MessageRequest request = new MessageRequest();
        request.setText("Test message");

        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(dialogRepository.findById(dialogId)).thenReturn(Optional.of(dialog));

        Message savedMessage = new Message();
        savedMessage.setId(messageId);
        savedMessage.setDialogId(dialogId);
        savedMessage.setSenderId(myId);
        savedMessage.setText("Test message");
        savedMessage.setSentAt(LocalDateTime.now());
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        MessageResponse expected = new MessageResponse();
        expected.setId(messageId);
        expected.setSenderLogin(MY_LOGIN);
        expected.setText("Test message");
        when(messageMapper.toMessageResponse(savedMessage, MY_LOGIN)).thenReturn(expected);

        MessageResponse response = dialogService.sendMessage(dialogId, MY_LOGIN, request);

        assertNotNull(response);
        assertEquals(messageId, response.getId());
        assertEquals(MY_LOGIN, response.getSenderLogin());
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void sendMessage_shouldThrowWhenUserNotInDialog() {
        Dialog dialog = new Dialog();
        dialog.setId(dialogId);
        dialog.setUser1Id(UUID.randomUUID());
        dialog.setUser2Id(UUID.randomUUID());

        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(dialogRepository.findById(dialogId)).thenReturn(Optional.of(dialog));

        MessageRequest request = new MessageRequest();
        assertThrows(DialogAccessDeniedException.class,
                () -> dialogService.sendMessage(dialogId, MY_LOGIN, request));
        verify(messageRepository, never()).save(any());
    }

    //  DELETE DIALOG 

    @Test
    void deleteDialog_shouldDelete() {
        Dialog dialog = new Dialog();
        dialog.setId(dialogId);
        dialog.setUser1Id(myId);
        dialog.setUser2Id(otherId);

        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(dialogRepository.findById(dialogId)).thenReturn(Optional.of(dialog));

        dialogService.deleteDialog(dialogId, MY_LOGIN);

        verify(dialogRepository).deleteById(dialogId);
    }

    @Test
    void deleteDialog_shouldThrowWhenAccessDenied() {
        Dialog dialog = new Dialog();
        dialog.setId(dialogId);
        dialog.setUser1Id(UUID.randomUUID());
        dialog.setUser2Id(UUID.randomUUID());

        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(dialogRepository.findById(dialogId)).thenReturn(Optional.of(dialog));

        assertThrows(DialogAccessDeniedException.class,
                () -> dialogService.deleteDialog(dialogId, MY_LOGIN));
        verify(dialogRepository, never()).deleteById(any());
    }

    // DELETE MESSAGE 

    @Test
    void deleteMessage_shouldDeleteWhenUserIsSender() {
        Message message = new Message();
        message.setId(messageId);
        message.setSenderId(myId);

        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        dialogService.deleteMessage(messageId, MY_LOGIN);

        verify(messageRepository).deleteById(messageId);
    }

    @Test
    void deleteMessage_shouldThrowWhenNotSender() {
        Message message = new Message();
        message.setId(messageId);
        message.setSenderId(UUID.randomUUID()); // чужой

        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        assertThrows(DialogAccessDeniedException.class,
                () -> dialogService.deleteMessage(messageId, MY_LOGIN));
        verify(messageRepository, never()).deleteById(any());
    }

    @Test
    void deleteMessage_shouldThrowWhenMessageNotFound() {
        when(userServiceClient.getByLogin(MY_LOGIN)).thenReturn(me);
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(DialogNotFoundException.class,
                () -> dialogService.deleteMessage(messageId, MY_LOGIN));
    }
}