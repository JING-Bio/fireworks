package org.reactome.web.fireworks.controls.settings.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

import java.util.Arrays;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class InteractorsTabPanel extends Composite {
    private EventBus eventBus;
    private ListBox resourcesLB;

    public InteractorsTabPanel(EventBus eventBus) {
        this.eventBus = eventBus;

        Label tabHeader = new Label("Interactor Overlays");
        tabHeader.setStyleName(RESOURCES.getCSS().tabHeader());

        Label lb = new Label("Existing resources:");
        lb.setStyleName(RESOURCES.getCSS().interactorLabel());

        resourcesLB = new ListBox();
        resourcesLB.setMultipleSelect(false);
        setResourcesList(Arrays.asList("Resource 1", "Resource 2", "Resource 3"));

        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().interactorsPanel());
        main.add(tabHeader);
        main.add(lb);
        main.add(resourcesLB);
        initWidget(main);
    }

    private void setResourcesList(List<String> resourcesList){
        for(String name : resourcesList){
            resourcesLB.addItem(name);
        }
    }


    public static Resources RESOURCES;

    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @CssResource.ImportedWithPrefix("fireworks-InteractorsTabPanel")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/fireworks/controls/settings/tabs/InteractorsTabPanel.css";

        String interactorsPanel();

        String interactorLabel();

        String tabHeader();
    }
}
