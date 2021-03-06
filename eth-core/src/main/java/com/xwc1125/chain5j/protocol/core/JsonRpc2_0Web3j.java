package com.xwc1125.chain5j.protocol.core;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import com.xwc1125.chain5j.protocol.Web3j;
import com.xwc1125.chain5j.protocol.Web3jService;
import com.xwc1125.chain5j.protocol.core.methods.request.ShhFilter;
import com.xwc1125.chain5j.protocol.core.methods.request.ShhPost;
import com.xwc1125.chain5j.protocol.core.methods.request.Transaction;
import com.xwc1125.chain5j.protocol.core.methods.response.*;
import io.reactivex.Flowable;

import com.xwc1125.chain5j.protocol.core.methods.response.DbGetHex;
import com.xwc1125.chain5j.protocol.core.methods.response.DbPutHex;
import com.xwc1125.chain5j.protocol.core.methods.response.EthBlockNumber;
import com.xwc1125.chain5j.protocol.core.methods.response.EthCoinbase;
import com.xwc1125.chain5j.protocol.core.methods.response.EthCompileLLL;
import com.xwc1125.chain5j.protocol.core.methods.response.EthEstimateGas;
import com.xwc1125.chain5j.protocol.core.methods.response.EthFilter;
import com.xwc1125.chain5j.protocol.core.methods.response.EthGasPrice;
import com.xwc1125.chain5j.protocol.core.methods.response.EthGetBalance;
import com.xwc1125.chain5j.protocol.core.methods.response.EthGetCode;
import com.xwc1125.chain5j.protocol.core.methods.response.EthGetCompilers;
import com.xwc1125.chain5j.protocol.core.methods.response.EthGetStorageAt;
import com.xwc1125.chain5j.protocol.core.methods.response.EthGetTransactionReceipt;
import com.xwc1125.chain5j.protocol.core.methods.response.EthGetUncleCountByBlockHash;
import com.xwc1125.chain5j.protocol.core.methods.response.EthGetUncleCountByBlockNumber;
import com.xwc1125.chain5j.protocol.core.methods.response.EthLog;
import com.xwc1125.chain5j.protocol.core.methods.response.EthMining;
import com.xwc1125.chain5j.protocol.core.methods.response.EthProtocolVersion;
import com.xwc1125.chain5j.protocol.core.methods.response.EthSubmitWork;
import com.xwc1125.chain5j.protocol.core.methods.response.EthSubscribe;
import com.xwc1125.chain5j.protocol.core.methods.response.EthTransaction;
import com.xwc1125.chain5j.protocol.core.methods.response.Log;
import com.xwc1125.chain5j.protocol.core.methods.response.NetListening;
import com.xwc1125.chain5j.protocol.core.methods.response.NetVersion;
import com.xwc1125.chain5j.protocol.core.methods.response.ShhMessages;
import com.xwc1125.chain5j.protocol.core.methods.response.ShhNewFilter;
import com.xwc1125.chain5j.protocol.core.methods.response.ShhNewIdentity;
import com.xwc1125.chain5j.protocol.core.methods.response.Web3ClientVersion;
import com.xwc1125.chain5j.protocol.core.methods.response.Web3Sha3;
import com.xwc1125.chain5j.protocol.rx.JsonRpc2_0Rx;
import com.xwc1125.chain5j.protocol.websocket.events.LogNotification;
import com.xwc1125.chain5j.protocol.websocket.events.NewHeadsNotification;
import com.xwc1125.chain5j.utils.Async;
import com.xwc1125.chain5j.utils.Numeric;

/**
 * JSON-RPC 2.0 factory implementation.
 */
public class JsonRpc2_0Web3j implements Web3j {

    public static final int DEFAULT_BLOCK_TIME = 15 * 1000;

    protected final Web3jService web3jService;
    private final JsonRpc2_0Rx web3jRx;
    private final long blockTime;
    private final ScheduledExecutorService scheduledExecutorService;
    protected final String clientIdentifier;

    public JsonRpc2_0Web3j(Web3jService web3jService, String clientIdentifier) {
        this(web3jService, clientIdentifier, DEFAULT_BLOCK_TIME, Async.defaultExecutorService());
    }

