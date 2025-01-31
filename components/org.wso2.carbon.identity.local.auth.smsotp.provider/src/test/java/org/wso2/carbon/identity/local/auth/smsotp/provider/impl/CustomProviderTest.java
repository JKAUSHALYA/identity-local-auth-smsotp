/*
 * Copyright (c) 2023, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.local.auth.smsotp.provider.impl;

import io.jsonwebtoken.lang.Assert;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.local.auth.smsotp.provider.exception.ProviderException;
import org.wso2.carbon.identity.local.auth.smsotp.provider.exception.PublisherException;
import org.wso2.carbon.identity.local.auth.smsotp.provider.model.SMSData;
import org.wso2.carbon.identity.local.auth.smsotp.provider.model.SMSMetadata;
import org.wso2.carbon.identity.notification.sender.tenant.config.dto.SMSSenderDTO;

import static org.mockito.Mockito.when;

public class CustomProviderTest {

    private CustomProvider customProvider;

    @Mock
    private SMSSenderDTO smsSenderDTO = Mockito.mock(SMSSenderDTO.class);

    @BeforeTest
    public void createNewObject() {
        customProvider = new CustomProvider();
    }

    @Test
    public void testGetName() {
        String name = customProvider.getName();
        Assert.notNull(name);
    }

    @Test(expectedExceptions = ProviderException.class)
    public void testInitNotInit() throws ProviderException {

        SMSData smsData = new SMSData();
        SMSMetadata smsMetadata = new SMSMetadata();
        smsData.setToNumber("1234567890");

        smsData.setSmsMetadata(smsMetadata);
        customProvider.send(smsData, smsSenderDTO, "carbon.super");
    }

    @Test(expectedExceptions = {ProviderException.class})
    public void testNullTelephoneNumberTest() throws ProviderException {

        SMSData smsData = new SMSData();

        SMSMetadata smsMetadata = new SMSMetadata();
        smsData.setSmsMetadata(smsMetadata);

        customProvider.send(smsData, smsSenderDTO, "carbon.super");
    }

    @Test(expectedExceptions = {PublisherException.class, ProviderException.class})
    public void testInitSuccess() throws ProviderException {

        when(smsSenderDTO.getProviderURL()).thenReturn("https://localhost:8888");
        when(smsSenderDTO.getKey()).thenReturn("key");
        when(smsSenderDTO.getSecret()).thenReturn("secret");
        when(smsSenderDTO.getSender()).thenReturn("sender");
        when(smsSenderDTO.getContentType()).thenReturn("contentType");

        SMSData smsData = new SMSData();
        smsData.setToNumber("1234567890");

        SMSMetadata smsMetadata = new SMSMetadata();
        smsData.setSmsMetadata(smsMetadata);

        customProvider.send(smsData, smsSenderDTO, "carbon.super");
    }

    @Test(expectedExceptions = {PublisherException.class, ProviderException.class})
    public void testSend() throws ProviderException {

        when(smsSenderDTO.getProviderURL()).thenReturn("https://localhost:8888");
        when(smsSenderDTO.getKey()).thenReturn("key");
        when(smsSenderDTO.getSecret()).thenReturn("secret");
        when(smsSenderDTO.getSender()).thenReturn("sender");
        when(smsSenderDTO.getContentType()).thenReturn("contentType");

        SMSData smsData = new SMSData();
        smsData.setToNumber("1234567890");

        SMSMetadata smsMetadata = new SMSMetadata();
        smsData.setSmsMetadata(smsMetadata);

        customProvider.send(smsData, smsSenderDTO, "carbon.super");
    }
}
