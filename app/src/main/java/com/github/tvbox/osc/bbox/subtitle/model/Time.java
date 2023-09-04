/**
 * Class that represents the .ASS and .SSA subtitle file format
 *
 * <br><br>
 * Copyright (c) 2012 J. David Requejo <br>
 * j[dot]david[dot]requejo[at] Gmail
 * <br><br>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 * <br><br>
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 * <br><br>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * @author J. David REQUEJO
 *
 */

package com.github.tvbox.osc.bbox.subtitle.model;

public class Time {
	
	/**
	 * Constructor to create a time object.
	 * 
	 * @param format supported formats: "hh:mm:ss,ms", "h:mm:ss.cs" and "h:m:s:f/fps"
	 * @param value  string in the correct format
	 */
	public Time(String format, String value) {
		if (format.equalsIgnoreCase("hh:mm:ss,ms")){
			// this type of format:  01:02:22,501 (used in .SRT)
			int h, m, s, ms;
			h = Integer.parseInt(value.substring(0, 2));
			m = Integer.parseInt(value.substring(3, 5));
			s = Integer.parseInt(value.substring(6, 8));
			ms = Integer.parseInt(value.substring(9, 12));
			
			mseconds = ms + s*1000 + m*60000 + h*3600000;
			
		} else if (format.equalsIgnoreCase("h:mm:ss.cs")){
			// this type of format:  1:02:22.51 (used in .ASS/.SSA) 
			int h, m, s, cs;
			h = Integer.parseInt(value.substring(0, 1));
			m = Integer.parseInt(value.substring(2, 4));
			s = Integer.parseInt(value.substring(5, 7));
			cs = Integer.parseInt(value.substring(8, 10));

			mseconds = cs*10 + s*1000 + m*60000 + h*3600000;
		} else if (format.equalsIgnoreCase("h:m:s:f/fps")){
			int h, m, s, f;
			float fps;
			String[] args = value.split("/");
			fps = Float.parseFloat(args[1]);
			args = args[0].split(":");
			h = Integer.parseInt(args[0]);
			m = Integer.parseInt(args[1]);
			s = Integer.parseInt(args[2]);
			f = Integer.parseInt(args[3]);

			mseconds = (int)(f*1000/fps) + s*1000 + m*60000 + h*3600000;
		}
	}

	// in an integer we can store 24 days worth of milliseconds, no need for a long
	public int mseconds;
	
	
	/* METHODS */
	
	/**
	 * Method to return a formatted value of the time stored
	 * 
	 * @param  format supported formats: "hh:mm:ss,ms", "h:mm:ss.cs" and "hhmmssff/fps"
	 * @return formatted time in a string
	 */
	public String getTime(String format) {
		//we use string builder for efficiency
		StringBuilder time = new StringBuilder();
		String aux;
		if(format.equalsIgnoreCase("hh:mm:ss,ms")){
			// this type of format:  01:02:22,501 (used in .SRT)
			int h, m, s, ms;
			h =  mseconds/3600000;
			aux = String.valueOf(h);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			time.append(':');
			m = (mseconds/60000)%60;
			aux = String.valueOf(m);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			time.append(':');
			s = (mseconds/1000)%60;
			aux = String.valueOf(s);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			time.append(',');
			ms = mseconds%1000;
			aux = String.valueOf(ms);
			if (aux.length()==1) time.append("00");
			else if (aux.length()==2) time.append('0');
			time.append(aux);
			
		} else if(format.equalsIgnoreCase("h:mm:ss.cs")){
			// this type of format:  1:02:22.51 (used in .ASS/.SSA)
			int h, m, s, cs;
			h =  mseconds/3600000;
			aux = String.valueOf(h);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			time.append(':');
			m = (mseconds/60000)%60;
			aux = String.valueOf(m);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			time.append(':');
			s = (mseconds/1000)%60;
			aux = String.valueOf(s);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			time.append('.');
			cs = (mseconds/10)%100;
			aux = String.valueOf(cs);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			
		} else if (format.startsWith("hhmmssff/")){
			//this format is used in EBU's STL
			int h, m, s, f;
			float fps;
			String[] args = format.split("/");
			fps = Float.parseFloat(args[1]);
			//now we concatenate time
			h =  mseconds/3600000;
			aux = String.valueOf(h);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			m = (mseconds/60000)%60;
			aux = String.valueOf(m);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			s = (mseconds/1000)%60;
			aux = String.valueOf(s);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			f = (mseconds%1000)*(int)fps/1000;
			aux = String.valueOf(f);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			
		} else if (format.startsWith("h:m:s:f/")){
			//this format is used in EBU's STL
			int h, m, s, f;
			float fps;
			String[] args = format.split("/");
			fps = Float.parseFloat(args[1]);
			//now we concatenate time
			h =  mseconds/3600000;
			aux = String.valueOf(h);
			//if (aux.length()==1) time.append('0');
			time.append(aux);
			time.append(':');
			m = (mseconds/60000)%60;
			aux = String.valueOf(m);
			//if (aux.length()==1) time.append('0');
			time.append(aux);
			time.append(':');
			s = (mseconds/1000)%60;
			aux = String.valueOf(s);
			//if (aux.length()==1) time.append('0');
			time.append(aux);
			time.append(':');
			f = (mseconds%1000)*(int)fps/1000;
			aux = String.valueOf(f);
			//if (aux.length()==1) time.append('0');
			time.append(aux);
		} else if (format.startsWith("hh:mm:ss:ff/")){
			//this format is used in SCC
			int h, m, s, f;
			float fps;
			String[] args = format.split("/");
			fps = Float.parseFloat(args[1]);
			//now we concatenate time
			h =  mseconds/3600000;
			aux = String.valueOf(h);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			time.append(':');
			m = (mseconds/60000)%60;
			aux = String.valueOf(m);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			time.append(':');
			s = (mseconds/1000)%60;
			aux = String.valueOf(s);
			if (aux.length()==1) time.append('0');
			time.append(aux);
			time.append(':');
			f = (mseconds%1000)*(int)fps/1000;
			aux = String.valueOf(f);
			if (aux.length()==1) time.append('0');
			time.append(aux);
		}

		return time.toString();
	}
	
	

}
