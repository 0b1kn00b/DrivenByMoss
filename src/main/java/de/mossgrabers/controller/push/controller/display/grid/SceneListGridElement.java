// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.push.controller.display.grid;

import de.mossgrabers.controller.push.PushConfiguration;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.data.IScene;
import de.mossgrabers.framework.graphics.Align;
import de.mossgrabers.framework.graphics.IGraphicsContext;

import java.util.ArrayList;
import java.util.List;


/**
 * An element in the grid which contains several text items. Each item can be selected.
 *
 * Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SceneListGridElement extends AbstractGridElement
{
    private final List<IScene> scenes;


    /**
     * Constructor.
     *
     * @param scenes The scenes
     */
    public SceneListGridElement (final List<IScene> scenes)
    {
        super (null, false, null, null, null, false);

        this.scenes = new ArrayList<> (scenes);
    }


    /** {@inheritDoc} */
    @Override
    public void draw (final IGraphicsContext gc, final double left, final double width, final double height, final PushConfiguration configuration)
    {
        final int size = this.scenes.size ();
        final double itemLeft = left + SEPARATOR_SIZE;
        final double itemWidth = width - SEPARATOR_SIZE;
        final double itemHeight = DISPLAY_HEIGHT / (double) size;

        final ColorEx textColor = configuration.getColorText ();
        final ColorEx borderColor = configuration.getColorBackgroundLighter ();

        for (int i = 0; i < size; i++)
        {
            final double itemTop = i * itemHeight;

            final IScene scene = this.scenes.get (i);
            final ColorEx backgroundColor = new ColorEx (scene.getColor ());
            gc.fillRectangle (itemLeft, itemTop + SEPARATOR_SIZE, itemWidth, itemHeight - 2 * SEPARATOR_SIZE, backgroundColor);
            if (scene.doesExist ())
                gc.drawTextInBounds (scene.getName (), itemLeft + INSET, itemTop - 1, itemWidth - 2 * INSET, itemHeight, Align.LEFT, ColorEx.calcContrastColor (backgroundColor), itemHeight / 2);
            gc.strokeRectangle (itemLeft, itemTop + SEPARATOR_SIZE, itemWidth, itemHeight - 2 * SEPARATOR_SIZE, scene.isSelected () ? textColor : borderColor, scene.isSelected () ? 2 : 1);
        }
    }
}
