package com.github.charlieboggus.sgl.audio.data;

import com.github.charlieboggus.sgl.audio.AudioData;
import com.github.charlieboggus.sgl.utility.FileIO;
import com.github.charlieboggus.sgl.utility.Logger;
import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.ByteBuffer;

public class WaveData extends AudioData
{
    private static final Logger logger = Logger.getLogger(WaveData.class);

    private static class WaveInputStream extends FilterInputStream
    {
        int channels;
        int sampleRate;
        int dataRemaining;

        WaveInputStream(String file) throws IOException
        {
            super(FileIO.getInputStream(file));

            // Header Positions 1-4: "RIFF" - Marks the file as a RIFF file - 4 bytes
            if(this.read() != 'R' || this.read() != 'I' || this.read() != 'F' || this.read() != 'F')
            {
                logger.error("Failed to find RIFF file header for Wav file: " + file);
                throw new IOException();
            }

            // Header Positions 5-8: File size - Size of the overall file minus 8 - 4 bytes
            this.skipBytes(4);

            // Header Positions 9-12: "WAVE" - Marks the file as a WAVE file - 4 bytes
            if(this.read() != 'W' || this.read() != 'A' || this.read() != 'V' || this.read() != 'E')
            {
                logger.error("Failed to find WAVE file header for Wav file: " + file);
                throw new IOException();
            }

            // Header Positions 13-16: "fmt " - Marks the Format chunk (incl. trailing null) - 4 bytes
            if(this.read() != 'f' || this.read() != 'm' || this.read() != 't' || this.read() != ' ')
            {
                logger.error("Failed to find fmt chunk header for Wav file: " + file);
                throw new IOException();
            }

            // Header Positions 17-20: Format chunk size - 4 bytes
            this.skipBytes(4);

            // Header Positions 21-22: Format tag (1 for pcm) - 2 bytes
            int tag = this.readInt(2);
            if(tag != 1)
            {
                logger.error("Invalid format tag (1 for PCM) for Wav file: " + file + " (" + tag + ")");
                throw new IOException();
            }

            // Header Positions 23-24: Number of channels - 2 bytes
            this.channels = this.readInt(2);
            if(this.channels <= 0 || (this.channels != 1 && this.channels != 2))
            {
                logger.error("Unsupported number of channels for Wav file: " + file + " (" + this.channels + ")");
                throw new IOException();
            }


            // Header Positions 25-28: Sample Rate - 4 bytes
            this.sampleRate = this.readInt(4);
            if(this.sampleRate <= 0)
            {
                logger.error("Invalid sample rate for Wav file: " + file + " (" + this.sampleRate + ")");
                throw new IOException();
            }

            // Header Positions 29-32: Byte Rate - (SampleRate * BitsPerSample * Channels) / 8 - 4 bytes
            this.skipBytes(4);

            // Header Positions 33-34: Block Align - (BitsPerSample * Channels) / 8 - 2 bytes
            this.skipBytes(2);

            // Header Positions 35-36: Bits Per Sample - 2 bytes
            this.skipBytes(2);

            // Header Positions 37+: Location of the data chunk - usually starts at 37 but may not...
            this.dataRemaining = this.seekChunk('d', 'a', 't', 'a');
        }

        private int readInt(int bytes) throws IOException
        {
            int shift = 0;
            int value = 0;
            for(int i = 0; i < bytes; i++)
            {
                value |= (this.read() & 0xFF) << shift;
                shift += 8;
            }

            return value;
        }

        private int seekChunk(char c1, char c2, char c3, char c4) throws IOException
        {
            while(true)
            {
                boolean found;
                found  = (this.read() == c1);
                found &= (this.read() == c2);
                found &= (this.read() == c3);
                found &= (this.read() == c4);

                int len = (this.read() & 0xFF) | ((this.read() & 0xFF) << 8) | ((this.read() & 0xFF) << 16) | ((this.read() & 0xFF) << 24);
                if(len == 0)
                    throw new IOException();
                if(found)
                    return len;

                this.skipBytes(len);
            }
        }

        private void skipBytes(int bytes) throws IOException
        {
            while(bytes > 0)
            {
                long skipped = this.skip(bytes);
                if(skipped <= 0)
                    throw new IOException();

                bytes -= skipped;
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
}
