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
package org.openhab.binding.icalendar.internal.handler;

import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_CURRENT_EVENT_COMMENT;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_CURRENT_EVENT_CONTACT;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_CURRENT_EVENT_DESCRIPTION;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_CURRENT_EVENT_END;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_CURRENT_EVENT_LOCATION;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_CURRENT_EVENT_PRESENT;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_CURRENT_EVENT_START;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_CURRENT_EVENT_SUMMARY;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_NEXT_EVENT_COMMENT;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_NEXT_EVENT_CONTACT;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_NEXT_EVENT_DESCRIPTION;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_NEXT_EVENT_END;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_NEXT_EVENT_LOCATION;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_NEXT_EVENT_START;
import static org.openhab.binding.icalendar.internal.ICalendarBindingConstants.CHANNEL_NEXT_EVENT_SUMMARY;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.icalendar.internal.config.LiveEventConfiguration;
import org.openhab.binding.icalendar.internal.handler.PullJob.CalendarUpdateListener;
import org.openhab.binding.icalendar.internal.logic.AbstractPresentableCalendar;
import org.openhab.binding.icalendar.internal.logic.Event;
import org.openhab.binding.icalendar.internal.logic.EventTextFilter;
import org.openhab.core.i18n.TimeZoneProvider;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.util.ThingHandlerHelper;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link LiveEventHandler} shows the current and next event from a calendar.
 *
 * @author Michael Wodniok - Initial Contribution, mostly a copy of the bridge {@link ICalendarHandler}.
 */
@NonNullByDefault
public class LiveEventHandler extends BaseThingHandler implements CalendarUpdateListener {

    private @Nullable LiveEventConfiguration configuration;
    private final Logger logger = LoggerFactory.getLogger(LiveEventHandler.class);
    private final TimeZoneProvider tzProvider;
    private @Nullable ScheduledFuture<?> updateFuture;
    private @Nullable AbstractPresentableCalendar calendar;

