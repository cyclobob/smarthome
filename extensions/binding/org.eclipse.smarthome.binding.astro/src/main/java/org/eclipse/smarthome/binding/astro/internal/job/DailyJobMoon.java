/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.astro.internal.job;

import static com.google.common.base.Preconditions.checkArgument;
import static org.eclipse.smarthome.binding.astro.AstroBindingConstants.*;
import static org.eclipse.smarthome.binding.astro.internal.job.Job.scheduleEvent;

import org.eclipse.smarthome.binding.astro.handler.AstroThingHandler;
import org.eclipse.smarthome.binding.astro.internal.model.Eclipse;
import org.eclipse.smarthome.binding.astro.internal.model.Moon;
import org.eclipse.smarthome.binding.astro.internal.model.MoonPhase;
import org.eclipse.smarthome.binding.astro.internal.model.Planet;

/**
 * Daily Scheduled Jobs For Moon Planet
 *
 * @author Gerhard Riegler - Initial contribution
 * @author Amit Kumar Mondal - Implementation to be compliant with ESH Scheduler
 */
public final class DailyJobMoon implements Job {

    private final String thingUID;
    private final AstroThingHandler handler;

    /**
     * Constructor
     *
     * @param thingUID
     *            the Thing UID
     * @param handler
     *            the {@link AstroThingHandler} instance
     * @throws NullPointerException
     *             if {@code thingUID} or {@code handler} is {@code null}
     */
    public DailyJobMoon(String thingUID, AstroThingHandler handler) {
        checkArgument(thingUID != null, "Thing UID cannot be null");
        checkArgument(handler != null, "AstroThingHandler instance cannot be null");

        this.thingUID = thingUID;
        this.handler = handler;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        handler.publishDailyInfo();
        logger.info("Scheduled Astro event-jobs for thing {}", thingUID);

        Planet planet = handler.getPlanet();
        if (planet == null) {
            logger.error("Planet not instantiated");
            return;
        }
        Moon moon = (Moon) planet;
        scheduleEvent(thingUID, handler, moon.getRise().getStart(), EVENT_START, EVENT_CHANNEL_ID_RISE);
        scheduleEvent(thingUID, handler, moon.getSet().getEnd(), EVENT_END, EVENT_CHANNEL_ID_SET);

        MoonPhase moonPhase = moon.getPhase();
        scheduleEvent(thingUID, handler, moonPhase.getFirstQuarter(), EVENT_PHASE_FIRST_QUARTER,
                EVENT_CHANNEL_ID_MOON_PHASE);
        scheduleEvent(thingUID, handler, moonPhase.getThirdQuarter(), EVENT_PHASE_THIRD_QUARTER,
                EVENT_CHANNEL_ID_MOON_PHASE);
        scheduleEvent(thingUID, handler, moonPhase.getFull(), EVENT_PHASE_FULL, EVENT_CHANNEL_ID_MOON_PHASE);
        scheduleEvent(thingUID, handler, moonPhase.getNew(), EVENT_PHASE_NEW, EVENT_CHANNEL_ID_MOON_PHASE);

        Eclipse eclipse = moon.getEclipse();
        scheduleEvent(thingUID, handler, eclipse.getPartial(), EVENT_ECLIPSE_PARTIAL, EVENT_CHANNEL_ID_ECLIPSE);
        scheduleEvent(thingUID, handler, eclipse.getTotal(), EVENT_ECLIPSE_TOTAL, EVENT_CHANNEL_ID_ECLIPSE);

        scheduleEvent(thingUID, handler, moon.getPerigee().getDate(), EVENT_PERIGEE, EVENT_CHANNEL_ID_PERIGEE);
        scheduleEvent(thingUID, handler, moon.getApogee().getDate(), EVENT_APOGEE, EVENT_CHANNEL_ID_APOGEE);
    }

    /** {@inheritDoc} */
    @Override
    public String getThingUID() {
        return thingUID;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (thingUID == null ? 0 : thingUID.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DailyJobMoon other = (DailyJobMoon) obj;
        if (thingUID == null) {
            if (other.thingUID != null) {
                return false;
            }
        } else if (!thingUID.equals(other.thingUID)) {
            return false;
        }
        return true;
    }

}