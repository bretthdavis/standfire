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

package capsis.extension.dataextractor;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jeeb.lib.util.Log;
import jeeb.lib.util.Translator;
import capsis.extension.PaleoDataExtractor;
import capsis.extension.dataextractor.format.DFCurves;
import capsis.kernel.GModel;
import capsis.kernel.GScene;
import capsis.kernel.MethodProvider;
import capsis.kernel.Step;
import capsis.kernel.extensiontype.GenericExtensionStarter;
import capsis.util.methodprovider.HdomProvider;
import capsis.util.methodprovider.ProdVProvider;

/**	Production of Volume over Time(/age) or over Hdom.
* 		including dead trees
*   With possibilty to view annual increment
*	@author Ph. Dreyfus - may 2009
*/
public class DETimeVProd2 extends PaleoDataExtractor implements DFCurves {
	protected Vector curves;
	protected MethodProvider methodProvider;

	private boolean availableHdom;
	//private boolean availableAliveVProd;

	static {
		Translator.addBundle("capsis.extension.dataextractor.DETimeVProd2");
	}

	/**	Phantom constructor.
	*	Only to ask for extension properties (authorName, version...).
	*/
	public DETimeVProd2 () {}

	/**	Official constructor. It uses the standard Extension starter.
	*/
	public DETimeVProd2 (GenericExtensionStarter s) {
		super (s);
		try {
			curves = new Vector ();

			checkMethodProvider ();

			setPropertyEnabled ("showIncrement", true); // PhD 2008-06-25
			setPropertyEnabled ("xIsHdom", availableHdom); // PhD 2008-12-19
			//setPropertyEnabled ("onlyAliveV", availableAliveVProd); // PhD 2008-12-19
		} catch (Exception e) {
			Log.println (Log.ERROR, "DETimeVProd2.c ()", "Exception occured while object construction : ", e);
		}
	}

	/**	Extension dynamic compatibility mechanism.
	*	This matchwith method checks if the extension can deal (i.e. is compatible) with the referent.
	*/
	public boolean matchWith (Object referent) {
		try {
			if (!(referent instanceof GModel)) {return false;}
			GModel m = (GModel) referent;
			MethodProvider mp = m.getMethodProvider ();
			if (!(mp instanceof ProdVProvider)) {return false;}

		} catch (Exception e) {
			Log.println (Log.ERROR, "DETimeVProd2.matchWith ()", "Error in matchWith () (returned false)", e);
			return false;
		}

		return true;
	}

	//	Evaluate MethodProvider's capabilities to propose more or less options
	//	fc - 6.2.2004 - step variable is available
	//
	private void checkMethodProvider () {
		// Retrieve method provider
		methodProvider = step.getProject ().getModel ().getMethodProvider ();

		if (methodProvider instanceof HdomProvider) {availableHdom = true;}
		//if (methodProvider instanceof ProdAliveVProvider) {availableAliveVProd = true;}

	}

	/**	This method is called by superclass DataExtractor.
	*/
	public void setConfigProperties () {
		// Choose configuration properties
		addConfigProperty (PaleoDataExtractor.HECTARE);
		addConfigProperty (PaleoDataExtractor.TREE_GROUP);		// group multiconfiguration
		addConfigProperty (PaleoDataExtractor.I_TREE_GROUP);		// group individual configuration
		addBooleanProperty ("showIncrement"); // PhD 2008-06-25
		addBooleanProperty ("xIsHdom"); // PhD 2008-12-19
		//addBooleanProperty ("onlyAliveV"); // PhD 2008-12-19
	}

