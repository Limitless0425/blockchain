// SPDX-License-Identifier: Apache 2.0
pragma solidity ^0.8.20;

/*
 * 导入 OpenZeppelin 的标准合约：
 * 1. ERC20.sol: 包含了所有 ERC20 标准接口 (如 balanceOf, transfer等) 
 * 2. Ownable.sol: 辅助合约，用于实现权限管理
 */
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

/*
 * 定义合约 ERC20Test
 * "is ERC20, Ownable" 表示它继承了这两个合约的所有功能。
 * 这自动满足了"必须重写上述ERC20所有接口定义"的要求 。
 */
contract ERC20Test is ERC20, Ownable {

    /**
     * @dev 合约的构造函数，只在部署时运行一次。
     */
    constructor() 
        ERC20("WXQToken", "WXQ") 
        Ownable(msg.sender)     
    {
        // 构造函数内容
    }

    /**
     * @dev 实现 "mint" 接口 
     * 允许合约所有者 (Owner) 铸造新的代币。
     * "onlyOwner" 是一个修饰符 (来自 Ownable.sol)，它确保只有所有者才能调用此函数。
     */
    function mint(address to, uint256 amount) public onlyOwner {
        _mint(to, amount); // 调用 ERC20 内部的 _mint 函数
    }

    /**
     * @dev 实现 "burn" 接口 
     * 允许任何用户销毁 (burn) 他们自己的代币。
     * msg.sender 指的是调用这个函数的用户。
     */
    function burn(uint256 amount) public {
        _burn(msg.sender, amount); // 调用 ERC20 内部的 _burn 函数
    }
}