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
package org.openhab.binding.rainsoft.handler;

import static org.openhab.binding.rainsoft.RainSoftBindingConstants.*;

import org.openhab.binding.rainsoft.internal.RainSoftDeviceRegistry;
import org.openhab.binding.rainsoft.internal.data.RainSoftDevice;
import org.openhab.binding.rainsoft.internal.errors.DeviceNotFoundException;
import org.openhab.binding.rainsoft.internal.errors.IllegalDeviceClassException;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;

/**
 * The {@link RainSoftDeviceHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Ben Rosenblum - Initial contribution
 */

public abstract class RainSoftDeviceHandler extends AbstractRainSoftHandler {

    /**
     * The RainSoftDevice instance linked to this thing.
     */
    protected RainSoftDevice device;

    public RainSoftDeviceHandler(Thing thing) {
        super(thing);
    }

    /**
     * Link the device, and update the device with the status CONFIGURED.
     *
     * @param id the device id
     * @param deviceClass the expected class
     * @throws DeviceNotFoundException when device is not found in the RainSoftDeviceRegistry.
     * @throws IllegalDeviceClassException when the regitered device is of the wrong type.
     */
    protected void linkDevice(String id, Class<?> deviceClass)
            throws DeviceNotFoundException, IllegalDeviceClassException {
        device = RainSoftDeviceRegistry.getInstance().getRainSoftDevice(id);
        if (device.getClass().equals(deviceClass)) {
            device.setRegistrationStatus(RainSoftDeviceRegistry.Status.CONFIGURED);
            device.setRainSoftDeviceHandler(this);
            // thing.setProperty("Description", device.getDescription());
            // thing.setProperty("Kind", device.getKind());
            // thing.setProperty("Device ID", device.getDeviceId());
        } else {
            throw new IllegalDeviceClassException(
                    "Class '" + deviceClass.getName() + "' expected but '" + device.getClass().getName() + "' found.");
        }
    }

    /**
     * Handle generic commands, common to all RainSoft Devices.
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }
}
