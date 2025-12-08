package com.eltiosento.economyapp.transfer;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eltiosento.economyapp.dto.ExpenseDTO;
import com.eltiosento.economyapp.dto.NewExpenseDTO;
import com.eltiosento.economyapp.dto.converter.ExpenseDTOConverter;
import com.eltiosento.economyapp.error.CategoryNotFoundException;
import com.eltiosento.economyapp.error.CategoryValidationException;
import com.eltiosento.economyapp.error.ExpenseBadRequestException;
import com.eltiosento.economyapp.error.UserNotFoundException;
import com.eltiosento.economyapp.model.Category;
import com.eltiosento.economyapp.model.Expense;
import com.eltiosento.economyapp.model.User;
import com.eltiosento.economyapp.repository.CategoryRepository;
import com.eltiosento.economyapp.repository.ExpenseRepository;
import com.eltiosento.economyapp.repository.UserRepository;

@Service
public class TransferRequestImp implements TransferService {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private ExpenseRepository expenseRepository;

        @Autowired
        private ExpenseDTOConverter expenseDTOConverter;

        @Override
        public List<ExpenseDTO> transferSavingsToExpenses(TransferRequestDTO transferRequest) {
                // 1. Validacions bàsiques
                User user = userRepository.findById(transferRequest.getUserId()).orElseThrow(
                                () -> new UserNotFoundException(
                                                "User with ID " + transferRequest.getUserId() + " not found"));

                Category fromCat = categoryRepository.findById(
                                transferRequest.getFromCategoryId()).orElseThrow(
                                                () -> new CategoryNotFoundException("Category with ID "
                                                                + transferRequest.getFromCategoryId() + " not found"));

                Category toCat = categoryRepository.findById(transferRequest.getToCategoryId())
                                .orElseThrow(() -> new CategoryNotFoundException("Category with ID "
                                                + transferRequest.getToCategoryId() + " not found"));

                if (!fromCat.isSaving()) {
                        throw new CategoryValidationException("Category with ID " + transferRequest.getFromCategoryId()
                                        + " is not a savings category");
                }
                // Impedim que es creen despeses amb categories pare
                if (toCat.getParentCategory() == null) {
                        throw new CategoryValidationException("Category with category id " + toCat.getId()
                                        + " is a parent category. Please select a subcategory.");
                }

                if (fromCat.getId() == toCat.getId()) {
                        throw new CategoryValidationException("Category with ID " +
                                        transferRequest.getToCategoryId()
                                        + " is the same as the source category, cannot transfer to it");
                }

                // 2. Comprovar saldo d’estalvis
                BigDecimal allSaveds = expenseRepository.sumAllSavingsByTypeCategory().orElse(BigDecimal.ZERO);

                BigDecimal savigsFromCategory = expenseRepository
                                .sumExpensesByCategoryId(fromCat.getId()).orElse(BigDecimal.ZERO);

                // Comprovar que hi ha prou saldo
                if (transferRequest.getAmount().compareTo(allSaveds) > 0)
                        throw new ExpenseBadRequestException(
                                        "Insufficient savings to transfer: requested "
                                                        + transferRequest.getAmount() + ", available " + allSaveds);

                if (transferRequest.getAmount().compareTo(savigsFromCategory) > 0)
                        throw new ExpenseBadRequestException(
                                        "Insufficient savings in category "
                                                        + fromCat.getName() + ": requested "
                                                        + transferRequest.getAmount() + ", available "
                                                        + savigsFromCategory);

                // 3. Crear despesa negativa (redempció)
                NewExpenseDTO redemDTO = new NewExpenseDTO();
                redemDTO.setUserId(user.getId());
                redemDTO.setCategoryId(fromCat.getId());
                redemDTO.setAmount(transferRequest.getAmount().negate());
                redemDTO.setDescription("Redención (uso de ahorros): " + transferRequest.getDescription());
                redemDTO.setExpenseDate(transferRequest.getDate());

                // 4. Crear despesa positiva (compra)
                NewExpenseDTO compDTO = new NewExpenseDTO();
                compDTO.setUserId(user.getId());
                compDTO.setCategoryId(toCat.getId());
                compDTO.setAmount(transferRequest.getAmount());
                compDTO.setDescription(
                                "Transferencia realizada desde " + fromCat.getName() + " para: "
                                                + transferRequest.getDescription());
                compDTO.setExpenseDate(transferRequest.getDate());

                Expense redemption = expenseRepository.save(expenseDTOConverter.convertToEntity(redemDTO));
                Expense purchase = expenseRepository.save(expenseDTOConverter.convertToEntity(compDTO));

                // 5. Convertir a DTO i retornar
                // Amb List.of() es crea una llista amb els dos elements
                // i es converteix a DTO
                return List.of(redemption, purchase)
                                .stream()
                                .map(expenseDTOConverter::convertToDTO)
                                .collect(Collectors.toList());

        }
        /*
         * @Override
         * public List<ExpenseDTO> transferSavingsToSaving(TransferRequestDTO
         * transferRequest) {
         * // 1. Validacions bàsiques
         * User user = userRepository.findById(transferRequest.getUserId()).orElseThrow(
         * () -> new UserNotFoundException(
         * "User with ID " + transferRequest.getUserId() + " not found"));
         * 
         * Category fromCat = categoryRepository.findById(
         * transferRequest.getFromCategoryId()).orElseThrow(
         * () -> new CategoryNotFoundException("Category with ID "
         * + transferRequest.getFromCategoryId() + " not found"));
         * 
         * Category toCat =
         * categoryRepository.findById(transferRequest.getToCategoryId())
         * .orElseThrow(() -> new CategoryNotFoundException("Category with ID "
         * + transferRequest.getToCategoryId() + " not found"));
         * 
         * if (!fromCat.isSaving()) {
         * throw new CategoryValidationException("Category with ID " +
         * transferRequest.getFromCategoryId()
         * + " is not a savings category");
         * }
         * 
         * if (!toCat.isSaving()) {
         * throw new CategoryValidationException("Category with ID " +
         * transferRequest.getToCategoryId()
         * + " is not a savings category, cannot transfer to it");
         * }
         * 
         * BigDecimal savigsFromCategory = expenseRepository
         * .sumExpensesByCategoryId(fromCat.getId()).orElse(BigDecimal.ZERO);
         * 
         * // Comprovar que hi ha prou saldo
         * if (transferRequest.getAmount().compareTo(savigsFromCategory) > 0)
         * throw new ExpenseBadRequestException(
         * "Insufficient savings in category "
         * + fromCat.getName() + ": requested "
         * + transferRequest.getAmount() + ", available "
         * + savigsFromCategory);
         * 
         * // 3. Crear despesa negativa (redempció)
         * NewExpenseDTO redemDTO = new NewExpenseDTO();
         * redemDTO.setUserId(user.getId());
         * redemDTO.setCategoryId(fromCat.getId());
         * redemDTO.setAmount(transferRequest.getAmount().negate());
         * redemDTO.setDescription("Redención (uso de ahorros): " +
         * transferRequest.getDescription());
         * redemDTO.setExpenseDate(transferRequest.getDate());
         * 
         * // 4. Crear despesa positiva (compra)
         * NewExpenseDTO compDTO = new NewExpenseDTO();
         * compDTO.setUserId(user.getId());
         * compDTO.setCategoryId(toCat.getId());
         * compDTO.setAmount(transferRequest.getAmount());
         * compDTO.setDescription(
         * "Ingreso desde " + fromCat.getName() + ": " +
         * transferRequest.getDescription());
         * compDTO.setExpenseDate(transferRequest.getDate());
         * 
         * Expense redemption =
         * expenseRepository.save(expenseDTOConverter.convertToEntity(redemDTO));
         * Expense purchase =
         * expenseRepository.save(expenseDTOConverter.convertToEntity(compDTO));
         * 
         * // 5. Convertir a DTO i retornar
         * // Amb List.of() es crea una llista amb els dos elements
         * // i es converteix a DTO
         * return List.of(redemption, purchase)
         * .stream()
         * .map(expenseDTOConverter::convertToDTO)
         * .collect(Collectors.toList());
         * }
         */
}
