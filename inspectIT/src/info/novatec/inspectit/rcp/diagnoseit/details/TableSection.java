package info.novatec.inspectit.rcp.diagnoseit.details;

import info.novatec.inspectit.rcp.editor.table.TableSubView;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class TableSection extends SectionPart {
	private static final int DFLT_MAX_HEIGHT = 200;
	private TableViewer tableViewer;
private int colIdx = 0;
	public TableSection(Composite parent, FormToolkit toolkit, String title, int maxHeight) {
		super(parent, toolkit, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		getSection().setText(title);

		TableWrapData tableWrapData = new TableWrapData();
		tableWrapData.grabHorizontal = true;
		tableWrapData.align = TableWrapData.FILL;
		tableWrapData.maxHeight = maxHeight;
		getSection().setLayout(new TableWrapLayout());
		getSection().setLayoutData(tableWrapData);

		Table table = toolkit.createTable(getSection(), SWT.NONE | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		getSection().setClient(table);
	
		
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new ArrayContentProvider());
	
	}

	public TableSection(Composite parent, FormToolkit toolkit, String title) {
		this(parent, toolkit, title, DFLT_MAX_HEIGHT);
		
		
	}

	public void addColumn(String title, int width, Image image, ColumnLabelProvider labelProvider) {
		if (labelProvider == null) {
			throw new IllegalStateException("Label provider has not been set, yet!");
		}

		TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.BORDER_SOLID, colIdx);
		viewerColumn.setLabelProvider(labelProvider);
		viewerColumn.getColumn().setText(title);
		viewerColumn.getColumn().setWidth(width);
		viewerColumn.getColumn().setImage(image);
		colIdx++;
	}



	public void setInput(Object input) {
		tableViewer.setInput(input);
	}
	
	@Override
	public void refresh() {
		super.refresh();
		tableViewer.refresh();
	}
}
