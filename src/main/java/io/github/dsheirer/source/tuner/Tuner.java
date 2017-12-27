/*******************************************************************************
 *     SDR Trunk 
 *     Copyright (C) 2014-2016 Dennis Sheirer
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 ******************************************************************************/
package io.github.dsheirer.source.tuner;

import io.github.dsheirer.sample.Broadcaster;
import io.github.dsheirer.sample.Listener;
import io.github.dsheirer.source.ISourceEventProcessor;
import io.github.dsheirer.source.SourceEvent;
import io.github.dsheirer.source.tuner.TunerEvent.Event;
import io.github.dsheirer.source.tuner.manager.AbstractSourceManager;
import io.github.dsheirer.source.tuner.manager.TunerSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tuner - provides tuner channel sources, representing a channel frequency
 */
public abstract class Tuner
{
    private final static Logger mLog = LoggerFactory.getLogger(Tuner.class);

    protected Broadcaster<TunerEvent> mTunerEventBroadcaster = new Broadcaster<>();
    private TunerController mTunerController;
    private String mName;
    private AbstractSourceManager mTunerSourceManager;

    /**
     * Abstract tuner class.
     * @param name of the tuner
     * @param tunerController for the tuner
     */
    public Tuner(String name, TunerController tunerController)
    {
        mName = name;
        mTunerController = tunerController;

        mTunerSourceManager = new TunerSourceManager(mTunerController);

        //Rebroadcast frequency and sample rate change events as tuner events
        mTunerController.addListener(new ISourceEventProcessor()
        {
            @Override
            public void process(SourceEvent event)
            {
                switch(event.getEvent())
                {
                    case NOTIFICATION_FREQUENCY_CHANGE:
                        broadcast(new TunerEvent(Tuner.this, Event.FREQUENCY));
                        break;
                    case NOTIFICATION_SAMPLE_RATE_CHANGE:
                        broadcast(new TunerEvent(Tuner.this, Event.SAMPLE_RATE));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * Source Manager.  Provides access to registering for complex buffer samples and source event notifications.
     */
    public AbstractSourceManager getSourceManager()
    {
        return mTunerSourceManager;
    }

    public TunerController getTunerController()
    {
        return mTunerController;
    }

    public String toString()
    {
        return mName;
    }

    public void dispose()
    {
    }

    public void setName(String name)
    {
        mName = name;
    }

    /**
     * Unique identifier for this tuner, used to lookup a tuner configuration
     * from the settings manager.
     *
     * @return - unique identifier like a serial number, or a usb bus location
     * or ip address and port.  Return some form of unique identification that
     * allows this tuner to be identified from among several of the same types
     * of tuners.
     */
    public abstract String getUniqueID();

    /**
     * @return - tuner class enum entry
     */
    public abstract TunerClass getTunerClass();

    /**
     * @return - tuner type enum entry
     */
    public abstract TunerType getTunerType();

    /**
     * Name of this tuner object
     *
     * @return - string name of this tuner object
     */
    public String getName()
    {
        return mName;
    }

    /**
     * Sample size in bits
     */
    public abstract double getSampleSize();

    /**
     * Registers the listener
     */
    public void addTunerChangeListener(Listener<TunerEvent> listener)
    {
        mTunerEventBroadcaster.addListener(listener);
    }

    /**
     * Removes the registered listener
     */
    public void removeTunerChangeListener(Listener<TunerEvent> listener)
    {
        mTunerEventBroadcaster.removeListener(listener);
    }

    /**
     * Broadcasts the tuner change event
     */
    public void broadcast(TunerEvent tunerEvent)
    {
        mTunerEventBroadcaster.broadcast(tunerEvent);
    }
}