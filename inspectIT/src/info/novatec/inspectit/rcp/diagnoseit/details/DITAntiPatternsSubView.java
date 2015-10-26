package info.novatec.inspectit.rcp.diagnoseit.details;

import info.novatec.inspectit.communication.DefaultData;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITResultProblemInstance;
import info.novatec.inspectit.rcp.editor.AbstractSubView;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceEventCallback.PreferenceEvent;
import info.novatec.inspectit.rcp.editor.preferences.PreferenceId;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.diagnoseit.spike.result.AntipatternInstance;
import org.diagnoseit.spike.result.ProblemInstance;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class DITAntiPatternsSubView extends AbstractSubView {

	private Composite rootComposite;
	private SashForm contentSash;
	private AntipatternDetailsView antipatternDetailsComposite;
	private ProblemInstance problemInstance;
	private AntipatternsOverview antipatternsOverview;
//private Label noAntipatternsLabel;
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void createPartControl(Composite parent, FormToolkit toolkit) {

		 rootComposite = toolkit.createComposite(parent);
		 rootComposite.setLayout(new FillLayout());

		 
		 contentSash = new SashForm(rootComposite, SWT.HORIZONTAL);
		 contentSash.setLayout(new FillLayout());

		
		ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					Object selection = ((IStructuredSelection) event.getSelection()).getFirstElement();
					if (selection instanceof AntipatternInstance) {
						antipatternDetailsComposite.setInput((AntipatternInstance) selection);
					}
				}

			}
		};

		antipatternsOverview = new AntipatternsOverview(contentSash, selectionChangedListener);
		antipatternDetailsComposite = new AntipatternDetailsView(contentSash, toolkit);
		int[] weights = { 1, 3 };
		contentSash.setWeights(weights);
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

			List<AntipatternInstance> antipatterns = problemInstance.getAntipatternInstances();
			
			if(antipatterns == null || antipatterns.isEmpty()){
				contentSash.setVisible(false);
				antipatternDetailsComposite.setInput(null);
			}else{
				contentSash.setVisible(true);
				antipatternsOverview.setInput(antipatterns);
			}
			rootComposite.layout();

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

	private class AntipatternsOverview {

		private ListViewer listViewer;

		public AntipatternsOverview(Composite parent, ISelectionChangedListener selectionChangedListener) {
			listViewer = new ListViewer(parent);
			listViewer.addSelectionChangedListener(selectionChangedListener);
			listViewer.setContentProvider(new IStructuredContentProvider() {

				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
					// TODO Auto-generated method stub

				}

				@Override
				public void dispose() {
					// TODO Auto-generated method stub

				}

				@Override
				public Object[] getElements(Object inputElement) {
					if (inputElement instanceof List<?>) {
						List<AntipatternInstance> antiPatternList = (List<AntipatternInstance>) inputElement;
						return antiPatternList.toArray();
					}
					return new Object[0];
				}
			});
			listViewer.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element) {
					if (element instanceof AntipatternInstance) {
						return ((AntipatternInstance) element).getAntipatternName();
					}

					return element.toString();
				}
			});
		}

		public void setInput(List<AntipatternInstance> antipatterns) {
			listViewer.setInput(antipatterns);
			listViewer.refresh();
		}
	}

	private class AntipatternDetailsView {
		private ScrolledForm mainForm;
		private FormTextSection generalDescriptionSection;
		private FormTextSection manifestationDescriptionSection;
		private FormTextSection solutionDescriptionSection;

		public AntipatternDetailsView(Composite parent, FormToolkit toolkit) {
			ManagedForm managedForm = new ManagedForm(parent);
			toolkit = managedForm.getToolkit();
			mainForm = managedForm.getForm();
			managedForm.getToolkit().decorateFormHeading(mainForm.getForm());
			mainForm.setLayout(new FillLayout());
			mainForm.getBody().setLayout(new TableWrapLayout());
			createAntiPatternDescriptionSections(managedForm, toolkit, mainForm.getBody());
		
		}

		private void createAntiPatternDescriptionSections(ManagedForm managedForm, FormToolkit toolkit, Composite parent) {
			generalDescriptionSection = new FormTextSection(parent, toolkit, "Anti-Pattern Description", null);
			managedForm.addPart(generalDescriptionSection);
			manifestationDescriptionSection = new FormTextSection(parent, toolkit, "Manifestation of this Problem Instance", null);
			managedForm.addPart(manifestationDescriptionSection);
			solutionDescriptionSection = new FormTextSection(parent, toolkit, "General Solution Description", null);
			managedForm.addPart(solutionDescriptionSection);

		}

		public void setInput(AntipatternInstance antipattern) {
			if(antipattern == null){
				mainForm.setText("");

				
				generalDescriptionSection.setText("<form></form>");

			
				manifestationDescriptionSection.setText("<form></form>");

				solutionDescriptionSection.setText("<form></form>");
			}else{
				mainForm.setText(antipattern.getAntipatternName());

				StringBuilder strBuilder = new StringBuilder();

				strBuilder.append("<form>");
				strBuilder.append("<p>");
				strBuilder.append(antipattern.getGeneralDescription());
				strBuilder.append("</p>");
				strBuilder.append("</form>");
				generalDescriptionSection.setText(strBuilder.toString());

				strBuilder = new StringBuilder();

				strBuilder.append("<form>");
				strBuilder.append("<p>");
				strBuilder.append(antipattern.getManifestationDescription());
				strBuilder.append("</p>");
				strBuilder.append("</form>");
				manifestationDescriptionSection.setText(strBuilder.toString());

				strBuilder = new StringBuilder();

				strBuilder.append("<form>");
				strBuilder.append("<p>");
				strBuilder.append(antipattern.getGeneralSolution());
				strBuilder.append("</p>");
				strBuilder.append("</form>");
				solutionDescriptionSection.setText(strBuilder.toString());
			}


			refresh();
		}
		
		public void refresh(){
			generalDescriptionSection.refresh();
			manifestationDescriptionSection.refresh();
			solutionDescriptionSection.refresh();
			mainForm.reflow(true);
		}
	}

}
