/*
 * Capsis 4 - Computer-Aided Projections of Strategies in Silviculture
 *
 * Copyright (C) 2000-2001  Philippe Dreyfus
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

package capsis.extension.dataextractor;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jeeb.lib.util.Log;
import jeeb.lib.util.Translator;
import capsis.extension.PaleoDataExtractor;
import capsis.kernel.GModel;
import capsis.kernel.GScene;
import capsis.kernel.MethodProvider;
import capsis.kernel.Step;
import capsis.kernel.extensiontype.GenericExtensionStarter;
import capsis.util.methodprovider.LivingBranchNitrogenContentProvider;

/**
 * Living branch nitrogen content versus Date.
 *
 * @author L. Saint-André - august 2002
 */
public class DETimeLIVINGBNC extends DETimeG {
	protected Vector curves;
	protected Vector labels;
	protected MethodProvider methodProvider;

	static {
		Translator.addBundle("capsis.extension.dataextractor.DETimeLIVINGBNC");
	}

	/**
	 * Phantom constructor.
	 * Only to ask for extension properties (authorName, version...).
	 */
	public DETimeLIVINGBNC () {}

	/**
	 * Official constructor. It uses the standard Extension starter.
	 */
	public DETimeLIVINGBNC (GenericExtensionStarter s) {
		//~ this (s.getStep ());
	//~ }

	//~ /**
	 //~ * Functional constructor.
	 //~ */
	//~ protected DETimeLIVINGBNC (Step stp) {
		super (s);
		try {
			curves = new Vector ();
			labels = new Vector ();

			// This is Configuration stuff, not memorized in settings
			// Reminder: only MultiConfiguration stuff is memorized in settings.

		} catch (Exception e) {
			Log.println (Log.ERROR, "DETimeLIVINGBNC.c ()", "Exception occured while object construction : ", e);
		}
	}

	/**
	 * Extension dynamic compatibility mechanism.
	 * This matchwith method checks if the extension can deal (i.e. is compatible) with the referent.
	 */
	public boolean matchWith (Object referent) {
		try {
			if (!(referent instanceof GModel)) {return false;}
			GModel m = (GModel) referent;
			MethodProvider mp = m.getMethodProvider ();
			if (!(mp instanceof LivingBranchNitrogenContentProvider)) {return false;}

		} catch (Exception e) {
			Log.println (Log.ERROR, "DETimeLIVINGBNC.matchWith ()", "Error in matchWith () (returned false)", e);
			return false;
		}

		return true;
	}

	/**
	 * This method is called by superclass DataExtractor.
	 */
	public void setConfigProperties () {
		// Choose configuration properties
		addConfigProperty (PaleoDataExtractor.TREE_GROUP);
		addConfigProperty (PaleoDataExtractor.I_TREE_GROUP);
	}

	/**
	 * From DataExtractor SuperClass.
	 *
	 * Computes the data series. This is the real output building.
	 * It needs a particular Step.
	 *
	 * Return false if trouble while extracting.
	 */
	public boolean doExtraction () {
		if (upToDate) {return true;}
		if (step == null) {return false;}
		
		// Retrieve method provider
		methodProvider = step.getProject ().getModel ().getMethodProvider ();

		try {

			// Retrieve Steps from root to this step
			Vector steps = step.getProject ().getStepsFromRoot (step);

			Vector c1 = new Vector ();		// x coordinates
			Vector c2 = new Vector ();		// y coordinates

			// Data extraction : points with (Integer, Double) coordinates
			Iterator i = steps.iterator ();
			do {
				Step s = (Step) i.next ();

				// Consider restriction to one particular group if needed
				GScene stand = s.getScene ();
				Collection trees = doFilter (stand);

				int date = stand.getDate ();

				double livingBNC = ((LivingBranchNitrogenContentProvider) methodProvider).
						getLivingBranchNitrogenContent (stand, trees);	// (NC : t)

				c1.add (new Integer (date));
				c2.add (new Double (livingBNC));

			} while (i.hasNext());

			curves.clear ();
			curves.add (c1);
			curves.add (c2);

			labels.clear ();
			labels.add (new Vector ());		// no x labels
			Vector y1Labels = new Vector ();
			y1Labels.add ("LivingBNC");
			labels.add (y1Labels);

		} catch (Exception exc) {
			Log.println (Log.ERROR, "DETimeLIVINGBNC.doExtraction ()", "Exception caught : ",exc);
			return false;
		}

		upToDate = true;
		return true;
	}

	/**
	 * From DataFormat interface.
	 */
	public String getName() {
		return getNamePrefix ()+Translator.swap ("DETimeLIVINGBNC");
	}

	/**
	 * From DFCurves interface.
	 */
	public List<String> getAxesNames () {
		Vector v = new Vector ();
		v.add (Translator.swap ("DETimeLIVINGBNC.xLabel"));
			v.add (Translator.swap ("DETimeLIVINGBNC.yLabel"));
		return v;
	}

	/**
	 * From DFCurves interface.
	 */
	public int getNY () {
		return 1;
	}

	/**
	 * From DFCurves interface.
	 */
	public List<List<? extends Number>> getCurves () {
		return curves;
	}

	/**
	 * From DFCurves interface.
	 */
	public List<List<String>> getLabels () {
		return labels;
	}

	/**
	 * From Extension interface.
	 */
	public String getVersion () {return VERSION;}
	public static final String VERSION = "1.1";

	/**
	 * From Extension interface.
	 */
	public String getAuthor () {return "L. Saint-André";}

	/**
	 * From Extension interface.
	 */
	public String getDescription () {return Translator.swap ("DETimeLIVINGBNC.description");}




}