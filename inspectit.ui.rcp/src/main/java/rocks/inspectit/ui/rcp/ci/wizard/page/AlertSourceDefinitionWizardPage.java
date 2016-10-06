package rocks.inspectit.ui.rcp.ci.wizard.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;

import rocks.inspectit.shared.cs.cmr.service.IInfluxDBService;
import rocks.inspectit.ui.rcp.InspectIT;
import rocks.inspectit.ui.rcp.InspectITImages;

public class AlertSourceDefinitionWizardPage extends WizardPage {

	private static final String TITLE = "Alert Definition Source";

	private static final String DEFAULT_MESSAGE = "Define the name and source for the new alert definition.";

	private static final int NUM_LAYOUT_COLUMNS = 5;

	private String initialName;

	private String initialMeasurement;

	private Map<String, String> initialTags;

	/**
	 * Name box.
	 */
	private Text nameBox;

	/**
	 * Measurement box.
	 */
	private Combo measurementBox;

	/**
	 * List of existing items defining which names are taken.
	 */
	private final Collection<String> existingItems;

	private final List<TagKeyValueUIComponent> tagComponents = new ArrayList<>();

	private Listener pageCompletionListener;

	private IInfluxDBService influxService;

	/**
	 * Constructor.
	 */
	public AlertSourceDefinitionWizardPage(IInfluxDBService influxService, Collection<String> existingNames) {
		this(influxService, existingNames, null, null, null);
	}

	/**
	 * Constructor.
	 */
	public AlertSourceDefinitionWizardPage(IInfluxDBService influxService, Collection<String> existingNames, String name, String measurement, Map<String, String> tags) {
		super(TITLE);
		setTitle(TITLE);
		setMessage(DEFAULT_MESSAGE);
		this.influxService = influxService;
		if (null != existingNames) {
			existingItems = existingNames;
		} else {
			existingItems = Collections.emptyList();
		}
		this.initialName = name;
		this.initialMeasurement = measurement;
		this.initialTags = tags;

		pageCompletionListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				setPageComplete(isPageComplete());
				setPageMessage();
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(Composite parent) {
		final ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		final Composite main = new Composite(scrolledComposite, SWT.NONE);

		main.setLayout(new GridLayout(NUM_LAYOUT_COLUMNS, false));

		Label nameLabel = new Label(main, SWT.LEFT);
		nameLabel.setText("Name:");
		nameBox = new Text(main, SWT.BORDER);
		nameBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, NUM_LAYOUT_COLUMNS - 1, 1));

