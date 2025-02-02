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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.local.auth.smsotp.provider.Provider;
import org.wso2.carbon.identity.local.auth.smsotp.provider.constant.Constants;
import org.wso2.carbon.identity.local.auth.smsotp.provider.exception.ProviderException;
import org.wso2.carbon.identity.local.auth.smsotp.provider.exception.PublisherException;
import org.wso2.carbon.identity.local.auth.smsotp.provider.http.HTTPPublisher;
import org.wso2.carbon.identity.local.auth.smsotp.provider.model.SMSData;
import org.wso2.carbon.identity.local.auth.smsotp.provider.model.SMSMetadata;
import org.wso2.carbon.identity.local.auth.smsotp.provider.util.ProviderUtil;
import org.wso2.carbon.identity.notification.sender.tenant.config.dto.SMSSenderDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation for the custom SMS provider. This provider is used to send the SMS using the custom SMS gateway.
 * Configuration details are available in {@link SMSSenderDTO}.
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class CustomProvider implements Provider {

    private static final Log LOG = LogFactory.getLog(CustomProvider.class);

    @Override
    public String getName() {
        return "Custom";
    }

    @Override
    public void send(SMSData smsData, SMSSenderDTO smsSenderDTO, String tenantDomain) throws ProviderException {

        if (StringUtils.isBlank(smsData.getToNumber())) {
            throw new ProviderException("To number is null or blank. Cannot send SMS");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Sending SMS to " + ProviderUtil.hashTelephoneNumber(smsData.getToNumber())
                    + " using custom provider");
        }

        SMSMetadata smsMetadata = new SMSMetadata();

        smsMetadata.setKey(smsSenderDTO.getKey());
        smsMetadata.setSecret(smsSenderDTO.getSecret());
        smsMetadata.setSender(smsSenderDTO.getSender());
        smsMetadata.setContentType(smsSenderDTO.getContentType());
        smsMetadata.setTenantDomain(tenantDomain);
        smsMetadata.setHeaders(constructHeaders(smsSenderDTO.getProperties().get(Constants.HTTP_HEADERS)));
        smsMetadata.setHttpMethod(smsSenderDTO.getProperties().get(Constants.HTTP_METHOD));

        // If we have additional payload to be sent to the custom provider, we will append it to the SMS body.
        String additionalPayload = smsSenderDTO.getProperties().get(Constants.HTTP_BODY);
        if (StringUtils.isNotBlank(additionalPayload)) {
            smsData.setSMSBody(smsData.getSMSBody() + "\n\r" + additionalPayload);
        }
        smsData.setFromNumber(smsSenderDTO.getSender());
        smsData.setSmsMetadata(smsMetadata);

        try {
            HTTPPublisher publisher = new HTTPPublisher();
            publisher.publish(smsData, smsSenderDTO.getProviderURL());
        } catch (PublisherException e) {
            throw new ProviderException("Error occurred while publishing the SMS data to the custom provider", e);
        }
    }

    private Map<String, String> constructHeaders(String headers) {

        if (StringUtils.isBlank(headers)) {
            return new HashMap<>();
        }

        String[] headerList = headers.split(",");
        Map<String, String> headerMap = new HashMap<>();

        // From the list of headers, construct the header map by splitting the header name and value using ':'.
        for (String header : headerList) {
            String[] headerArray = header.split(":");
            if (headerArray.length == 2) {
                headerMap.put(headerArray[0], headerArray[1]);
            }
        }
        return headerMap;
    }
}
