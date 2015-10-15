package info.novatec.inspectit.rcp.diagnoseit.details;

import static info.novatec.inspectit.rcp.model.Modifier.isPackage;
import static info.novatec.inspectit.rcp.model.Modifier.isPrivate;
import static info.novatec.inspectit.rcp.model.Modifier.isProtected;
import static info.novatec.inspectit.rcp.model.Modifier.isPublic;
import info.novatec.inspectit.cmr.model.MethodIdent;
import info.novatec.inspectit.cmr.service.ICachedDataService;
import info.novatec.inspectit.communication.data.ExceptionSensorData;
import info.novatec.inspectit.communication.data.InvocationSequenceData;
import info.novatec.inspectit.communication.data.SqlStatementData;
import info.novatec.inspectit.communication.data.TimerData;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITInvocDetailLabelExtra;
import info.novatec.inspectit.rcp.editor.inputdefinition.InputDefinition;
import info.novatec.inspectit.rcp.editor.inputdefinition.extra.InputDefinitionExtrasMarkerFactory;
import info.novatec.inspectit.rcp.editor.tree.input.SteppingInvocDetailInputController;
import info.novatec.inspectit.rcp.editor.viewers.StyledCellIndexLabelProvider;
import info.novatec.inspectit.rcp.formatter.TextFormatter;
import info.novatec.inspectit.rcp.model.ModifiersImageFactory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

public class DITSteppingInvocDetailInputController extends SteppingInvocDetailInputController {

	/**
	 * The ID of this subview / controller.
	 */
	public static final String ID = "inspectit.subview.tree.dit.steppinginvocdetail";

