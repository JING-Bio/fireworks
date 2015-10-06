package org.reactome.web.fireworks.util.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.fireworks.controls.common.ControlButton;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ImageDownloadDialog extends PopupPanel {

    public ImageDownloadDialog(final Image image, final String diagramStId){
        super();
        String userAgent = Window.Navigator.getUserAgent().toLowerCase();
        boolean isIE = userAgent.contains("msie") || userAgent.contains("trident");
        this.setAutoHideEnabled(true);
        this.setModal(true);
        this.setAnimationEnabled(true);
        this.setGlassEnabled(true);
        this.setAutoHideOnHistoryEventsEnabled(true);
        this.addStyleName(RESOURCES.getCSS().popupPanel());

        FlowPanel vp = new FlowPanel();                         // Main panel
        vp.addStyleName(RESOURCES.getCSS().analysisPanel());
        vp.add(setTitlePanel());                                // Title panel with label & button

        FlowPanel imagePanel = new FlowPanel();
        imagePanel.add(image);
        imagePanel.setStyleName(RESOURCES.getCSS().imagePanel());
        image.setStyleName(RESOURCES.getCSS().image());
        vp.add(imagePanel);
        this.add(vp);

        FlowPanel buttons = new FlowPanel();
        if (isIE) {
            Label infoLabel = new Label("To save the image, simply right-click on it, and then click \'Save Picture As...\'");
            infoLabel.addStyleName(RESOURCES.getCSS().infoLabel());
            buttons.add(infoLabel);
        } else {
            Anchor anchor = new Anchor();                     // For downloading the image
            anchor.setHref(image.getUrl());
            anchor.getElement().setAttribute("download", "DiagramImage.png");
            Button button = getButton("Download as PNG", RESOURCES.downloadNormal());
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    hide();
                }
            });
            button.setStyleName(RESOURCES.getCSS().downloadPNG());
            button.setTitle("Save pathways overview as a PNG image");
            anchor.getElement().appendChild(button.getElement());
            buttons.add(anchor);
        }
        if(gsUploadByPostAvailable()){
            Button genomespace = getButton("Upload to GenomeSpace", RESOURCES.uploadNormal());
            genomespace.addClickHandler( new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    String mimeString = "image/png";
                    String dataURL = image.getUrl();
                    String base64 = dataURL.split(",")[1];
                    // Convert the base64 string to a blob and send to
                    // GenomeSpace using their JavaScript API
                    uploadToGenomeSpace(base64, mimeString, diagramStId);
                    hide();
                }
            });
            genomespace.setStyleName(RESOURCES.getCSS().genomespace());
            genomespace.setTitle("Upload image to GenomeSpace");
            buttons.add(genomespace);
        }
        vp.add(buttons);
    }

    private Widget setTitlePanel(){
        FlowPanel header = new FlowPanel();
        header.setStyleName(RESOURCES.getCSS().header());
        header.addStyleName(RESOURCES.getCSS().unselectable());
        Image image = new Image(RESOURCES.headerIcon());
        image.setStyleName(RESOURCES.getCSS().headerIcon());
        image.addStyleName(RESOURCES.getCSS().undraggable());
        header.add(image);
        Label title = new Label("Export pathways overview to image");
        title.setStyleName(RESOURCES.getCSS().headerText());
        Button closeBtn = new ControlButton("Close", RESOURCES.getCSS().close(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                ImageDownloadDialog.this.hide();
            }
        });
        header.add(title);
        header.add(closeBtn);
        return header;
    }

    private Button getButton(String text, ImageResource imageResource){
        FlowPanel fp = new FlowPanel();
        fp.add(new Image(imageResource));
        fp.add(new InlineLabel(text));

        SafeHtml safeHtml = SafeHtmlUtils.fromSafeConstant(fp.toString());
        return new Button(safeHtml);
    }

    @Override
    public void hide() {
        super.hide();
        this.removeFromParent();
    }

    @Override
    public void show() {
        super.show();
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                center();
            }
        });
    }

    private static native boolean gsUploadByPostAvailable() /*-{
        return $wnd.gsUploadByPost;
    }-*/;

    private static native void uploadToGenomeSpace(String base64, String mimeString, String identifier) /*-{
        if(!$wnd.gsUploadByPost) return;
        var binary = atob(base64);                 //
        //noinspection JSPrimitiveTypeWrapperUsage
        var array  = new Array();                  // Adapted from
        for(var i = 0; i < binary.length; i++) {   // stackoverflow.com/questions/4998908
            array.push(binary.charCodeAt(i));      // and many similar discussions
        }                                          //
        var uarray = new Uint8Array(array);        //
        var blob = new Blob([uarray], {type: mimeString});
        var formData = new FormData();
        var imageName = "Reactome_pathway_" + identifier + ".png";
        formData.append("webmasterfile", blob, imageName);
        $wnd.gsUploadByPost(formData);
    }-*/;


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/header_icon.png")
        ImageResource headerIcon();

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();

        @Source("images/download_normal.png")
        ImageResource downloadNormal();

        @Source("images/upload_normal.png")
        ImageResource uploadNormal();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("fireworks-ImageDownloadDialog")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/fireworks/util/popups/ImageDownloadDialog.css";

        String popupPanel();

        String analysisPanel();

        String header();

        String headerIcon();

        String headerText();

        String close();

        String unselectable();

        String undraggable();

        String imagePanel();

        String image();

        String downloadPNG();

        String infoLabel();

        String genomespace();
    }
}
