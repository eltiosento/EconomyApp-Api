package com.eltiosento.economyapp.transfer;

import java.util.List;

import com.eltiosento.economyapp.dto.ExpenseDTO;

public interface TransferService {

    List<ExpenseDTO> transferSavingsToExpenses(TransferRequestDTO transferRequest);

    // List<ExpenseDTO> transferSavingsToSaving(TransferRequestDTO transferRequest);

}
