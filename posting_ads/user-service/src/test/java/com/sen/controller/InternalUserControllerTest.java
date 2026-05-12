package com.sen.controller;

import com.sen.dto.internal.UserInternal;
import com.sen.enums.Role;
import com.sen.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternalUserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private InternalUserController controller;

    @Test
    void getInternalUserByLogin_shouldReturnDto() {
        UserInternal dto = new UserInternal();
        dto.setId(UUID.randomUUID());
        dto.setLogin("seller");
        dto.setRole("USER");
        dto.setBlocked(false);
        when(userService.getInternalUserByLogin("seller")).thenReturn(dto);

        ResponseEntity<UserInternal> result = controller.getInternalUserByLogin("seller");

        assertEquals("seller", result.getBody().getLogin());
        assertFalse(result.getBody().getBlocked());
    }

    @Test
    void getInternalUserById_shouldReturnDto() {
        UUID userId = UUID.randomUUID();
        UserInternal dto = new UserInternal();
        dto.setId(userId);
        dto.setLogin("seller");
        dto.setRole("USER");
        dto.setBlocked(false);
        when(userService.getInternalUserById(userId)).thenReturn(dto);

        ResponseEntity<UserInternal> result = controller.getInternalUserById(userId);

        assertEquals(userId, result.getBody().getId());
        assertEquals("seller", result.getBody().getLogin());
    }

    @Test
    void getInternalUsersByIds_shouldReturnList() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<UUID> ids = List.of(id1, id2);

        UserInternal dto1 = new UserInternal();
        dto1.setId(id1);
        dto1.setLogin("user1");
        UserInternal dto2 = new UserInternal();
        dto2.setId(id2);
        dto2.setLogin("user2");

        when(userService.getInternalUsersByIds(ids)).thenReturn(List.of(dto1, dto2));

        ResponseEntity<List<UserInternal>> result = controller.getInternalUsersByIds(ids);

        assertEquals(2, result.getBody().size());
        assertEquals("user1", result.getBody().get(0).getLogin());
        assertEquals("user2", result.getBody().get(1).getLogin());
    }
}