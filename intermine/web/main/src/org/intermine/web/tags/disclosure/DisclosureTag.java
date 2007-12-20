package org.intermine.web.tags.disclosure;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.intermine.web.logic.Constants;
import org.intermine.web.logic.results.GuiObject;

/**
 * This class renders disclosure tag. See tag library descriptor for tag description.
 * 
 */

public class DisclosureTag extends BaseDisclosureTag {

	private static final String DEFAULT_STYLE_CLASS = "disclosure";
	private static final String CONSISTENT = "consistent";
	private String id;
	private boolean opened = true;
	private String onClick;
	private String type = "simple";
	
	/**
	 * Returns type of tag. At this moment is relevant only 'consistent' type.
	 * @return type of tag
	 */
	public String getType() {
		return type.toLowerCase();
	}

	/**
	 * Sets type of tag.
	 * @param type type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultStyleClass() {
		return DEFAULT_STYLE_CLASS;
	}
	
	/**
	 * Returns additional javascript code, that should be executed on element change. 
	 * @return
	 */
	public String getOnClick() {
		return onClick;
	}

	/**
	 * Sets additional javascript code, that should be executed on element change. 
	 * @return
	 */	
	public void setOnClick(String onChange) {
		this.onClick = onChange;
	}

	/**
	 * Sets element id. Disclosure tag is implemented with div, it sets div id.
	 * @param id element id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets element id.
	 * @return element id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns true if disclosure is opened else false.
	 * @return disclosure state
	 */
	public boolean getOpened() {
		if (isConsistentType()) {
			GuiObject guiObject = (GuiObject) getJspContext().getAttribute(Constants.GUI_OBJECT, PageContext.SESSION_SCOPE);
			if (guiObject != null) {
				Boolean ret = guiObject.getToggledElements().get(getId());
				if (ret != null) {
					return ret;
				}
			}
		} 
		return opened;
	}
	
	/**
	 * Returns true if disclosure is consistent type. 
	 * @return
	 */
	public boolean isConsistentType() {
		return getType().equals(CONSISTENT);
	}
	
	/**
	 * Sets new state of disclosure.
	 * @param opened true if should be opened
	 */
	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	/**
	 * Renders tag. 
	 */
	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();
		out.write("<div");
		printStyleAndClass(out);
		out.write(">");
		getJspBody().invoke(null);
		// It is displayed opened and hidden (if specified) with javascript -> 
		// Client browser without javascript doesn't hide the content and user can see it
		// Else he wouldn't have possibility to see the content
		if (!getOpened()) {
			printJavascriptHides(out);
		}
		out.write("</div>");
	}

	private void printJavascriptHides(JspWriter out) throws IOException {
		out.write("<script type=\"text/javascript\">toggleHidden(\'");
		out.write(getId());
		out.write("\')</script>");
	}
	
	/**
	 * Returns link switching between displayed and hidden state. 
	 * @return link
	 */
	public String getLink() {
		StringBuilder sb = new StringBuilder();
		sb.append("javascript:toggleHidden(\'");
		sb.append(getId());
		sb.append("\');");
		if (isConsistentType()) {
			sb.append("saveToggleState(\'");
			sb.append(getId());
			sb.append("\');");
		}
		if (getOnClick() != null) {
			sb.append(getOnClick());
			sb.append(";");
		}
		return sb.toString();
	}
}
