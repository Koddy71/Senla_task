package ru.ilya.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.ilya.dao.jpa.ServiceDaoJpa;
import ru.ilya.model.Service;

@ExtendWith(MockitoExtension.class)
class ServiceManagerImplTest {

    @Mock
    private ServiceDaoJpa serviceDao;

    @InjectMocks
    private ServiceManagerImpl serviceManager;

    private static Service service(Integer id, String name, int price) {
        Service service = new Service(name, price);
        service.setId(id);
        return service;
    }

    @Test
    void addService_success() {
        Service service = service(1, "Spa", 500);
        when(serviceDao.create(service)).thenReturn(service);

        boolean result = serviceManager.addService(service);

        assertTrue(result);
        verify(serviceDao).create(service);
    }

    @Test
    void addService_null_returnsFalse() {
        boolean result = serviceManager.addService(null);

        assertFalse(result);
        verifyNoInteractions(serviceDao);
    }

    @Test
    void removeService_success() {
        when(serviceDao.delete(1)).thenReturn(true);

        boolean result = serviceManager.removeService(1);

        assertTrue(result);
        verify(serviceDao).delete(1);
    }

    @Test
    void removeService_notFound_returnsFalse() {
        when(serviceDao.delete(1)).thenReturn(false);

        boolean result = serviceManager.removeService(1);

        assertFalse(result);
    }

    @Test
    void findService_found() {
        Service service = service(1, "Spa", 500);
        when(serviceDao.findById(1)).thenReturn(service);

        Service result = serviceManager.findService(1);

        assertEquals(service, result);
    }

    @Test
    void findService_notFound_returnsNull() {
        when(serviceDao.findById(1)).thenReturn(null);

        Service result = serviceManager.findService(1);

        assertNull(result);
    }

    @Test
    void changePrice_success() {
        Service service = service(1, "Spa", 500);
        when(serviceDao.findById(1)).thenReturn(service);
        when(serviceDao.update(service)).thenReturn(service);

        boolean result = serviceManager.changePrice(1, 700);

        assertTrue(result);
        assertEquals(700, service.getPrice());
        verify(serviceDao).update(service);
    }

    @Test
    void changePrice_invalidPrice_returnsFalse() {
        Service service = service(1, "Spa", 500);
        when(serviceDao.findById(1)).thenReturn(service);

        boolean result = serviceManager.changePrice(1, 0);

        assertFalse(result);
        verify(serviceDao, never()).update(any(Service.class));
    }

    @Test
    void getAllServices_returnsDaoList() {
        Service spa = service(1, "Spa", 500);
        Service breakfast = service(2, "Breakfast", 300);
        when(serviceDao.findAll()).thenReturn(List.of(spa, breakfast));

        List<Service> result = serviceManager.getAllServices();

        assertEquals(List.of(spa, breakfast), result);
    }

    @Test
    void getAllServices_emptyList_returnsEmpty() {
        when(serviceDao.findAll()).thenReturn(List.of());

        List<Service> result = serviceManager.getAllServices();

        assertEquals(List.of(), result);
    }
}
