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
package org.openhab.binding.rainsoft.internal.errors;

/**
 * DeviceNotFoundException will be thrown if an device is requested from
 * the device registry with an id that is not registered.
 *
 * @author Ben Rosenblum - Initial contribution
 */

public class DeviceNotFoundException extends Exception {

    private static final long serialVersionUID = -463646377949508962L;

    public DeviceNotFoundException(String message) {
        super(message);
    }
}
