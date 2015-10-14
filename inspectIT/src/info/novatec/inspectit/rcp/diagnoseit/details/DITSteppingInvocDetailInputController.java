package info.novatec.inspectit.rcp.diagnoseit.details;

import info.novatec.inspectit.cmr.model.MethodIdent;
import info.novatec.inspectit.cmr.service.ICachedDataService;
import info.novatec.inspectit.communication.data.InvocationSequenceData;
import info.novatec.inspectit.rcp.InspectIT;
import info.novatec.inspectit.rcp.InspectITImages;
import info.novatec.inspectit.rcp.diagnoseit.overview.DITInvocDetailLabelExtra;
import info.novatec.inspectit.rcp.editor.inputdefinition.extra.InputDefinitionExtrasMarkerFactory;
import info.novatec.inspectit.rcp.editor.tree.input.SteppingInvocDetailInputController;
import info.novatec.inspectit.rcp.editor.viewers.StyledCellIndexLabelProvider;
import info.novatec.inspectit.rcp.model.ModifiersImageFactory;

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
	public IBaseLabelProvider getLabelProvider() {
		return new DITInvocDetailLabelProvider();
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
				Image image = ModifiersImageFactory.getImage(methodIdent.getModifiers());

				if (getInputDefinition().hasInputDefinitionExtra(InputDefinitionExtrasMarkerFactory.DIAGNOSEIT_INVOC_DETAILS_LABEL_EXTRAS_MARKER)) {
					DITInvocDetailLabelExtra labelExtra = getInputDefinition().getInputDefinitionExtra(InputDefinitionExtrasMarkerFactory.DIAGNOSEIT_INVOC_DETAILS_LABEL_EXTRAS_MARKER);
					DITInvocDetailLabelExtra.DITResultLabel label = labelExtra.getDiagnoseITResultLabel(data.getId());
					if (label != null) {
						if (label.isBusinessTransaction()) {
							image = decorateImage(image, InspectITImages.IMG_DIAGNOSEIT_BT_OVERLAY, IDecoration.TOP_LEFT);
						}

						if (label.isEntryPoint()) {
							image = decorateImage(image, InspectITImages.IMG_DIAGNOSEIT_EP_OVERLAY, IDecoration.BOTTOM_LEFT);
						}

						if (label.isProblemContext()) {
							image = decorateImage(image, InspectITImages.IMG_DIAGNOSEIT_PC_OVERLAY, IDecoration.TOP_RIGHT);
						}
						if (label.isCause()) {
							image = decorateImage(image, InspectITImages.IMG_DIAGNOSEIT_C_OVERLAY, IDecoration.BOTTOM_RIGHT);
						}
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
