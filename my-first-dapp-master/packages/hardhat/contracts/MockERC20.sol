
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.20;

// 导入OpenZeppelin的ERC20基础合约
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

// 定义WKYToken合约，继承ERC20基础合约
contract WKYToken is ERC20 {

    constructor() ERC20("WKYToken", "WKY") {}

    //1. 实现mint功能
    function mint(address _to, uint256 _amount) public {
        _mint(_to, _amount);
    }

    // 2. 实现burn功能
    function burn(uint256 _amount) public {
        _burn(msg.sender, _amount);
    }

    // 3. 获取代币名称
    function name() public view override returns (string memory) {
        return super.name();
    }

    // 4. 获取代币符号
    function symbol() public view override returns (string memory) {
        return super.symbol();
    }

    // 5. 获取代币精度（默认18位）
    function decimals() public view override returns (uint8) {
        return super.decimals();
    }

    // 6. 获取代币总供应量
    function totalSupply() public view override returns (uint256) {
        return super.totalSupply();
    }

    // 7. 查询指定地址的代币余额
    function balanceOf(address _owner) public view override returns (uint256) {
        return super.balanceOf(_owner);
    }

    // 8. 从调用者地址向指定地址转账代币
    function transfer(address _to, uint256 _value) public override returns (bool) {
        return super.transfer(_to, _value);
    }

    // 9. 从指定地址向目标地址转账代币
    function transferFrom(address _from, address _to, uint256 _value) public override returns (bool) {
        return super.transferFrom(_from, _to, _value);
    }

    // 10. 授权第三方地址使用调用者的指定数量代币
    function approve(address _spender, uint256 _value) public override returns (bool) {
        return super.approve(_spender, _value);
    }

    // 11. 查询第三方地址可使用的指定所有者的代币剩余额度
    function allowance(address _owner, address _spender) public view override returns (uint256) {
        return super.allowance(_owner, _spender);
    }
}









