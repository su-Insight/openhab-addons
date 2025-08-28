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
package org.openhab.binding.romyrobot.internal.api;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.openhab.binding.romyrobot.internal.RomyRobotConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The {@link RomyApiV6} interface defines the functions which are
 * controllable on the Romy API interface Version 6.
 *
 * @author Bernhard Kreuz - Initial contribution
 */
@NonNullByDefault
public class RomyApiV6 implements RomyApi {

    private String hostname;
    private RomyRobotConfiguration config;
    protected HttpRequestSender http;
    private @Nullable String firmwareVersion;
    private @Nullable String name;
    private @Nullable String mode;
    private @Nullable String activePumpVolume;
    private @Nullable String charging;
    private int batteryLevel;
    private @Nullable String powerStatus;
    private String mapsJson = "";
    private int rssi;
    private @Nullable String strategy;
    private @Nullable String suctionMode;
    private @Nullable String selectedMap;

    // that was the newest version when this code was written
    private int protocolVersionMajor = 6;
    private int protocolVersionMinor = 49;

    private HashMap<String, String> availableMaps = new HashMap<String, String>();
    private static final String CMD_GET_ROBOT_ID = "get/robot_id";
    private static final String CMD_GET_STATUS = "get/status";
    private static final String CMD_GET_MAPS = "get/maps";
    private static final String CMD_GET_WIFI_STATUS = "get/wifi_status";
    private static final String CMD_GET_POWER_STATUS = "get/power_status";
    private static final String CMD_GET_PROTOCOL_VERSION = "get/protocol_version";

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public RomyApiV6(final HttpClient httpClient, final RomyRobotConfiguration config) {
        this.config = config;
        if (config.hostname.startsWith("http://") || config.hostname.startsWith("https://")) {
            this.hostname = config.hostname;
        } else {
            this.hostname = "http://" + config.hostname;
        }

        this.http = new HttpRequestSender(httpClient);
    }

    /**
     * Returns the hostname and port formatted URL as a String.
     *
     * @return String representation of the OpenSprinkler API URL.
     */
    protected String getBaseUrl() {
        return hostname + ":" + config.port + "/";
    }

    @Override
    public void refreshID() throws Exception {
        String returnContent = http.sendHttpGet(getBaseUrl() + CMD_GET_ROBOT_ID, null);
        JsonNode jsonNode = new ObjectMapper().readTree(returnContent);
        firmwareVersion = jsonNode.get("firmware").asText();
        if (firmwareVersion == null) {
            logger.error("There was a problem in the HTTP communication: firmware was empty.");
        }
        name = jsonNode.get("name").asText();
    }

    @Override
    public void refreshProtocolVersion() throws Exception {
        String returnContent = http.sendHttpGet(getBaseUrl() + CMD_GET_PROTOCOL_VERSION, null);
        JsonNode jsonNode = new ObjectMapper().readTree(returnContent);
        protocolVersionMajor = jsonNode.get("version_major").intValue();
        protocolVersionMinor = jsonNode.get("version_minor").intValue();
    }

    @Override
    public void refresh() throws Exception {
        String returnContent = http.sendHttpGet(getBaseUrl() + CMD_GET_POWER_STATUS, null);
        powerStatus = new ObjectMapper().readTree(returnContent).get("power_status").asText();

        returnContent = http.sendHttpGet(getBaseUrl() + CMD_GET_STATUS, null);
        JsonNode jsonNode = new ObjectMapper().readTree(returnContent);
        mode = jsonNode.get("mode").asText();
        activePumpVolume = jsonNode.get("active_pump_volume").asText();
        charging = jsonNode.get("charging").asText();
        batteryLevel = jsonNode.get("battery_level").asInt();

        returnContent = http.sendHttpGet(getBaseUrl() + CMD_GET_WIFI_STATUS, null);
        jsonNode = new ObjectMapper().readTree(returnContent);
        rssi = jsonNode.get("rssi").asInt();

        mapsJson = http.sendHttpGet(getBaseUrl() + CMD_GET_MAPS, null);
        parseMaps(mapsJson);
    }

    private void parseMaps(String jsonString) throws JsonMappingException, JsonProcessingException {
        JsonNode node = new ObjectMapper().readTree(jsonString);
        JsonNode maps = node.get("maps");
        if (maps != null && maps.textValue() != null && !maps.textValue().isBlank() && maps.isArray()) {
            availableMaps.clear();
            for (final JsonNode field : maps) {
                String value = field.get("map_meta_data").textValue();
                String key = field.get("map_id").asInt() + "";
                String permanentFlag = field.get("permanent_flag").textValue();
                if ("true".equalsIgnoreCase(permanentFlag)) {
                    availableMaps.put(key, value);
                }
            }
            if (availableMaps.size() == 1 || selectedMap == null) {
                selectedMap = availableMaps.values().iterator().next();
            }
        } else {
            logger.warn("ROMY has no maps yet, please start a explore to create one!");
        }
    }

    @Override
    public @Nullable String getFirmwareVersion() {
        return firmwareVersion;
    }

    @Override
    public @Nullable String getName() {
        return name;
    }

    @Override
    public @Nullable String getModeString() {
        return mode;
    }

    @Override
    public @Nullable String getActivePumpVolume() {
        return activePumpVolume;
    }

    @Override
    public void setActivePumpVolume(String volume) {
        this.activePumpVolume = volume;
    }

    @Override
    public @Nullable String getStrategy() {
        return strategy;
    }

    @Override
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    @Override
    public @Nullable String getSuctionMode() {
        return suctionMode;
    }

    @Override
    public void setSuctionMode(String suctionMode) {
        this.suctionMode = suctionMode;
    }

