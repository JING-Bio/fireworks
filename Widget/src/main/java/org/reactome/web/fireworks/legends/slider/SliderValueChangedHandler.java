package org.reactome.web.fireworks.legends.slider;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface SliderValueChangedHandler extends EventHandler {

    void onSliderValueChanged(SliderValueChangedEvent event);

}
