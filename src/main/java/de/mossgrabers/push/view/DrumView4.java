// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.view;

import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.push.controller.PushColors;
import de.mossgrabers.push.controller.PushControlSurface;


/**
 * The Drum 4 view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DrumView4 extends DrumViewBase
{
    private static final int NUM_DISPLAY_COLS = 16;


    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public DrumView4 (final PushControlSurface surface, final IModel model)
    {
        super (Views.VIEW_NAME_DRUM4, surface, model, 2, 0);

        this.soundOffset = 0;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (!this.model.canSelectedTrackHoldNotes () || velocity == 0)
            return;

        final int index = note - 36;
        final int x = index % 8;
        final int y = index / 8;

        final int sound = y % 4 + this.soundOffset;
        final int col = 8 * (1 - y / 4) + x;

        this.clip.toggleStep (col, this.offsetY + this.selectedPad + sound, this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        if (!this.model.canSelectedTrackHoldNotes ())
        {
            this.surface.getPadGrid ().turnOff ();
            return;
        }

        // Clip length/loop area
        final int step = this.clip.getCurrentStep ();

        // Paint the sequencer steps
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int blueHi = isPush2 ? PushColors.PUSH2_COLOR2_BLUE_HI : PushColors.PUSH1_COLOR2_BLUE_HI;
        final int greenLo = isPush2 ? PushColors.PUSH2_COLOR2_GREEN_LO : PushColors.PUSH1_COLOR2_GREEN_LO;
        final int greenHi = isPush2 ? PushColors.PUSH2_COLOR2_GREEN_HI : PushColors.PUSH1_COLOR2_GREEN_HI;
        final int off = isPush2 ? PushColors.PUSH2_COLOR2_BLACK : PushColors.PUSH1_COLOR2_BLACK;
        final int hiStep = this.isInXRange (step) ? step % DrumView4.NUM_DISPLAY_COLS : -1;
        for (int sound = 0; sound < 4; sound++)
        {
            for (int col = 0; col < DrumView4.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = this.clip.getStep (col, this.offsetY + this.selectedPad + sound + this.soundOffset);
                final boolean hilite = col == hiStep;
                final int x = col % 8;
                int y = col / 8;
                if (col < 8)
                    y += 5;
                y += sound;
                this.surface.getPadGrid ().lightEx (x, 8 - y, isSet > 0 ? hilite ? greenLo : blueHi : hilite ? greenHi : off, -1, false);
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.surface.setKeyTranslationTable (this.scales.translateMatrixToGrid (Scales.getEmptyMatrix ()));
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, ColorManager.BUTTON_STATE_ON);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, ColorManager.BUTTON_STATE_ON);
    }


    /** {@inheritDoc} */
    @Override
    protected void onLowerScene (final int index)
    {
        // 7, 6, 5, 4
        this.soundOffset = 4 * (7 - index);
        this.surface.getDisplay ().notify ("Offset: " + this.soundOffset);
    }


    /** {@inheritDoc} */
    @Override
    protected void updateLowerSceneButtons ()
    {
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int yellow = isPush2 ? PushColors.PUSH2_COLOR_SCENE_YELLOW : PushColors.PUSH1_COLOR_SCENE_YELLOW;
        final int green = isPush2 ? PushColors.PUSH2_COLOR_SCENE_GREEN : PushColors.PUSH1_COLOR_SCENE_GREEN;
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE1, this.soundOffset == 0 ? yellow : green);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE2, this.soundOffset == 4 ? yellow : green);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE3, this.soundOffset == 8 ? yellow : green);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE4, this.soundOffset == 12 ? yellow : green);
    }
}