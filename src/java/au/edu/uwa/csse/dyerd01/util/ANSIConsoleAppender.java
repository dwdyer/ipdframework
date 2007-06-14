// $Header:  $
package au.edu.uwa.csse.dyerd01.util;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Colour-coded console appender for Log4J, based on ANSIColorLogger from
 * Jakarta Ant (http://jakarta.apache.org/ant).
 * @author Daniel Dyer
 * @version $Revision: $
 */
public class ANSIConsoleAppender extends ConsoleAppender
{
    private static final int ATTR_NORMAL = 0;
    private static final int ATTR_BRIGHT = 1;
    private static final int ATTR_DIM = 2;
    private static final int ATTR_UNDERLINE = 3;
    private static final int ATTR_BLINK = 5;
    private static final int ATTR_REVERSE = 7;
    private static final int ATTR_HIDDEN = 8;

    private static final int FG_BLACK = 30;
    private static final int FG_RED = 31;
    private static final int FG_GREEN = 32;
    private static final int FG_YELLOW = 33;
    private static final int FG_BLUE = 34;
    private static final int FG_MAGENTA = 35;
    private static final int FG_CYAN = 36;
    private static final int FG_WHITE = 37;

    private static final int BG_BLACK = 40;
    private static final int BG_RED = 41;
    private static final int BG_GREEN = 42;
    private static final int BG_YELLOW = 44;
    private static final int BG_BLUE = 44;
    private static final int BG_MAGENTA = 45;
    private static final int BG_CYAN = 46;
    private static final int BG_WHITE = 47;

    private static final String PREFIX = "\u001b[";
    private static final String SUFFIX = "m";
    private static final char SEPARATOR = ';';
    private static final String END_COLOUR = PREFIX + SUFFIX;


    // TO DO: Make these configurable.
    private String fatalColour = PREFIX + ATTR_BRIGHT + SEPARATOR + FG_RED + SUFFIX;
    private String errorColour = PREFIX + ATTR_DIM + SEPARATOR + FG_RED + SUFFIX;
    private String warnColour = PREFIX + ATTR_DIM + SEPARATOR + FG_YELLOW + SUFFIX;
    private String infoColour = PREFIX + ATTR_DIM + SEPARATOR + FG_GREEN + SUFFIX;
    private String debugColour = PREFIX + ATTR_DIM + SEPARATOR + FG_CYAN + SUFFIX;

    private final boolean coloursOn;

    public ANSIConsoleAppender()
    {
        // For the moment turn off ANSI codes on Windows, don't seem to work.
        coloursOn = System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") < 0;
    }


    /**
     * Copied from ancestor, WriterAppender, with modifications for ANSI
     * control characters.
     */
    protected void subAppend(LoggingEvent event)
    {
        if (coloursOn)
        {
            String colour = null;
            switch (event.getLevel().toInt())
            {
                case Priority.FATAL_INT:
                {
                    colour = fatalColour;
                    break;
                }
                case Priority.ERROR_INT:
                {
                    colour = errorColour;
                    break;
                }
                case Priority.WARN_INT:
                {
                    colour = warnColour;
                    break;
                }
                case Priority.INFO_INT:
                {
                    colour = infoColour;
                    break;
                }
                default:
                {
                    colour = debugColour;
                }
            }
            this.qw.write(colour);
        }
        this.qw.write(this.layout.format(event));

        // Not sure what the rest of this method does (something to do with
        // displaying stack traces), just copied from ancestor class method.
        if(layout.ignoresThrowable())
        {
            String[] s = event.getThrowableStrRep();
            if (s != null)
            {
                int len = s.length;
                for(int i = 0; i < len; i++)
                {
                    this.qw.write(s[i]);
                    this.qw.write(Layout.LINE_SEP);
                }
            }
        }

        if (coloursOn)
        {
            this.qw.write(END_COLOUR);
        }

        if(this.immediateFlush)
        {
            this.qw.flush();
        }
    }
}