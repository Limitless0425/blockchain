package com.wetech.demo.web3j.service;

import com.wetech.demo.web3j.contracts.erc20test.ERC20Test; // ⚠️ 1. 确保导入路径正确
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Slf4j // 参照 SimpleStorageService，使用 Slf4j 进行日志记录
@Service
@RequiredArgsConstructor // 参照 SimpleStorageService，使用 Lombok 自动注入 final 字段
public class ERC20TestService {

    // 自动注入 (因为是 final 和 @RequiredArgsConstructor)
    private final Web3j web3j;
    private final Credentials credentials;
    private final ContractGasProvider gasProvider;

    // 参照 SimpleStorageService，在类中保存合约实例
    private ERC20Test contract;

    /**
     * -- GETTER --
     * Get the address of the currently loaded contract
     *
     * @return the contract address
     */
    @Getter // 参照 SimpleStorageService
    private String contractAddress;

    /**
     * 部署一个新的 ERC20Test 合约。
     * (已根据你的 ERC20Test.java 文件调整，使用空构造函数)
     *
     * @return 部署后的合约地址
     */
    public CompletableFuture<String> deploy() {
        log.info("Deploying ERC20Test contract...");

        // 你的合约 deploy 方法返回 RemoteCall
        RemoteCall<ERC20Test> deployCall = ERC20Test.deploy(
                web3j,
                credentials,
                gasProvider
        );

        // 参照 SimpleStorageService，使用 sendAsync 和 thenApply
        return deployCall.sendAsync().thenApply(deployedContract -> {
            this.contract = deployedContract;
            this.contractAddress = deployedContract.getContractAddress();
            log.info("ERC20Test contract deployed to: {}", contractAddress);
            return contractAddress;
        });
    }

    /**
     * 参照 SimpleStorageService，加载一个已存在的合约
     * @param address 合约地址
     */
    public void loadContract(String address) {
        log.info("Loading ERC20Test contract from address: {}", address);
        this.contract = ERC20Test.load(address, web3j, credentials, gasProvider);
        this.contractAddress = address;
        log.info("ERC20Test contract loaded.");
    }

    // 辅助方法，用于在调用前检查合约是否已加载
    private void checkContractLoaded() {
        if (contract == null) {
            log.error("Contract not deployed or loaded");
            throw new IllegalStateException("Contract not deployed or loaded. Please deploy or load a contract first.");
        }
    }

    // --- 以下是 ERC20 的五个核心方法 ---

    /**
     * (读操作) 查询指定地址的代币余额。
     * @param ownerAddress 要查询的地址
     * @return 余额的 CompletableFuture
     */
    public CompletableFuture<BigInteger> balanceOf(String ownerAddress) {
        checkContractLoaded();
        log.info("Getting balanceOf for {} from contract at address: {}", ownerAddress, contractAddress);
        return contract.balanceOf(ownerAddress).sendAsync();
    }

    /**
     * (写操作) 铸造新的代币。
     * @param to 接收代币的地址
     * @param amount 铸造的数量 (最小单位)
     * @return 交易回执的 CompletableFuture
     */
    public CompletableFuture<TransactionReceipt> mint(String to, BigInteger amount) {
        checkContractLoaded();
        log.info("Minting {} tokens to {} in contract at address: {}", amount, to, contractAddress);
        return contract.mint(to, amount).sendAsync();
    }

    /**
     * (写操作) 将代币从当前签名者账户转移给他人。
     * @param to 接收代币的地址
     * @param amount 转移的数量 (最小单位)
     * @return 交易回执的 CompletableFuture
     */
    public CompletableFuture<TransactionReceipt> transfer(String to, BigInteger amount) {
        checkContractLoaded();
        log.info("Transferring {} tokens to {} from contract at address: {}", amount, to, contractAddress);
        return contract.transfer(to, amount).sendAsync();
    }

    /**
     * (写操作) 授权 spender 地址可以从当前签名者账户中提取一定数量的代币。
     * @param spender 被授权的地址
     * @param amount 授权的数量 (最小单位)
     * @return 交易回执的 CompletableFuture
     */
    public CompletableFuture<TransactionReceipt> approve(String spender, BigInteger amount) {
        checkContractLoaded();
        log.info("Approving {} to spend {} tokens from contract at address: {}", spender, amount, contractAddress);
        return contract.approve(spender, amount).sendAsync();
    }

    /**
     * (写操作) 从 from 地址转移代币到 to 地址。
     * 必须由被 approve 的 spender (即本服务配置的私钥) 来调用。
     * @param from 代币转出地址 (必须已授权给本服务)
     * @param to 代币接收地址
     * @param amount 转移数量
     * @return 交易回执的 CompletableFuture
     */
    public CompletableFuture<TransactionReceipt> transferFrom(String from, String to, BigInteger amount) {
        checkContractLoaded();
        log.info("Transferring (via transferFrom) {} tokens from {} to {} in contract at address: {}", amount, from, to, contractAddress);
        return contract.transferFrom(from, to, amount).sendAsync();
    }

    /**
     * (读操作) 查询授权额度。
     * (这是 approve 的配套方法，用于验证)
     * @param owner 授权人地址
     * @param spender 被授权人地址
     * @return 额度的 CompletableFuture
     */
    public CompletableFuture<BigInteger> allowance(String owner, String spender) {
        checkContractLoaded();
        log.info("Getting allowance for spender {} on owner {} from contract at address: {}", spender, owner, contractAddress);
        return contract.allowance(owner, spender).sendAsync();
    }
}