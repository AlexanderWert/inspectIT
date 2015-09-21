package info.novatec.inspectit.rcp.diagnoseit.details;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledFormText;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class FormTextSection extends SectionPart {

	private static final int DFLT_MAX_HEIGHT = 400;

	private ScrolledFormText scrolledFormText;

	public FormTextSection(Composite parent, FormToolkit toolkit, String title, int maxHeight) {
		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		getSection().setText(title);

		TableWrapData tableWrapData = new TableWrapData();
		tableWrapData.grabHorizontal = true;
		tableWrapData.align = TableWrapData.FILL;
		tableWrapData.maxHeight = maxHeight;
		getSection().setLayout(new TableWrapLayout());
		getSection().setLayoutData(tableWrapData);

		TableWrapData formTextLayoutData = new TableWrapData();
		formTextLayoutData.grabHorizontal = true;
		formTextLayoutData.grabVertical = true;
		formTextLayoutData.align = TableWrapData.FILL;
		formTextLayoutData.valign = TableWrapData.FILL;
		formTextLayoutData.heightHint = maxHeight - 50;

		scrolledFormText = new ScrolledFormText(getSection(), false);

		scrolledFormText.setBackground(toolkit.getColors().getBackground());
		scrolledFormText.setForeground(toolkit.getColors().getForeground());
		scrolledFormText.setLayout(new TableWrapLayout());
		scrolledFormText.setLayoutData(formTextLayoutData);
		getSection().setClient(scrolledFormText);

		FormText formText = toolkit.createFormText(scrolledFormText, false);
		formText.setLayoutData(formTextLayoutData);
		scrolledFormText.setFormText(formText);
	}

	public FormTextSection(Composite parent, FormToolkit toolkit, String title) {
		this(parent, toolkit, title, DFLT_MAX_HEIGHT);
	}

	public void addImage(String key, Image image) {

		scrolledFormText.getFormText().setImage(key, image);
	}

	public void setText(String text) {
		scrolledFormText.getFormText().setText(text, true, true);
	}
	
	public void expand(boolean expanded){
		getSection().setExpanded(expanded);
	}

	@Override
	public void refresh() {
		super.refresh();
		scrolledFormText.reflow(true);
	}

}
