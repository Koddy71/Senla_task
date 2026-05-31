package com.sen.controller;

import com.sen.dto.request.CreateDialogRequest;
import com.sen.dto.request.MessageRequest;
import com.sen.dto.response.DialogResponse;
import com.sen.dto.response.MessageResponse;
import com.sen.service.DialogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DialogControllerTest {

    @Mock
    private DialogService dialogService;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private DialogController controller;

    private static final String USER_LOGIN = "testUser";
    private final UUID dialogId = UUID.randomUUID();
    private final UUID messageId = UUID.randomUUID();

    @Test
    void createDialog_shouldReturnCreatedWithResponse() {
        when(userDetails.getUsername()).thenReturn(USER_LOGIN);

        CreateDialogRequest request = new CreateDialogRequest();
        request.setParticipantLogin("otherUser");

        DialogResponse responseDto = new DialogResponse();
        responseDto.setId(dialogId);
        when(dialogService.createDialog(USER_LOGIN, request)).thenReturn(responseDto);

        var result = controller.createDialog(userDetails, request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
        verify(dialogService).createDialog(USER_LOGIN, request);
    }

    @Test
    void getMyDialogs_shouldReturnOkWithList() {
        when(userDetails.getUsername()).thenReturn(USER_LOGIN);

        List<DialogResponse> dialogs = List.of(new DialogResponse(), new DialogResponse());
        when(dialogService.getMyDialogs(USER_LOGIN)).thenReturn(dialogs);

        var result = controller.getMyDialogs(userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(dialogs, result.getBody());
        verify(dialogService).getMyDialogs(USER_LOGIN);
    }

    @Test
    void getDialog_shouldReturnOkWithMessages() {
        when(userDetails.getUsername()).thenReturn(USER_LOGIN);

        List<MessageResponse> messages = List.of(new MessageResponse(), new MessageResponse());
        when(dialogService.getDialog(dialogId, USER_LOGIN)).thenReturn(messages);

        var result = controller.getDialog(dialogId, userDetails);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(messages, result.getBody());
        verify(dialogService).getDialog(dialogId, USER_LOGIN);
    }

    @Test
    void sendMessage_shouldReturnCreatedWithMessage() {
        when(userDetails.getUsername()).thenReturn(USER_LOGIN);

        MessageRequest request = new MessageRequest();
        request.setText("Hello");

        MessageResponse responseDto = new MessageResponse();
        responseDto.setId(messageId);
        when(dialogService.sendMessage(dialogId, USER_LOGIN, request)).thenReturn(responseDto);

        var result = controller.sendMessage(dialogId, userDetails, request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(responseDto, result.getBody());
        verify(dialogService).sendMessage(dialogId, USER_LOGIN, request);
    }

    @Test
    void deleteDialog_shouldReturnNoContent() {
        when(userDetails.getUsername()).thenReturn(USER_LOGIN);
        doNothing().when(dialogService).deleteDialog(dialogId, USER_LOGIN);

        var result = controller.deleteDialog(dialogId, userDetails);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(dialogService).deleteDialog(dialogId, USER_LOGIN);
    }

    @Test
    void deleteMessage_shouldReturnNoContent() {
        when(userDetails.getUsername()).thenReturn(USER_LOGIN);
        doNothing().when(dialogService).deleteMessage(messageId, USER_LOGIN);

        var result = controller.deleteMessage(messageId, userDetails);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(dialogService).deleteMessage(messageId, USER_LOGIN);
    }
}
