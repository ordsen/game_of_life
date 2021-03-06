/**
 * MIT License
 * <p>
 * Copyright (c) 2017 Giorgio Groß
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.giorgio_gross.gol.elements.environment;

import de.giorgio_gross.gol.App;
import de.giorgio_gross.gol.elements.View;

/**
 * View to show the environment on screen.
 */
public class EnvironmentView extends View {
    private int day_cycle_width;
    private int sun_moon_distance;
    private int zenith;
    private final int simulationHeight;
    private int cellHeight;
    private int cellWidth;

    private Environment env;

    public EnvironmentView(int x, int y, int width, int height) {
        super(x, y, width, height);

        simulationHeight = height / App.NUM_ROWS;
        cellHeight = height / App.NUM_ROWS;
        cellWidth = width / App.NUM_COLUMNS;

        zenith = width / 2;
        sun_moon_distance = width;
        day_cycle_width = 2 * width;
    }

    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Override
    public void render() {
        float dayProgress = env.getDayProgression();

        int moonPos = ((int) (dayProgress * day_cycle_width) + zenith) % day_cycle_width;
        int sunPos = (moonPos + sun_moon_distance) % day_cycle_width;

        getContext().background(ColorManager.GetBackground().getRGB());
        getContext().fill(ColorManager.GetLineDark().getRGB());
        getContext().rect(0, 0, getWidth(), simulationHeight);

        drawMoon(moonPos);
        drawSun(sunPos);
        drawZenithMarker();
        drawGrid();

        getContext().fill(ColorManager.GetAccentLight().getRGB());
        getContext().textSize(simulationHeight - 4);
        getContext().text(env.getTotalDayCount().toString(), 10, simulationHeight - 4);
    }

    @Override
    protected void performClickAction() {
        // none
    }

    private void drawGrid() {
        getContext().noFill();
        getContext().stroke(ColorManager.GetLineLight().getRGB());
        // vertical
        for (int i = 1; i < App.NUM_COLUMNS; i++) {
            getContext().line(i * cellWidth, simulationHeight, i * cellWidth, getContext().height);
        }
        // horizontal
        for (int i = 1; i < App.NUM_ROWS; i++) {
            getContext().line(0, i * cellHeight, getContext().width, i * cellHeight);
        }
    }

    private void drawZenithMarker() {
        getContext().noFill();
        getContext().stroke(ColorManager.GetAccentLight().getRGB());
        getContext().line(zenith, simulationHeight / 2 - 10, zenith, simulationHeight / 2 + 10);
    }

    private void drawSun(int x) {
        getContext().fill(ColorManager.GetSunColor().getRGB());
        getContext().stroke(ColorManager.GetSunColor().getRGB());
        getContext().line(x - simulationHeight / 4, simulationHeight / 2,
                x + simulationHeight / 4, simulationHeight / 2);
        getContext().line(x, simulationHeight / 2 - simulationHeight / 4,
                x, simulationHeight / 2 + simulationHeight / 4);
        getContext().line(x - simulationHeight / 5, simulationHeight / 2 - simulationHeight / 5,
                x + simulationHeight / 5, simulationHeight / 2 + simulationHeight / 5);
        getContext().line(x + simulationHeight / 5, simulationHeight / 2 - simulationHeight / 5,
                x - simulationHeight / 5, simulationHeight / 2 + simulationHeight / 5);
        getContext().ellipse(x, simulationHeight / 2, simulationHeight / 4, simulationHeight / 4);
    }

    private void drawMoon(int x) {
        getContext().noStroke();

        getContext().fill(ColorManager.GetMoonColor().getRGB());
        getContext().ellipse(x, simulationHeight / 2, simulationHeight / 4, simulationHeight / 4);

        getContext().fill(ColorManager.GetLineDark().getRGB());
        getContext().ellipse(x + simulationHeight / 15, simulationHeight / 2, simulationHeight / 6, simulationHeight / 6);
    }
}
