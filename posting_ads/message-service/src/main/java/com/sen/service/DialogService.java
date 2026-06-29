package com.sen.service;

import java.util.List;
import java.util.UUID;

import com.sen.dto.request.CreateDialogRequest;
import com.sen.dto.request.MessageRequest;
import com.sen.dto.response.DialogResponse;
import com.sen.dto.response.MessageResponse;

public interface DialogService {
    //создание диалога, возврат инфоррмации о диалоге. request содержит логин второго участника
    DialogResponse createDialog(String myLogin, CreateDialogRequest request);

    //получение инфомрации о всех моих диалогах
    List<DialogResponse> getMyDialogs(String myLogin);

    //получение сообщений моего диалога
    List<MessageResponse> getDialog(UUID dialogId, String myLogin);

    //отправка сообщения. request содержит текст сообщения
    MessageResponse sendMessage(UUID dialogId, String myLogin, MessageRequest request);

    void deleteDialog(UUID dialogId, String myLogin);

    void deleteMessage(UUID messageId, String myLogin);
}
