package com.finance.ledger.controller;

import com.finance.ledger.config.JwtTokenProvider;
import com.finance.ledger.dto.TransactionRequest;
import com.finance.ledger.dto.TransactionResponse;
import com.finance.ledger.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<String> create(
            @RequestBody TransactionRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);

        transactionService.create(request, email);

        return ResponseEntity.ok("거래내역 등록 성공");
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getMyTransactions(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);

        List<TransactionResponse> transactions = transactionService.getMyTransactions(email);

        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{id}") //update
    public ResponseEntity<String> update(
            @PathVariable Long id,
            @RequestBody TransactionRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);

        transactionService.update(id, request,email);

        return ResponseEntity.ok("거래 수정 성공");
    }

    @DeleteMapping("/{id}") //delete
    public ResponseEntity<String> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.replace("Bearer ", "");
        String email = jwtTokenProvider.getEmailFromToken(token);

        transactionService.delete(id,email);

        return ResponseEntity.ok("거래 삭제 성공");
    }
}