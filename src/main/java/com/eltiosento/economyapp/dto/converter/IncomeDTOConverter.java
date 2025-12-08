package com.eltiosento.economyapp.dto.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eltiosento.economyapp.dto.IncomeDTO;
import com.eltiosento.economyapp.dto.NewIncomeDTO;
import com.eltiosento.economyapp.model.Income;

@Component
public class IncomeDTOConverter {

    @Autowired
    private ModelMapper modelMapper;

    public Income convertToEntity(NewIncomeDTO newIncomeDTO) {

        return modelMapper.map(newIncomeDTO, Income.class);
    }

    public IncomeDTO convertToDTO(Income income) {
        return modelMapper.map(income, IncomeDTO.class);
    }
}