    public LiveEventHandler(Thing thing, TimeZoneProvider tzProvider) {
        super(thing);
        this.tzProvider = tzProvider;
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        if (bridgeStatusInfo.getStatus() == ThingStatus.OFFLINE) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
        } else if (bridgeStatusInfo.getStatus() == ThingStatus.ONLINE) {
            updateStates();
        } else {
            updateStatus(ThingStatus.UNKNOWN);
        }
    }

    @Override
    public void dispose() {
        final ScheduledFuture<?> currentUpdateFuture = updateFuture;
        if (currentUpdateFuture != null) {
            currentUpdateFuture.cancel(true);
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            updateStates();
        }
    }

    @Override
    public void initialize() {
        final Bridge iCalendarBridge = getBridge();
        if (iCalendarBridge == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "This thing requires a bridge configured to work.");
            return;
        }

        final LiveEventConfiguration config = getConfigAs(LiveEventConfiguration.class);
        if (config.offset == null) {
            config.offset = new BigDecimal(0);
        }
        if (config.textEventField != null && config.textValueType == null) {
            logger.warn("Event field is set but not match type. This will ignore the filter.");
        }
        configuration = config;

        if (iCalendarBridge.getStatus() != ThingStatus.ONLINE) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            return;
        }
        updateStatus(ThingStatus.UNKNOWN);
        retrieveCalendar();
        updateStates();
        rescheduleCalendarStateUpdate();
    }

    @Override
    public void onCalendarUpdated() {
        retrieveCalendar();
        updateStates();
        rescheduleCalendarStateUpdate();
    }

    private void retrieveCalendar() {
        if (!ThingHandlerHelper.isHandlerInitialized(this)) {
            logger.debug("Ignoring call for updating states as this instance is not initialized yet.");
            return;
        }
        final Bridge iCalendarBridge = getBridge();
        if (iCalendarBridge == null) {
            logger.debug("Bridge not instantiated!");
            return;
        }
        final ICalendarHandler iCalendarHandler = (ICalendarHandler) iCalendarBridge.getHandler();
        if (iCalendarHandler == null) {
            logger.debug("ICalendarHandler not instantiated!");
            return;
        }
        this.calendar = iCalendarHandler.getRuntimeCalendar();
    }

    /**
     * Updates all states and channels. Reschedules an update if no error occurs.
     */
    private void updateStates() {
        if (!ThingHandlerHelper.isHandlerInitialized(this)) {
            logger.debug("Ignoring call for updating states as this instance is not initialized yet.");
            return;
        }
        final Bridge iCalendarBridge = getBridge();
        if (iCalendarBridge == null) {
            logger.debug("Bridge not instantiated!");
            return;
        }
        final ICalendarHandler iCalendarHandler = (ICalendarHandler) iCalendarBridge.getHandler();
        if (iCalendarHandler == null) {
            logger.debug("ICalendarHandler not instantiated!");
            return;
        }
        final LiveEventConfiguration config = configuration;
        if (config == null) {
            logger.debug("Configuration not instantiated!");
            return;
        }
        final BigDecimal offsetPre = config.offset;
        if (offsetPre == null) {
            logger.debug("Configuration not valid!");
            return;
        }
        final long offset = offsetPre.longValue();
        final AbstractPresentableCalendar cal = this.calendar;
        if (cal != null) {
            updateStatus(ThingStatus.ONLINE);

            final Instant reference = Instant.now().plus(offset, ChronoUnit.SECONDS);
            EventTextFilter filter = null;

            try {
                final String textFilterValue = config.textEventValue;
                if (textFilterValue != null) {
                    final String textEventField = config.textEventField;
                    final String textValueType = config.textValueType;
                    if (textEventField == null || textValueType == null) {
                        throw new ConfigBrokenException("Text filter settings are not set properly.");
                    }
                    try {
                        final EventTextFilter.Field textFilterField = EventTextFilter.Field.valueOf(textEventField);
                        final EventTextFilter.Type textFilterType = EventTextFilter.Type.valueOf(textValueType);

                        filter = new EventTextFilter(textFilterField, textFilterValue, textFilterType);
                    } catch (IllegalArgumentException e2) {
                        throw new ConfigBrokenException("textEventField or textValueType are not set properly.");
                    }
                }
            } catch (ConfigBrokenException e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
                return;
            }

            final Event currentEvent = cal.getCurrentEvent(reference, filter);
            if (currentEvent != null) {
                updateState(CHANNEL_CURRENT_EVENT_PRESENT, OnOffType.ON);
                updateState(CHANNEL_CURRENT_EVENT_SUMMARY, new StringType(currentEvent.title));
                updateState(CHANNEL_CURRENT_EVENT_START,
                        new DateTimeType(currentEvent.start.atZone(tzProvider.getTimeZone())));
                updateState(CHANNEL_CURRENT_EVENT_END,
                        new DateTimeType(currentEvent.end.atZone(tzProvider.getTimeZone())));
                updateState(CHANNEL_CURRENT_EVENT_DESCRIPTION, new StringType(currentEvent.description));
                updateState(CHANNEL_CURRENT_EVENT_LOCATION, new StringType(currentEvent.location));
                updateState(CHANNEL_CURRENT_EVENT_COMMENT, new StringType(currentEvent.comment));
                updateState(CHANNEL_CURRENT_EVENT_CONTACT, new StringType(currentEvent.contact));
            } else {
                updateState(CHANNEL_CURRENT_EVENT_PRESENT, OnOffType.OFF);
                updateState(CHANNEL_CURRENT_EVENT_SUMMARY, UnDefType.UNDEF);
                updateState(CHANNEL_CURRENT_EVENT_START, UnDefType.UNDEF);
                updateState(CHANNEL_CURRENT_EVENT_END, UnDefType.UNDEF);
                updateState(CHANNEL_CURRENT_EVENT_DESCRIPTION, UnDefType.UNDEF);
                updateState(CHANNEL_CURRENT_EVENT_LOCATION, UnDefType.UNDEF);
                updateState(CHANNEL_CURRENT_EVENT_COMMENT, UnDefType.UNDEF);
                updateState(CHANNEL_CURRENT_EVENT_CONTACT, UnDefType.UNDEF);
            }

            final Event nextEvent = cal.getNextEvent(reference, filter);
            if (nextEvent != null) {
                updateState(CHANNEL_NEXT_EVENT_SUMMARY, new StringType(nextEvent.title));
                updateState(CHANNEL_NEXT_EVENT_START,
                        new DateTimeType(nextEvent.start.atZone(tzProvider.getTimeZone())));
                updateState(CHANNEL_NEXT_EVENT_END, new DateTimeType(nextEvent.end.atZone(tzProvider.getTimeZone())));
                updateState(CHANNEL_NEXT_EVENT_DESCRIPTION, new StringType(nextEvent.description));
                updateState(CHANNEL_NEXT_EVENT_LOCATION, new StringType(nextEvent.location));
                updateState(CHANNEL_NEXT_EVENT_COMMENT, new StringType(nextEvent.comment));
                updateState(CHANNEL_NEXT_EVENT_CONTACT, new StringType(nextEvent.contact));
            } else {
                updateState(CHANNEL_NEXT_EVENT_SUMMARY, UnDefType.UNDEF);
                updateState(CHANNEL_NEXT_EVENT_START, UnDefType.UNDEF);
                updateState(CHANNEL_NEXT_EVENT_END, UnDefType.UNDEF);
                updateState(CHANNEL_NEXT_EVENT_DESCRIPTION, UnDefType.UNDEF);
                updateState(CHANNEL_NEXT_EVENT_LOCATION, UnDefType.UNDEF);
                updateState(CHANNEL_NEXT_EVENT_COMMENT, UnDefType.UNDEF);
                updateState(CHANNEL_NEXT_EVENT_CONTACT, UnDefType.UNDEF);
            }
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Calendar has not been retrieved yet.");
        }
    }

    /**
     * Reschedules the next update of the states.
     */
    private void rescheduleCalendarStateUpdate() {
        final ScheduledFuture<?> currentUpdateJobFuture = updateFuture;
        if (currentUpdateJobFuture != null) {
            if (!(currentUpdateJobFuture.isCancelled() || currentUpdateJobFuture.isDone())) {
                currentUpdateJobFuture.cancel(true);
            }
            updateFuture = null;
        }
        final AbstractPresentableCalendar currentCalendar = calendar;
        if (currentCalendar == null) {
            return;
        }
        final LiveEventConfiguration config = configuration;
        if (config == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Something is broken, the configuration is not available.");
            return;
        }
        final BigDecimal currentOffsetPre = config.offset;
        if (currentOffsetPre == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Something is broken, the configuration is not invalid.");
            return;
        }
        final long offset = currentOffsetPre.longValue();
        final Instant now = Instant.now().plus(offset, ChronoUnit.SECONDS);
        Instant nextRegularUpdate = null;
        if (currentCalendar.isEventPresent(now)) {
            final Event currentEvent = currentCalendar.getCurrentEvent(now);
            if (currentEvent == null) {
                logger.debug(
                        "Could not schedule next update of states, due to unexpected behaviour of calendar implementation.");
                return;
            }
            nextRegularUpdate = currentEvent.end;
        }

        final Event nextEvent = currentCalendar.getNextEvent(now);
        if (nextEvent != null) {
            if (nextRegularUpdate == null || nextEvent.start.isBefore(nextRegularUpdate)) {
                nextRegularUpdate = nextEvent.start;
            }
        }

        if (nextRegularUpdate != null) {
            updateFuture = scheduler.schedule(() -> {
                LiveEventHandler.this.updateStates();
                LiveEventHandler.this.rescheduleCalendarStateUpdate();
            }, nextRegularUpdate.getEpochSecond() - now.getEpochSecond(), TimeUnit.SECONDS);
            logger.debug("Scheduled update in {} seconds", nextRegularUpdate.getEpochSecond() - now.getEpochSecond());
        } else {
            logger.debug("No");
        }
    }
}
