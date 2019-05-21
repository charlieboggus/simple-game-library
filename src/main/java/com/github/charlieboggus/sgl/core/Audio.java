package com.github.charlieboggus.sgl.core;

import com.github.charlieboggus.sgl.audio.data.WaveData;
import com.github.charlieboggus.sgl.utility.FileIO;
import com.github.charlieboggus.sgl.utility.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.stb.STBVorbisInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.ALC11.ALC_ALL_DEVICES_SPECIFIER;
import static org.lwjgl.openal.ALC11.ALC_MONO_SOURCES;
import static org.lwjgl.openal.ALC11.ALC_STEREO_SOURCES;
import static org.lwjgl.openal.EXTEfx.ALC_EFX_MAJOR_VERSION;
import static org.lwjgl.openal.EXTEfx.ALC_EFX_MINOR_VERSION;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Audio
{
    private static final Logger logger = Logger.getLogger(Audio.class);

    private static Configuration config;

    private static long device;
    private static long context;

    private static float globalVolume;
    private static float globalPan;

    static void create(Configuration cfg)
    {
        config = cfg;

        // TODO: set global audio setting values from config
        globalVolume = 50.0f;
        globalPan = 0.0f;

        // Open audio device
        device = alcOpenDevice((ByteBuffer) null);
        if(device == NULL)
            logger.error("Failed to open audio device!");

        // Get the device capabilities & make sure OpenALC 10 is supported
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        if(!deviceCaps.OpenALC10)
            logger.error("OpenALC 10 is not supported by the default audio device!");

        // Create the OpenAL context & make the context current
        context = alcCreateContext(device, (IntBuffer) null);
        alcSetThreadContext(context);
        AL.createCapabilities(deviceCaps);

        // Log Audio info if debug mode enabled
        if(config.isDebugging())
        {
            // List available audio devices
            List< String > devices = ALUtil.getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
            if(devices != null)
            {
                StringBuilder b = new StringBuilder();
                b.append("Audio Devices: [");
                for(int i = 0; i < devices.size(); i++)
                {
                    b.append(devices.get(i));
                    if(i != devices.size() - 1)
                        b.append(", ");
                }
                b.append("]");

                logger.debug(b.toString());
            }

            // Log other audio debug information
            String defaultDevice = alcGetString(NULL, ALC_DEFAULT_DEVICE_SPECIFIER);
            logger.debug("Default Audio Device: " + defaultDevice);
            logger.debug("OpenALC 10 Supported: " + deviceCaps.OpenALC10);
            logger.debug("OpenALC 11 Supported: " + deviceCaps.OpenALC11);
            logger.debug("ALC_EXT_EFX Supported: " + deviceCaps.ALC_EXT_EFX);
            if(deviceCaps.ALC_EXT_EFX)
            {
                logger.debug("ALC_EFX_MAJOR_VERSION: " + alcGetInteger(device, ALC_EFX_MAJOR_VERSION));
                logger.debug("ALC_EFX_MINOR_VERSION: " + alcGetInteger(device, ALC_EFX_MINOR_VERSION));
            }
            logger.debug("ALC_MAX_AUXILIARY_SENDS: " + alcGetInteger(device, ALC_MAX_AUXILIARY_SENDS));
            logger.debug("OpenAL Frequency: " + alcGetInteger(device, ALC_FREQUENCY) + "Hz");
            logger.debug("OpenAL Refresh Rate: " + alcGetInteger(device, ALC_REFRESH) + "Hz");
            logger.debug("OpenAL Sync: " + (alcGetInteger(device, ALC_SYNC) == ALC_TRUE));
            logger.debug("OpenAL Mono Sources: " + alcGetInteger(device, ALC_MONO_SOURCES));
            logger.debug("OpenAL Stereo Sources: " + alcGetInteger(device, ALC_STEREO_SOURCES));
        }

        // Set listener parameters
        alListener3f(AL_VELOCITY, 0.0f, 0.0f, 0.0f);
        alListener3f(AL_ORIENTATION, 0.0f, 0.0f, -1.0f);
    }

    static void destroy()
    {
        alcMakeContextCurrent(NULL);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
