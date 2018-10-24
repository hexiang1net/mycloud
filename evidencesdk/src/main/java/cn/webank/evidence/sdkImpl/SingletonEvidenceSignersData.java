package cn.webank.evidence.sdkImpl;

import cn.webank.evidence.contract.EvidenceSignersData;
import cn.webank.web3j.crypto.Credentials;
import cn.webank.web3j.protocol.Web3j;
public class SingletonEvidenceSignersData {
    private static SingletonEvidenceSignersData EVIDENCE_SIGNERS_DATA = null;
    private SingletonEvidenceSignersData(String contractAddress, Web3j web3j, Credentials credentials) {
        evidenceSignersData = EvidenceSignersData.load(contractAddress,web3j, credentials, BCConstant.gasPrice, BCConstant.gasLimit);
    }
    public static void createInstance(String contractAddress, Web3j web3j, Credentials credentials) {
        if (EVIDENCE_SIGNERS_DATA == null) {
            synchronized (SingletonEvidenceSignersData.class) {
                if (EVIDENCE_SIGNERS_DATA == null) {
                    EVIDENCE_SIGNERS_DATA = new SingletonEvidenceSignersData(contractAddress, web3j, credentials);
                }
            }
        }
    }
    public static SingletonEvidenceSignersData getInstance() {
        if (EVIDENCE_SIGNERS_DATA == null) {
            throw new NullPointerException("getInstance() is Null, please call SingletonEvidenceSignersData.createInstance(context) first!");
        }
        return EVIDENCE_SIGNERS_DATA;
    }

    private EvidenceSignersData evidenceSignersData;

    public EvidenceSignersData getEvidenceSignersData() {
        return evidenceSignersData;
    }
}