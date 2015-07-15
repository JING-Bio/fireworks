package org.reactome.web.fireworks.launcher.search.suggester;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.Event;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.reactome.web.fireworks.launcher.search.events.SearchPerformedEvent;
import org.reactome.web.fireworks.launcher.search.events.SuggestionSelectedEvent;
import org.reactome.web.fireworks.launcher.search.handlers.SearchPerformedHandler;
import org.reactome.web.fireworks.launcher.search.handlers.SuggestionSelectedHandler;
import org.reactome.web.fireworks.launcher.search.panels.AbstractAccordionPanel;
import org.reactome.web.fireworks.launcher.search.searchbox.SearchBoxArrowKeysEvent;
import org.reactome.web.fireworks.launcher.search.searchbox.SearchBoxArrowKeysHandler;
import org.reactome.web.fireworks.model.Node;


import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SuggestionPanel extends AbstractAccordionPanel implements SearchPerformedHandler, SearchBoxArrowKeysHandler,
        SelectionChangeEvent.Handler {
    private final SingleSelectionModel<Node> selectionModel;
    private CellList<Node> suggestions;
    private ListDataProvider<Node> dataProvider;

    public static SuggestionResources RESOURCES;

    static {
        RESOURCES = GWT.create(SuggestionResources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * The key provider that provides the unique ID of a DatabaseObject.
     */
    public static final ProvidesKey<Node> KEY_PROVIDER = new ProvidesKey<Node>() {
        @Override
        public Object getKey(Node item) {
            return item == null ? null : item.getDbId();
        }
    };

    public SuggestionPanel() {
        this.sinkEvents(Event.ONCLICK);

        // Add a selection model so we can select cells.
        selectionModel = new SingleSelectionModel<Node>(KEY_PROVIDER);
        selectionModel.addSelectionChangeHandler(this);

        SuggestionCell suggestionCell = new SuggestionCell();

        suggestions = new CellList<Node>(suggestionCell, KEY_PROVIDER);
        suggestions.sinkEvents(Event.FOCUSEVENTS);
        suggestions.setSelectionModel(selectionModel);

        suggestions.setKeyboardPagingPolicy(HasKeyboardPagingPolicy.KeyboardPagingPolicy.INCREASE_RANGE);
        suggestions.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);

        this.add(suggestions);
        //Setting the legend style
        setStyleName(RESOURCES.getCSS().suggestionPanel());
    }

    public HandlerRegistration addClickHandler(ClickHandler handler){
        return this.addHandler(handler, ClickEvent.getType());
    }

    public HandlerRegistration addSuggestionSelectedHandler(SuggestionSelectedHandler handler) {
        return addHandler(handler, SuggestionSelectedEvent.TYPE);
    }

    @Override
    public void onArrowKeysPressed(SearchBoxArrowKeysEvent event) {
        if(suggestions.getRowCount()>0){
            Node current = selectionModel.getSelectedObject();
            int currentIndex = current==null?-1:dataProvider.getList().indexOf(current);
            int toIndex = currentIndex;
            if(event.getValue() == KeyCodes.KEY_DOWN) {
                toIndex = currentIndex + 1 < dataProvider.getList().size() ? currentIndex + 1 : dataProvider.getList().size() - 1;
            }else if(event.getValue() == KeyCodes.KEY_UP) {
                toIndex = currentIndex - 1 > 0 ? currentIndex - 1 : 0;
            }
            if(toIndex!=-1 && toIndex!=currentIndex) {
                Node newSelection = dataProvider.getList().get(toIndex);
                suggestions.getRowElement(toIndex).scrollIntoView();
                selectionModel.setSelected(newSelection, true);
            }
        }
    }

    @Override
    public void onSearchPerformed(SearchPerformedEvent event) {
        Node sel = selectionModel.getSelectedObject();
        List<Node> searchResult = event.getSuggestions();
        if(!searchResult.isEmpty() && !searchResult.contains(sel)) selectionModel.clear();

        dataProvider = new ListDataProvider<Node>(searchResult);
        dataProvider.addDataDisplay(this.suggestions);
        if (dataProvider.getList().isEmpty()) {
            fireEvent(new SuggestionSelectedEvent(null));
        }
    }

    @Override
    public void onSelectionChange(SelectionChangeEvent event) {
        fireEvent(new SuggestionSelectedEvent(selectionModel.getSelectedObject()));
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface SuggestionResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(SuggestionPanelCSS.CSS)
        SuggestionPanelCSS getCSS();

        @Source("images/pathway.png")
        ImageResource pathway();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("fireworks-SuggestionPanel")
    public interface SuggestionPanelCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/fireworks/launcher/search/suggester/SuggestionPanel.css";

        String suggestionPanel();
    }

}