    @Override
    public int getBatteryLevel() {
        return batteryLevel;
    }

    @Override
    public @Nullable String getChargingStatus() {
        return charging;
    }

    @Override
    public int getRssi() {
        return rssi;
    }

    @Override
    public @Nullable String getPowerStatus() {
        return powerStatus;
    }

    @Override
    public String getAvailableMapsJson() {
        return mapsJson;
    }

    /**
     * This class contains helper methods for communicating HTTP GET and HTTP POST
     * requests.
     *
     * @author Chris Graham - Initial contribution
     * @author Florian Schmidt - Reduce visibility of Http communication to Api
     */
    protected class HttpRequestSender {
        private final HttpClient httpClient;

        public HttpRequestSender(HttpClient httpClient) {
            this.httpClient = httpClient;
        }

        /**
         * Given a URL and a set parameters, send a HTTP GET request to the URL location
         * created by the URL and parameters.
         *
         * @param url The URL to send a GET request to.
         * @param urlParameters List of parameters to use in the URL for the GET
         *            request. Null if no parameters.
         * @return String contents of the response for the GET request.
         * @throws Exception
         */
        public String sendHttpGet(String url, @Nullable String urlParameters) throws Exception {
            String location = null;
            if (urlParameters != null) {
                location = url + "?" + urlParameters;
            } else {
                location = url;
            }

            logger.debug("sendHttpGet location:{}", location);
            ContentResponse response;
            try {
                response = withGeneralProperties(httpClient.newRequest(location))
                        .timeout(config.timeout, TimeUnit.SECONDS).method(HttpMethod.GET).send();
            } catch (Exception e) {
                logger.error("Request to RomyRobot device failed: {}", e.getMessage());
                return "";
            }

            if (response.getStatus() == HttpStatus.FORBIDDEN_403) {
                // forbiden, looks like http interface is locked, try to unlock it
                // ------------------------------------------------------------------
                URL netUrl = new URL(url);
                try {
                    logger.info(
                            "looks like http interface is locked, try to unlock it now with password from config...");
                    String unlock = netUrl.getProtocol() + "://" + netUrl.getHost() + ":" + netUrl.getPort()
                            + "/set/unlock_http?pass=" + config.password;
                    response = withGeneralProperties(httpClient.newRequest(unlock))
                            .timeout(config.timeout, TimeUnit.SECONDS).method(HttpMethod.GET).send();
                } catch (Exception e) {
                    logger.error("Request to unlock RomyRobot device with password {} failed: {}", config.password,
                            e.getMessage());
                }

                // send request again after unlocking
                // -------------------------------------
                try {
                    response = withGeneralProperties(httpClient.newRequest(location))
                            .timeout(config.timeout, TimeUnit.SECONDS).method(HttpMethod.GET).send();
                } catch (Exception e) {
                    logger.error("Request to RomyRobot device failed: {}", e.getMessage());
                }
            }
            if (response.getStatus() != HttpStatus.OK_200) {
                logger.error("Error sending HTTP GET request to {}. Got response code: {}", url, response.getStatus());
            }
            return response.getContentAsString();
        }

        private Request withGeneralProperties(Request request) {
            return request;
        }

        /**
         * Given a URL and a set parameters, send a HTTP POST request to the URL
         * location created by the URL and parameters.
         *
         * @param url The URL to send a POST request to.
         * @param urlParameters List of parameters to use in the URL for the POST
         *            request. Null if no parameters.
         * @return String contents of the response for the POST request.
         * @throws Exception
         */
        public String sendHttpPost(String url, String urlParameters) throws Exception {
            ContentResponse response;
            try {
                response = withGeneralProperties(httpClient.newRequest(url)).timeout(config.timeout, TimeUnit.SECONDS)
                        .method(HttpMethod.POST).content(new StringContentProvider(urlParameters)).send();
            } catch (Exception e) {
                logger.error("Request to RomyRobot device failed: {}", e.getMessage());
                return "";
            }
            if (response.getStatus() != HttpStatus.OK_200) {
                logger.error("Error sending HTTP POST request to {}. Got response code: {}", url, response.getStatus());
            }
            return response.getContentAsString();
        }
    }

    @Override
    public void executeCommand(String command) throws Exception {
        if ("REFRESH".equalsIgnoreCase(command)) {
            return;
        }
        // String query = "/$" + command;
        String query = null;
        List<String> params = new ArrayList<String>();
        if ("clean_start_or_continue".equalsIgnoreCase(command) || "clean_all".equalsIgnoreCase(command)
                || "clean_spot".equalsIgnoreCase(command) || "clean_map".equalsIgnoreCase(command)) {
            if (suctionMode != null && !"REFRESH".equals(suctionMode)) {
                params.add("cleaning_parameter_set=" + suctionMode);
            }
            if (strategy != null && !"REFRESH".equals(strategy)) {
                params.add("cleaning_strategy_mode=" + strategy);
            }
            if (activePumpVolume != null) {
                params.add("pump_volume=" + activePumpVolume);
            }
            if (params.isEmpty()) {
                query = String.join("&", params);
            }
        } else if ("redo_explore".equalsIgnoreCase(command) || "clean_map".equalsIgnoreCase(command)) {
            params.add("map_id" + selectedMap);
        }
        String url = getBaseUrl() + "set/" + command;
        logger.info("executing RomyRobot command: {} at url {}", query, url);
        http.sendHttpGet(url, query);
    }

    @Override
    public HashMap<String, String> getAvailableMaps() {
        return availableMaps;
    }

    @Override
    public int getProtocolVersionMajor() {
        return protocolVersionMajor;
    }

    @Override
    public int getProtocolVersionMinor() {
        return protocolVersionMinor;
    }
}
