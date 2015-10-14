package info.novatec.inspectit.rcp.diagnoseit.details;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.diagnoseit.spike.result.AntipatternInstance;
import org.diagnoseit.spike.result.ProblemInstance;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import info.novatec.inspectit.communication.DefaultData;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITResultProblemInstance;
import info.novatec.inspectit.rcp.editor.AbstractSubView;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceEventCallback.PreferenceEvent;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceId;

public class DITAntiPatternDetailsSubView extends AbstractSubView {

	private Composite rootComposite;
	private ManagedForm managedForm;
	private FormTextSection descriptionSection;
	private ProblemInstance problemInstance;

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void createPartControl(Composite parent, FormToolkit toolkit) {
		rootComposite = toolkit.createComposite(parent);
		rootComposite.setLayout(new FillLayout());

		managedForm = new ManagedForm(rootComposite);
		toolkit = managedForm.getToolkit();
		ScrolledForm mainForm = managedForm.getForm();
		managedForm.getToolkit().decorateFormHeading(mainForm.getForm());
		mainForm.setText("AntiPattern Instance Details");
		mainForm.setLayout(new FillLayout());
		mainForm.getBody().setLayout(new TableWrapLayout());

		createAntiPatternDescriptionSection(toolkit, mainForm.getBody());
	}

	private void createAntiPatternDescriptionSection(FormToolkit toolkit, Composite parent) {
		descriptionSection = new FormTextSection(parent, toolkit, "AntiPattern Description");
		managedForm.addPart(descriptionSection);

	}

	@Override
	public Set<PreferenceId> getPreferenceIds() {
		return Collections.emptySet();
	}

	@Override
	public void doRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void preferenceEventFired(PreferenceEvent preferenceEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDataInput(List<? extends DefaultData> data) {
		if (data.size() == 1 && (data.get(0) instanceof DITResultProblemInstance)) {
			this.problemInstance = ((DITResultProblemInstance) data.get(0)).getProblemInstance();

			List<AntipatternInstance> antipattern = problemInstance.getAntipatternInstances();

			if (!antipattern.isEmpty()) {
//				String text = antipattern.get().stream().map(a -> a.toString()).collect(Collectors.joining("<br>"));
				
				StringBuilder strBuilder = new StringBuilder();
				
				strBuilder.append("<form>");
				
				for (AntipatternInstance a : antipattern) {
					strBuilder.append("<p>");
					strBuilder.append("<b>Name:</b> " + a.getAntipatternName());
					strBuilder.append("</p>");
					strBuilder.append("<p>");
					strBuilder.append("<b>General Description:</b> " + a.getGeneralDescription());
					strBuilder.append("</p>");
					strBuilder.append("<p>");
					strBuilder.append("<b>Manifestation:</b> " + a.getManifestationDescription());
					strBuilder.append("</p>");
					strBuilder.append("<p>");
					strBuilder.append("<b>General Solution:</b> " + a.getGeneralSolution());
					strBuilder.append("</p>");
				}
				
				strBuilder.append("</form>");
				
				descriptionSection.setText(strBuilder.toString());
				descriptionSection.refresh();
				
//				managedForm.refresh();
//				managedForm.getForm().reflow(true);
			}

		}

	}

	@Override
	public Control getControl() {
		return rootComposite;
	}

	@Override
	public ISelectionProvider getSelectionProvider() {
		// TODO Auto-generated method stub
		return null;
	}

}
