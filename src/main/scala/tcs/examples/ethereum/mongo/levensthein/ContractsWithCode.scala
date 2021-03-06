package tcs.examples.ethereum.mongo.levensthein

import org.web3j.protocol.http.HttpService
import tcs.blockchain.BlockchainLib
import tcs.blockchain.ethereum.{EthereumBlockchain, EthereumSettings}
import tcs.db.DatabaseSettings
import tcs.mongo.Collection

object ContractsWithCode {
  def main(args: Array[String]): Unit = {
    val blockchain: EthereumBlockchain = BlockchainLib.getEthereumBlockchain(new EthereumSettings("http://localhost:8545"))
    val mongo = new DatabaseSettings("myDatabase")
    val contractsWithCode = new Collection("contractsWithCode", mongo)

    blockchain.start(1000000).end(1200000).foreach(block => {
      if (block.number % 100 == 0) {
        println("Current block ->" + block.number)
      }
      block.transactions.foreach(tx => {
        if (tx.addressCreated != null) {
          val list = List(
            ("contractAddress", tx.addressCreated),
            ("contractCode", blockchain.getContractCode(tx.addressCreated))
          )
          contractsWithCode.append(list)
        }
      })
    })
    contractsWithCode.close
  }
}
