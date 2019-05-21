package com.github.charlieboggus.sgl.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger
{
    public static Logger getLogger(Class type)
    {
        return new Logger(type);
    }

    private final Class type;
    private final SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");

    private Logger(Class type)
    {
        this.type = type;
    }

    public void debug(String msg)
    {
        this.log("DEBUG", msg);
    }

    public void info(String msg)
    {
        this.log("INFO", msg);
    }

    public void warning(String msg)
    {
        this.log("WARNING", msg);
    }

    public void error(String msg)
    {
        this.log("ERROR", msg);
    }

    public void fatal(String msg)
    {
        this.log("FATAL", msg);
    }

    private void log(String level, String msg)
    {
        String out = fmt.format(new Date()) + " - [" + level + "][" + this.type.getSimpleName() + "]: " + msg;
        System.out.println(out);
    }
}
