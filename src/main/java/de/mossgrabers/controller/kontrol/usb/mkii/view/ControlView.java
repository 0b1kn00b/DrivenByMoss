// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.kontrol.usb.mkii.view;

import de.mossgrabers.controller.kontrol.usb.mkii.Kontrol2Configuration;
import de.mossgrabers.controller.kontrol.usb.mkii.command.trigger.Kontrol2CursorCommand;
import de.mossgrabers.controller.kontrol.usb.mkii.controller.Kontrol2ControlSurface;
import de.mossgrabers.controller.kontrol.usb.mkii.mode.Modes;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.scale.Scale;
import de.mossgrabers.framework.view.AbstractView;
import de.mossgrabers.framework.view.View;


/**
 * The view for controlling the DAW.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ControlView extends AbstractView<Kontrol2ControlSurface, Kontrol2Configuration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ControlView (final Kontrol2ControlSurface surface, final IModel model)
    {
        super ("Control", surface, model);
        this.scales = model.getScales ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        final ModeManager modeManager = this.surface.getModeManager ();
        final boolean isBrowseMode = modeManager.isActiveMode (Modes.MODE_BROWSER);
        final ITransport transport = this.model.getTransport ();
        final IChannelBank currentTrackBank = this.model.getCurrentTrackBank ();
        final ITrack t = currentTrackBank.getSelectedTrack ();
        final Kontrol2Configuration configuration = this.surface.getConfiguration ();

        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_SHIFT, this.surface.isShiftPressed () ? Kontrol2ControlSurface.BUTTON_STATE_HI : Kontrol2ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_SCALE, configuration.isScaleIsActive () ? Kontrol2ControlSurface.BUTTON_STATE_HI : Kontrol2ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_ARP, this.surface.isShiftPressed () && transport.isMetronomeTicksOn () || !this.surface.isShiftPressed () && transport.isMetronomeOn () ? Kontrol2ControlSurface.BUTTON_STATE_HI : Kontrol2ControlSurface.BUTTON_STATE_ON);

        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_LOOP, transport.isLoop () ? Kontrol2ControlSurface.BUTTON_STATE_HI : Kontrol2ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_RWD, this.surface.isPressed (Kontrol2ControlSurface.BUTTON_RWD) ? Kontrol2ControlSurface.BUTTON_STATE_HI : Kontrol2ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_FWD, this.surface.isPressed (Kontrol2ControlSurface.BUTTON_FWD) ? Kontrol2ControlSurface.BUTTON_STATE_HI : Kontrol2ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_PLAY, transport.isPlaying () ? Kontrol2ControlSurface.BUTTON_STATE_HI : Kontrol2ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_REC, transport.isRecording () ? Kontrol2ControlSurface.BUTTON_STATE_HI : Kontrol2ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_STOP, this.surface.isPressed (Kontrol2ControlSurface.BUTTON_STOP) ? Kontrol2ControlSurface.BUTTON_STATE_HI : Kontrol2ControlSurface.BUTTON_STATE_ON);

        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_PAGE_LEFT, Kontrol2ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_PAGE_RIGHT, Kontrol2ControlSurface.BUTTON_STATE_ON);

        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_NAVIGATE_UP, isBrowseMode ? Kontrol2ControlSurface.BUTTON_STATE_OFF : Kontrol2ControlSurface.BUTTON_STATE_ON);
        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_NAVIGATE_DOWN, isBrowseMode ? Kontrol2ControlSurface.BUTTON_STATE_OFF : Kontrol2ControlSurface.BUTTON_STATE_ON);

        final View activeView = this.surface.getViewManager ().getActiveView ();
        if (activeView != null)
            ((Kontrol2CursorCommand) activeView.getTriggerCommand (Commands.COMMAND_ARROW_DOWN)).updateArrows ();

        if (modeManager.isActiveMode (Modes.MODE_TRACK) || modeManager.isActiveMode (Modes.MODE_VOLUME))
        {
            this.surface.updateButton (Kontrol2ControlSurface.BUTTON_BACK, t != null && t.isMute () ? Kontrol2ControlSurface.BUTTON_STATE_HI : Kontrol2ControlSurface.BUTTON_STATE_ON);
            this.surface.updateButton (Kontrol2ControlSurface.BUTTON_ENTER, t != null && t.isSolo () ? Kontrol2ControlSurface.BUTTON_STATE_HI : Kontrol2ControlSurface.BUTTON_STATE_ON);
        }
        else if (isBrowseMode)
        {
            this.surface.updateButton (Kontrol2ControlSurface.BUTTON_BACK, Kontrol2ControlSurface.BUTTON_STATE_ON);
            this.surface.updateButton (Kontrol2ControlSurface.BUTTON_ENTER, Kontrol2ControlSurface.BUTTON_STATE_ON);
        }
        else
        {
            this.surface.updateButton (Kontrol2ControlSurface.BUTTON_BACK, Kontrol2ControlSurface.BUTTON_STATE_OFF);
            this.surface.updateButton (Kontrol2ControlSurface.BUTTON_ENTER, Kontrol2ControlSurface.BUTTON_STATE_OFF);
        }

        this.surface.updateButton (Kontrol2ControlSurface.BUTTON_BROWSE, isBrowseMode ? Kontrol2ControlSurface.BUTTON_STATE_HI : Kontrol2ControlSurface.BUTTON_STATE_ON);

        this.surface.updateButtonLEDs ();

        this.updateKeyLEDs (t == null ? new double []
        {
            0,
            0,
            0
        } : t.getColor ());
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        // Intentionally empty
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        // Intentionally empty
    }


    private void updateKeyLEDs (final double [] color)
    {
        int red = 0;
        int green = 0;
        int blue = 0;

        final boolean isActive = this.surface.getConfiguration ().isScaleIsActive ();
        if (isActive)
        {
            red = (int) Math.round (color[0] * 127);
            green = (int) Math.round (color[1] * 127);
            blue = (int) Math.round (color[2] * 127);
        }

        if (this.scales.isChromatic () || !isActive)
        {
            for (int i = 0; i < 88; i++)
                this.surface.setKeyLED (i, red, green, blue);
        }
        else
        {
            final Scale scale = this.scales.getScale ();
            final int scaleOffset = this.scales.getScaleOffset ();
            for (int i = 0; i < 88; i++)
            {
                final int key = i % 12;
                final boolean inScale = scale.isInScale (key);
                final int brighter = scaleOffset == key ? 10 : 0;
                this.surface.setKeyLED (i, inScale ? Math.min (red + brighter, 127) : 0, inScale ? Math.min (green + brighter, 127) : 0, inScale ? Math.min (blue + brighter, 127) : 0);
            }
        }

        this.surface.updateKeyLEDs ();
    }
}