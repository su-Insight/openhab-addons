/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.energidataservice.internal;

import static org.openhab.binding.energidataservice.internal.EnergiDataServiceBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * {@link DatahubTariff} maps pricelists from the DatahubPricelist dataset to related channels.
 *
 * @author Jacob Laursen - Initial contribution
 */
@NonNullByDefault
public enum DatahubTariff {
    NET_TARIFF(CHANNEL_NET_TARIFF),
    SYSTEM_TARIFF(CHANNEL_SYSTEM_TARIFF),
    ELECTRICITY_TAX(CHANNEL_ELECTRICITY_TAX),
    TRANSMISSION_NET_TARIFF(CHANNEL_TRANSMISSION_NET_TARIFF);

    String channelId;

    DatahubTariff(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }
}
