package com.example.financeanalyzer.repository;

import com.example.financeanalyzer.model.Transaction;
import com.example.financeanalyzer.model.TransactionType;
import com.example.financeanalyzer.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("""
            select t from Transaction t
            join fetch t.category
            where t.user = :user
            order by t.transactionDate desc, t.id desc
            """)
    List<Transaction> findTop5ByUserOrderByTransactionDateDescIdDesc(
            @Param("user") User user, org.springframework.data.domain.Pageable pageable);

    @Query("""
            select t from Transaction t
            join fetch t.category
            where t.user = :user
              and (:type is null or t.type = :type)
              and (:keyword is null or lower(t.title) like lower(concat('%', :keyword, '%')))
              and (:fromDate is null or t.transactionDate >= :fromDate)
              and (:toDate is null or t.transactionDate <= :toDate)
            order by t.transactionDate desc, t.id desc
            """)
    List<Transaction> search(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("keyword") String keyword,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("select t from Transaction t join fetch t.category where t.id = :id")
    java.util.Optional<Transaction> findByIdWithCategory(@Param("id") Long id);

    @Query("select coalesce(sum(t.amount), 0) from Transaction t where t.user = :user and t.type = :type")
    BigDecimal sumByUserAndType(@Param("user") User user, @Param("type") TransactionType type);

    @Query("""
            select c.name, coalesce(sum(t.amount), 0)
            from Transaction t join t.category c
            where t.user = :user and t.type = com.example.financeanalyzer.model.TransactionType.EXPENSE
            group by c.name
            order by sum(t.amount) desc
            """)
    List<Object[]> expenseByCategory(@Param("user") User user);

    @Query("""
            select function('date_format', t.transactionDate, '%Y-%m'), coalesce(sum(t.amount), 0)
            from Transaction t
            where t.user = :user and t.type = com.example.financeanalyzer.model.TransactionType.EXPENSE
            group by function('date_format', t.transactionDate, '%Y-%m')
            order by function('date_format', t.transactionDate, '%Y-%m')
            """)
    List<Object[]> monthlyExpenses(@Param("user") User user);

    @Query("""
            select coalesce(sum(t.amount), 0)
            from Transaction t
            where t.user = :user and t.type = :type and t.transactionDate between :start and :end
            """)
    BigDecimal sumByMonthAndType(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
