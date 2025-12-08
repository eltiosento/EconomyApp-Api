package com.eltiosento.economyapp.dto.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eltiosento.economyapp.dto.ExpenseDTO;
import com.eltiosento.economyapp.dto.NewExpenseDTO;
import com.eltiosento.economyapp.model.Expense;

@Component
public class ExpenseDTOConverter {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Converts a NewExpenseDTO to an Expense entity
     * 
     * @param newExpenseDTO
     * @return the Expense entity
     */
    public Expense convertToEntity(NewExpenseDTO newExpenseDTO) {

        return modelMapper.map(newExpenseDTO, Expense.class);
    }

    /**
     * Converts an Expense entity to an ExpenseDTO
     * 
     * @param expense the Expense entity to convert
     * @return the ExpenseDTO
     */
    public ExpenseDTO convertToDTO(Expense expense) {
        return modelMapper.map(expense, ExpenseDTO.class);
    }

}
