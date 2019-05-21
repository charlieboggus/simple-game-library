package com.github.charlieboggus.sgl.audio.data;

import com.github.charlieboggus.sgl.utility.FileIO;

import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.openal.AL10.*;

public class WaveDataOld
{
    private int format;
    private int fileSize;
    private int channels;
    private int sampleRate;
    private int byteRate;
    private int blockAlign;
    private int bitsPerSample;
    private int dataSize;

    private InputStream stream;

    public WaveDataOld(String file) throws IOException
    {
        this.stream = FileIO.getInputStream(file);

        // Header Positions 1-4: "RIFF" - Marks the file as a RIFF file - 4 bytes
        if(this.stream.read() != 'R' || this.stream.read() != 'I' || this.stream.read() != 'F' || this.stream.read() != 'F')
        {
            // TODO: logging - "unable to find RIFF file header"
            throw new IOException();
        }

        // Header Positions 5-8: File size - Size of the overall file minus 8 bytes - 4 bytes
        this.fileSize = this.readIntValue(4) + 8;
        if(this.fileSize <= 0)
        {
            // TODO: logging - "unable to read file size"
            throw new IOException();
        }

        // Header Positions 9-12: "WAVE" - Marks the file as a WAVE file - 4 bytes
        if(this.stream.read() != 'W' || this.stream.read() != 'A' || this.stream.read() != 'V' || this.stream.read() != 'E')
        {
            // TODO: logging - "unable to find WAVE file header"
            throw new IOException();
        }

        // Header Positions 13-16: "fmt " - Marks the Format chunk (incl. trailing null) - 4 bytes
        if(this.stream.read() != 'f' || this.stream.read() != 'm' || this.stream.read() != 't' || this.stream.read() != ' ')
        {
            // TODO: logging - "unable to find fmt_ file header"
            throw new IOException();
        }

        // Header Positions 17-20: Length of format data - 4 bytes
        int fmtLen = this.readIntValue(4);
        if(fmtLen <= 0)
        {
            // TODO: logging - "unable to read format data length"
            throw new IOException();
        }

        // Header Positions 21-22: Type of format - 1 for pcm - 2 bytes
        int fmtType = this.readIntValue(2);
        if(fmtType != 1)
        {
            // TODO: logging - "invalid format type: file not PCM"
            throw new IOException();
        }

        // Header Positions 23-24: Number of channels - 2 bytes
        this.channels = this.readIntValue(2);
        if(this.channels != 1 && this.channels != 2)
        {
            // TODO: logging - "unsupported number of channels"
            throw new IOException();
        }

        // Header Positions 25-28: Sample Rate - 4 bytes
        this.sampleRate = this.readIntValue(4);
        if(this.sampleRate <= 0)
        {
            // TODO: logging - "unable to read sample rate"
            throw new IOException();
        }

        // Header Positions 29-32: Byte Rate - (SampleRate * BitsPerSample * Channels) / 8 - 4 bytes
        this.byteRate = this.readIntValue(4);
        if(this.byteRate <= 0)
        {
            // TODO: logging - "unable to read byte rate"
            throw new IOException();
        }

        // Header Positions 33-34: Block Align - (BitsPerSample * Channels) / 8 - 2 bytes
        this.blockAlign = this.readIntValue(2);
        if(this.blockAlign <= 0)
        {
            // TODO: logging - "unable to read block align value"
            throw new IOException();
        }

        // Header Positions 35-36: Bits Per Sample - 2 bytes
        this.bitsPerSample = this.readIntValue(2);
        if(this.bitsPerSample <= 0)
        {
            // TODO: logging - "unable to read bits per sample"
            throw new IOException();
        }

        // Header Positions 37+: Location of the data chunk - usually starts at 37 but may not...
        this.dataSize = this.seekChunk('d', 'a', 't', 'a');

        // After the "data" chunk is located & the data chunk size is read all other stream.read() calls will read audio data

        // Determine the audio format from channels & bits per sample
        if(this.channels == 1)
        {
            if(this.bitsPerSample == 8)
                this.format = AL_FORMAT_MONO8;

            else if(this.bitsPerSample == 16)
                this.format = AL_FORMAT_MONO16;

            else
            {
                // TODO: logging - "Illegal Sample Size"
                throw new IOException();
            }
        }
        else if(this.channels == 2)
        {
            if(this.bitsPerSample == 8)
                this.format = AL_FORMAT_STEREO8;

            else if(this.bitsPerSample == 16)
                this.format = AL_FORMAT_STEREO16;

            else
            {
                // TODO: logging - "Illegal Sample Size"
                throw new IOException();
            }
        }
        else
        {
            // TODO: logging - "Illegal Number of Channels"
            throw new IOException();
        }
    }

    public int getFormat()
    {
        return this.format;
    }

    public int getSampleRate()
    {
        return this.sampleRate;
    }

    public int getBytesPerSecond()
    {
        return this.sampleRate * (this.bitsPerSample / 8) * this.channels;
    }

    public int getFileSize()
    {
        return this.fileSize;
    }

    public int fillBuffer(ByteBuffer buffer, int size) throws IOException
    {
        int bytesRead = 0;
        for(int i = 0; i < size; i++)
        {
            int nextByte = this.stream.read() & 0xFF;
            if(nextByte <= 0)
                break;

            buffer.put(i, (byte)(nextByte & 0xFF));
            bytesRead++;
        }

        return bytesRead;
    }

    private int readIntValue(int numBytes) throws IOException
    {
        int shift = 0;
        int value = 0;
        for(int i = 0; i < numBytes; i++)
        {
            value |= (this.stream.read() & 0xFF) << shift;
            shift += 8;
        }

        return value;
    }

    private int seekChunk(char c1, char c2, char c3, char c4) throws IOException
    {
        while(true)
        {
            boolean found;
            found  = (this.stream.read() == c1);
            found &= (this.stream.read() == c2);
            found &= (this.stream.read() == c3);
            found &= (this.stream.read() == c4);

            int chunkLen = (this.stream.read() & 0xFF) | (this.stream.read() & 0xFF) << 8 | (this.stream.read() & 0xFF) << 16 | (this.stream.read() & 0xFF) << 24;
            if(chunkLen == -1)
            {
                // TODO: logging - "unable to find c1c2c3c4 chunk"
                throw new IOException();
            }
            if(found)
                return chunkLen;

            this.skipBytes(chunkLen);
        }
    }

    private void skipBytes(int count) throws IOException
    {
        while(count > 0)
        {
            long skipped = this.stream.skip(count);
            if(skipped <= 0)
                throw new IOException();

            count -= skipped;
        }
    }
}
