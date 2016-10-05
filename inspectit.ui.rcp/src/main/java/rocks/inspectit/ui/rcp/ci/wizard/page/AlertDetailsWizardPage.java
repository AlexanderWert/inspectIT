package rocks.inspectit.ui.rcp.ci.wizard.page;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import rocks.inspectit.shared.all.util.Pair;
import rocks.inspectit.shared.cs.ci.AlertingDefinition.ThresholdType;
import rocks.inspectit.ui.rcp.InspectIT;
import rocks.inspectit.ui.rcp.InspectITImages;

/**
 * @author Alexander Wert
 *
 */
public class AlertDetailsWizardPage extends WizardPage {

	private static final String TITLE = "Alert Threshold";
	private static final String DEFAULT_MESSAGE = "Define the threshold and check interval for the new alert definition.";
	private static final int DEFAULT_TIMERANGE = 5;
	private static final int NUM_LAYOUT_COLUMNS = 3;
	private static final Pattern EMAIL_PATTERN = Pattern.compile(
			"(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)");
	private Double initialThreshold;
	private int initialTimerange;
	private List<String> initialsEmails;
	private boolean initialLowerThreshold;

	private Text thresholdBox;
	private Spinner timerangeSpinner;
	private StyledText emailsBox;
	private Button lowerThresholdCheckBox;

	/**
	 * Default Constructor.
	 */
	public AlertDetailsWizardPage() {
		this(null, false, DEFAULT_TIMERANGE, null);
	}

	public AlertDetailsWizardPage(Double initialThreshold, boolean initialLowerThreshold, int initialTimerange, List<String> initialsEmails) {
		super(TITLE);
		setTitle(TITLE);
		setMessage(DEFAULT_MESSAGE);
		this.initialThreshold = initialThreshold;
		this.initialTimerange = initialTimerange;
		this.initialsEmails = initialsEmails;
		this.initialLowerThreshold = initialLowerThreshold;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(Composite parent) {
		final Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(NUM_LAYOUT_COLUMNS, false));

		Label thresholdLabel = new Label(main, SWT.LEFT);
		thresholdLabel.setText("Alert threshold:");
		thresholdLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		thresholdBox = new Text(main, SWT.BORDER);
		thresholdBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (null != initialThreshold) {
			thresholdBox.setText(String.valueOf(initialThreshold));
		}
		thresholdBox.addVerifyListener(new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent event) {
				Text text = (Text) event.getSource();

				final String previousText = text.getText();
				String newText = previousText.substring(0, event.start) + event.text + previousText.substring(event.end);

				if (!newText.isEmpty() && !NumberUtils.isNumber(newText)) {
					event.doit = false;
				}
			}
		});

		lowerThresholdCheckBox = new Button(main, SWT.CHECK);
		lowerThresholdCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, NUM_LAYOUT_COLUMNS - 2, 1));
		lowerThresholdCheckBox.setText("Use as lower threshold");
		lowerThresholdCheckBox.setSelection(initialLowerThreshold);

		Label timerangeLabel = new Label(main, SWT.LEFT);
		timerangeLabel.setText("Check interval [min]:");
		timerangeLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		timerangeSpinner = new Spinner(main, SWT.BORDER);
		timerangeSpinner.setMinimum(1);
		timerangeSpinner.setMaximum(Integer.MAX_VALUE);
		timerangeSpinner.setSelection(initialTimerange);
		timerangeSpinner.setIncrement(1);
		timerangeSpinner.setPageIncrement(10);
		timerangeSpinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, NUM_LAYOUT_COLUMNS - 1, 1));

		Label emailsLabel = new Label(main, SWT.LEFT);
		emailsLabel.setText("Send alerts to the following e-mail addresses:");
		emailsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, NUM_LAYOUT_COLUMNS - 1, 1));

		Label infoLabel = new Label(main, SWT.RIGHT);
		infoLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		infoLabel.setImage(InspectIT.getDefault().getImage(InspectITImages.IMG_INFORMATION));
		infoLabel.setToolTipText("Specify email addresses to which notifications about an alert shall be sent.\nEnter one address per line in the text box.");

		emailsBox = new StyledText(main, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		emailsBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, NUM_LAYOUT_COLUMNS, 1));
		if (null != initialsEmails) {
			String emailsText = "";
			for (String email : initialsEmails) {
				if (!emailsText.isEmpty()) {
					emailsText += System.getProperty("line.separator");
				}
				emailsText += email;
			}
			emailsBox.setText(emailsText);
		}
		emailsBox.addLineStyleListener(new LineStyleListener() {
			@Override
			public void lineGetStyle(LineStyleEvent event) {
				StyleRange range = new StyleRange();
				range.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
				int maxNumLine = emailsBox.getLineCount();
				int bulletLength = Integer.toString(maxNumLine).length();
				// right padding
				int widthBullet = ((bulletLength + 1) * emailsBox.getLineHeight()) / 2;
				range.metrics = new GlyphMetrics(0, 0, widthBullet);
				event.bullet = new Bullet(ST.BULLET_TEXT, range);
				event.bullet.text = String.format("%" + bulletLength + "s", emailsBox.getLineAtOffset(event.lineOffset) + 1);
			}
		});
		emailsBox.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				// redraw line numbers
				emailsBox.redraw();
			}
		});

		Listener pageCompletionListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				setPageComplete(isPageComplete());
				setPageMessage();
			}
		};

		thresholdBox.addListener(SWT.Modify, pageCompletionListener);
		timerangeSpinner.addListener(SWT.Modify, pageCompletionListener);
		emailsBox.addListener(SWT.Modify, pageCompletionListener);

		setControl(main);
	}

	/**
	 * Sets the message based on the page selections.
	 */
	protected void setPageMessage() {
		if (thresholdBox.getText().isEmpty()) {
			setMessage("Threshold must not be empty!", ERROR);
			return;
		}

		if (timerangeSpinner.getText().isEmpty()) {
			setMessage("Check interval must not be empty!", ERROR);
			return;
		}
		Pair<Integer, String> emailsErrorMessage = checkEmailText();
		if (null != emailsErrorMessage) {
			setMessage("The email address '" + emailsErrorMessage.getSecond() + "' in line " + emailsErrorMessage.getFirst() + " is not valid!", ERROR);
			return;
		}
		setMessage(DEFAULT_MESSAGE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPageComplete() {
		return !thresholdBox.getText().isEmpty() && !timerangeSpinner.getText().isEmpty() && (checkEmailText() == null);
	}

	private Pair<Integer, String> checkEmailText() {
		String emailText = emailsBox.getText();
		if (emailText.isEmpty()) {
			return null;
		}
		String[] emails = emailText.split(System.getProperty("line.separator"));
		for (int i = 0; i < emails.length; i++) {
			Matcher matcher = EMAIL_PATTERN.matcher(emails[i].trim());
			if (!matcher.matches()) {
				return new Pair<Integer, String>(i + 1, emails[i]);
			}
		}

		return null;

	}

	public double getThreshold() {
		return Double.parseDouble(thresholdBox.getText());
	}

	public int getTimerange() {
		return Integer.parseInt(timerangeSpinner.getText());
	}

	public List<String> getEmailAddresses() {
		String[] array = emailsBox.getText().split(System.getProperty("line.separator"));
		List<String> emailAddresses = new ArrayList<>();
		for (String element : array) {
			emailAddresses.add(element.trim());
		}
		return emailAddresses;
	}

	public ThresholdType getThresholdType() {
		return lowerThresholdCheckBox.getSelection() ? ThresholdType.LOWER_THRESHOLD : ThresholdType.UPPER_THRESHOLD;
	}

}
