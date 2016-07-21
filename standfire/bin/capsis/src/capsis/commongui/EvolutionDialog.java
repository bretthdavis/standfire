/** 
 * Capsis - Computer-Aided Projections of Strategies in Silviculture
 * 
 * Copyright (C) 1999-2010 INRA 
 * 
 * Authors: F. de Coligny, S. Dufour-Kowalski, 
 * 
 * This file is part of Capsis
 * Capsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * Capsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU lesser General Public License
 * along with Capsis.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package capsis.commongui;

import javax.swing.JFrame;

import jeeb.lib.util.AmapDialog;
import capsis.kernel.EvolutionParameters;

/** 
 * 
 * @author S. Dufour
 */
public abstract class EvolutionDialog extends AmapDialog {
	
	private EvolutionParameters evolutionParameters; 
	
	
	/**	Constructor
	 */
	public EvolutionDialog(JFrame frame) {
		super(frame);
	}
	
	/**	Constructor
	 */
	public EvolutionDialog() {
		super();
	}
	
	public EvolutionParameters getEvolutionParameters () {
		return evolutionParameters;
	}
	
	public void setEvolutionParameters (EvolutionParameters ev) {
		evolutionParameters = ev;
	}
	

}