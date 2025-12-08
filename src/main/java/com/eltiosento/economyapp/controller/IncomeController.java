package com.eltiosento.economyapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eltiosento.economyapp.dto.IncomeDTO;
import com.eltiosento.economyapp.dto.NewIncomeDTO;
import com.eltiosento.economyapp.service.IncomeService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "Income", description = "Controlador per a gestionar i consultar els ingressos.")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @GetMapping("/income/{id}")
    public ResponseEntity<?> getIncomeById(@PathVariable Long id) {

        return ResponseEntity.ok(incomeService.getIncomeById(id));
    }

    @GetMapping("/incomes")
    public ResponseEntity<?> getAllIncomes() {

        return ResponseEntity.ok(incomeService.getAllIncomes());
    }

    @GetMapping("/incomes/user/{userId}")
    public ResponseEntity<?> getAllIncomesByUserId(@PathVariable Long userId) {

        return ResponseEntity.ok(incomeService.getIncomesByUserId(userId));
    }

    @PostMapping("/income")
    public ResponseEntity<?> createIncome(@Valid @RequestBody NewIncomeDTO newIncomeDTO) {

        IncomeDTO incomeDTO = incomeService.createIncome(newIncomeDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(incomeDTO);

    }

    @PutMapping("/income/{id}")
    public ResponseEntity<?> updateIncome(@PathVariable Long id, @RequestBody NewIncomeDTO newIncomeDTO) {

        IncomeDTO incomeDTO = incomeService.updateIncome(id, newIncomeDTO);

        return ResponseEntity.ok(incomeDTO);
    }

    @DeleteMapping("/income/{id}")
    public ResponseEntity<?> deleteIncome(@PathVariable Long id) {

        incomeService.deleteIncome(id);

        return ResponseEntity.noContent().build();
    }

}
