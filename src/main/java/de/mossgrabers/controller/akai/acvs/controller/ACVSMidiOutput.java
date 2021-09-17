// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.akai.acvs.controller;

import de.mossgrabers.controller.akai.acvs.ACVSDevice;
import de.mossgrabers.framework.controller.color.ColorEx;
import de.mossgrabers.framework.daw.midi.IMidiOutput;


/**
 * Wraps the MIDI output and adds helper methods for the ACVS system exclusive commands.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ACVSMidiOutput implements IMidiOutput
{
    /** The ID of the ping message. */
    public static final int   MESSAGE_ID_PING  = 0x00;
    /** The ID of the pong message. */
    public static final int   MESSAGE_ID_PONG  = 0x01;
    /** The ID of the text message. */
    public static final int   MESSAGE_ID_TEXT  = 0x10;
    /** The ID of the color message. */
    public static final int   MESSAGE_ID_COLOR = 0x11;

    private final byte []     pingMessage      =
    {
        (byte) 0xF0,
        0x47,
        0x00,
        0x00,                                         // Overwritten with the device ID
        MESSAGE_ID_PING,
        (byte) 0xF7
    };

    private final byte []     messageHeader    =
    {
        (byte) 0xF0,
        0x47,
        0x00,
        0x00                                          // Overwritten with the device ID
    };

    private final IMidiOutput output;


    /**
     * Constructor.
     *
     * @param acvsDevice The specific ACVS device
     * @param output The MIDI output to wrap
     */
    public ACVSMidiOutput (final ACVSDevice acvsDevice, final IMidiOutput output)
    {
        this.output = output;

        this.pingMessage[3] = acvsDevice.getId ();
        this.messageHeader[3] = acvsDevice.getId ();
    }


    /**
     * Get the content from a ACVS message.
     *
     * @param data The system exclusive message from which to get the content
     * @return The content or an empty array if it is not an ACVS message
     */
    public int [] getMessageContent (final int [] data)
    {
        final int contentLength = data.length - this.messageHeader.length - 1;
        if (contentLength <= 0 || data[data.length - 1] != 0xF7)
            return new int [0];
        for (int i = 0; i < this.messageHeader.length; i++)
        {
            if (this.messageHeader[i] != (byte) data[i])
                return new int [0];
        }

        final int [] result = new int [contentLength];
        System.arraycopy (data, this.messageHeader.length, result, 0, contentLength);
        return result;
    }


    /**
     * Send a ping message to the device.
     */
    public void sendPing ()
    {
        this.output.sendSysex (this.pingMessage);
    }


    /**
     * Send a text system exclusive message to the device.
     *
     * @param itemID The ID of the item for which the text is intended
     * @param text The text to send
     */
    public void sendText (final int itemID, final String text)
    {
        final byte [] textBytes = text.getBytes ();
        final int size = 4 + textBytes.length;
        final byte [] data = new byte [size];
        // Item ID MSB / LSB
        data[0] = (byte) (itemID >> 8 & 0x7F);
        data[1] = (byte) (itemID & 0x7F);
        // Text length MSB / LSB
        data[2] = (byte) (textBytes.length >> 8 & 0x7F);
        data[3] = (byte) (textBytes.length & 0x7F);
        System.arraycopy (textBytes, 0, data, 4, textBytes.length);
        this.sendSysex (MESSAGE_ID_TEXT, data);
    }


    /**
     * Send a color system exclusive message to the device.
     *
     * @param itemID The ID of the item for which the color is intended
     * @param color The color to send
     */
    public void sendColor (final int itemID, final ColorEx color)
    {
        final byte [] data = new byte [5];
        // Item ID MSB / LSB
        data[0] = (byte) (itemID >> 7 & 0x7F);
        data[1] = (byte) (itemID & 0x7F);
        // RGB
        final int [] rgb = color.toIntRGB127 ();
        data[2] = (byte) rgb[0];
        data[3] = (byte) rgb[1];
        data[4] = (byte) rgb[2];
        this.sendSysex (MESSAGE_ID_COLOR, data);
    }


    /**
     * Send a system exclusive message to the device.
     *
     * @param messageTypeID The ID of the message, see MESSAGE_ID_* constants
     * @param data The data to send
     */
    public void sendSysex (final int messageTypeID, final byte [] data)
    {
        final int size = this.messageHeader.length + data.length + 2;
        final byte [] message = new byte [size];
        System.arraycopy (this.messageHeader, 0, message, 0, this.messageHeader.length);
        message[this.messageHeader.length] = (byte) messageTypeID;
        System.arraycopy (data, 0, message, this.messageHeader.length + 1, data.length);
        message[message.length - 1] = (byte) 0xF7;
        this.output.sendSysex (message);
    }


    /** {@inheritDoc} */
    @Override
    public void sendCC (final int cc, final int value)
    {
        this.output.sendCC (cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendCCEx (final int channel, final int cc, final int value)
    {
        this.output.sendCCEx (channel, cc, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendNote (final int note, final int velocity)
    {
        this.output.sendNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void sendNoteEx (final int channel, final int note, final int velocity)
    {
        this.output.sendNoteEx (channel, note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPolyphonicAftertouch (final int data1, final int data2)
    {
        this.output.sendPolyphonicAftertouch (data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPolyphonicAftertouch (final int channel, final int data1, final int data2)
    {
        this.output.sendPolyphonicAftertouch (channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendChannelAftertouch (final int data1, final int data2)
    {
        this.output.sendChannelAftertouch (data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendChannelAftertouch (final int channel, final int data1, final int data2)
    {
        this.output.sendChannelAftertouch (channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPitchbend (final int data1, final int data2)
    {
        this.output.sendPitchbend (data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendPitchbend (final int channel, final int data1, final int data2)
    {
        this.output.sendPitchbend (channel, data1, data2);
    }


    /** {@inheritDoc} */
    @Override
    public void sendProgramChange (final int bankMSB, final int bankLSB, final int value)
    {
        this.output.sendProgramChange (bankMSB, bankLSB, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendProgramChange (final int channel, final int bankMSB, final int bankLSB, final int value)
    {
        this.output.sendProgramChange (channel, bankMSB, bankLSB, value);
    }


    /** {@inheritDoc} */
    @Override
    public void sendSysex (final byte [] data)
    {
        this.output.sendSysex (data);
    }


    /** {@inheritDoc} */
    @Override
    public void sendSysex (final String data)
    {
        this.output.sendSysex (data);
    }
}
