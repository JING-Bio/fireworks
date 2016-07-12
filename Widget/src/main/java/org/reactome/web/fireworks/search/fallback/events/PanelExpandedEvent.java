package org.reactome.web.fireworks.search.fallback.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.fireworks.search.fallback.handlers.PanelExpandedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class PanelExpandedEvent extends GwtEvent<PanelExpandedHandler> {
    public static Type<PanelExpandedHandler> TYPE = new Type<>();

    @Override
    public Type<PanelExpandedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PanelExpandedHandler handler) {
        handler.onPanelExpanded(this);
    }

}
