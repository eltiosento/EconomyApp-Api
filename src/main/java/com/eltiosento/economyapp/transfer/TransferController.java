package com.eltiosento.economyapp.transfer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eltiosento.economyapp.dto.ExpenseDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @PostMapping("/transfer/savings_to_expenses")
    public ResponseEntity<List<ExpenseDTO>> transferSavingsToExpense(@Valid @RequestBody TransferRequestDTO req) {
        List<ExpenseDTO> result = transferService.transferSavingsToExpenses(req);
        return ResponseEntity.ok(result);
    }
    /*
     * @PostMapping("/transfer/savings_to_savings")
     * public ResponseEntity<List<ExpenseDTO>>
     * transferSavingsToSavings(@Valid @RequestBody TransferRequestDTO req) {
     * List<ExpenseDTO> result = transferService.transferSavingsToSaving(req);
     * return ResponseEntity.ok(result);
     * }
     */
}
