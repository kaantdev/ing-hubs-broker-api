package com.ing.hubs.broker_api.controller;

import com.ing.hubs.broker_api.dto.AssetTransactionRequestDTO;
import com.ing.hubs.broker_api.dto.AssetTransactionResponseDTO;
import com.ing.hubs.broker_api.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping("/deposit")
    public ResponseEntity<AssetTransactionResponseDTO> deposit(@AuthenticationPrincipal UserDetails userDetails,
                                                               @RequestBody AssetTransactionRequestDTO request) {
        AssetTransactionResponseDTO response = assetService.depositMoney(request.getCustomerId(), request.getAmount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AssetTransactionResponseDTO> withdraw(@AuthenticationPrincipal UserDetails userDetails,
                                                                @RequestBody AssetTransactionRequestDTO request) {
        AssetTransactionResponseDTO response = assetService.withdrawMoney(request.getCustomerId(), request.getAmount());
        return ResponseEntity.ok(response);
    }
}