	/**	From DataExtractor SuperClass.
	*
	*	Computes the data series. This is the real output building.
	*	It needs a particular Step.
	*	This output computes the basal area of the stand versus date
	*	from the root Step to this one.
	*
	*	Return false if trouble while extracting.
	*/
	public boolean doExtraction () {
		if (upToDate) {return true;}
		if (step == null) {return false;}

		// Retrieve method provider
		methodProvider = step.getProject ().getModel ().getMethodProvider ();

		try {
			// per Ha computation
			double coefHa = 1;
			if (settings.perHa) {
				coefHa = 10000 / step.getScene ().getArea ();
			}

			// Retrieve Steps from root to this step
			Vector steps = step.getProject ().getStepsFromRoot (step);

			Vector c1 = new Vector ();		// x coordinates
			Vector c2 = new Vector ();		// y coordinates

			// Data extraction : points with (Double, Double) coordinates
			// modified in order to show increment instead of direct value, depending on "showIncrement" button (to be changed in Configuration (Common)) - PhD 2008-06-25
			double read = 0, value, previous = 0;
			int date;
			double hdom = 0;
			double previousHdom = 0;	// PhD 2014-11-25
			Iterator i = steps.iterator ();

			if (i.hasNext ()) { // if a least one step (... !)
				Step s = (Step) i.next ();
				// Consider restriction to one particular group if needed
				GScene stand = s.getScene ();
				Collection trees = doFilter (stand);
				date = stand.getDate ();
				hdom = ((HdomProvider) methodProvider).getHdom (stand, trees);
				//if (isSet ("onlyAliveV") && (methodProvider instanceof ProdAliveVProvider)) { // PhD 2008-12-19
				//	read = ((ProdAliveVProvider) methodProvider).getProdAliveV (stand, trees) * coefHa;
				//} else {
					read = ((ProdVProvider) methodProvider).getProdV (stand, trees, null) * coefHa;	// fc - 24.3.2004
				//}

				if (!i.hasNext()) { // if only 1 step (no second step), the 1st value or a null increment is added, and extraction is finished
					//c1.add (new Integer (date));
					if (isSet ("xIsHdom")) { // PhD 2008-12-19
						c1.add (new Double (hdom));
					} else {
						c1.add (new Double (date));
					}
					if (isSet ("showIncrement")) { // PhD 2008-06-25
						c2.add (new Double (0));
					} else {
						c2.add (new Double (read));
					}
				} else { // there is a 2nd step (and possibly more steps)
					if (isSet ("showIncrement")) { // PhD 2008-06-25
						previous = read;
						// ... what was read is assigned to "previous" - nothing is added at now
					} else { // value is added
						if (isSet ("xIsHdom")) { // PhD 2008-12-19
							c1.add (new Double (hdom));
						} else {
							c1.add (new Double (date));
						}
						c2.add (new Double (read));
					}
				}

				while(i.hasNext ()) { // ... beginning at the second date, if any
					s = (Step) i.next ();
					// Consider restriction to one particular group if needed
					stand = s.getScene ();
					trees = doFilter (stand);
					date = stand.getDate ();
					hdom = ((HdomProvider) methodProvider).getHdom (stand, trees);
					//read = ((ProdVProvider) methodProvider).getProdV (stand, trees) * coefHa;	// fc - 24.3.2004
					//if (isSet ("onlyAliveV") && (methodProvider instanceof ProdAliveVProvider)) { // PhD 2008-12-19
					//	read = ((ProdAliveVProvider) methodProvider).getProdAliveV (stand, trees) * coefHa;
					//} else {
						read = ((ProdVProvider) methodProvider).getProdV (stand, trees, null) * coefHa;	// fc - 24.3.2004
					//}
					if (isSet ("xIsHdom")) { // PhD 2008-12-19
						//c1.add (new Double (hdom));
						if(hdom > 0) {
							c1.add (new Double (hdom));
						} else {	// PhD 2014-11-25 : after clearcutting, hdom might have been set to 0
							c1.add (new Double (previousHdom));
						}
					} else {
						c1.add (new Double (date));
					}
					if (isSet ("showIncrement")) { // PhD 2008-06-25
						c2.add (new Double (read - previous));
						previous = read;
					} else {
						c2.add (new Double (read));
					}
					previousHdom = hdom;	// PhD 2014-11-25
				}
			}

			curves.clear ();
			curves.add (c1);
			curves.add (c2);

		} catch (Exception exc) {
			Log.println (Log.ERROR, "DETimeVProd.doExtraction ()", "Exception caught : ",exc);
			return false;
		}

		upToDate = true;
		return true;
	}

	/**	From DataFormat interface.
	*	From Extension interface.
	*/
	public String getName () {
		return getNamePrefix ()+Translator.swap ("DETimeVProd2");
	}

	/**	From DFCurves interface.
	*/
	public List<List<? extends Number>> getCurves () {
		return curves;
	}

	/**	From DFCurves interface.
	*/
	public List<List<String>> getLabels () {
		return null;	// optional : unused
	}

	/**	From DFCurves interface.
	*/
	public List<String> getAxesNames () {
		Vector v = new Vector ();
		if (isSet ("xIsHdom")) { // PhD 2008-12-19
			v.add (Translator.swap ("DETimeVProd2.xLabelHdom"));
		} else {
			v.add (Translator.swap ("DETimeVProd2.xLabelDate"));
		}
		/*if (settings.perHa) {
			v.add (Translator.swap ("DETimeVProd2.yLabel")+" (ha)");
		} else {
			v.add (Translator.swap ("DETimeVProd2.yLabel"));
		}*/
		String yLab;
		yLab = Translator.swap ("DETimeVProd2.yLabel");
		//if (isSet ("onlyAliveV")) { // PhD 2008-12-19
		//	yLab = Translator.swap ("DETimeVProd2.yLabelAlive");
		//} else {
			yLab = Translator.swap ("DETimeVProd2.yLabel");
		//}
		if (settings.perHa) {
			yLab = yLab +" (ha)";
		}
		if (isSet ("showIncrement")) { // PhD 2008-06-25
			yLab = "d " + yLab;
		}
		v.add (yLab);
		return v;
	}

	/**	From DFCurves interface.
	*/
	public int getNY () {
		return 1;
	}

	/**	From Extension interface.
	*/
	public String getVersion () {return VERSION;}
	public static final String VERSION = "1.0";

	/**	From Extension interface.
	*/
	public String getAuthor () {return "Ph. Dreyfus";}

	/**	From Extension interface.
	*/
	public String getDescription () {return Translator.swap ("DETimeVProd2.description");}



}

