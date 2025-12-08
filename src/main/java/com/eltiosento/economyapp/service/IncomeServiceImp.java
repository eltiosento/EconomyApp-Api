package com.eltiosento.economyapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eltiosento.economyapp.dto.IncomeDTO;
import com.eltiosento.economyapp.dto.NewIncomeDTO;
import com.eltiosento.economyapp.dto.converter.IncomeDTOConverter;
import com.eltiosento.economyapp.error.IncomeNotFoundException;
import com.eltiosento.economyapp.error.UserNotFoundException;
import com.eltiosento.economyapp.model.Income;
import com.eltiosento.economyapp.model.User;
import com.eltiosento.economyapp.repository.IncomeRepository;
import com.eltiosento.economyapp.repository.UserRepository;

@Service
public class IncomeServiceImp implements IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private IncomeDTOConverter incomeDTOConverter;

    @Autowired
    private UserRepository userRepository;

    @Override
    public IncomeDTO getIncomeById(Long id) {

        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new IncomeNotFoundException("Income with " + id + " not found"));

        return incomeDTOConverter.convertToDTO(income);
    }

    @Override
    public List<IncomeDTO> getAllIncomes() {

        List<Income> incomes = incomeRepository.findAll();

        List<IncomeDTO> incomeDTOs = new ArrayList<>();

        if (!incomes.isEmpty()) {

            incomeDTOs = incomes.stream()
                    .map(income -> incomeDTOConverter.convertToDTO(income))
                    .collect(Collectors.toList());
        }
        return incomeDTOs;
    }

    @Override
    public List<IncomeDTO> getIncomesByUserId(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with user id " + userId + " not exist."));

        List<Income> incomes = incomeRepository.findAllByUserId(user.getId());

        List<IncomeDTO> incomeDTOs = new ArrayList<>();

        if (!incomes.isEmpty()) {

            incomeDTOs = incomes.stream()
                    .map(income -> incomeDTOConverter.convertToDTO(income))
                    .collect(Collectors.toList());
        }
        return incomeDTOs;
    }

    @Override
    public IncomeDTO createIncome(NewIncomeDTO newIncomeDTO) {

        User user = userRepository.findById(newIncomeDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "User with user id " + newIncomeDTO.getUserId() + " not exist."));

        Income income = incomeDTOConverter.convertToEntity(newIncomeDTO);

        Income savedIncome = incomeRepository.save(income);

        IncomeDTO incomeDTO = new IncomeDTO();

        incomeDTO.setId(savedIncome.getId());
        incomeDTO.setAmount(savedIncome.getAmount());
        incomeDTO.setCreatedAt(savedIncome.getCreatedAt());
        incomeDTO.setUpdatedAt(savedIncome.getUpdatedAt());
        incomeDTO.setUserId(user.getId());
        incomeDTO.setUserUsername(user.getUsername());
        incomeDTO.setDescription(savedIncome.getDescription());
        incomeDTO.setIncomeDate(savedIncome.getIncomeDate());

        return incomeDTO;
    }

    @Override
    public IncomeDTO deleteIncome(Long id) {

        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new IncomeNotFoundException("Income with " + id + " not found"));

        IncomeDTO incomeDTO = incomeDTOConverter.convertToDTO(income);

        incomeRepository.delete(income);

        return incomeDTO;

    }

    @Override
    public IncomeDTO updateIncome(Long id, NewIncomeDTO newIncomeDTO) {

        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new IncomeNotFoundException("Income with " + id + " not found"));

        if (newIncomeDTO.getUserId() != null) {
            User user = userRepository.findById(newIncomeDTO.getUserId())
                    .orElseThrow(() -> new UserNotFoundException(
                            "User with user id " + newIncomeDTO.getUserId() + " not exist."));
            income.setUser(user);
        }

        if (newIncomeDTO.getAmount() != null) {
            income.setAmount(newIncomeDTO.getAmount());
        }

        if (newIncomeDTO.getDescription() != null) {
            income.setDescription(newIncomeDTO.getDescription());
        }

        if (newIncomeDTO.getIncomeDate() != null) {
            income.setIncomeDate(newIncomeDTO.getIncomeDate());
        }

        Income savedIncome = incomeRepository.save(income);

        return incomeDTOConverter.convertToDTO(savedIncome);

    }

}
