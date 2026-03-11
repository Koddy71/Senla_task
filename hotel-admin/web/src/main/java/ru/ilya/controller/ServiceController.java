package ru.ilya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;
import ru.ilya.dto.ServiceDTO;
import ru.ilya.dto.ServiceRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private ServiceManager serviceManager;

    @Autowired
    public ServiceController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    private ServiceDTO convertToDto(Service service) {
        if (service == null)
            return null;
        ServiceDTO dto = new ServiceDTO();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setPrice(service.getPrice());
        return dto;
    }

    @GetMapping
    public List<ServiceDTO> getAllServices() {
        List<Service> services = serviceManager.getAllServices();
        return services.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getServiceById(@PathVariable int id) {
        Service service = serviceManager.findService(id);
        if (service == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(convertToDto(service));
    }

    @PostMapping
    public ResponseEntity<Void> addService(@RequestBody ServiceRequest request) {
        Service service = new Service(request.getName(), request.getPrice());
        if (serviceManager.addService(service)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeService(@PathVariable int id) {
        if (serviceManager.removeService(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<Void> changePrice(@PathVariable int id, @RequestParam int price) {
        if (serviceManager.changePrice(id, price)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}