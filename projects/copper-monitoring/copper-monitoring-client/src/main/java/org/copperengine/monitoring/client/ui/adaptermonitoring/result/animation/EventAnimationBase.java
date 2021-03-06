/*
 * Copyright 2002-2014 SCOOP Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.copperengine.monitoring.client.ui.adaptermonitoring.result.animation;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

public class EventAnimationBase extends AnimationPartBase {

    public EventAnimationBase(Color color, AnimationPartParameter animationPartBaseParameter) {
        super(animationPartBaseParameter);
        this.color = color;
    }

    public static final int EVENT_HEIGHT = 35;
    public static final int EVENT_WIDTH = 110;
    final Color color;

    @Override
    public Node createVisualRepresentation() {
        Pane pane = new Pane();
        final Rectangle rectangle = new Rectangle(EVENT_WIDTH, EVENT_HEIGHT);
        rectangle.setFill(color);
        rectangle.setArcHeight(20);
        rectangle.setArcWidth(20);
        final Text text = new Text(getDisplayText());
        text.setFontSmoothingType(FontSmoothingType.LCD);
        final double textWidth = text.getBoundsInLocal().getWidth();
        final double testHeight = text.getBoundsInLocal().getHeight();
        text.setX((rectangle.getWidth() / 2)-textWidth / 2);
        text.setY(rectangle.getHeight() / 2+testHeight / 4);

        pane.getChildren().add(rectangle);
        pane.getChildren().add(text);
        return pane;
    }

    protected String getDisplayText() {
        return id;
    }

}