    public JsonRpc2_0Web3j(
            Web3jService web3jService, String clientIdentifier, long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        this.web3jService = web3jService;
        if (clientIdentifier == null || clientIdentifier.trim().equals("")) {
            this.clientIdentifier = "eth";
        } else {
            this.clientIdentifier = clientIdentifier;
        }
        this.web3jRx = new JsonRpc2_0Rx(this, scheduledExecutorService);
        this.blockTime = pollingInterval;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public Request<?, Web3ClientVersion> web3ClientVersion() {
        return new Request<>(
                "web3_clientVersion",
                Collections.<String>emptyList(),
                web3jService,
                Web3ClientVersion.class);
    }

    @Override
    public Request<?, Web3Sha3> web3Sha3(String data) {
        return new Request<>(
                "web3_sha3",
                Arrays.asList(data),
                web3jService,
                Web3Sha3.class);
    }

    @Override
    public Request<?, NetVersion> netVersion() {
        return new Request<>(
                "net_version",
                Collections.<String>emptyList(),
                web3jService,
                NetVersion.class);
    }

    @Override
    public Request<?, NetListening> netListening() {
        return new Request<>(
                "net_listening",
                Collections.<String>emptyList(),
                web3jService,
                NetListening.class);
    }

    @Override
    public Request<?, NetPeerCount> netPeerCount() {
        return new Request<>(
                "net_peerCount",
                Collections.<String>emptyList(),
                web3jService,
                NetPeerCount.class);
    }

    @Override
    public Request<?, EthProtocolVersion> ethProtocolVersion() {
        return new Request<>(
                clientIdentifier + "_protocolVersion",
                Collections.<String>emptyList(),
                web3jService,
                EthProtocolVersion.class);
    }

    @Override
    public Request<?, EthCoinbase> ethCoinbase() {
        return new Request<>(
                clientIdentifier + "_coinbase",
                Collections.<String>emptyList(),
                web3jService,
                EthCoinbase.class);
    }

    @Override
    public Request<?, EthSyncing> ethSyncing() {
        return new Request<>(
                clientIdentifier + "_syncing",
                Collections.<String>emptyList(),
                web3jService,
                EthSyncing.class);
    }

    @Override
    public Request<?, EthMining> ethMining() {
        return new Request<>(
                clientIdentifier + "_mining",
                Collections.<String>emptyList(),
                web3jService,
                EthMining.class);
    }

    @Override
    public Request<?, EthHashrate> ethHashrate() {
        return new Request<>(
                clientIdentifier + "_hashrate",
                Collections.<String>emptyList(),
                web3jService,
                EthHashrate.class);
    }

    @Override
    public Request<?, EthGasPrice> ethGasPrice() {
        return new Request<>(
                clientIdentifier + "_gasPrice",
                Collections.<String>emptyList(),
                web3jService,
                EthGasPrice.class);
    }

    @Override
    public Request<?, EthAccounts> ethAccounts() {
        return new Request<>(
                clientIdentifier + "_accounts",
                Collections.<String>emptyList(),
                web3jService,
                EthAccounts.class);
    }

    @Override
    public Request<?, EthBlockNumber> ethBlockNumber() {
        return new Request<>(
                clientIdentifier + "_blockNumber",
                Collections.<String>emptyList(),
                web3jService,
                EthBlockNumber.class);
    }

    @Override
    public Request<?, EthGetBalance> ethGetBalance(
            String address, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                clientIdentifier + "_getBalance",
                Arrays.asList(address, defaultBlockParameter.getValue()),
                web3jService,
                EthGetBalance.class);
    }

