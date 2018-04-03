// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.graphics;

import java.nio.ByteBuffer;


/**
 * An interface to a bitmap, which can also be displayed in a window.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface IBitmap
{
    /**
     * Set a title for the window, which displays the Bitmap.
     *
     * @param title The title
     */
    void setDisplayWindowTitle (String title);


    /**
     * Show the display window.
     */
    void showDisplayWindow ();


    /**
     * Render the content of the bitmap.
     *
     * @param renderer The renderer to draw on the bitmap
     */
    void render (IRenderer renderer);


    /**
     * Fill and encode the buffer for USB transmission of the bitmap.
     *
     * @param buffer The buffer to fill
     */
    void fillTransferBuffer (ByteBuffer buffer);
}
