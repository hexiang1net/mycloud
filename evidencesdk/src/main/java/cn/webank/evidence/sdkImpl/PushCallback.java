package cn.webank.evidence.sdkImpl;

import cn.webank.channel.client.ChannelPushCallback;
import cn.webank.channel.dto.ChannelPush;
import cn.webank.channel.dto.ChannelResponse;

class PushCallback extends ChannelPushCallback {
    @Override
    public void onPush(ChannelPush push) {
        ChannelResponse response = new ChannelResponse();
        try {
            if (evidenceFaceImpl != null)
            {
                evidenceFaceImpl.onPush(push);
            }
            response.setErrorCode(0);
        }catch (Exception e)
        {
            response.setErrorCode(-1);
        }
        response.setContent("receive request seq:" + String.valueOf(push.getMessageID()) + ", content:" + push.getContent());
        push.sendResponse(response);
    }

    public void setSDK(EvidenceFaceImpl evidenceFace) {
        this.evidenceFaceImpl = evidenceFace;
    }

    EvidenceFaceImpl evidenceFaceImpl;
}
