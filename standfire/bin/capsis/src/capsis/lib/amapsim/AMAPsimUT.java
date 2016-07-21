/* 
 * Capsis 4 - Computer-Aided Projections of Strategies in Silviculture
 * 
 * Copyright (C) 2000-2001  Francois de Coligny
 * 
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 2 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */

package capsis.lib.amapsim;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;

/**
 * AMAPsimUT defines an AMAPsim topological unit.
 * 
 * @author F. de Coligny - january 2004
 */
public class AMAPsimUT implements Serializable {

	public int numberOfCycles;					// 19.1.2004
	public Collection cycles;					// 19.1.2004
	

	public String toString () {	// AMAPsimUT cycles=3
		StringBuffer b = new StringBuffer ();
		b.append ("AMAPsimUT cycles=");
		if (cycles == null) {
			b.append ("null");
		} else {
			b.append (cycles.size ());
		}
		return b.toString ();
	}

	private static NumberFormat style = NumberFormat.getNumberInstance (Locale.ENGLISH);
	static {
		style.setMaximumFractionDigits (2);
		style.setGroupingUsed (false);
	}

}

