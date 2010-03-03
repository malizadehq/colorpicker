/**
 * 
 */
package com.nauj27.android.colorpicker.ral;

import android.graphics.Color;

/**
 * @author jmartin
 *
 */
public class RalColor extends Color{
	private int ralCode;
	/*private int red;
	private int green;
	private int blue;
	private int hexValue;*/
	
	/**
	 * @param ralCode the ralCode to set
	 */
	public void setRalCode(int ralCode) {
		this.ralCode = ralCode;
	}
	/**
	 * @return the ralCode
	 */
	public int getRalCode() {
		return ralCode;
	}
}
