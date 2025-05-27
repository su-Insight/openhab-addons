/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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
package org.openhab.binding.evcc.internal.api.dto;

import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * This class represents the result object of the status response (/api/state).
 * This DTO was written for evcc version 0.123.1
 *
 * @author Florian Hotze - Initial contribution
 * @author Luca Arnecke - update to evcc version 0.123.1
 */
public class Result {
    // Data types from https://github.com/evcc-io/evcc/blob/master/api/api.go
    // and from https://docs.evcc.io/docs/reference/configuration/messaging/#msg

    // "auth" is left out because it does not provide any useful information

    @SerializedName("batteryCapacity")
    private float batteryCapacity;

    @SerializedName("batteryConfigured")
    private boolean batteryConfigured;

    @SerializedName("batteryPower")
    private float batteryPower;

    @SerializedName("batterySoc")
    private float batterySoC;

    @SerializedName("batteryDischargeControl")
    private boolean batteryDischargeControl;

    @SerializedName("batteryMode")
    private String batteryMode;

    @SerializedName("gridConfigured")
    private boolean gridConfigured;

    @SerializedName("gridPower")
    private float gridPower;

    @SerializedName("homePower")
    private float homePower;

    @SerializedName("loadpoints")
    private Loadpoint[] loadpoints;

    @SerializedName("prioritySoc")
    private float prioritySoC;

    @SerializedName("bufferSoc")
    private float bufferSoC;

    @SerializedName("bufferStartSoc")
    private float bufferStartSoC;

    @SerializedName("residualPower")
    private float residualPower;

    @SerializedName("pvConfigured")
    private boolean pvConfigured;

    @SerializedName("pvPower")
    private float pvPower;

    @SerializedName("siteTitle")
    private String siteTitle;

    @SerializedName("vehicles")
    private Map<String, Vehicle> vehicles;

    /**
     * @return battery's capacity
     */
    public float getBatteryCapacity() {
        return batteryCapacity;
    }

    /**
     * @return whether battery is configured
     */
    public boolean getBatteryConfigured() {
        return batteryConfigured;
    }

    /**
     * @return battery's power
     */
    public float getBatteryPower() {
        return batteryPower;
    }

    /**
     * @return battery's priority state of charge
     */
    public float getPrioritySoC() {
        return prioritySoC;
    }

    /**
     * @return Battery Buffer SoC
     */
    public float getBufferSoC() {
        return bufferSoC;
    }

    /**
     * @return Battery Buffer Start SoC
     */
    public float getBufferStartSoC() {
        return bufferStartSoC;
    }

    /**
     * @return Grid Residual Power
     */
    public float getResidualPower() {
        return residualPower;
    }

    /**
     * @return battery's state of charge
     */
    public float getBatterySoC() {
        return batterySoC;
    }

    /**
     * @return battery discharge control
     */
    public boolean getBatteryDischargeControl() {
        return batteryDischargeControl;
    }

    /**
     * @return battery mode
     */
    public String getBatteryMode() {
        return batteryMode;
    }

    /**
     * @return whether grid is configured
     */
    public boolean getGridConfigured() {
        return gridConfigured;
    }

    /**
     * @return grid's power
     */
    public float getGridPower() {
        return gridPower;
    }

    /**
     * @return home's power
     */
    public float getHomePower() {
        return homePower;
    }

    /**
     * @return all configured loadpoints
     */
    public Loadpoint[] getLoadpoints() {
        return loadpoints;
    }

    /**
     * @return whether pv is configured
     */
    public boolean getPvConfigured() {
        return pvConfigured;
    }

    /**
     * @return pv's power
     */
    public float getPvPower() {
        return pvPower;
    }

    /**
     * @return site's title/name
     */
    public String getSiteTitle() {
        return siteTitle;
    }

    public Map<String, Vehicle> getVehicles() {
        return vehicles;
    }
}
