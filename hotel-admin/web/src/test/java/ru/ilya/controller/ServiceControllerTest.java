package ru.ilya.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ru.ilya.exceptions.NotFoundException;
import ru.ilya.exceptions.ServiceException;
import ru.ilya.exceptions.ValidationException;
import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

    @Mock
    private ServiceManager serviceManager;

    @InjectMocks
    private ServiceController serviceController;

    private static Service service(Integer id, String name, int price) {
        Service service = new Service(name, price);
        service.setId(id);
        return service;
    }

    @Test
    void getAll_returnsServices() {
        Service spa = service(1, "Spa", 700);
        Service breakfast = service(2, "Breakfast", 200);
        when(serviceManager.getAllServices()).thenReturn(List.of(spa, breakfast));

        ResponseEntity<List<Service>> response = serviceController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(spa, breakfast), response.getBody());
    }

    @Test
    void getAll_emptyList_returnsEmptyBody() {
        when(serviceManager.getAllServices()).thenReturn(List.of());

        ResponseEntity<List<Service>> response = serviceController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
    }

    @Test
    void create_successReturnsOriginalService() {
        Service service = service(1, "Spa", 700);
        when(serviceManager.addService(service)).thenReturn(true);

        ResponseEntity<Service> response = serviceController.create(service);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(service, response.getBody());
        verify(serviceManager).addService(service);
    }

    @Test
    void create_serviceReturnedFalse_throwsServiceException() {
        Service service = service(1, "Spa", 700);
        when(serviceManager.addService(service)).thenReturn(false);

        assertThrows(ServiceException.class, () -> serviceController.create(service));
    }

    @Test
    void removeService_successReturnsNoContent() {
        when(serviceManager.removeService(1)).thenReturn(true);

        ResponseEntity<Void> response = serviceController.removeService(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void removeService_serviceReturnedFalse_throwsNotFound() {
        when(serviceManager.removeService(1)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> serviceController.removeService(1));
    }

    @Test
    void changePrice_successReturnsOk() {
        when(serviceManager.changePrice(1, 800)).thenReturn(true);

        ResponseEntity<Void> response = serviceController.changePrice(1, 800);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changePrice_invalidPrice_throwsValidationException() {
        assertThrows(ValidationException.class, () -> serviceController.changePrice(1, 0));
    }

    @Test
    void changePrice_serviceReturnedFalse_throwsNotFound() {
        when(serviceManager.changePrice(1, 800)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> serviceController.changePrice(1, 800));
    }

    @Test
    void get_foundReturnsService() {
        Service service = service(1, "Spa", 700);
        when(serviceManager.findService(1)).thenReturn(service);

        ResponseEntity<Service> response = serviceController.get(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(service, response.getBody());
    }

    @Test
    void get_notFound_throwsNotFound() {
        when(serviceManager.findService(1)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> serviceController.get(1));
    }
}
