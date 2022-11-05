package nnm.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class nFormatter 
	 extends Formatter {
        // This method is called for every log records
	 public String format(LogRecord rec) {
         StringBuffer buf = new StringBuffer(1000);
         buf.append("[");
         Format formatter = new SimpleDateFormat("MM.dd.yyyy HH.mm.ss");
     	 Date now = new Date();
         buf.append(formatter.format(now));
         buf.append("] ");
        // buf.append(rec.getLevel());
        // buf.append(": ");
         buf.append(formatMessage(rec));
         buf.append("\n");
         return buf.toString();
         
       
        }
}
