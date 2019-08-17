// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.mode.track;

import de.mossgrabers.controller.push.controller.PushColors;
import de.mossgrabers.controller.push.controller.PushControlSurface;
import de.mossgrabers.controller.push.mode.BaseMode;
import de.mossgrabers.controller.push.view.ColorView;
import de.mossgrabers.framework.controller.display.ITextDisplay;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.IChannel;
import de.mossgrabers.framework.graphics.display.DisplayModel;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.framework.view.Views;


/**
 * Mode for editing details of a layer.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class LayerDetailsMode extends BaseMode
{
    /**
     * Constructor.
     *
     * @param surface The control surface
     * @param model The model
     */
    public LayerDetailsMode (final PushControlSurface surface, final IModel model)
    {
        super ("Layer details", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onFirstRow (final int index, final ButtonEvent event)
    {
        if (event != ButtonEvent.UP)
            return;
        final IChannel channel = this.model.getCursorDevice ().getLayerOrDrumPadBank ().getSelectedItem ();
        if (channel == null)
            return;

        switch (index)
        {
            case 0:
                channel.toggleIsActivated ();
                break;
            case 2:
                channel.toggleMute ();
                break;
            case 3:
                channel.toggleSolo ();
                break;
            case 7:
                final ViewManager viewManager = this.surface.getViewManager ();
                ((ColorView) viewManager.getView (Views.COLOR)).setMode (ColorView.SelectMode.MODE_LAYER);
                viewManager.setActiveView (Views.COLOR);
                break;
            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateFirstRow ()
    {
        final IChannel deviceChain = this.model.getCursorDevice ().getLayerOrDrumPadBank ().getSelectedItem ();
        if (deviceChain == null)
        {
            this.disableFirstRow ();
            return;
        }

        final int off = this.isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        this.surface.updateTrigger (20, deviceChain.isActivated () ? this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_MD : PushColors.PUSH1_COLOR_YELLOW_MD : this.isPush2 ? PushColors.PUSH2_COLOR_YELLOW_LO : PushColors.PUSH1_COLOR_YELLOW_LO);
        this.surface.updateTrigger (21, off);
        this.surface.updateTrigger (22, deviceChain.isMute () ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_LO : PushColors.PUSH1_COLOR_ORANGE_LO);
        this.surface.updateTrigger (23, deviceChain.isSolo () ? this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_HI : PushColors.PUSH1_COLOR_ORANGE_HI : this.isPush2 ? PushColors.PUSH2_COLOR_ORANGE_LO : PushColors.PUSH1_COLOR_ORANGE_LO);
        this.surface.updateTrigger (24, off);
        this.surface.updateTrigger (25, off);
        this.surface.updateTrigger (26, off);
        this.surface.updateTrigger (27, this.isPush2 ? PushColors.PUSH2_COLOR_GREEN_HI : PushColors.PUSH1_COLOR_GREEN_HI);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay1 ()
    {
        final ITextDisplay d = this.surface.getDisplay ().clear ();
        final IChannel deviceChain = this.model.getCursorDevice ().getLayerOrDrumPadBank ().getSelectedItem ();
        if (deviceChain == null)
            d.setRow (1, "                     Please selecta layer...                        ").allDone ();
        else
        {
            final String layerName = deviceChain.getName ();
            d.setBlock (0, 0, "Layer: " + layerName);
            if (layerName.length () > 10)
                d.setBlock (0, 1, layerName.substring (10));
            d.setCell (2, 0, "Active").setCell (3, 0, deviceChain.isActivated () ? "On" : "Off");
            d.setCell (2, 1, "");
            d.setCell (3, 1, "");
            d.setCell (2, 2, "Mute").setCell (3, 2, deviceChain.isMute () ? "On" : "Off");
            d.setCell (2, 3, "Solo").setCell (3, 3, deviceChain.isSolo () ? "On" : "Off");
            d.setCell (2, 4, "");
            d.setCell (3, 4, "");
            d.setCell (2, 5, "");
            d.setCell (3, 5, "");
            d.setCell (2, 7, "Select");
            d.setCell (3, 7, "Color");
            d.allDone ();
        }
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay2 ()
    {
        final DisplayModel message = this.surface.getGraphicsDisplay ().getModel ();
        final IChannel deviceChain = this.model.getCursorDevice ().getLayerOrDrumPadBank ().getSelectedItem ();
        if (deviceChain == null)
            message.setMessage (3, "Please select a layer...");
        else
        {
            message.addOptionElement ("Layer: " + deviceChain.getName (), "", false, "", "Active", deviceChain.isActivated (), false);
            message.addEmptyElement ();
            message.addOptionElement ("", "", false, "", "Mute", deviceChain.isMute (), false);
            message.addOptionElement ("", "", false, "", "Solo", deviceChain.isSolo (), false);
            message.addEmptyElement ();
            message.addEmptyElement ();
            message.addEmptyElement ();
            message.addOptionElement ("", "", false, "", "Select Color", false, false);
        }
        message.send ();
    }


    /** {@inheritDoc} */
    @Override
    protected IChannelBank<?> getBank ()
    {
        return this.model.getCursorDevice ().getLayerOrDrumPadBank ();
    }
}