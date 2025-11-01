import { HardhatRuntimeEnvironment } from "hardhat/types";
import { DeployFunction } from "hardhat-deploy/types";
import { Contract } from "ethers";

const deployWKYToken: DeployFunction = async function (hre: HardhatRuntimeEnvironment) {
  const { deployer } = await hre.getNamedAccounts();
  const { deploy } = hre.deployments;
  const { ethers } = hre;
  // 构造函数无参数
  await deploy("WKYToken", {
    from: deployer,
    args: [], // 该合约构造函数无额外参数，留空
    log: true,
    autoMine: true,
  });

  const wkyToken = await hre.ethers.getContract<Contract>("WKYToken", deployer);
  console.log("✅ WKYToken合约地址:", wkyToken.address);
  console.log("✅ 代币名称:", await wkyToken.name());
  console.log("✅ 代币符号:", await wkyToken.symbol());
};

export default deployWKYToken;
deployWKYToken.tags = ["ERC20WPL202330551462"];