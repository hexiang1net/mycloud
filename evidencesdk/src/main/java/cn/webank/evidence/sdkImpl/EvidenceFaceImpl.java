package cn.webank.evidence.sdkImpl;

import cn.webank.channel.client.Service;
import cn.webank.channel.dto.ChannelPush;
import cn.webank.evidence.contract.Evidence;
import cn.webank.evidence.contract.EvidenceSignersData;
import cn.webank.evidence.sdk.Callback;
import cn.webank.evidence.sdk.EvidenceData;
import cn.webank.evidence.sdk.EvidenceFace;
import cn.webank.evidence.utils.Tools;
import cn.webank.web3j.abi.datatypes.*;
import cn.webank.web3j.abi.datatypes.generated.Bytes32;
import cn.webank.web3j.abi.datatypes.generated.Uint8;
import cn.webank.web3j.crypto.*;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.util.*;
import java.util.concurrent.ExecutionException;

import cn.webank.web3j.crypto.Sign;
import cn.webank.web3j.protocol.Web3j;
import cn.webank.web3j.protocol.core.methods.response.TransactionReceipt;
import cn.webank.web3j.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EvidenceFaceImpl implements EvidenceFace {
    static Logger logger = LoggerFactory.getLogger(EvidenceFaceImpl.class);

    @Override
    public String sha3(byte[] input) throws java.lang.IllegalArgumentException{
        if (input.length <= 0)
        {
            throw new java.lang.IllegalArgumentException();
        }
        byte[] output = Hash.sha3(input);
        return Numeric.toHexString(output,0,output.length,false);
    }

    @Override
    public String allSha3(String json, List<String> hashs) throws java.lang.IllegalArgumentException
    {
        if (json == null || hashs == null)
        {
            throw new java.lang.IllegalArgumentException();
        }
        Collections.sort(hashs, new Comparator<String>() {
            public int compare(String a, String b) {
                return a.compareTo(b);
            }
        });
        Iterator<String> te = hashs.iterator();
        while ( te.hasNext() ){
            json.concat(te.next());
        }

        return sha3(json.getBytes());
    }

    @Override
    public void loadPrivateKey(ECPrivateKey ecPrivateKey) {
        credentials = Credentials.create(ECKeyPair.create((ecPrivateKey).getS()));
    }

    @Override
    public String getPublickey() {
        return credentials.getAddress();
    }

    @Override
    public String signMessage(String evidenceHash) {
        try {
            Sign.SignatureData signatureData = Sign.signMessage(evidenceHash.getBytes(), credentials.getEcKeyPair());
            return Tools.signatureDataToString(signatureData);
        } catch (RuntimeException e) {
            logger.error("signMessage message:{} Exception:{}", evidenceHash, e);
            throw e;
        }
    }

    @Override
    public String verifySignedMessage(String message, String signatureData) throws SignatureException {
        Sign.SignatureData signatureData1 = Tools.stringToSignatureData(signatureData);
        try {
            return "0x" + Keys.getAddress(Sign.signedMessageToKey(message.getBytes(), signatureData1));
        } catch (SignatureException e) {
            logger.error("verifySignedMessage message:{} sign:{} Exception:{}", message, signatureData, e);
            throw e;
        }
    }

    @Override
    public EvidenceData getMessagebyHash(String address) throws InterruptedException, ExecutionException {
        logger.info("getMessagebyHash Address " + address);
        Evidence evidence = Evidence.load(address, web3j, credentials, BCConstant.gasPrice, BCConstant.gasLimit);
        EvidenceData evidenceData = new EvidenceData();
        try {
            List<Type> result2 = evidence.getEvidence().get();
            //证据字段为6个
            if (result2.size() >= 6) {
                evidenceData.setEvidenceHash(((Utf8String) result2.get(0)).getValue());
                evidenceData.setEvidenceInfo(((Utf8String) result2.get(1)).getValue());
                evidenceData.setEvidenceID(((Utf8String) result2.get(2)).getValue());
                List<Uint8> vlist = ((DynamicArray<Uint8>) result2.get(3)).getValue();
                List<Bytes32> rlist = ((DynamicArray<Bytes32>) result2.get(4)).getValue();
                List<Bytes32> slist = ((DynamicArray<Bytes32>) result2.get(5)).getValue();
                ArrayList<String> signatureList = new ArrayList<String>();
                for (int i = 0; i < vlist.size(); i++) {
                    Sign.SignatureData signature = new Sign.SignatureData(
                            ((vlist.get(i).getValue())).byteValue(),
                            (rlist.get(i)).getValue(),
                            (slist.get(i)).getValue());
                    signatureList.add(Tools.signatureDataToString(signature));
                }
                evidenceData.setSignatures(signatureList);
                List<Address> addresses = ((DynamicArray<Address>) result2.get(6)).getValue();
                ArrayList<String> addressesList = new ArrayList<String>();
                for (int i = 0; i < addresses.size(); i++) {
                    String str = addresses.get(i).toString();
                    addressesList.add(str);
                }
                evidenceData.setPublicKeys(addressesList);
            }
        } catch (InterruptedException e) {
            logger.error("getMessagebyHash Address:{} Exception:{}", address, e);
            throw e;
        } catch (ExecutionException e) {
            logger.error("getMessagebyHash Address:{} Exception:{}", address, e);
            throw e;
        }
        return evidenceData;
    }

    @Override
    public boolean sendSignatureToBlockChain(String address, String evidenceHash, String signatureData) throws InterruptedException, ExecutionException,SignatureException{
        logger.info("sendSignatureToBlockChain Address " + address);
        Evidence evidence = Evidence.load(address, web3j, credentials, BCConstant.gasPrice, BCConstant.gasLimit);
        Sign.SignatureData signature = Tools.stringToSignatureData(signatureData);
        try {
            String recoverAddress = verifySignedMessage(evidenceHash,signatureData);
            if(!getPublickey().equals(recoverAddress))
            {
                logger.error("ERROR! sendSignatureToBlockChain recoverAddress:{} getPublicKey:{}",recoverAddress,getPublickey());
                throw new SignatureException();
            }
            TransactionReceipt receipt = evidence.addSignatures(new Uint8(signature.getV()),
                    new Bytes32(signature.getR()),
                    new Bytes32(signature.getS())).get();
            List<Evidence.AddSignaturesEventEventResponse> addList = evidence.getAddSignaturesEventEvents(receipt);
            List<Evidence.AddRepeatSignaturesEventEventResponse> addList2 = evidence.getAddRepeatSignaturesEventEvents(receipt);

            if (addList.size() > 0 || addList2.size() >0)
            {
                return true;
            }
        } catch (InterruptedException e) {
            logger.error("sendSignatureToBlockChain Address:{} evi:{} sign:{} Exception:{}", address, evidenceHash, signatureData, e);
            throw e;
        } catch (ExecutionException e) {
            logger.error("sendSignatureToBlockChain Address:{} evi:{} sign:{} Exception:{}", address, evidenceHash, signatureData, e);
            throw e;
        }

        return false;
    }

    @Override
    public void setPushCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        service.run();
        PushCallback pushCallback = new PushCallback();
        pushCallback.setSDK(this);
        service.setPushCallback(pushCallback);
        try {
            Thread.sleep(3000);//此处为了等待service初始化完
        } catch (InterruptedException e) {
            logger.error("run Exception:{}" + e);
        }
    }


    public void onPush(ChannelPush push) {
        if (callback != null) {

            this.callback.onPush(push.getContent());
        }
    }

    @Override
    public Address newEvidence(Utf8String evi, Utf8String info, Utf8String id,String signatureDataString)
            throws InterruptedException, ExecutionException,SignatureException {
        Sign.SignatureData signatureData = Tools.stringToSignatureData(signatureDataString);
        try {
            String recoverAddress = verifySignedMessage(evi.getValue(),signatureDataString);
            if(!getPublickey().equals(recoverAddress))
            {
                logger.error("ERROR! newEvidence recoverAddress:{} getPublicKey:{}", recoverAddress, getPublickey());
                throw new SignatureException();
            }
            EvidenceSignersData evidenceSignersData = SingletonEvidenceSignersData.getInstance().getEvidenceSignersData();
            TransactionReceipt receipt = evidenceSignersData.newEvidence(evi, info, id, new Uint8(signatureData.getV()), new Bytes32(signatureData.getR()), new Bytes32(signatureData.getS())).get();
            List<EvidenceSignersData.NewEvidenceEventEventResponse> newEvidenceList = evidenceSignersData.getNewEvidenceEventEvents(receipt);
            if (newEvidenceList.size() > 0) {
                return newEvidenceList.get(0).addr;
            } else {
                return null;
            }
        } catch (InterruptedException e) {
            logger.error("newEvidence evi:{} info:{} sign:{} Exception:{}", evi, info, signatureDataString, e);
            throw e;
        } catch (ExecutionException e) {
            logger.error("newEvidence evi:{} info:{} sign{} Exception:{}", evi, info, signatureDataString, e);
            throw e;
        }
    }

    @Override
    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public Web3j getWebj() {return web3j;}

    @Override
    public Service getService() {return service;}

    @Override
    public void setWeb3j(Web3j web3j) {
        this.web3j = web3j;
    }

    @Override
    public boolean verifyEvidence(EvidenceData data) throws SignatureException {
        ArrayList<String> addressList = new ArrayList<>();
        for (String str : data.getSignatures()) {
            try {
                addressList.add(verifySignedMessage(data.getEvidenceHash(), str));
            } catch (SignatureException e) {
                logger.error("verifySignedMessage message:{} sign:{} Exception:{}", data.getEvidenceHash(), str, e);
                throw e;
            }
        }
        for (String addr : data.getPublicKeys()) {
            boolean flag = false;
            for (String str : addressList) {
                if (str.equals(addr)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void  createSignersDataInstance(String contractAddress)
    {
        SingletonEvidenceSignersData.createInstance(contractAddress, web3j, credentials);
    }


    private Credentials credentials;
    private Callback callback;
    private Service service;
    private Web3j web3j;
}
