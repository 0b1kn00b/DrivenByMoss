// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.mode;

import de.mossgrabers.framework.controller.ValueChanger;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.controller.display.Format;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.DisplayMessage;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.controller.PushDisplay;


/**
 * Editing of accent parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AccentMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public AccentMode (final PushControlSurface surface, final IModel model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnob (final int index, final int value)
    {
        // Will never need fine increments on accent velocity since they are integers
        final ValueChanger valueChanger = this.model.getValueChanger ();
        final PushConfiguration config = this.surface.getConfiguration ();
        final int fixedAccentValue = config.getFixedAccentValue ();
        config.setAccentValue (Math.max (1, valueChanger.changeValue (value, fixedAccentValue, 1, 128)));
    }


    /** {@inheritDoc} */
    @Override
    public void onValueKnobTouch (final int index, final boolean isTouched)
    {
        this.isKnobTouched[index] = isTouched;
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final int fixedAccentValue = this.surface.getConfiguration ().getFixedAccentValue ();
        final ValueChanger valueChanger = this.model.getValueChanger ();
        final Display d = this.surface.getDisplay ();
        d.clear ().setCell (0, 7, "Accent").setCell (1, 7, fixedAccentValue, Format.FORMAT_RAW).setCell (2, 7, valueChanger.toDAWValue (fixedAccentValue), Format.FORMAT_VALUE).allDone ();
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final int fixedAccentValue = this.surface.getConfiguration ().getFixedAccentValue ();
        final ValueChanger valueChanger = this.model.getValueChanger ();
        final PushDisplay display = (PushDisplay) this.surface.getDisplay ();
        final DisplayMessage message = display.createMessage ();
        for (int i = 0; i < 8; i++)
            message.addParameterElement (i == 7 ? "Accent" : "", i == 7 ? valueChanger.toDisplayValue (valueChanger.toDAWValue (fixedAccentValue)) : 0, i == 7 ? Integer.toString (fixedAccentValue) : "", this.isKnobTouched[i], -1);
        display.send (message);
    }
}