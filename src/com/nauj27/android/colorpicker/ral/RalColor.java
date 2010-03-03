/**
 * 
 */
package com.nauj27.android.colorpicker.ral;


/**
 * @author nauj27
 *
 */
public class RalColor {
	private int index;

	/**
	 * @param index the index to set
	 */
	private void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Set the RAL index of a color int.
	 * @param color the color int to search
	 */
	public void searchColor(int color) {
		
		// Pongo el primer elemento como índice para probar
		this.setIndex(1000);
		
	}
	
}
