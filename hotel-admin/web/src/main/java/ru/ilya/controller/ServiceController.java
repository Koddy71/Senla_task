package ru.ilya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;
import ru.ilya.exceptions.NotFoundException;
import ru.ilya.exceptions.ServiceException;
import ru.ilya.exceptions.ValidationException;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceManager serviceManager;

    @Autowired
    public ServiceController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @GetMapping
    public ResponseEntity<List<Service>> getAll() {
        return ResponseEntity.ok(serviceManager.getAllServices());
    }

    @PostMapping
    public ResponseEntity<Service> create(@RequestBody Service service) {
        boolean ok;
        try {
            ok = serviceManager.addService(service); 
        } catch (NoSuchMethodError | AbstractMethodError e) {
            throw new ServiceException("Нарушение контрактов сервисов: method signature mismatch");
        }

        if (!ok) {
            throw new ServiceException("Не удалось создать услугу (serviceManager.addService вернул false)");
        }

        return ResponseEntity.ok(service);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeService(@PathVariable int id) {
        boolean deleted;
        try {
            deleted = serviceManager.removeService(id);
        } catch (NoSuchMethodError | AbstractMethodError e) {
            throw new ServiceException("Нарушение контрактов сервисов: method signature mismatch");
        }

        if (!deleted) {
            throw new NotFoundException("Услуга с id=" + id + " не найдена");
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<Void> changePrice(@PathVariable int id, @RequestParam int price) {
        if (price <= 0) {
            throw new ValidationException("Price must be positive");
        }

        boolean changed;
        try {
            changed = serviceManager.changePrice(id, price);
        } catch (NoSuchMethodError | AbstractMethodError e) {
            throw new ServiceException("Нарушение контрактов сервисов: method signature mismatch");
        }

        if (!changed) {
            throw new NotFoundException("Услуга с id=" + id + " не найдена для изменения цены");
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Service> get(@PathVariable int id) {
        Service s = serviceManager.findService(id);
        if (s == null) {
            throw new NotFoundException("Услуга с id=" + id + " не найдена");
        }
        return ResponseEntity.ok(s);
    }
}