package com.wetech.demo.web3j.controller;

import com.wetech.demo.web3j.service.ERC20TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.core.methods.response.TransactionReceipt; // 确保导入

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/erc20test")
@RequiredArgsConstructor
public class ERC20TestController {

    // 2. 注入 ERC20TestService
    private final ERC20TestService erc20TestService;

    /**
     * 参照 SimpleStorageController，部署一个新的 ERC20Test 合约
     * @return 部署后的合约地址
     */
    @PostMapping("/deploy")
    public CompletableFuture<ResponseEntity<Map<String, String>>> deployContract() {
        log.info("Request to deploy ERC20Test contract");
        return erc20TestService.deploy()
                .thenApply(address -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("contractAddress", address);
                    log.info("ERC20Test contract deployed at: {}", address);
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * 参照 SimpleStorageController，加载一个已存在的合约
     * @param address 合约地址
     * @return 成功信息
     */
    @PostMapping("/load")
    public ResponseEntity<Map<String, String>> loadContract(@RequestParam String address) {
        log.info("Request to load ERC20Test contract from: {}", address);
        erc20TestService.loadContract(address);
        Map<String, String> response = new HashMap<>();
        response.put("message", "ERC20Test Contract loaded successfully");
        response.put("contractAddress", address);
        return ResponseEntity.ok(response);
    }

    /**
     * (读操作) 参照 getValue，实现 balanceOf
     * @param ownerAddress 要查询余额的地址
     * @return 余额
     */
    @GetMapping("/balanceOf")
    public CompletableFuture<ResponseEntity<Map<String, String>>> balanceOf(@RequestParam String ownerAddress) {
        log.info("Request to get balanceOf for: {}", ownerAddress);
        return erc20TestService.balanceOf(ownerAddress)
                .thenApply(balance -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("ownerAddress", ownerAddress);
                    response.put("balance", balance.toString());
                    response.put("contractAddress", erc20TestService.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * (写操作) 参照 setValue，实现 mint
     * @param to 接收代币的地址
     * @param amount 铸造的数量 (最小单位)
     * @return 交易收据
     */
    @PostMapping("/mint")
    public CompletableFuture<ResponseEntity<Map<String, String>>> mint(
            @RequestParam String to,
            @RequestParam String amount) {

        log.info("Request to mint {} tokens to {}", amount, to);
        BigInteger bigAmount = new BigInteger(amount); // 参照 setValue 的参数转换

        return erc20TestService.mint(to, bigAmount)
                .thenApply(receipt -> {
                    // 参照 setValue 的响应格式
                    Map<String, String> response = buildReceiptResponse(receipt);
                    response.put("action", "mint");
                    response.put("to", to);
                    response.put("amount", amount);
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * (写操作) 参照 setValue，实现 transfer
     * @param to 接收代币的地址
     * @param amount 转移的数量 (最小单位)
     * @return 交易收据
     */
    @PostMapping("/transfer")
    public CompletableFuture<ResponseEntity<Map<String, String>>> transfer(
            @RequestParam String to,
            @RequestParam String amount) {

        log.info("Request to transfer {} tokens to {}", amount, to);
        BigInteger bigAmount = new BigInteger(amount);

        return erc20TestService.transfer(to, bigAmount)
                .thenApply(receipt -> {
                    Map<String, String> response = buildReceiptResponse(receipt);
                    response.put("action", "transfer");
                    response.put("to", to);
                    response.put("amount", amount);
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * (写操作) 参照 setValue，实现 approve
     * @param spender 被授权的地址
     * @param amount 授权的数量 (最小单位)
     * @return 交易收据
     */
    @PostMapping("/approve")
    public CompletableFuture<ResponseEntity<Map<String, String>>> approve(
            @RequestParam String spender,
            @RequestParam String amount) {

        log.info("Request to approve {} to spend {} tokens", spender, amount);
        BigInteger bigAmount = new BigInteger(amount);

        return erc20TestService.approve(spender, bigAmount)
                .thenApply(receipt -> {
                    Map<String, String> response = buildReceiptResponse(receipt);
                    response.put("action", "approve");
                    response.put("spender", spender);
                    response.put("amount", amount);
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * (写操作) 参照 setValue，实现 transferFrom
     * @param from 代币转出地址
     * @param to 代币接收地址
     * @param amount 转移数量
     * @return 交易收据
     */
    @PostMapping("/transferFrom")
    public CompletableFuture<ResponseEntity<Map<String, String>>> transferFrom(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String amount) {

        log.info("Request to transferFrom {} tokens from {} to {}", amount, from, to);
        BigInteger bigAmount = new BigInteger(amount);

        return erc20TestService.transferFrom(from, to, bigAmount)
                .thenApply(receipt -> {
                    Map<String, String> response = buildReceiptResponse(receipt);
                    response.put("action", "transferFrom");
                    response.put("from", from);
                    response.put("to", to);
                    response.put("amount", amount);
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * (辅助方法) 参照 setValue 的响应格式，构建一个包含交易收据详情的 Map
     */
    private Map<String, String> buildReceiptResponse(TransactionReceipt receipt) {
        Map<String, String> response = new HashMap<>();
        response.put("transactionHash", receipt.getTransactionHash());
        response.put("blockNumber", receipt.getBlockNumber().toString());
        response.put("gasUsed", receipt.getGasUsed().toString());
        response.put("status", receipt.getStatus()); // "0x1" for success, "0x0" for failure
        response.put("contractAddress", erc20TestService.getContractAddress());
        return response;
    }

    /**
     * (读操作，推荐添加) 参照 getValue，实现 allowance (用于验证 approve)
     * @param owner 授权人地址
     * @param spender 被授权人地址
     * @return 授权额度
     */
    @GetMapping("/allowance")
    public CompletableFuture<ResponseEntity<Map<String, String>>> allowance(
            @RequestParam String owner,
            @RequestParam String spender) {

        log.info("Request to get allowance for spender {} on owner {}", spender, owner);
        return erc20TestService.allowance(owner, spender)
                .thenApply(allowance -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("owner", owner);
                    response.put("spender", spender);
                    response.put("allowance", allowance.toString());
                    response.put("contractAddress", erc20TestService.getContractAddress());
                    return ResponseEntity.ok(response);
                });
    }
}
