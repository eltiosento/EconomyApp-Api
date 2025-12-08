package com.eltiosento.economyapp.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eltiosento.economyapp.model.Income;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findAllByUserId(Long userId);

    @Query("SELECT SUM(i.amount) FROM Income i")
    Optional<BigDecimal> sumAllIncomes();

    @Query("SELECT SUM(i.amount) FROM Income i WHERE MONTH(i.incomeDate) = :month AND YEAR(i.incomeDate) = :year")
    Optional<BigDecimal> sumIncomesByMonth(int month, int year);

}
