package org.reactome.web.fireworks.search.searchbox;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A common TextBox with an extra event (SearchBoxUpdatedEvent) that
 * is fired when the user has changed the content of the box.
 *
 * It waits for the specified time without changes in the value to
 * fire the event to avoid unnecessary searches while the user is
 * still typing.
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SearchBox extends TextBox implements KeyUpHandler, KeyDownHandler {

    private final int searchDelay;
    private final Timer timer;
    private String value;

    public SearchBox() {
        this(250);
    }

    public SearchBox(int searchDelay) {
        this.searchDelay = searchDelay;
        this.timer = new Timer() {
            @Override
            public void run() {
                checkContent();
            }
        };
        this.value = "";
        addKeyUpHandler(this);
        addKeyDownHandler(this);
    }

    public HandlerRegistration addSearchBoxUpdatedHandler(SearchBoxUpdatedHandler handler){
        return addHandler(handler, SearchBoxUpdatedEvent.TYPE);
    }

    public HandlerRegistration addSearchBoxArrowKeysHandler(SearchBoxArrowKeysHandler handler){
        return addHandler(handler, SearchBoxArrowKeysEvent.TYPE);
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        int keyCode = event.getNativeKeyCode();
        switch (keyCode){
            case KeyCodes.KEY_UP:
                event.stopPropagation(); event.preventDefault();
                fireEvent(new SearchBoxArrowKeysEvent(keyCode));
                break;
            case KeyCodes.KEY_DOWN:
                event.stopPropagation(); event.preventDefault();
                fireEvent(new SearchBoxArrowKeysEvent(keyCode));
                break;
            case KeyCodes.KEY_ENTER:
                event.stopPropagation(); event.preventDefault();
                fireEvent(new SearchBoxArrowKeysEvent(keyCode));
                break;
            case KeyCodes.KEY_ESCAPE:
                event.stopPropagation(); event.preventDefault();
                fireEvent(new SearchBoxArrowKeysEvent(keyCode));
                break;
        }
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (timer.isRunning()) {
            timer.cancel();
        }
        timer.schedule(this.searchDelay);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        this.value = value;
        if(value==null || value.isEmpty()){
            this.fireEvent(new SearchBoxUpdatedEvent(value));
        }
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        super.setValue(value, fireEvents);
        this.value = value;
        if(value==null || value.isEmpty()){
            this.fireEvent(new SearchBoxUpdatedEvent(value));
        }
    }

    protected void checkContent(){
        String value = getValue();
        if(!this.value.equals(value)){
            this.value = value;
            this.fireEvent(new SearchBoxUpdatedEvent(value));
        }
    }
}
