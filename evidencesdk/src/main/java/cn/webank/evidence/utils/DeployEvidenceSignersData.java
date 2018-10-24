package cn.webank.evidence.utils;

import cn.webank.channel.client.Service;
import cn.webank.evidence.contract.EvidenceSignersData;
import cn.webank.evidence.sdkImpl.BCConstant;
import cn.webank.web3j.abi.datatypes.Address;
import cn.webank.web3j.abi.datatypes.DynamicArray;
import cn.webank.web3j.crypto.Credentials;
import cn.webank.web3j.crypto.ECKeyPair;
import cn.webank.web3j.protocol.Web3j;
import cn.webank.web3j.protocol.channel.ChannelEthereumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.interfaces.ECPrivateKey;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DeployEvidenceSignersData {
    static Logger logger = LoggerFactory.getLogger(DeployEvidenceSignersData.class);

    public static void main(String[] args) throws Exception {
        System.out.println("部署DeployEvidenceSignersData");

        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContextInitSigners.xml");
        Service service = context.getBean(Service.class);
        service.run();
        PublicAddressConf conf = context.getBean(PublicAddressConf.class);
        Thread.sleep(3000);

        System.out.println("开始部署");
        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);
        Web3j web3 = Web3j.build(channelEthereumService);
        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream ksInputStream =  DeployEvidenceSignersData.class.getClassLoader().getResourceAsStream("ectest.jks");
        ks.load(ksInputStream, "123456".toCharArray());
        Key key = ks.getKey("ec", "123456".toCharArray());
        ECKeyPair keyPair = ECKeyPair.create(((ECPrivateKey) key).getS());

        System.out.println("===================================================================");
        Credentials credentials = Credentials.create(keyPair);
        ConcurrentHashMap<String, String> addressConf = conf.getAllPublicAddress();
        ArrayList<Address> arrayList = addressConf.values().stream().map(Address::new).collect(Collectors.toCollection(ArrayList::new));
        DynamicArray<Address> evidenceSigners = new DynamicArray<Address>(arrayList);
        try {
            EvidenceSignersData evidenceSignersData = EvidenceSignersData.deploy(web3, credentials, BCConstant.gasPrice, BCConstant.gasLimit, BCConstant.initialWeiValue,evidenceSigners).get();
            System.out.println("DeployEvidenceSignersData getContractAddress " + evidenceSignersData.getContractAddress());
            DynamicArray<Address> signers = evidenceSignersData.getSigners().get();
            for (int i = 0; i < signers.getValue().size(); i++) {
                System.out.println("DeployEvidenceSignersData array[" + i + "] = " + signers.getValue().get(i));
            }
        } catch (Exception e) {
            System.out.println("error " + e);
        }
    }
}