		Label measurementLabel = new Label(main, SWT.LEFT);
		measurementLabel.setText("Measurement:");
		measurementLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		measurementBox = new Combo(main, SWT.BORDER | SWT.DROP_DOWN);
		measurementBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, NUM_LAYOUT_COLUMNS - 1, 1));

		List<String> measurements = influxService.getMeasurements();
		if (null != measurements) {
			measurementBox.setItems(measurements.toArray(new String[0]));
		}

		Listener measurementChangedListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				for (TagKeyValueUIComponent tagComponent : tagComponents) {
					tagComponent.updateAvailableTagKeys();
				}
			}
		};

		nameBox.addListener(SWT.Modify, pageCompletionListener);
		measurementBox.addListener(SWT.Modify, pageCompletionListener);
		measurementBox.addListener(SWT.Modify, measurementChangedListener);

		FormText headingText = new FormText(main, SWT.NONE);
		headingText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, NUM_LAYOUT_COLUMNS, 1));

		headingText.setFont("header", JFaceResources.getBannerFont());
		headingText.setText("<form><p><span color=\"header\" font=\"header\">Tag Specifications</span></p></form>", true, false);

		initContents(main);

		final FormText addText = new FormText(main, SWT.NONE);
		addText.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, NUM_LAYOUT_COLUMNS, 1));
		addText.setText("<form><p>Add tag specification ... <a href=\"delete\"><img href=\"addImg\" /></a></p></form>", true, false);
		addText.setImage("addImg", InspectIT.getDefault().getImage(InspectITImages.IMG_ADD));
		addText.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				TagKeyValueUIComponent tagComponent = new TagKeyValueUIComponent(main);
				addText.moveBelow(tagComponent.deleteText);
				tagComponents.add(tagComponent);
				main.layout(true, true);
				scrolledComposite.setMinSize(main.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				pageCompletionListener.handleEvent(null);
			}
		});

		main.layout();
		scrolledComposite.setMinSize(main.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(scrolledComposite);
		scrolledComposite.setContent(main);
	}

	/**
	 * Sets the message based on the page selections.
	 */
	protected void setPageMessage() {
		if (nameBox.getText().isEmpty()) {
			setMessage("No value for the name entered!", ERROR);
			return;
		}

		if (alreadyExists(getName())) {
			setMessage("An alert definition with this name already exists!", ERROR);
			return;
		}

		if (measurementBox.getText().isEmpty()) {
			setMessage("No value for the measurement entered!", ERROR);
			return;
		}

		for (TagKeyValueUIComponent tagComponent : tagComponents) {
			if (tagComponent.getTagKey().isEmpty()) {
				setMessage("Tag keys must not be empty!", ERROR);
				return;
			}
			if (tagComponent.getTagValue().isEmpty()) {
				setMessage("Tag value for key '" + tagComponent.getTagKey() + "' must not be empty!", ERROR);
				return;
			}
		}
		setMessage(DEFAULT_MESSAGE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPageComplete() {
		if (nameBox.getText().isEmpty() || alreadyExists(nameBox.getText())) {
			return false;
		}

		if (measurementBox.getText().isEmpty()) {
			return false;
		}

		for (TagKeyValueUIComponent tagComponent : tagComponents) {
			if (tagComponent.getTagKey().isEmpty() || tagComponent.getTagValue().isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getName() {
		return nameBox.getText();
	}

	public String getMeasurement() {
		return measurementBox.getText();
	}

	public Map<String, String> getTags() {
		Map<String, String> map = new HashMap<>();
		for (TagKeyValueUIComponent tagComponent : tagComponents) {
			map.put(tagComponent.getTagKey(), tagComponent.getTagValue());
		}
		return map;
	}

	/**
	 * Indicates whether an element with such a name already exists.
	 *
	 * @param name
	 *            name to check
	 * @return true, if an element with the same name already exists.
	 */
	private boolean alreadyExists(String name) {
		for (String item : existingItems) {
			if (item.equals(name)) {
				return true;
			}
		}
		return false;
	}

	private void initContents(Composite main) {
		if (null != initialName) {
			nameBox.setText(initialName);
		}

		if (null != initialMeasurement) {
			measurementBox.setText(initialMeasurement);
		}

		if (null != initialTags) {
			for (Entry<String, String> tag : initialTags.entrySet()) {
				TagKeyValueUIComponent tagComponent = new TagKeyValueUIComponent(main, tag.getKey(), tag.getValue());
				tagComponents.add(tagComponent);
			}
		}
	}

	private class TagKeyValueUIComponent {
		/**
		 * Key box.
		 */
		private Combo keyBox;

		/**
		 * Value box.
		 */
		private Combo valueBox;

		private Label keyLabel;

		private Label valueLabel;

		private FormText deleteText;

		public TagKeyValueUIComponent(Composite parent) {
			this(parent, null, null);
		}

		public TagKeyValueUIComponent(final Composite parent, String initialKey, String initialValue) {
			keyLabel = new Label(parent, SWT.LEFT);
			keyLabel.setText("Key:");
			keyLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
			keyBox = new Combo(parent, SWT.BORDER | SWT.DROP_DOWN);
			keyBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			if (null != initialKey) {
				keyBox.setText(initialKey);
			}
			updateAvailableTagKeys();

			Listener tagKeyChangedListener = new Listener() {
				@Override
				public void handleEvent(Event event) {
					updateAvailableTagValues();
				}
			};
			keyBox.addListener(SWT.Modify, pageCompletionListener);
			keyBox.addListener(SWT.Modify, tagKeyChangedListener);

			valueLabel = new Label(parent, SWT.LEFT);
			valueLabel.setText("Value:");
			valueLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
			valueBox = new Combo(parent, SWT.BORDER | SWT.DROP_DOWN);
			valueBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			if (null != initialValue) {
				valueBox.setText(initialValue);
			}

			updateAvailableTagValues();

			valueBox.addListener(SWT.Modify, pageCompletionListener);

			deleteText = new FormText(parent, SWT.NONE);
			deleteText.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
			deleteText.setText("<form><p><a href=\"delete\"><img href=\"deleteImg\" /></a></p></form>", true, false);
			deleteText.setImage("deleteImg", InspectIT.getDefault().getImage(InspectITImages.IMG_DELETE));
			deleteText.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					keyLabel.dispose();
					keyBox.dispose();
					valueLabel.dispose();
					valueBox.dispose();
					deleteText.dispose();
					parent.layout(true, true);
					tagComponents.remove(TagKeyValueUIComponent.this);
					pageCompletionListener.handleEvent(null);
				}
			});
		}

		public String getTagKey() {
			return keyBox.getText();
		}

		public String getTagValue() {
			return valueBox.getText();
		}

		public void updateAvailableTagKeys() {
			String currentText = keyBox.getText();

			List<String> tagKeys = influxService.getTags(measurementBox.getText());
			if (null != tagKeys) {
				keyBox.setItems(tagKeys.toArray(new String[0]));
			}

			keyBox.setText(currentText);
		}

		public void updateAvailableTagValues() {
			String currentText = valueBox.getText();

			List<String> tagValues = influxService.getTagValues(measurementBox.getText(), keyBox.getText());
			if (null != tagValues) {
				valueBox.setItems(tagValues.toArray(new String[0]));
			}

			valueBox.setText(currentText);
		}
	}

}
