package com.finance.ledger.service;

import com.finance.ledger.dto.TransactionRequest;
import com.finance.ledger.dto.TransactionResponse;
import com.finance.ledger.entity.Transaction;
import com.finance.ledger.entity.User;
import com.finance.ledger.enums.Category;
import com.finance.ledger.repository.TransactionRepository;
import com.finance.ledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.finance.ledger.dto.MonthlySummaryResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;


    public void create(TransactionRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Transaction transaction = new Transaction(
                request.getAmount(),
                request.getDescription(),
                request.getType(),
                request.getCategory(),
                user

        );

        transactionRepository.save(transaction);
    }

    public List<TransactionResponse> getMyTransactions(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return transactionRepository.findByUser(user)
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }

    @Transactional
    public void update(Long id, TransactionRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("거래를 찾을 수 없습니다."));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        transaction.update(
                request.getAmount(),
                request.getDescription(),
                request.getType(),
                request.getCategory()
        );
    }

    public void delete(Long id, String email) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("거래를 찾을 수 없습니다."));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        transactionRepository.delete(transaction);
    }

    @Transactional
    public String importCsv(MultipartFile file, String email) { //기존 csv 처리 코드

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Transaction> transactions = new ArrayList<>();

        int duplicateCount = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        file.getInputStream(),
                        StandardCharsets.UTF_8
                )
        )) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // 첫 번째 줄은 CSV 헤더
                if (lineNumber == 1) {
                    continue;
                }

                // 빈 줄은 무시
                if (line.isBlank()) {
                    continue;
                }

                String[] values = line.split(",",-1);

                if (values.length != 5) {
                    throw new IllegalArgumentException(
                            lineNumber + "번째 줄의 CSV 형식이 올바르지 않습니다."
                    );
                }

                LocalDate date = LocalDate.parse(values[0].trim());
                String description = values[1].trim();
                Integer amount = Integer.parseInt(values[2].trim());
                String type = values[3].trim().toUpperCase();

                String categoryValue = values[4].trim();

                Category category;

                if (categoryValue.isBlank()) {
                    category = classifyCategory(description);
                } else {
                    try {
                        category = Category.valueOf(categoryValue.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException(
                                lineNumber + "번째 줄의 카테고리가 올바르지 않습니다: "
                                        + categoryValue
                        );
                    }
                }

                boolean alreadyExists =
                        transactionRepository
                                .existsByUserAndDateAndAmountAndDescriptionAndTypeAndCategory(
                                        user,
                                        date,
                                        amount,
                                        description,
                                        type,
                                        category
                                );

                if (alreadyExists) {
                    duplicateCount++;
                    continue;
                }

                Transaction transaction = new Transaction(
                        amount,
                        description,
                        type,
                        category,
                        user,
                        date
                );

                transactions.add(transaction);
            }

            int savedCount = transactions.size();

            if (!transactions.isEmpty()) {
                transactionRepository.saveAll(transactions);
            }

            return "신규 저장 " + savedCount
                    + "건, 중복 제외 " + duplicateCount + "건";

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "CSV 파일을 처리하는 중 오류가 발생했습니다.",
                    e
            );
        }
    }
    private Category classifyCategory(String description) { //자동분류 코드

        String value = description.toLowerCase();

        if (containsAny(
                value,
                "스타벅스",
                "카페",
                "커피",
                "식당",
                "편의점",
                "배달",
                "맥도날드"
        )) {
            return Category.FOOD;
        }

        if (containsAny(
                value,
                "버스",
                "지하철",
                "택시",
                "카카오택시",
                "주유",
                "교통"
        )) {
            return Category.TRANSPORT;
        }

        if (containsAny(
                value,
                "쿠팡",
                "무신사",
                "쇼핑",
                "마켓컬리",
                "네이버페이"
        )) {
            return Category.SHOPPING;
        }

        if (containsAny(
                value,
                "월세",
                "관리비",
                "전기",
                "수도",
                "가스",
                "통신비",
                "휴대폰"
        )) {
            return Category.LIVING;
        }

        if (containsAny(
                value,
                "월급",
                "급여",
                "상여금",
                "성과급"
        )) {
            return Category.SALARY;
        }

        return Category.ETC;
    }

    private boolean containsAny(String value, String... keywords) {//키워드 확인 코드

        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    public MonthlySummaryResponse getMonthlySummary(
            int year,
            int month,
            String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(
                startDate.lengthOfMonth()
        );

        List<Transaction> transactions =
                transactionRepository.findByUserAndDateBetween(
                        user,
                        startDate,
                        endDate
                );

        int totalIncome = transactions.stream()
                .filter(transaction ->
                        "INCOME".equalsIgnoreCase(transaction.getType()))
                .mapToInt(Transaction::getAmount)
                .sum();

        int totalExpense = transactions.stream()
                .filter(transaction ->
                        "EXPENSE".equalsIgnoreCase(transaction.getType()))
                .mapToInt(Transaction::getAmount)
                .sum();

        int balance = totalIncome - totalExpense;

        return new MonthlySummaryResponse(
                year,
                month,
                totalIncome,
                totalExpense,
                balance
        );
    }

    public Map<Category, Integer> getMonthlyExpenseByCategory(
            int year,
            int month,
            String email
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(
                startDate.lengthOfMonth()
        );

        List<Transaction> transactions =
                transactionRepository.findByUserAndDateBetween(
                        user,
                        startDate,
                        endDate
                );

        Map<Category, Integer> categoryExpenses =
                new EnumMap<>(Category.class);

        // 모든 카테고리를 우선 0원으로 설정
        for (Category category : Category.values()) {
            categoryExpenses.put(category, 0);
        }

        for (Transaction transaction : transactions) {

            // 지출 거래만 계산
            if (!"EXPENSE".equalsIgnoreCase(transaction.getType())) {
                continue;
            }

            Category category = transaction.getCategory();
            Integer amount = transaction.getAmount();

            categoryExpenses.merge(
                    category,
                    amount,
                    Integer::sum
            );
        }

        return categoryExpenses;
    }
}