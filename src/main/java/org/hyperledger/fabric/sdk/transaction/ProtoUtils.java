/*
 *  Copyright 2016 DTCC, Fujitsu Australia Software Technology, IBM - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 	  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.hyperledger.fabric.sdk.transaction;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import org.hyperledger.fabric.protos.common.Common.ChannelHeader;
import org.hyperledger.fabric.protos.common.Common.HeaderType;
import org.hyperledger.fabric.protos.peer.Chaincode.ChaincodeDeploymentSpec;
import org.hyperledger.fabric.protos.peer.Chaincode.ChaincodeID;
import org.hyperledger.fabric.protos.peer.Chaincode.ChaincodeInput;
import org.hyperledger.fabric.protos.peer.Chaincode.ChaincodeSpec;
import org.hyperledger.fabric.protos.peer.Chaincode.ChaincodeSpec.Type;
import org.hyperledger.fabric.protos.peer.FabricProposal.ChaincodeHeaderExtension;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ProtoUtils {

    /**
     * createChannelHeader create chainHeader
     *
     * @param type
     * @param txID
     * @param chainID
     * @param epoch
     * @param chaincodeHeaderExtension
     * @return
     */
    public static ChannelHeader createChannelHeader(HeaderType type, String txID, String chainID, long epoch, ChaincodeHeaderExtension chaincodeHeaderExtension) {

        ChannelHeader.Builder ret = ChannelHeader.newBuilder()
                .setType(type.getNumber())
                .setVersion(1)
                .setTxId(txID)
                .setChannelId(chainID)
                .setEpoch(epoch);
        if (null != chaincodeHeaderExtension) {
            ret.setExtension(chaincodeHeaderExtension.toByteString());
        }

        return ret.build();

    }

    public static ChaincodeDeploymentSpec createDeploymentSpec(Type ccType, String name, String chaincodePath,
                                                               String chainCodeVersion, List<String> args,
                                                               byte[] codePackage) {


        ChaincodeID.Builder chaincodeIDBuilder = ChaincodeID.newBuilder().setName(name).setVersion(chainCodeVersion);
        if (chaincodePath != null) {
            chaincodeIDBuilder = chaincodeIDBuilder.setPath(chaincodePath);
        }

        ChaincodeID chaincodeID = chaincodeIDBuilder.build();

        // build chaincodeInput
        List<ByteString> argList = new ArrayList<>(args == null ? 0 : args.size());
        if (args != null && args.size() != 0) {

            for (String arg : args) {
                argList.add(ByteString.copyFrom(arg.getBytes(UTF_8)));
            }

        }

        //  argList.add(ByteString.copyFrom("init", StandardCharsets.UTF_8));


        ChaincodeInput chaincodeInput = ChaincodeInput.newBuilder().addAllArgs(argList).build();

        // Construct the ChaincodeSpec
        ChaincodeSpec chaincodeSpec = ChaincodeSpec.newBuilder().setType(ccType).setChaincodeId(chaincodeID)
                .setInput(chaincodeInput)
                .build();


        ChaincodeDeploymentSpec.Builder chaincodeDeploymentSpecBuilder = ChaincodeDeploymentSpec
                .newBuilder().setChaincodeSpec(chaincodeSpec) //.setEffectiveDate(context.getFabricTimestamp())
                .setExecEnv(ChaincodeDeploymentSpec.ExecutionEnvironment.DOCKER);


        if (codePackage != null) {
            chaincodeDeploymentSpecBuilder.setCodePackage(ByteString.copyFrom(codePackage));

        }

        return chaincodeDeploymentSpecBuilder.build();

    }

    public static ChaincodeSpec createChainCodeSpec(String name, ChaincodeSpec.Type ccType, Object... args) {

        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(name).build();


        List<ByteString> argList = new ArrayList<>(args.length);

        for (Object arg : args) {
            if (arg instanceof String) {
                arg = ByteString.copyFrom(((String) arg).getBytes(UTF_8));
            } else if (arg instanceof byte[]) {
                arg = ByteString.copyFrom((byte[]) arg);
            }
            argList.add((ByteString) arg);
        }

        ChaincodeInput chaincodeInput = ChaincodeInput.newBuilder()
                .addAllArgs(argList)
                .build();

        return ChaincodeSpec.newBuilder().setType(ccType).setChaincodeId(chaincodeID)
                .setInput(chaincodeInput)
                .build();

    }
}
