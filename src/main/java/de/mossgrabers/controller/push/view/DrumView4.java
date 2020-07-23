// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2020
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.view;

import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.NoteMode;
import de.mossgrabers.framework.controller.ButtonID;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.controller.hardware.IHwButton;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.INoteClip;
import de.mossgrabers.framework.daw.IStepInfo;
import de.mossgrabers.framework.daw.constants.Resolution;
import de.mossgrabers.framework.daw.data.IDrumDevice;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.mode.Modes;
import de.mossgrabers.framework.view.AbstractSequencerView;
import de.mossgrabers.framework.view.Views;


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
        if (!this.isActive () || velocity == 0)
            return;

        final int index = note - DRUM_START_KEY;
        final int x = index % 8;
        final int y = index / 8;

        final int sound = y % 4 + this.soundOffset;
        final int col = 8 * (1 - y / 4) + x;
        final int row = this.scales.getDrumOffset () + this.getSelectedPad () + sound;

        final int channel = this.configuration.getMidiEditChannel ();
        final int vel = this.configuration.isAccentActive () ? this.configuration.getFixedAccentValue () : velocity;
        final INoteClip clip = this.getClip ();

        if (this.handleNoteAreaButtonCombinations (clip, channel, col, y, row, vel))
            return;

        clip.toggleStep (channel, col, row, vel);
    }


    /**
     * Handle button combinations on the note area of the sequencer.
     *
     * @param clip The sequenced midi clip
     * @param channel The MIDI channel of the note
     * @param row The row in the current page in the clip
     * @param note The note in the current page of the pad in the clip
     * @param step The step in the current page in the clip
     * @param velocity The velocity
     * @return True if handled
     */
    private boolean handleNoteAreaButtonCombinations (final INoteClip clip, final int channel, final int step, final int row, final int note, final int velocity)
    {
        // Handle note duplicate function
        final IHwButton duplicateButton = this.surface.getButton (ButtonID.DUPLICATE);
        if (duplicateButton != null && duplicateButton.isPressed ())
        {
            duplicateButton.setConsumed ();
            final IStepInfo noteStep = clip.getStep (channel, step, note);
            if (noteStep.getState () == IStepInfo.NOTE_START)
                this.copyNote = noteStep;
            else if (this.copyNote != null)
                clip.setStep (channel, step, note, this.copyNote);
            return true;
        }

        // Change length of a note or create a new one with a length
        final int lines = 4;
        final boolean isLower = row / lines == 0;
        final int offset = row * 8;
        for (int s = 0; s < step; s++)
        {
            final IHwButton button = this.surface.getButton (ButtonID.get (ButtonID.PAD1, offset + s));
            if (button.isLongPressed ())
            {
                int start = s;
                if (isLower)
                    start += 8;
                button.setConsumed ();
                final int length = step - start + 1;
                final double duration = length * Resolution.getValueAt (this.getResolutionIndex ());
                final int state = note < 0 ? 0 : clip.getStep (channel, start, note).getState ();
                if (state == IStepInfo.NOTE_START)
                    clip.updateStepDuration (channel, start, note, duration);
                else
                    clip.setStep (channel, start, note, velocity, duration);
                return true;
            }
        }

        return false;
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNoteLongPress (final int note)
    {
        if (!this.isActive ())
            return;

        final int index = note - DRUM_START_KEY;
        this.surface.getButton (ButtonID.get (ButtonID.PAD1, index)).setConsumed ();

        final int x = index % 8;
        final int y = index / 8;

        final int sound = y % 4 + this.soundOffset;
        final int stepX = 8 * (1 - y / 4) + x;
        final int stepY = this.scales.getDrumOffset () + this.getSelectedPad () + sound;

        final int editMidiChannel = this.configuration.getMidiEditChannel ();
        final INoteClip clip = this.getClip ();
        final int state = clip.getStep (editMidiChannel, stepX, stepY).getState ();
        if (state != IStepInfo.NOTE_START)
            return;

        final ModeManager modeManager = this.surface.getModeManager ();
        final NoteMode noteMode = (NoteMode) modeManager.getMode (Modes.NOTE);
        noteMode.setValues (clip, editMidiChannel, stepX, stepY);
        modeManager.setActiveMode (Modes.NOTE);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final IPadGrid padGrid = this.surface.getPadGrid ();
        if (!this.isActive ())
        {
            padGrid.turnOff ();
            return;
        }

        // Clip length/loop area
        final INoteClip clip = this.getClip ();
        final int step = clip.getCurrentStep ();

        // Paint the sequencer steps
        final int hiStep = this.isInXRange (step) ? step % DrumView4.NUM_DISPLAY_COLS : -1;
        final int offsetY = this.scales.getDrumOffset ();
        final int editMidiChannel = this.configuration.getMidiEditChannel ();
        final int selPad = this.getSelectedPad ();
        final IDrumDevice primary = this.model.getDrumDevice ();
        for (int sound = 0; sound < 4; sound++)
        {
            final int padIndex = selPad + sound + this.soundOffset;
            final int noteRow = offsetY + padIndex;
            final ColorEx drumPadColor = this.getDrumPadColor (primary, padIndex);
            for (int col = 0; col < DrumView4.NUM_DISPLAY_COLS; col++)
            {
                final int isSet = clip.getStep (editMidiChannel, col, noteRow).getState ();
                final boolean hilite = col == hiStep;
                final int x = col % 8;
                int y = col / 8;
                if (col < 8)
                    y += 5;
                y += sound;
                padGrid.lightEx (x, 8 - y, this.getStepColor (isSet, hilite, drumPadColor));
            }
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateNoteMapping ()
    {
        this.surface.setKeyTranslationTable (this.scales.translateMatrixToGrid (EMPTY_TABLE));
    }


    /** {@inheritDoc} */
    @Override
    protected void onLowerScene (final int index)
    {
        if (index > 3)
            return;

        // 7, 6, 5, 4
        this.soundOffset = 4 * index;
        this.surface.getDisplay ().notify ("Offset: " + this.soundOffset);
    }


    /** {@inheritDoc} */
    @Override
    protected String updateLowerSceneButtons (final int scene)
    {
        final int [] offsets =
        {
            0,
            4,
            8,
            12
        };

        return this.soundOffset == offsets[scene] ? AbstractSequencerView.COLOR_TRANSPOSE_SELECTED : AbstractSequencerView.COLOR_TRANSPOSE;
    }
}