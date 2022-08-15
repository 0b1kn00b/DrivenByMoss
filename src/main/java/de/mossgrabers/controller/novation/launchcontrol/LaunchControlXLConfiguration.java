// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.novation.launchcontrol;

import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.ISettingsUI;
import de.mossgrabers.framework.controller.valuechanger.IValueChanger;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.midi.ArpeggiatorMode;

import java.util.List;


/**
 * The configuration settings for Launchkey Mini Mk3.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LaunchControlXLConfiguration extends AbstractConfiguration
{
    public enum ToggleMode
    {
        MUTE,
        SOLO,
        REC_ARM
    }


    private ToggleMode buttonFunction = ToggleMode.MUTE;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param valueChanger The value changer
     * @param arpeggiatorModes The available arpeggiator modes
     */
    public LaunchControlXLConfiguration (final IHost host, final IValueChanger valueChanger, final List<ArpeggiatorMode> arpeggiatorModes)
    {
        super (host, valueChanger, arpeggiatorModes);
    }


    /** {@inheritDoc} */
    @Override
    public void init (final ISettingsUI globalSettings, final ISettingsUI documentSettings)
    {
        ///////////////////////////
        // Transport

        this.activateBehaviourOnPauseSetting (globalSettings);
        this.activateRecordButtonSetting (globalSettings);
        this.activateShiftedRecordButtonSetting (globalSettings);
        this.activateNewClipLengthSetting (globalSettings);

        ///////////////////////////
        // Workflow

        this.activateExcludeDeactivatedItemsSetting (globalSettings);
        this.activateIncludeMasterSetting (globalSettings);
        this.activateSelectClipOnLaunchSetting (globalSettings);
    }


    /**
     * @return the buttonFunction
     */
    public ToggleMode getButtonFunction ()
    {
        return this.buttonFunction;
    }


    /**
     * @param buttonFunction the buttonFunction to set
     */
    public void setButtonFunction (final ToggleMode buttonFunction)
    {
        this.buttonFunction = buttonFunction;
    }
}
