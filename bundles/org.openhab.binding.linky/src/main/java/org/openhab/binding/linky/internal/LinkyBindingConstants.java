/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
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
package org.openhab.binding.linky.internal;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link LinkyBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Gaël L'hopital - Initial contribution
 * @author Laurent Arnal - Rewrite addon to use official dataconect API *
 */
@NonNullByDefault
public class LinkyBindingConstants {

    public static final String BINDING_ID = "linky";

    // public final String clientId = "e551937c-5250-48bc-b4a6-2323af68db92";
    // public final String clientSecret = "";

    public static final String clientId = "_88uJnEjEs_IMf4bjGZJV6gGxYga";
    public static final String clientSecret = "6lsPfCmu0fEXuKYy3e0e6w8ydIca";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_API_ENEDIS_BRIDGE = new ThingTypeUID(BINDING_ID, "EnedisBridge");
    public static final ThingTypeUID THING_TYPE_API_MYELECTRICALDATA_BRIDGE = new ThingTypeUID(BINDING_ID,
            "MyElectricalDataBridge");
    public static final ThingTypeUID THING_TYPE_LINKY = new ThingTypeUID(BINDING_ID, "linky");

    public static final Set<ThingTypeUID> SUPPORTED_DEVICE_THING_TYPES_UIDS = Set.of(THING_TYPE_API_ENEDIS_BRIDGE,
            THING_TYPE_API_MYELECTRICALDATA_BRIDGE, THING_TYPE_LINKY);

    // Thing properties
    public static final String PUISSANCE = "puissance";
    public static final String PRM_ID = "prmId";
    public static final String USER_ID = "customerId";

    // List of all Channel id's
    public static final String DAY_MINUS_1 = "daily#day-1";
    public static final String DAY_MINUS_2 = "daily#day-2";
    public static final String DAY_MINUS_3 = "daily#day-3";

    public static final String PEAK_POWER_DAY_MINUS_1 = "daily#maxPower-day-1";
    public static final String PEAK_POWER_TS_DAY_MINUS_1 = "daily#maxPowerTs-day-1";
    public static final String PEAK_POWER_DAY_MINUS_2 = "daily#maxPower-day-2";
    public static final String PEAK_POWER_TS_DAY_MINUS_2 = "daily#maxPowerTs-day-2";
    public static final String PEAK_POWER_DAY_MINUS_3 = "daily#maxPower-day-3";
    public static final String PEAK_POWER_TS_DAY_MINUS_3 = "daily#maxPowerTs-day-3";

    public static final String WEEK_MINUS_0 = "weekly#week-0";
    public static final String WEEK_MINUS_1 = "weekly#week-1";
    public static final String WEEK_MINUS_2 = "weekly#week-2";
    public static final String MONTH_MINUS_0 = "monthly#month-0";
    public static final String MONTH_MINUS_1 = "monthly#month-1";
    public static final String MONTH_MINUS_2 = "monthly#month-2";
    public static final String YEAR_MINUS_0 = "yearly#year-0";
    public static final String YEAR_MINUS_1 = "yearly#year-1";
    public static final String YEAR_MINUS_2 = "yearly#year-2";

    public static final String TEST_SELECT = "main#linkyTestSelect";

    public static final String MAIN_IDENTITY = "main#Identity";

    public static final String MAIN_CONTRACT_SUBSCRIBED_POWER = "main#contractSubscribedPower";
    public static final String MAIN_CONTRACT_LAST_ACTIVATION_DATE = "main#contractLastActivationDate";
    public static final String MAIN_CONTRACT_DISTRIBUTION_TARIFF = "main#contractDistributionTariff";
    public static final String MAIN_CONTRACT_OFF_PEAK_HOURS = "main#contractOffpeakHours";
    public static final String MAIN_CONTRACT_CONTRACT_STATUS = "main#contractStatus";
    public static final String MAIN_CONTRACT_CONTRACT_TYPE = "main#contractType";
    public static final String MAIN_CONTRACT_LAST_DISTRIBUTION_TARIFF_CHANGE_DATE = "main#contractLastDistributionTariffChangeDate";
    public static final String MAIN_CONTRACT_SEGMENT = "main#contractSegment";

    public static final String MAIN_USAGEPOINT_ID = "main#usagePointId";
    public static final String MAIN_USAGEPOINT_STATUS = "main#usagePointStatus";
    public static final String MAIN_USAGEPOINT_METER_TYPE = "main#usagePointMeterType";

    public static final String MAIN_USAGEPOINT_METER_ADDRESS_CITY = "main#usagePointAddressCity";
    public static final String MAIN_USAGEPOINT_METER_ADDRESS_COUNTRY = "main#usagePointAddressCountry";
    public static final String MAIN_USAGEPOINT_METER_ADDRESS_INSEE_CODE = "main#usagePointAddressInseeCode";
    public static final String MAIN_USAGEPOINT_METER_ADDRESS_POSTAL_CODE = "main#usagePointAddressPostalCode";
    public static final String MAIN_USAGEPOINT_METER_ADDRESS_STREET = "main#usagePointAddressStreet";

    public static final String MAIN_CONTACT_MAIL = "main#contactMail";
    public static final String MAIN_CONTACT_PHONE = "main#contactPhone";

    // Authorization related Servlet and resources aliases.
    public static final String LINKY_ALIAS = "/connectlinky";
    public static final String LINKY_IMG_ALIAS = "/img";

    /**
     * Smartthings scopes needed by this binding to work.
     */
    public static final String LINKY_SCOPES = Stream.of("am_application_scope", "default")
            .collect(Collectors.joining(" "));
    // "r:devices:*", "w:devices:*", "x:devices:*", "r:hubs:*",
    // "r:locations:*", "w:locations:*", "x:locations:*", "r:scenes:*", "x:scenes:*", "r:rules:*", "w:rules:*",
    // "r:installedapps", "w:installedapps"

    // List of Linky services related urls, information
    public static final String LINKY_MYELECTRICALDATA_ACCOUNT_URL = "https://www.myelectricaldata.fr/";
    public static final String LINKY_MYELECTRICALDATA_AUTHORIZE_URL = LINKY_MYELECTRICALDATA_ACCOUNT_URL
            + "v1/oauth2/authorize";
    public static final String LINKY_MYELECTRICALDATA_API_TOKEN_URL = LINKY_MYELECTRICALDATA_ACCOUNT_URL + "token";

    public static final String ENEDIS_ACCOUNT_URL_PROD = "https://mon-compte-particulier.enedis.fr/";
    public static final String ENEDIS_AUTHORIZE_URL_PROD = ENEDIS_ACCOUNT_URL_PROD + "dataconnect/v1/oauth2/authorize";
    public static final String ENEDIS_API_TOKEN_URL_PROD = ENEDIS_ACCOUNT_URL_PROD + "oauth2/v3/token";

    public static final String ENEDIS_ACCOUNT_URL_PREPROD = "https://ext.prod-sandbox.api.enedis.fr/";
    public static final String ENEDIS_AUTHORIZE_URL_PREPROD = ENEDIS_ACCOUNT_URL_PREPROD
            + "dataconnect/v1/oauth2/authorize";
    public static final String ENEDIS_API_TOKEN_URL_PREPROD = ENEDIS_ACCOUNT_URL_PREPROD + "oauth2/v3/token";
}
