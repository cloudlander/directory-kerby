/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.apache.kerby.kerberos.kdc.identitybackend.typeAdapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.util.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;

public class EncryptionKeyAdapter implements JsonSerializer<EncryptionKey>,
        JsonDeserializer<EncryptionKey> {
    private static final Logger LOG = LoggerFactory.getLogger(EncryptionKeyAdapter.class);

    @Override
    public EncryptionKey deserialize(JsonElement jsonElement, Type type,
                                     JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {
        JsonObject jsonObject = (JsonObject) jsonElement;
        EncryptionKey encryptionKey = new EncryptionKey();

        try {
            encryptionKey.decode(HexUtil.hex2bytes(jsonObject.get("key").getAsString()));
        } catch (IOException e) {
            LOG.error("Fail to decode encryption key. " + e);
        }
        encryptionKey.setKvno(jsonObject.get("kvno").getAsInt());
        return encryptionKey;
    }

    @Override
    public JsonElement serialize(EncryptionKey encryptionKey,
                                 Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("kvno", encryptionKey.getKvno());
        try {
            jsonObject.addProperty("key", HexUtil.bytesToHex(KrbCodec.encode(encryptionKey)));
        } catch (KrbException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }
}
