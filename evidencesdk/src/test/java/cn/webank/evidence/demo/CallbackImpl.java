package cn.webank.evidence.demo;

import cn.webank.evidence.sdk.Callback;
import cn.webank.evidence.sdk.EvidenceData;
import cn.webank.evidence.sdk.EvidenceFace;

/**
 * 实现一个自己的Callback，并Override其中的onPush方法
 */

public class CallbackImpl implements Callback {
    @Override
    public void onPush(String address) {
        try {
            //用证据地址获取证据
            EvidenceData evidenceData =  evidenceFace.getMessagebyHash(address);
            //证据签名
            String signatureData = evidenceFace.signMessage(evidenceData.getEvidenceHash());
            //发送证据上链
            evidenceFace.sendSignatureToBlockChain(address,evidenceData.getEvidenceHash(),signatureData);
        }
        catch (Exception e)
        {


        }
    }

    public void setSDK(EvidenceFace evidenceFace) {
        this.evidenceFace = evidenceFace;
    }

    EvidenceFace evidenceFace;
}
