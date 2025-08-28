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
 * DuplicateIdException will be thrown if an device is added to
 * the device registry with an id that is already registered.
 *
 * @author Ben Rosenblum - Initial contribution
 */

public class DuplicateIdException extends Exception {

    private static final long serialVersionUID = -4010587859949508962L;

    public DuplicateIdException(String message) {
        super(message);
    }
}
