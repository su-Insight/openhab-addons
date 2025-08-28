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
package org.openhab.binding.icalendar.internal.config;

import java.math.BigDecimal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The LiveEventConfiguration holds configuration for the Live Event Item Type.
 *
 * @author Michael Wodniok - Initial contribution
 */
@NonNullByDefault
public class LiveEventConfiguration {
    @Nullable
    public BigDecimal offset;
    @Nullable
    public String textEventField;
    @Nullable
    public String textEventValue;
    @Nullable
    public String textValueType;
}
