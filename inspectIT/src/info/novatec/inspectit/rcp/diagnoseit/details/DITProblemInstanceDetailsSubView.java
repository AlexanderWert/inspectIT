package info.novatec.inspectit.rcp.diagnoseit.details;

import info.novatec.inspectit.communication.DefaultData;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITResultProblemInstance;
import info.novatec.inspectit.rcp.editor.AbstractSubView;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceEventCallback.PreferenceEvent;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceId;
import info.novatec.inspectit.rcp.editor.root.AbstractRootEditor;
import info.novatec.inspectit.rcp.handlers.ShowAffectedInvocationSequencesHandler;
import info.novatec.inspectit.rcp.repository.RepositoryDefinition;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.diagnoseit.spike.result.GenericProblemDescriptionText;
import org.diagnoseit.spike.result.ProblemInstance;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Form;
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
	private FormTextSection affectedNodesSection;
	private TableSection problemContextSection;
	private ManagedForm managedForm;
	private Form rootForm;

	private DITResultProblemInstance ditResultProblemInstance;

	@Override
	public void init() {

	}

	@Override
	public void createPartControl(Composite parent, FormToolkit toolkit) {
		rootForm = toolkit.createForm(parent);
		rootForm.getBody().setLayout(new FillLayout());
		rootForm.setLayout(new FillLayout());
		toolkit.decorateFormHeading(rootForm);

		rootForm.setText("General Problem Instance Details");
		toolkit.decorateFormHeading(rootForm);
		Composite header = createHeadComposite(rootForm, toolkit);
		rootForm.setHeadClient(header);

		managedForm = new ManagedForm(rootForm.getBody());

		ScrolledForm contentForm = managedForm.getForm();

		contentForm.setLayout(new FillLayout());
		contentForm.getBody().setLayout(new TableWrapLayout());

		createLegend(toolkit, contentForm.getBody());

		createProblemContextSection(toolkit, contentForm.getBody());

		createProblemDescriptionSection(toolkit, contentForm.getBody());

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;
		layoutData.grabVertical = true;
		layoutData.align = TableWrapData.FILL;
		layoutData.valign = TableWrapData.FILL;

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;

		SashForm sashForm = new SashForm(contentForm.getBody(), SWT.HORIZONTAL);
		sashForm.setLayout(layout);
		sashForm.setLayoutData(layoutData);

		createAffectedNodesSection(toolkit, sashForm);

		createFullNamesSection(toolkit, sashForm);

		int[] weights = { 1, 1 };
		sashForm.setWeights(weights);

	}

	private Composite createHeadComposite(Form form, FormToolkit toolkit) {

		Composite header = toolkit.createComposite(form.getHead());
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		header.setLayout(gridLayout);

		IToolBarManager toolBarManager = form.getToolBarManager();

		final AbstractRootEditor rootEditor = this.getRootEditor();
		Action action = new Action("Show Affected Invocation Sequences", InspectIT.getDefault().getImageDescriptor(InspectITImages.IMG_DIAGNOSEIT_SEQUENCE)) {
			@Override
			public void run() {
				RepositoryDefinition repositoryDefinition = rootEditor.getInputDefinition().getRepositoryDefinition();
				ShowAffectedInvocationSequencesHandler.openInvocationSequences(repositoryDefinition, ditResultProblemInstance);
			}
		};
		toolBarManager.add(action);
		toolBarManager.update(true);
		return header;
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
		problemContextSection = new TableSection(parent, toolkit, "Context Information", getRootEditor());
		managedForm.addPart(problemContextSection);
	}

	private void createProblemDescriptionSection(FormToolkit toolkit, Composite parent) {
		final AbstractRootEditor rootEditor = this.getRootEditor();
		IHyperlinkListener listener = new IHyperlinkListener() {

			@Override
			public void linkExited(HyperlinkEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void linkActivated(HyperlinkEvent event) {
				if ("affectedTraces".equals(event.getHref().toString())) {
					RepositoryDefinition repositoryDefinition = rootEditor.getInputDefinition().getRepositoryDefinition();
					ShowAffectedInvocationSequencesHandler.openInvocationSequences(repositoryDefinition, ditResultProblemInstance);
				}
			}
		};
		problemDescriptionSection = new FormTextSection(parent, toolkit, "Problem Description", listener);
		problemDescriptionSection.addImage(IMAGE_BT_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_BT));
		problemDescriptionSection.addImage(IMAGE_EP_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_EP));
		problemDescriptionSection.addImage(IMAGE_PC_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_PC));
		problemDescriptionSection.addImage(IMAGE_C_KEY, InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT_C));
		managedForm.addPart(problemDescriptionSection);
	}

	private void createAffectedNodesSection(FormToolkit toolkit, Composite parent) {
		affectedNodesSection = new FormTextSection(parent, toolkit, "Affected Nodes", null);
		affectedNodesSection.expand(false);

		managedForm.addPart(affectedNodesSection);
	}

	private void createFullNamesSection(FormToolkit toolkit, Composite parent) {
		fullInformationSection = new FormTextSection(parent, toolkit, "Full Names", null);
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
		problemContextSection.refresh();
		problemDescriptionSection.refresh();
		managedForm.refresh();
		managedForm.getForm().reflow(true);
	}

	@Override
	public void preferenceEventFired(PreferenceEvent preferenceEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDataInput(List<? extends DefaultData> data) {
		if (data.size() == 1 && (data.get(0) instanceof DITResultProblemInstance)) {
			ditResultProblemInstance = (DITResultProblemInstance) data.get(0);
			updateContent(ditResultProblemInstance.getProblemInstance());
		}

	}

	private void updateContent(ProblemInstance problemInstance) {

		problemContextSection.setProblemInstance(problemInstance);

		problemDescriptionSection.setText(buildDescriptionText(problemInstance));

		affectedNodesSection.setText(buildAffectedNodesText(problemInstance));

		fullInformationSection.setText(buildFullNamesText(problemInstance));
		doRefresh();

	}

	private String buildAffectedNodesText(ProblemInstance problemInstance) {
		String text = "<form>";
		for (String affNode : problemInstance.getAffectedNodes()) {
			text += "<li>" + problemInstance.getNodeType() + ": " + affNode + "</li>";
		}

		text += "</form>";
		return text;
	}

	private String buildFullNamesText(ProblemInstance problemInstance) {
		String text = "<form>";
		text += "<p><img href='" + IMAGE_BT_KEY + "'/> " + problemInstance.getBusinessTransaction() + "</p>";
		text += "<p><img href='" + IMAGE_EP_KEY + "'/> " + problemInstance.getEntryPoint() + "</p>";
		text += "<p><img href='" + IMAGE_PC_KEY + "'/> " + problemInstance.getProblemContext() + "</p>";
		text += "<p><img href='" + IMAGE_C_KEY + "'/> " + problemInstance.getCause() + "</p>";
		text += "</form>";
		return text;
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
		strBuilder.append(genericDescriptionText.createOccurrencesText());
		strBuilder.append("</p>");
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
		return rootForm;
	}

	@Override
	public ISelectionProvider getSelectionProvider() {
		// TODO Auto-generated method stub
		return null;
	}

}
