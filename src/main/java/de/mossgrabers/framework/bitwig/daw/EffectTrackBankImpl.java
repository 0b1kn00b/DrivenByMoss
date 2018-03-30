// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2018
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.bitwig.daw;

import de.mossgrabers.framework.controller.IValueChanger;
import de.mossgrabers.framework.daw.IChannelBank;

import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.CursorTrack;


/**
 * A track bank of all effect tracks.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class EffectTrackBankImpl extends AbstractTrackBankImpl
{
    private IChannelBank audioInstrumentTrackBank;


    /**
     * Constructor.
     *
     * @param host The host
     * @param valueChanger The value changer
     * @param cursorTrack The cursor track
     * @param numTracks The number of track of a bank page
     * @param numScenes The number of scenes of a bank page
     * @param audioInstrumentTrackBank The trackbank which monitors the audio and instrument tracks
     */
    public EffectTrackBankImpl (final ControllerHost host, final IValueChanger valueChanger, final CursorTrack cursorTrack, final int numTracks, final int numScenes, final IChannelBank audioInstrumentTrackBank)
    {
        super (valueChanger, numTracks, numScenes, 0);

        this.audioInstrumentTrackBank = audioInstrumentTrackBank;

        this.trackBank = host.createEffectTrackBank (numTracks, numScenes);
        this.trackBank.followCursorTrack (cursorTrack);

        this.init ();
    }


    /** {@inheritDoc} */
    @Override
    public void scrollToChannel (final int channel)
    {
        final int chann = channel - this.audioInstrumentTrackBank.getTrackCount ();
        if (chann >= 0 && chann < this.getTrackCount ())
            this.trackBank.scrollPosition ().set (chann / this.numTracks * this.numTracks);
    }


    /** {@inheritDoc} */
    @Override
    public boolean canEditSend (final int sendIndex)
    {
        // Sends don't have sends.
        return false;
    }


    /** {@inheritDoc} */
    @Override
    public String getEditSendName (final int sendIndex)
    {
        return "";
    }
}