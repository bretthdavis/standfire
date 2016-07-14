/* 
 * Capsis 4 - Computer-Aided Projections of Strategies in Silviculture
 * 
 * Copyright (C) 2000-2003  Francois de Coligny
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package capsis.util.methodprovider;

import java.util.Vector;

import capsis.kernel.GScene;
import capsis.kernel.MethodProvider;

/**
 * Canopy cover
 * 
 * @author F. de Coligny - june 2001 
 */
public interface CoverProvider extends MethodProvider  {

	/**	Canopy cover.
	*	Return a Vector of Double. First value is total cover for stand in m2.
	*	Other optional values are cover for canopy layers. Layers are given from bottom to the top.
	*/
	public Vector getCover (GScene stand);

}

