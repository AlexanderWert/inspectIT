package info.novatec.inspectit.rcp.diagnoseit.details;

import info.novatec.inspectit.communication.DefaultData;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITOverviewInputController;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITResultElement;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITResultProblemInstance;
import info.novatec.inspectit.rcp.editor.ISubView;
import info.novatec.inspectit.rcp.editor.composite.SashCompositeSubView;
import info.novatec.inspectit.rcp.editor.composite.TabbedCompositeSubView;
import info.novatec.inspectit.rcp.editor.table.TableSubView;
import info.novatec.inspectit.rcp.editor.tree.TreeSubView;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class DITSashSubView extends SashCompositeSubView {

	private TabbedCompositeSubView tabbedProblemInstanceDetailsSubView;
	private TableSubView problemInstanceOverviewSubView;

	public DITSashSubView() {
		super();

		tabbedProblemInstanceDetailsSubView = new TabbedCompositeSubView();
		DITProblemInstanceDetailsSubView generalProblemDesriptionSubView = new DITProblemInstanceDetailsSubView();

		tabbedProblemInstanceDetailsSubView.addSubView(generalProblemDesriptionSubView, "General Details", InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT));
		tabbedProblemInstanceDetailsSubView.addSubView(new TabbedCompositeSubView(), "Anti-Patterns", InspectIT.getDefault().getImage(InspectITImages.IMG_DIAGNOSEIT));
		
		problemInstanceOverviewSubView = new TableSubView(new ProblemInstancesTableInputController());

		ISubView diagnoseITOverview = new TreeSubView(new DITOverviewInputController());
		addSubView(diagnoseITOverview, 1);
		addSubView(tabbedProblemInstanceDetailsSubView, 2);
		addSubView(problemInstanceOverviewSubView, 2);

	}
	
	@Override
	public void createPartControl(Composite parent, FormToolkit toolkit) {
		super.createPartControl(parent, toolkit);
		tabbedProblemInstanceDetailsSubView.getControl().setVisible(false);
		problemInstanceOverviewSubView.getControl().setVisible(false);
	}

	@Override
	public void setDataInput(List<? extends DefaultData> data) {

		if (data.size() == 1 && (data.get(0) instanceof DITResultProblemInstance)) {
			tabbedProblemInstanceDetailsSubView.getControl().setVisible(true);
			problemInstanceOverviewSubView.getControl().setVisible(false);
		} else if (data.size() == 1 && (data.get(0) instanceof DITResultElement)) {
			tabbedProblemInstanceDetailsSubView.getControl().setVisible(false);
			problemInstanceOverviewSubView.getControl().setVisible(true);
		} else {
			tabbedProblemInstanceDetailsSubView.getControl().setVisible(false);
			problemInstanceOverviewSubView.getControl().setVisible(false);
		}

		super.setDataInput(data);
	}
}
