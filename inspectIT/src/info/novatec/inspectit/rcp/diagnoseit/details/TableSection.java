package info.novatec.inspectit.rcp.diagnoseit.details;

import info.novatec.inspectit.rcp.editor.root.AbstractRootEditor;
import info.novatec.inspectit.rcp.editor.table.TableSubView;

import org.diagnoseit.spike.result.ProblemInstance;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class TableSection extends SectionPart {
	private TableSubView tableSubView;

	ProblemContextInputController inputController = new ProblemContextInputController();

	public TableSection(Composite parent, FormToolkit toolkit, String title, AbstractRootEditor rootEditor) {
		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		getSection().setText(title);

		TableWrapData tableWrapData = new TableWrapData();
		tableWrapData.grabHorizontal = true;
		tableWrapData.align = TableWrapData.FILL;
		getSection().setLayout(new FillLayout());
		getSection().setLayoutData(tableWrapData);

		tableSubView = new TableSubView(inputController);
		tableSubView.setRootEditor(rootEditor);
		tableSubView.createPartControl(getSection(), toolkit);

		getSection().setClient(tableSubView.getControl());

	}

	public void setProblemInstance(ProblemInstance problemInstance) {
		inputController.setProblemInstance(problemInstance);
	}

	@Override
	public void refresh() {
		super.refresh();
		tableSubView.doRefresh();
	}
}
