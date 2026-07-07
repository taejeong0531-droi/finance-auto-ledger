package com.finance.ledger.service;

import com.finance.ledger.dto.TransactionRequest;
import com.finance.ledger.dto.TransactionResponse;
import com.finance.ledger.entity.Transaction;
import com.finance.ledger.entity.User;
import com.finance.ledger.repository.TransactionRepository;
import com.finance.ledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                request.getType()
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
}