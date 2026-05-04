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
    void getInternalUser_shouldReturnDto() {
        UserInternal dto = new UserInternal();
        dto.setId(UUID.randomUUID());
        dto.setLogin("seller");
        dto.setRole(Role.USER);
        dto.setBlocked(false);
        when(userService.getInternalUserByLogin("seller")).thenReturn(dto);

        ResponseEntity<UserInternal> result = controller.getInternalUserByLogin("seller");

        assertEquals("seller", result.getBody().getLogin());
        assertFalse(result.getBody().getBlocked());
    }
}