	/**
	 * The resource manager is used for the images etc.
	 */
	private LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());

	/**
	 * The cached service is needed because of the ID mappings.
	 */
	private ICachedDataService cachedDataService;

	public DITSteppingInvocDetailInputController(boolean initVisible) {
		super(initVisible);
	}

	@Override
	public void setInputDefinition(InputDefinition inputDefinition) {
		super.setInputDefinition(inputDefinition);
		cachedDataService = inputDefinition.getRepositoryDefinition().getCachedDataService();
	}

	@Override
	public IBaseLabelProvider getLabelProvider() {
		return new DITInvocDetailLabelProvider();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getElementTextualRepresentation(Object invAwareData) {
		if (invAwareData instanceof SqlStatementData) {
			SqlStatementData sqlData = (SqlStatementData) invAwareData;
			if (0 == sqlData.getId()) {
				return "SQL: " + sqlData.getSql() + " [All]";
			} else {
				return "SQL: " + sqlData.getSql() + " [Single]";
			}
		} else if (invAwareData instanceof TimerData) {
			TimerData timerData = (TimerData) invAwareData;
			MethodIdent methodIdent = cachedDataService.getMethodIdentForId(timerData.getMethodIdent());
			if (0 == timerData.getId()) {
				return TextFormatter.getMethodString(methodIdent) + " [All]";
			} else {
				return TextFormatter.getMethodString(methodIdent) + " [Single]";
			}
		} else if (invAwareData instanceof ExceptionSensorData) {
			ExceptionSensorData exData = (ExceptionSensorData) invAwareData;
			if (0 == exData.getId()) {
				return "Exception: " + exData.getThrowableType() + " [All]";
			} else {
				return "Exception: " + exData.getThrowableType() + " [Single]";
			}
		} else if (invAwareData instanceof InvocationSequenceData) {
			InvocationSequenceData invocationSequenceData = (InvocationSequenceData) invAwareData;
			MethodIdent methodIdent = cachedDataService.getMethodIdentForId(invocationSequenceData.getMethodIdent());
			if (0 == invocationSequenceData.getId() && invocationSequenceData.getMethodIdent() == 0 && invocationSequenceData.getNestedSequences() != null
					&& !invocationSequenceData.getNestedSequences().isEmpty()) {
				return "Problem Cause";
			} else if (0 == invocationSequenceData.getId()) {
				return TextFormatter.getMethodString(methodIdent) + " [All]";
			} else {
				return TextFormatter.getMethodString(methodIdent) + " [Single]";
			}
		}
		return "";
	}

	/**
	 * The invoc detail label provider for this view.
	 * 
	 * @author Patrice Bouillet
	 * 
	 */
	private final class DITInvocDetailLabelProvider extends StyledCellIndexLabelProvider {

		/**
		 * Creates the styled text.
		 * 
		 * @param element
		 *            The element to create the styled text for.
		 * @param index
		 *            The index in the column.
		 * @return The created styled string.
		 */
		@Override
		public StyledString getStyledText(Object element, int index) {
			InvocationSequenceData data = (InvocationSequenceData) element;
			MethodIdent methodIdent = cachedDataService.getMethodIdentForId(data.getMethodIdent());
			Column enumId = Column.fromOrd(index);

			return getStyledTextForColumn(data, methodIdent, enumId);
		}

		/**
		 * Returns the column image for the given element at the given index.
		 * 
		 * @param element
		 *            The element.
		 * @param index
		 *            The index.
		 * @return Returns the Image.
		 */
		@Override
		public Image getColumnImage(Object element, int index) {
			InvocationSequenceData data = (InvocationSequenceData) element;
			MethodIdent methodIdent = cachedDataService.getMethodIdentForId(data.getMethodIdent());
			Column enumId = Column.fromOrd(index);

			switch (enumId) {
			case METHOD:
				InspectIT inspectIT = InspectIT.getDefault();
				Image image = null;

				List<String> labelImageKeys = new ArrayList<String>();
				if (getInputDefinition().hasInputDefinitionExtra(InputDefinitionExtrasMarkerFactory.DIAGNOSEIT_INVOC_DETAILS_LABEL_EXTRAS_MARKER)) {
					DITInvocDetailLabelExtra labelExtra = getInputDefinition().getInputDefinitionExtra(InputDefinitionExtrasMarkerFactory.DIAGNOSEIT_INVOC_DETAILS_LABEL_EXTRAS_MARKER);
					DITInvocDetailLabelExtra.DITResultLabel label = labelExtra.getDiagnoseITResultLabel(data.getId());
					if (label != null) {
						if (label.isBusinessTransaction()) {
							labelImageKeys.add(InspectITImages.IMG_DIAGNOSEIT_BT);
						}

						if (label.isEntryPoint()) {
							labelImageKeys.add(InspectITImages.IMG_DIAGNOSEIT_EP);
						}

						if (label.isProblemContext()) {
							labelImageKeys.add(InspectITImages.IMG_DIAGNOSEIT_PC);
						}

						if (label.isCause()) {
							labelImageKeys.add(InspectITImages.IMG_DIAGNOSEIT_C);
						}
					}
				}

				if (labelImageKeys.isEmpty()) {
					image = ModifiersImageFactory.getImage(methodIdent.getModifiers());
				} else if (labelImageKeys.size() == 1) {
					image = inspectIT.getImage(labelImageKeys.get(0));

					String decoKey = "";
					if (isPrivate(methodIdent.getModifiers())) {
						decoKey = InspectITImages.IMG_METHOD_PRIVATE_SMALL;
					} else if (isPackage(methodIdent.getModifiers())) {
						decoKey = InspectITImages.IMG_METHOD_DEFAULT_SMALL;
					} else if (isProtected(methodIdent.getModifiers())) {
						decoKey = InspectITImages.IMG_METHOD_PROTECTED_SMALL;
					} else if (isPublic(methodIdent.getModifiers())) {
						decoKey = InspectITImages.IMG_METHOD_PUBLIC_SMALL;
					}

					image = decorateImage(image, decoKey, IDecoration.BOTTOM_RIGHT);
				} else {
					String overlayPostfix = "_overlay.gif";
					image = ModifiersImageFactory.getImage(methodIdent.getModifiers());
					int position = IDecoration.TOP_LEFT;

					for (String imgKey : labelImageKeys) {
						image = decorateImage(image, imgKey.replace(".gif", overlayPostfix), position);
						position++;
					}

				}

				return image;
			case DURATION:
				return null;
			case CPUDURATION:
				return null;
			case EXCLUSIVE:
				return null;
			case SQL:
				return null;
			case PARAMETER:
				return null;
			default:
				return null;
			}
		}

		private Image decorateImage(Image image, String imageKey, int position) {
			ImageDescriptor imgnDesc = InspectIT.getDefault().getImageDescriptor(imageKey);
			DecorationOverlayIcon icon = new DecorationOverlayIcon(image, imgnDesc, position);
			image = resourceManager.createImage(icon);
			return image;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Color getBackground(Object element, int index) {
			InvocationSequenceData data = (InvocationSequenceData) element;

			if (getInputDefinition().hasInputDefinitionExtra(InputDefinitionExtrasMarkerFactory.DIAGNOSEIT_INVOC_DETAILS_LABEL_EXTRAS_MARKER)) {
				DITInvocDetailLabelExtra labelExtra = getInputDefinition().getInputDefinitionExtra(InputDefinitionExtrasMarkerFactory.DIAGNOSEIT_INVOC_DETAILS_LABEL_EXTRAS_MARKER);
				DITInvocDetailLabelExtra.DITResultLabel label = labelExtra.getDiagnoseITResultLabel(data.getId());
				if (label != null) {
					if (label.isCause()) {
						return resourceManager.createColor(new RGB(255, 200, 200));
					} else if (label.isProblemContext()) {
						resourceManager.createColor(new RGB(255, 234, 189));
					} else if (label.isEntryPoint()) {
						resourceManager.createColor(new RGB(235, 235, 235));
					} else if (label.isBusinessTransaction()) {
						resourceManager.createColor(new RGB(200, 225, 255));
					}
				}
			}

			return null;
		}

	}

}