    @Override
    public Request<?, EthGetStorageAt> ethGetStorageAt(
            String address, BigInteger position, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                clientIdentifier + "_getStorageAt",
                Arrays.asList(
                        address,
                        Numeric.encodeQuantity(position),
                        defaultBlockParameter.getValue()),
                web3jService,
                EthGetStorageAt.class);
    }

    @Override
    public Request<?, EthGetTransactionCount> ethGetTransactionCount(
            String address, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                clientIdentifier + "_getTransactionCount",
                Arrays.asList(address, defaultBlockParameter.getValue()),
                web3jService,
                EthGetTransactionCount.class);
    }

    @Override
    public Request<?, EthGetBlockTransactionCountByHash> ethGetBlockTransactionCountByHash(
            String blockHash) {
        return new Request<>(
                clientIdentifier + "_getBlockTransactionCountByHash",
                Arrays.asList(blockHash),
                web3jService,
                EthGetBlockTransactionCountByHash.class);
    }

    @Override
    public Request<?, EthGetBlockTransactionCountByNumber> ethGetBlockTransactionCountByNumber(
            DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                clientIdentifier + "_getBlockTransactionCountByNumber",
                Arrays.asList(defaultBlockParameter.getValue()),
                web3jService,
                EthGetBlockTransactionCountByNumber.class);
    }

    @Override
    public Request<?, EthGetUncleCountByBlockHash> ethGetUncleCountByBlockHash(String blockHash) {
        return new Request<>(
                clientIdentifier + "_getUncleCountByBlockHash",
                Arrays.asList(blockHash),
                web3jService,
                EthGetUncleCountByBlockHash.class);
    }

    @Override
    public Request<?, EthGetUncleCountByBlockNumber> ethGetUncleCountByBlockNumber(
            DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                clientIdentifier + "_getUncleCountByBlockNumber",
                Arrays.asList(defaultBlockParameter.getValue()),
                web3jService,
                EthGetUncleCountByBlockNumber.class);
    }

    @Override
    public Request<?, EthGetCode> ethGetCode(
            String address, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                clientIdentifier + "_getCode",
                Arrays.asList(address, defaultBlockParameter.getValue()),
                web3jService,
                EthGetCode.class);
    }

    @Override
    public Request<?, EthSign> ethSign(String address, String sha3HashOfDataToSign) {
        return new Request<>(
                clientIdentifier + "_sign",
                Arrays.asList(address, sha3HashOfDataToSign),
                web3jService,
                EthSign.class);
    }

    @Override
    public Request<?, EthSendTransaction>
    ethSendTransaction(
            Transaction transaction) {
        return new Request<>(
                clientIdentifier + "_sendTransaction",
                Arrays.asList(transaction),
                web3jService,
                EthSendTransaction.class);
    }

    @Override
    public Request<?, EthSendTransaction>
    ethSendRawTransaction(
            String signedTransactionData) {
        return new Request<>(
                clientIdentifier + "_sendRawTransaction",
                Arrays.asList(signedTransactionData),
                web3jService,
                EthSendTransaction.class);
    }

    @Override
    public Request<?, EthCall> ethCall(
            Transaction transaction, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                clientIdentifier + "_call",
                Arrays.asList(transaction, defaultBlockParameter),
                web3jService,
                EthCall.class);
    }

    @Override
    public Request<?, EthEstimateGas> ethEstimateGas(Transaction transaction) {
        return new Request<>(
                clientIdentifier + "_estimateGas",
                Arrays.asList(transaction),
                web3jService,
                EthEstimateGas.class);
    }

    @Override
    public Request<?, EthBlock> ethGetBlockByHash(
            String blockHash, boolean returnFullTransactionObjects) {
        return new Request<>(
                clientIdentifier + "_getBlockByHash",
                Arrays.asList(
                        blockHash,
                        returnFullTransactionObjects),
                web3jService,
                EthBlock.class);
    }

    @Override
    public Request<?, EthBlock> ethGetBlockByNumber(
            DefaultBlockParameter defaultBlockParameter,
            boolean returnFullTransactionObjects) {
        return new Request<>(
                clientIdentifier + "_getBlockByNumber",
                Arrays.asList(
                        defaultBlockParameter.getValue(),
                        returnFullTransactionObjects),
                web3jService,
                EthBlock.class);
    }

    @Override
    public Request<?, EthTransaction> ethGetTransactionByHash(String transactionHash) {
        return new Request<>(
                clientIdentifier + "_getTransactionByHash",
                Arrays.asList(transactionHash),
                web3jService,
                EthTransaction.class);
    }

    @Override
    public Request<?, EthTransaction> ethGetTransactionByBlockHashAndIndex(
            String blockHash, BigInteger transactionIndex) {
        return new Request<>(
                clientIdentifier + "_getTransactionByBlockHashAndIndex",
                Arrays.asList(
                        blockHash,
                        Numeric.encodeQuantity(transactionIndex)),
                web3jService,
                EthTransaction.class);
    }

    @Override
    public Request<?, EthTransaction> ethGetTransactionByBlockNumberAndIndex(
            DefaultBlockParameter defaultBlockParameter, BigInteger transactionIndex) {
        return new Request<>(
                clientIdentifier + "_getTransactionByBlockNumberAndIndex",
                Arrays.asList(
                        defaultBlockParameter.getValue(),
                        Numeric.encodeQuantity(transactionIndex)),
                web3jService,
                EthTransaction.class);
    }

    @Override
    public Request<?, EthGetTransactionReceipt> ethGetTransactionReceipt(String transactionHash) {
        return new Request<>(
                clientIdentifier + "_getTransactionReceipt",
                Arrays.asList(transactionHash),
                web3jService,
                EthGetTransactionReceipt.class);
    }

    @Override
    public Request<?, EthBlock> ethGetUncleByBlockHashAndIndex(
            String blockHash, BigInteger transactionIndex) {
        return new Request<>(
                clientIdentifier + "_getUncleByBlockHashAndIndex",
                Arrays.asList(
                        blockHash,
                        Numeric.encodeQuantity(transactionIndex)),
                web3jService,
                EthBlock.class);
    }

    @Override
    public Request<?, EthBlock> ethGetUncleByBlockNumberAndIndex(
            DefaultBlockParameter defaultBlockParameter, BigInteger uncleIndex) {
        return new Request<>(
                clientIdentifier + "_getUncleByBlockNumberAndIndex",
                Arrays.asList(
                        defaultBlockParameter.getValue(),
                        Numeric.encodeQuantity(uncleIndex)),
                web3jService,
                EthBlock.class);
    }

    @Override
    public Request<?, EthGetCompilers> ethGetCompilers() {
        return new Request<>(
                clientIdentifier + "_getCompilers",
                Collections.<String>emptyList(),
                web3jService,
                EthGetCompilers.class);
    }

    @Override
    public Request<?, EthCompileLLL> ethCompileLLL(String sourceCode) {
        return new Request<>(
                clientIdentifier + "_compileLLL",
                Arrays.asList(sourceCode),
                web3jService,
                EthCompileLLL.class);
    }

    @Override
    public Request<?, EthCompileSolidity> ethCompileSolidity(String sourceCode) {
        return new Request<>(
                clientIdentifier + "_compileSolidity",
                Arrays.asList(sourceCode),
                web3jService,
                EthCompileSolidity.class);
    }

    @Override
    public Request<?, EthCompileSerpent> ethCompileSerpent(String sourceCode) {
        return new Request<>(
                clientIdentifier + "_compileSerpent",
                Arrays.asList(sourceCode),
                web3jService,
                EthCompileSerpent.class);
    }

    @Override
    public Request<?, EthFilter> ethNewFilter(
            com.xwc1125.chain5j.protocol.core.methods.request.EthFilter ethFilter) {
        return new Request<>(
                clientIdentifier + "_newFilter",
                Arrays.asList(ethFilter),
                web3jService,
                EthFilter.class);
    }

    @Override
    public Request<?, EthFilter> ethNewBlockFilter() {
        return new Request<>(
                clientIdentifier + "_newBlockFilter",
                Collections.<String>emptyList(),
                web3jService,
                EthFilter.class);
    }

    @Override
    public Request<?, EthFilter> ethNewPendingTransactionFilter() {
        return new Request<>(
                clientIdentifier + "_newPendingTransactionFilter",
                Collections.<String>emptyList(),
                web3jService,
                EthFilter.class);
    }

    @Override
    public Request<?, EthUninstallFilter> ethUninstallFilter(BigInteger filterId) {
        return new Request<>(
                clientIdentifier + "_uninstallFilter",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                web3jService,
                EthUninstallFilter.class);
    }

    @Override
    public Request<?, EthLog> ethGetFilterChanges(BigInteger filterId) {
        return new Request<>(
                clientIdentifier + "_getFilterChanges",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                web3jService,
                EthLog.class);
    }

    @Override
    public Request<?, EthLog> ethGetFilterLogs(BigInteger filterId) {
        return new Request<>(
                clientIdentifier + "_getFilterLogs",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                web3jService,
                EthLog.class);
    }

    @Override
    public Request<?, EthLog> ethGetLogs(
            com.xwc1125.chain5j.protocol.core.methods.request.EthFilter ethFilter) {
        return new Request<>(
                clientIdentifier + "_getLogs",
                Arrays.asList(ethFilter),
                web3jService,
                EthLog.class);
    }

    @Override
    public Request<?, EthGetWork> ethGetWork() {
        return new Request<>(
                clientIdentifier + "_getWork",
                Collections.<String>emptyList(),
                web3jService,
                EthGetWork.class);
    }

    @Override
    public Request<?, EthSubmitWork> ethSubmitWork(
            String nonce, String headerPowHash, String mixDigest) {
        return new Request<>(
                clientIdentifier + "_submitWork",
                Arrays.asList(nonce, headerPowHash, mixDigest),
                web3jService,
                EthSubmitWork.class);
    }

    @Override
    public Request<?, EthSubmitHashrate> ethSubmitHashrate(String hashrate, String clientId) {
        return new Request<>(
                clientIdentifier + "_submitHashrate",
                Arrays.asList(hashrate, clientId),
                web3jService,
                EthSubmitHashrate.class);
    }

    @Override
    public Request<?, DbPutString> dbPutString(
            String databaseName, String keyName, String stringToStore) {
        return new Request<>(
                "db_putString",
                Arrays.asList(databaseName, keyName, stringToStore),
                web3jService,
                DbPutString.class);
    }

    @Override
    public Request<?, DbGetString> dbGetString(String databaseName, String keyName) {
        return new Request<>(
                "db_getString",
                Arrays.asList(databaseName, keyName),
                web3jService,
                DbGetString.class);
    }

    @Override
    public Request<?, DbPutHex> dbPutHex(String databaseName, String keyName, String dataToStore) {
        return new Request<>(
                "db_putHex",
                Arrays.asList(databaseName, keyName, dataToStore),
                web3jService,
                DbPutHex.class);
    }

    @Override
    public Request<?, DbGetHex> dbGetHex(String databaseName, String keyName) {
        return new Request<>(
                "db_getHex",
                Arrays.asList(databaseName, keyName),
                web3jService,
                DbGetHex.class);
    }

    @Override
    public Request<?, com.xwc1125.chain5j.protocol.core.methods.response.ShhPost> shhPost(ShhPost shhPost) {
        return new Request<>(
                "shh_post",
                Arrays.asList(shhPost),
                web3jService,
                com.xwc1125.chain5j.protocol.core.methods.response.ShhPost.class);
    }

    @Override
    public Request<?, ShhVersion> shhVersion() {
        return new Request<>(
                "shh_version",
                Collections.<String>emptyList(),
                web3jService,
                ShhVersion.class);
    }

    @Override
    public Request<?, ShhNewIdentity> shhNewIdentity() {
        return new Request<>(
                "shh_newIdentity",
                Collections.<String>emptyList(),
                web3jService,
                ShhNewIdentity.class);
    }

    @Override
    public Request<?, ShhHasIdentity> shhHasIdentity(String identityAddress) {
        return new Request<>(
                "shh_hasIdentity",
                Arrays.asList(identityAddress),
                web3jService,
                ShhHasIdentity.class);
    }

    @Override
    public Request<?, ShhNewGroup> shhNewGroup() {
        return new Request<>(
                "shh_newGroup",
                Collections.<String>emptyList(),
                web3jService,
                ShhNewGroup.class);
    }

    @Override
    public Request<?, ShhAddToGroup> shhAddToGroup(String identityAddress) {
        return new Request<>(
                "shh_addToGroup",
                Arrays.asList(identityAddress),
                web3jService,
                ShhAddToGroup.class);
    }

    @Override
    public Request<?, ShhNewFilter> shhNewFilter(ShhFilter shhFilter) {
        return new Request<>(
                "shh_newFilter",
                Arrays.asList(shhFilter),
                web3jService,
                ShhNewFilter.class);
    }

    @Override
    public Request<?, ShhUninstallFilter> shhUninstallFilter(BigInteger filterId) {
        return new Request<>(
                "shh_uninstallFilter",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                web3jService,
                ShhUninstallFilter.class);
    }

    @Override
    public Request<?, ShhMessages> shhGetFilterChanges(BigInteger filterId) {
        return new Request<>(
                "shh_getFilterChanges",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                web3jService,
                ShhMessages.class);
    }

    @Override
    public Request<?, ShhMessages> shhGetMessages(BigInteger filterId) {
        return new Request<>(
                "shh_getMessages",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                web3jService,
                ShhMessages.class);
    }

    @Override
    public Flowable<NewHeadsNotification> newHeadsNotifications() {
        return web3jService.subscribe(
                new Request<>(
                        clientIdentifier + "_subscribe",
                        Collections.singletonList("newHeads"),
                        web3jService,
                        EthSubscribe.class),
                clientIdentifier + "_unsubscribe",
                NewHeadsNotification.class
        );
    }

    @Override
    public Flowable<LogNotification> logsNotifications(
            List<String> addresses, List<String> topics) {

        Map<String, Object> params = createLogsParams(addresses, topics);

        return web3jService.subscribe(
                new Request<>(
                        clientIdentifier + "_subscribe",
                        Arrays.asList("logs", params),
                        web3jService,
                        EthSubscribe.class),
                clientIdentifier + "_unsubscribe",
                LogNotification.class
        );
    }

    private Map<String, Object> createLogsParams(List<String> addresses, List<String> topics) {
        Map<String, Object> params = new HashMap<>();
        if (!addresses.isEmpty()) {
            params.put("address", addresses);
        }
        if (!topics.isEmpty()) {
            params.put("topics", topics);
        }
        return params;
    }

    @Override
    public Flowable<String> ethBlockHashFlowable() {
        return web3jRx.ethBlockHashFlowable(blockTime);
    }

    @Override
    public Flowable<String> ethPendingTransactionHashFlowable() {
        return web3jRx.ethPendingTransactionHashFlowable(blockTime);
    }

    @Override
    public Flowable<Log> ethLogFlowable(
            com.xwc1125.chain5j.protocol.core.methods.request.EthFilter ethFilter) {
        return web3jRx.ethLogFlowable(ethFilter, blockTime);
    }

    @Override
    public Flowable<com.xwc1125.chain5j.protocol.core.methods.response.Transaction>
    transactionFlowable() {
        return web3jRx.transactionFlowable(blockTime);
    }

    @Override
    public Flowable<com.xwc1125.chain5j.protocol.core.methods.response.Transaction>
    pendingTransactionFlowable() {
        return web3jRx.pendingTransactionFlowable(blockTime);
    }

    @Override
    public Flowable<EthBlock> blockFlowable(boolean fullTransactionObjects) {
        return web3jRx.blockFlowable(fullTransactionObjects, blockTime);
    }

    @Override
    public Flowable<EthBlock> replayPastBlocksFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock,
            boolean fullTransactionObjects) {
        return web3jRx.replayBlocksFlowable(startBlock, endBlock, fullTransactionObjects);
    }

    @Override
    public Flowable<EthBlock> replayPastBlocksFlowable(DefaultBlockParameter startBlock,
                                                       DefaultBlockParameter endBlock,
                                                       boolean fullTransactionObjects,
                                                       boolean ascending) {
        return web3jRx.replayBlocksFlowable(startBlock, endBlock,
                fullTransactionObjects, ascending);
    }

    @Override
    public Flowable<EthBlock> replayPastBlocksFlowable(
            DefaultBlockParameter startBlock, boolean fullTransactionObjects,
            Flowable<EthBlock> onCompleteFlowable) {
        return web3jRx.replayPastBlocksFlowable(
                startBlock, fullTransactionObjects, onCompleteFlowable);
    }

    @Override
    public Flowable<EthBlock> replayPastBlocksFlowable(
            DefaultBlockParameter startBlock, boolean fullTransactionObjects) {
        return web3jRx.replayPastBlocksFlowable(startBlock, fullTransactionObjects);
    }

    @Override
    public Flowable<com.xwc1125.chain5j.protocol.core.methods.response.Transaction>
    replayPastTransactionsFlowable(DefaultBlockParameter startBlock,
                                   DefaultBlockParameter endBlock) {
        return web3jRx.replayTransactionsFlowable(startBlock, endBlock);
    }

    @Override
    public Flowable<com.xwc1125.chain5j.protocol.core.methods.response.Transaction>
    replayPastTransactionsFlowable(DefaultBlockParameter startBlock) {
        return web3jRx.replayPastTransactionsFlowable(startBlock);
    }

    @Override
    public Flowable<EthBlock> replayPastAndFutureBlocksFlowable(
            DefaultBlockParameter startBlock, boolean fullTransactionObjects) {
        return web3jRx.replayPastAndFutureBlocksFlowable(
                startBlock, fullTransactionObjects, blockTime);
    }

    @Override
    public Flowable<com.xwc1125.chain5j.protocol.core.methods.response.Transaction>
    replayPastAndFutureTransactionsFlowable(DefaultBlockParameter startBlock) {
        return web3jRx.replayPastAndFutureTransactionsFlowable(
                startBlock, blockTime);
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
        try {
            web3jService.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close web3j service", e);
        }
    }
}
