package cn.webank.evidence.sdk;
import cn.webank.channel.client.Service;
import cn.webank.web3j.abi.datatypes.Address;
import cn.webank.web3j.abi.datatypes.Utf8String;
import cn.webank.web3j.protocol.Web3j;

import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface EvidenceFace {
    /**
     * 计算特定数据的hash值
     *
     */
    public String sha3(byte[] input);

    /**
     * 本接口会连接存证的json和相关文件的hash算出存证的hash
     *
     */
    public String allSha3(String json, List<String> hashs);

    /**
     *加载私钥
     *
     * @param ecPrivateKey
     */
    public void loadPrivateKey(ECPrivateKey ecPrivateKey);

    /**
     *获取公钥
     *
     */
    public String getPublickey();

    /**
     *设置基础通信的Service类
     */
    public void setService(Service service);

    /**
     *获取基础通信的Service类
     */
    public Service getService();

    /**
     *设置用于访问区块链接口的Web3J类
     */
    public void setWeb3j(Web3j web3j);

    /**
     *获取访问区块链接口的Web3J类
     */
    public Web3j getWebj();

    /**
     *run SDK
     */
    void run();


    /**
     *对特定数据进行签名
     *返回一个签名
     */
    public String signMessage(String evidenceHash);

    /**
     *验证特定数据的签名
     *返回一个公钥
     */
    public String verifySignedMessage(String evidenceHash, String signatureData) throws SignatureException;

    /**
     *根据区块链hash值，获取证据数据
     *
     */
    public EvidenceData getMessagebyHash(String address)throws InterruptedException, ExecutionException;

    /**
     *将特定证据数据的签名发送至区块链
     *
     * @param address 证据地址
     * @param evidenceHash 证据信息
     * @param signatureData 证据签名
     * @return boolean 成功返回true
     */
    public boolean sendSignatureToBlockChain(String address, String evidenceHash, String signatureData)throws InterruptedException, ExecutionException, SignatureException;

    /**
     *设置异步回调
     */
    public void setPushCallback(Callback callback);

    /**
     *此接口WB需要调用，在调用newEvidence之前调用，用来初始化存证合约的工厂合约
     */
    public void createSignersDataInstance(String contractAddress);

    /**
     *新建一个存证
     */
    public Address newEvidence(Utf8String evi, Utf8String info, Utf8String id, String signatureDataString)throws InterruptedException, ExecutionException, SignatureException;

    /**
     *验证证据是否收集全了签名
     */
    public boolean verifyEvidence(EvidenceData data)throws SignatureException;



}
