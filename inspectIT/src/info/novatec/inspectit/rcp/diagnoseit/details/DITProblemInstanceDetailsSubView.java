package info.novatec.inspectit.rcp.diagnoseit.details;

import info.novatec.inspectit.communication.DefaultData;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITResultProblemInstance;
import info.novatec.inspectit.rcp.diagnoseit.overview.NameUtils;
import info.novatec.inspectit.rcp.editor.AbstractSubView;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceEventCallback.PreferenceEvent;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceId;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.diagnoseit.spike.result.GenericProblemDescriptionText;
import org.diagnoseit.spike.result.ProblemInstance;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class DITProblemInstanceDetailsSubView extends AbstractSubView {

	private static final String IMAGE_BT_KEY = "businessTransaction";
	private static final String IMAGE_EP_KEY = "entryPoint";
	private static final String IMAGE_PC_KEY = "problemContext";
	private static final String IMAGE_C_KEY = "cause";

	private FormTextSection problemDescriptionSection;
	private FormTextSection fullInformationSection;
	private TableSection problemContextSection;
	private ManagedForm managedForm;
	private Composite rootComposite;
	ProblemInstance problemInstance;

	@Override
	public void init() {

	}

	@Override
	public void createPartControl(Composite parent, FormToolkit toolkit) {

		rootComposite = toolkit.createComposite(parent);
		rootComposite.setLayout(new FillLayout());

		managedForm = new ManagedForm(rootComposite);
		toolkit = managedForm.getToolkit();
		ScrolledForm mainForm = managedForm.getForm();
		managedForm.getToolkit().decorateFormHeading(mainForm.getForm());
		mainForm.setText("General Problem Instance Details");
		mainForm.setLayout(new FillLayout());
		mainForm.getBody().setLayout(new TableWrapLayout());

		createLegend(toolkit, mainForm.getBody());

		createProblemContextSection(toolkit, mainForm.getBody());

		createProblemDescriptionSection(toolkit, mainForm.getBody());
		
		createFullInformationSection(toolkit, mainForm.getBody());
		
	}

	private void createLegend(FormToolkit toolkit, Composite parent) {
		FormText legend = toolkit.createFormText(parent, false);
		legend.setImage(IMAGE_BT_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_BT));
		legend.setImage(IMAGE_EP_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_EP));
		legend.setImage(IMAGE_PC_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_PC));
		legend.setImage(IMAGE_C_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_C));
		legend.setWhitespaceNormalized(false);
		String space = "     ";
		String text = "<form><p>";
		text += "<img href='" + IMAGE_BT_KEY + "'/> Business Transaction";
		text += space;
		text += "<img href='" + IMAGE_EP_KEY + "'/> Entry Point";
		text += space;
		text += "<img href='" + IMAGE_PC_KEY + "'/> Problem Context";
		text += space;
		text += "<img href='" + IMAGE_C_KEY + "'/> Cause";
		text += "</p></form>";
		legend.setText(text, true, false);
		TableWrapData tableWrapData = new TableWrapData();
		tableWrapData.grabHorizontal = true;
		tableWrapData.align = TableWrapData.FILL;
		tableWrapData.maxHeight = 20;
		legend.setLayoutData(tableWrapData);
	}

	private void createProblemContextSection(FormToolkit toolkit, Composite parent) {
		problemContextSection = new TableSection(parent, toolkit, "Context Information");
		managedForm.addPart(problemContextSection);

		for (DITProblemContextColumn column : DITProblemContextColumn.values()) {
			problemContextSection.addColumn(column.getName(), column.getWidth(), column.getImage(), new ProblemContextLabelProvider(column));
		}
	}

	private void createProblemDescriptionSection(FormToolkit toolkit, Composite parent) {
		problemDescriptionSection = new FormTextSection(parent, toolkit, "Problem Description");
		problemDescriptionSection.addImage(IMAGE_BT_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_BT));
		problemDescriptionSection.addImage(IMAGE_EP_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_EP));
		problemDescriptionSection.addImage(IMAGE_PC_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_PC));
		problemDescriptionSection.addImage(IMAGE_C_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_C));
		managedForm.addPart(problemDescriptionSection);
	}

	private void createFullInformationSection(FormToolkit toolkit, Composite parent) {
		fullInformationSection = new FormTextSection(parent, toolkit, "Full Information");
		fullInformationSection.expand(false);
		fullInformationSection.addImage(IMAGE_BT_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_BT));
		fullInformationSection.addImage(IMAGE_EP_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_EP));
		fullInformationSection.addImage(IMAGE_PC_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_PC));
		fullInformationSection.addImage(IMAGE_C_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_C));
		managedForm.addPart(fullInformationSection);
	}

	@Override
	public Set<PreferenceId> getPreferenceIds() {
		return Collections.EMPTY_SET;
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
			updateContent(((DITResultProblemInstance) (data.get(0))).getProblemInstance());
		} 

	}

	private void updateContent(ProblemInstance problemInstance) {
		this.problemInstance = problemInstance;

		ContextInformationInputElement[] input = new ContextInformationInputElement[4];
		input[0] = new ContextInformationInputElement(problemInstance.getBusinessTransaction(), "business transaction", InspectITImages.IMG_DIAGNOSEIT_BT,
				problemInstance.getBusinessTransactionData(), null, null);
		input[1] = new ContextInformationInputElement(NameUtils.getStringRepresentationFromElementData(problemInstance.getEntryPointData()), "entry point", InspectITImages.IMG_DIAGNOSEIT_EP,
				problemInstance.getEntryPointData(), null, null);
		input[2] = new ContextInformationInputElement(NameUtils.getStringRepresentationFromElementData(problemInstance.getProblemContextData()), "problem context", InspectITImages.IMG_DIAGNOSEIT_PC,
				problemInstance.getProblemContextData(), null, null);
		input[3] = new ContextInformationInputElement(NameUtils.getStringRepresentationFromElementData(problemInstance.getCauseData()), "cause", InspectITImages.IMG_DIAGNOSEIT_C,
				problemInstance.getCauseData(), problemInstance.getCauseEclusiveTimeSumStats(), problemInstance.getCauseCPUEclusiveTimeSumStats());
		problemContextSection.setInput(input);

		problemDescriptionSection.setText(buildDescriptionText(problemInstance));

		String text = "<form>";
		text += "<p><img href='" + IMAGE_BT_KEY + "'/> " + problemInstance.getBusinessTransaction() + "</p>";
		text += "<p><img href='" + IMAGE_EP_KEY + "'/> " + problemInstance.getEntryPoint() + "</p>";
		text += "<p><img href='" + IMAGE_PC_KEY + "'/> " + problemInstance.getProblemContext() + "</p>";
		text += "<p><img href='" + IMAGE_C_KEY + "'/> " + problemInstance.getCause() + "</p>";
		text += "</form>";
		fullInformationSection.setText(text);
		
		managedForm.refresh();
		managedForm.getForm().reflow(true);

	}

	private String buildDescriptionText(ProblemInstance problemInstance) {

		String businessTransactionIdentifier = "<img href='" + IMAGE_BT_KEY + "'/>";
		String problemContextIdentifier = "<img href='" + IMAGE_PC_KEY + "'/>";
		String causeIdentifier = "<img href='" + IMAGE_C_KEY + "'/>";

		GenericProblemDescriptionText genericDescriptionText = problemInstance.generateProblemDescriptionText();
		genericDescriptionText.setBusinessTransactionIdentifier(businessTransactionIdentifier);
		genericDescriptionText.setProblemContextIdentifier(problemContextIdentifier);
		genericDescriptionText.setCauseIdentifier(causeIdentifier);
		genericDescriptionText.setEmphasizeTags("<b>", "</b>");

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("<form>");
		strBuilder.append("<p>");
		strBuilder.append(genericDescriptionText.createCauseExecutionAmountText());
		strBuilder.append("</p>");
		strBuilder.append("<p>");
		strBuilder.append(genericDescriptionText.createDurationConsumptionText());
		strBuilder.append("</p>");
		strBuilder.append("<p>");
		strBuilder.append(genericDescriptionText.createCPUTimeConsumtionText());
		strBuilder.append("</p>");
		strBuilder.append("</form>");

		return strBuilder.toString();
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
