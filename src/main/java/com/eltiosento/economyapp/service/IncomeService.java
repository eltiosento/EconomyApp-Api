package com.eltiosento.economyapp.service;

import java.util.List;

import com.eltiosento.economyapp.dto.IncomeDTO;
import com.eltiosento.economyapp.dto.NewIncomeDTO;

public interface IncomeService {

    IncomeDTO getIncomeById(Long id);

    List<IncomeDTO> getAllIncomes();

    List<IncomeDTO> getIncomesByUserId(Long userId);

    IncomeDTO createIncome(NewIncomeDTO newIncomeDTO);

    IncomeDTO deleteIncome(Long id);

    IncomeDTO updateIncome(Long id, NewIncomeDTO newIncomeDTO);

}
