package ru.ilya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.ilya.model.Priceable;
import ru.ilya.service.PriceService;

import java.util.List;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    private PriceService priceService;

    @Autowired
    public PriceController(PriceService priceService){
        this.priceService=priceService;
    }

    @GetMapping
    public List<Priceable> getRoomsAndServices(@RequestParam(required = false) String orderBy) {
        return priceService.getRoomsAndServices(orderBy);
    }
}