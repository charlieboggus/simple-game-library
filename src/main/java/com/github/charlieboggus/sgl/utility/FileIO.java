package com.github.charlieboggus.sgl.utility;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileIO
{
    /**
     * Helper method for readByteBuffer. Resizes the given ByteBuffer to the given size
     *
     * @param buffer ByteBuffer that will be resized
     * @param size the size to resize the given ByteBuffer to
     * @return the resized ByteBuffer
     */
    private static ByteBuffer resizeByteBuffer(ByteBuffer buffer, int size)
    {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(size);
        buffer.flip();
        newBuffer.put(buffer);

        return newBuffer;
    }

    /**
     * Method to open an InputStream for a given file
     *
     * @param file the file to open the InputStream for
     * @return opened InputStream
     * @throws IOException if the InputStream is unable to be opened
     */
    public static InputStream getInputStream(String file) throws IOException
    {
        InputStream in = null;
        if(Files.isReadable(Paths.get(file)))
            in = new FileInputStream(file);
        else
        {
            URL url = Thread.currentThread().getContextClassLoader().getResource(file);
            if(url != null)
                in = url.openStream();
        }
        if(in == null)
            throw new IOException();

        return in;
    }

    /**
     * Method to read a file into a byte array
     *
     * @param file name of the file to read
     * @return a byte array containing all the bytes read from the file
     * @throws IOException if there is an error reading the file
     */
    public static byte[] readBytes(String file) throws IOException
    {
        try(InputStream in = getInputStream(file))
        {
            byte[] buffer = new byte[512];
            int size = 0;
            int read;
            while((read = in.read(buffer, size, buffer.length - size)) > 0)
            {
                size += read;
                if(size == buffer.length)
                    buffer = Arrays.copyOf(buffer, size * 2);
            }

            if(size < buffer.length)
                buffer = Arrays.copyOf(buffer, size);

            return buffer;
        }
    }

    /**
     * Method to read the contents of a file into a String
     *
     * @param file name of the file to read
     * @return String comprised of all the data read from file
     * @throws IOException if there is an error reading the file
     */
    public static String readString(String file) throws IOException
    {
        try(InputStream in = getInputStream(file); BufferedReader reader = new BufferedReader(new InputStreamReader(in)))
        {
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
                builder.append(line).append("\n");

            return builder.toString();
        }
    }

    /**
     * Method to read a file into a ByteBuffer
     *
     * @param file name of the file to read
     * @return ByteBuffer containing all data read from the file
     * @throws IOException if there is an error reading the file
     */
    public static ByteBuffer readByteBuffer(String file) throws IOException
    {
        try(InputStream in = getInputStream(file); ReadableByteChannel rbc = Channels.newChannel(in))
        {
            ByteBuffer buffer = BufferUtils.createByteBuffer(4096);
            while(true)
            {
                int bytes = rbc.read(buffer);
                if(bytes == -1)
                    break;
                if(buffer.remaining() == 0)
                    buffer = resizeByteBuffer(buffer, buffer.capacity() * 3 / 2);
            }

            buffer.flip();
            return buffer.slice();
        }
    }

    /**
     * Method to write a BufferedImage to disk
     *
     * @param file filename of image to write
     * @param img BufferedImage to write to disk
     * @throws IOException if the BufferedImage is unable to be written to disk
     */
    public static void writeBufferedImage(String file, BufferedImage img) throws IOException
    {
        String formatName;
        if(file.endsWith("png"))
            formatName = "png";
        else if(file.endsWith("jpg") || file.endsWith("jpeg"))
            formatName = "jpg";
        else if(file.endsWith("gif"))
            formatName = "gif";
        else
            throw new IOException("Invalid image format");

        ImageIO.write(img, formatName, new File(file));
    }
}
