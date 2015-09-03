package org.reactome.web.fireworks.launcher.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.reactome.web.fireworks.launcher.search.events.PanelCollapsedEvent;
import org.reactome.web.fireworks.launcher.search.events.PanelExpandedEvent;
import org.reactome.web.fireworks.launcher.search.events.SearchPerformedEvent;
import org.reactome.web.fireworks.launcher.search.events.SuggestionSelectedEvent;
import org.reactome.web.fireworks.launcher.search.handlers.PanelCollapsedHandler;
import org.reactome.web.fireworks.launcher.search.handlers.PanelExpandedHandler;
import org.reactome.web.fireworks.launcher.search.handlers.SearchPerformedHandler;
import org.reactome.web.fireworks.launcher.search.handlers.SuggestionSelectedHandler;
import org.reactome.web.fireworks.launcher.search.provider.SuggestionsProvider;
import org.reactome.web.fireworks.launcher.search.provider.SuggestionsProviderImpl;
import org.reactome.web.fireworks.launcher.search.searchbox.*;
import org.reactome.web.fireworks.legends.ControlButton;
import org.reactome.web.fireworks.model.Graph;
import org.reactome.web.fireworks.model.Node;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SearchLauncher extends AbsolutePanel implements ClickHandler, SearchBoxUpdatedHandler,
        SuggestionSelectedHandler, SearchBoxArrowKeysHandler {

    @SuppressWarnings("FieldCanBeLocal")
    private static String OPENING_TEXT = "Search for a pathway ...";

    private EventBus eventBus;
    private SuggestionsProvider<Node> suggestionsProvider;

    private SearchBox input = null;
    private ControlButton searchBtn = null;

    private Boolean isExpanded = false;

    public SearchLauncher(EventBus eventBus, Graph graph) {
        //Setting the search style
        setStyleName(RESOURCES.getCSS().launchPanel());

        this.eventBus = eventBus;
        this.suggestionsProvider = new SuggestionsProviderImpl(graph);

        this.searchBtn = new ControlButton("Search pathways", RESOURCES.getCSS().launch(), this);
        this.add(searchBtn);

        this.input = new SearchBox();
        this.input.setStyleName(RESOURCES.getCSS().input());
        this.input.getElement().setPropertyString("placeholder", OPENING_TEXT);
        this.add(input);

        this.initHandlers();
        this.searchBtn.setEnabled(true);
    }

    public HandlerRegistration addPanelCollapsedHandler(PanelCollapsedHandler handler){
        return addHandler(handler, PanelCollapsedEvent.TYPE);
    }

    public HandlerRegistration addPanelExpandedHandler(PanelExpandedHandler handler){
        return addHandler(handler, PanelExpandedEvent.TYPE);
    }

    public HandlerRegistration addSearchBoxArrowKeysHandler(SearchBoxArrowKeysHandler handler){
        return input.addSearchBoxArrowKeysHandler(handler);
    }

    public HandlerRegistration addSearchPerformedHandler(SearchPerformedHandler handler){
        return addHandler(handler, SearchPerformedEvent.TYPE);
    }

    @Override
    public void onClick(ClickEvent event) {
        if(event.getSource().equals(this.searchBtn)){
            if(!isExpanded){
                expandPanel();
            }else{
                collapsePanel();
            }
        }
    }

    @Override
    public void onSearchUpdated(SearchBoxUpdatedEvent event) {
        if(suggestionsProvider!=null) {
            List<Node> suggestions = suggestionsProvider.getSuggestions(input.getText().trim());
            fireEvent(new SearchPerformedEvent(suggestions));
        }
    }
    @Override
    public void onSuggestionSelected(SuggestionSelectedEvent event) {
        eventBus.fireEventFromSource(event, this);
    }

    private void collapsePanel(){
        removeStyleName(RESOURCES.getCSS().launchPanelExpanded());
        input.removeStyleName(RESOURCES.getCSS().inputActive());
        isExpanded = false;
        fireEvent(new PanelCollapsedEvent());
    }

    private void expandPanel(){
        addStyleName(RESOURCES.getCSS().launchPanelExpanded());
        input.addStyleName(RESOURCES.getCSS().inputActive());
        isExpanded = true;
        fireEvent(new PanelExpandedEvent());
    }

    private void initHandlers(){
        this.input.addSearchBoxUpdatedHandler(this);
        this.input.addSearchBoxArrowKeysHandler(this);
    }

    public void setFocus(boolean focused){
        this.input.setFocus(focused);
    }


    public static SearchLauncherResources RESOURCES;
    static {
        RESOURCES = GWT.create(SearchLauncherResources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    @Override
    public void onKeysPressed(SearchBoxArrowKeysEvent event) {
        if(event.getValue() == KeyCodes.KEY_ESCAPE) {
            setFocus(false);
            this.collapsePanel();
        }
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface SearchLauncherResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(SearchLauncherCSS.CSS)
        SearchLauncherCSS getCSS();

        @Source("images/search_clicked.png")
        ImageResource launchClicked();

        @Source("images/search_disabled.png")
        ImageResource launchDisabled();

        @Source("images/search_hovered.png")
        ImageResource launchHovered();

        @Source("images/search_normal.png")
        ImageResource launchNormal();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-SearchLauncher")
    public interface SearchLauncherCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/fireworks/launcher/search/SearchLauncher.css";

        String launchPanel();

        String launchPanelExpanded();

        String launch();

        String input();

        String inputActive();
    }

}
