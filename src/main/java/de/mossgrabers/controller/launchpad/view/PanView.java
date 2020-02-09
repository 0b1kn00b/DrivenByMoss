// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.launchpad.view;

import de.mossgrabers.controller.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.framework.daw.DAWColor;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.ITrack;


/**
 * 8 panorama faders.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PanView extends AbstractFaderView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PanView (final LaunchpadControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        this.model.getCurrentTrackBank ().getItem (index).setPan (value);
    }


    /** {@inheritDoc} */
    @Override
    protected int smoothFaderValue (final int index, final int row, final int value)
    {
        if (row == 3 || row == 4)
            return 64;
        return super.smoothFaderValue (index, row, value);
    }


    /** {@inheritDoc} */
    @Override
    protected int getFaderValue (final int index)
    {
        return this.model.getCurrentTrackBank ().getItem (index).getPan ();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        for (int i = 0; i < 8; i++)
        {
            final ITrack track = tb.getItem (i);
            final int color = track.doesExist () ? this.colorManager.getColorIndex (DAWColor.getColorIndex (track.getColor ())) : 0;
            if (this.trackColors[i] != color)
            {
                this.trackColors[i] = color;
                this.setupFader (i);
            }
            this.surface.setFaderValue (i, track.getPan ());
        }
    }


    /** {@inheritDoc} */
    @Override
    public void setupFader (final int index)
    {
        final ITrack track = this.model.getCurrentTrackBank ().getItem (index);
        final int color = track.doesExist () ? this.colorManager.getColorIndex (DAWColor.getColorIndex (track.getColor ())) : 0;
        this.surface.setupFader (index, color, true);
    }
}