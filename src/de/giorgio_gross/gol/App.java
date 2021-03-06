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

package de.giorgio_gross.gol;

import de.giorgio_gross.gol.data.CellStateManager;
import de.giorgio_gross.gol.elements.Element;
import de.giorgio_gross.gol.elements.OnClickListener;
import de.giorgio_gross.gol.elements.cell.Cell;
import de.giorgio_gross.gol.elements.cell.CellView;
import de.giorgio_gross.gol.elements.environment.Environment;
import de.giorgio_gross.gol.elements.environment.EnvironmentView;
import processing.core.PApplet;

import java.util.ArrayList;

/**
 * Root class for the Game of Live App. Here happens initialization, kick off of rendering, management of UI's and
 * delegation of sprite rendering.
 */
public class App extends PApplet {
    /* global variables */
    public static final int NUM_COLUMNS = 32;
    public static final int NUM_ROWS = 20 + 1; // +1 for sun and moon simulation
    public static final int MAX_MS_PER_CYCLE = 20000;  // max 20sec day cycle
    public static final int MIN_MS_PER_CYCLE = 400;  // min 400ms day cycle
    public static final int DELTA_MS_PER_CYCLE = 400;  // manipulate in +/- 400ms steps

    /* private data */
    private int msPerCycle = 8000;  // default is 4s day, 4s night
    private static App instance;

    private ArrayList<OnClickListener> clickListeners = new ArrayList<OnClickListener>();
    private ArrayList<OnSettingsChangedListener> settingsListeners = new ArrayList<OnSettingsChangedListener>();
    private ArrayList<Element> sceneElements = new ArrayList<Element>();
    private Environment env;

    /* event providers */
    private EventProvider<OnClickListener> clickProvider = new EventProvider<OnClickListener>() {

        @Override
        public void register(OnClickListener eventListener) {
            clickListeners.add(eventListener);
        }

    };
    private EventProvider<OnSettingsChangedListener> settingsChangedProvider = new EventProvider<OnSettingsChangedListener>() {

        @Override
        public void register(OnSettingsChangedListener eventListener) {
            settingsListeners.add(eventListener);
        }

    };

    /**
     * Don't fully implement singleton pattern with private constructor to allow PApplet to instantiate App.
     * Yet, there should only be one App instance!
     */
    public App() {
        if (App.instance != null) throw new IllegalAccessError();
        App.instance = this;
    }

    /**
     * Get an instance of the PApplet context to allow all components drawing on the screen
     *
     * @return active PApplet instance
     */
    public static PApplet getInstance() {
        return App.instance;
    }

    /**
     * Starts up the app
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        PApplet.main("de.giorgio_gross.gol.App", args);
    }

    @Override
    public void settings() {
        // frameRate(DESIRED_FPS);  // unstable on macOS
        // fullScreen();  // buggy on retina displays
        size(displayWidth / 3 * 2, displayHeight / 3 * 2);
        // pixelDensity(2);  // for retina displays, poor fps

        initDataStructures();
    }

    private void initDataStructures() {
        // create and bind environment to its view
        EnvironmentView envView = new EnvironmentView(0, 0, width, height);
        env = new Environment(envView, msPerCycle);
        settingsChangedProvider.register(env);
        envView.setEnvironment(env);

        // init cell state manager
        CellStateManager csManager = new CellStateManager();
        env.register(csManager);

        // init cells
        int cellHeight = height / App.NUM_ROWS;
        int cellWidth = width / App.NUM_COLUMNS;
        for (int idx_x = 0; idx_x < NUM_COLUMNS; idx_x++) {
            for (int idx_y = 0; idx_y < NUM_ROWS - 1; idx_y++) {
                // create and bind cell to its view
                CellView cv = new CellView(idx_x * cellWidth, (idx_y + 1) * cellHeight, cellWidth, cellHeight);
                Cell c = new Cell(cv, csManager, idx_x, idx_y);
                cv.setCell(c);

                env.register(cv);  // environment listener
                clickProvider.register(c);  // click listener

                sceneElements.add(c);
            }
        }
    }

    @Override
    public void setup() {
        background(0);
    }

    @Override
    public void draw() {
        // firstly, render the environment
        env.executeLoopCycle();

        // now render all other objects on the screen
        for (Element e : sceneElements) {
            e.executeLoopCycle();
        }
    }

    @Override
    public void keyTyped() {
        if (key == '-' && msPerCycle < MAX_MS_PER_CYCLE) {  // + looks like things becoming slower to the user
            msPerCycle += DELTA_MS_PER_CYCLE;
        }
        if (key == '+' && msPerCycle > MIN_MS_PER_CYCLE) {  // + looks like things becoming faster to the user
            msPerCycle -= DELTA_MS_PER_CYCLE;
        }

        for(OnSettingsChangedListener l : settingsListeners) {
            l.onSetDayCycle(msPerCycle);
        }
    }

    @Override
    public void mouseClicked() {
        for (OnClickListener cl : clickListeners) {
            if (cl.onClick(mouseX, mouseY)) {
                return;
            }
        }
    }

}
