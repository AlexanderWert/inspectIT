package info.novatec.inspectit.rcp.diagnoseit.overview;

import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;

import org.eclipse.swt.graphics.Image;

/**
 * Defines the columns of the diagnoseIT results overview.
 * 
 * @author Alexander Wert
 * 
 */
public enum DITOverviewColumn {
	/** Problem instances column. */
	PROBLEM_OVERVIEW("Problem Overview", 600, null),
	/** Severity column. */
	SEVERITY("Severity", 60, null),
	/** Count column. */
	NUM_OCCURRENCES("# Occurrences", 90, null),
	/** # Problem instances column. */
	NUM_INSTANCES("# Problems", 80, null),
	/** # Affected column. */
	NUM_AFFECTED_NODES("# Affected Nodes", 110, null),
	/** Last Occurrence. */
	LAST_OCCURENCE("Last Occurence", 100, null);

	/** The name. */
	private String name;
	/** The width of the column. */
	private int width;
	/** The image descriptor. Can be <code>null</code> */
	private Image image;

	/**
	 * Default constructor which creates a column enumeration object.
	 * 
	 * @param name
	 *            The name of the column.
	 * @param width
	 *            The width of the column.
	 * @param imageName
	 *            The name of the image. Names are defined in {@link InspectITImages}.
	 */
	private DITOverviewColumn(String name, int width, String imageName) {
		this.name = name;
		this.width = width;
		this.image = InspectIT.getDefault().getImage(imageName);
	}

	/**
	 * Converts an ordinal into a column.
	 * 
	 * @param i
	 *            The ordinal.
	 * @return The appropriate column.
	 */
	public static DITOverviewColumn fromOrd(int i) {
		if (i < 0 || i >= DITOverviewColumn.values().length) {
			throw new IndexOutOfBoundsException("Invalid ordinal");
		}
		return DITOverviewColumn.values()[i];
	}

	/**
	 * Gets {@link #name}.
	 *   
	 * @return {@link #name}  
	 */
	public String getName() {
		return name;
	}

	/**  
	 * Sets {@link #name}.  
	 *   
	 * @param name  
	 *            New value for {@link #name}  
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets {@link #width}.
	 *   
	 * @return {@link #width}  
	 */
	public int getWidth() {
		return width;
	}

	/**  
	 * Sets {@link #width}.  
	 *   
	 * @param width  
	 *            New value for {@link #width}  
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Gets {@link #image}.
	 *   
	 * @return {@link #image}  
	 */
	public Image getImage() {
		return image;
	}

	/**  
	 * Sets {@link #image}.  
	 *   
	 * @param image  
	 *            New value for {@link #image}  
	 */
	public void setImage(Image image) {
		this.image = image;
	}
	
	
}