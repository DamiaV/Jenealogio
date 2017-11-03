/*
 * Copyright © 2017 Damien Vergnet
 * 
 * This file is part of Jenealogio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.darmo_creations.jenealogio.gui.components.display_panel;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

import javax.swing.SwingUtilities;

import net.darmo_creations.gui_framework.ApplicationRegistry;
import net.darmo_creations.jenealogio.events.CardDragEvent;
import net.darmo_creations.jenealogio.events.CardEvent;
import net.darmo_creations.jenealogio.events.CardsSelectionEvent;
import net.darmo_creations.jenealogio.events.LinkEvent;
import net.darmo_creations.utils.events.SubsribeEvent;

/**
 * This controller handles cards and links selection and notifies the DisplayPanel.
 * 
 * @author Damien Vergnet
 */
class DisplayController extends MouseAdapter {
  private DisplayPanel panel;
  private Point mouseLocation;
  private Point selectionStart;
  private Rectangle selection;

  DisplayController(DisplayPanel panel) {
    this.panel = panel;
    this.mouseLocation = new Point();
    this.selectionStart = null;
    this.selection = null;
  }

  /**
   * @return the last location of the mouse
   */
  Point getMouseLocation() {
    return (Point) this.mouseLocation.clone();
  }

  /**
   * @return selection's starting position
   */
  Optional<Rectangle> getSelection() {
    return Optional.ofNullable(this.selection);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (SwingUtilities.isLeftMouseButton(e)) {
      this.selectionStart = e.getPoint();
      this.selection = new Rectangle(this.selectionStart);
      ApplicationRegistry.EVENTS_BUS.dispatchEvent(new CardEvent.Clicked(-1, false));
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (SwingUtilities.isLeftMouseButton(e)) {
      Optional<long[]> opt = this.panel.getPanelsInsideRectangle(this.selection);
      if (opt.isPresent()) {
        ApplicationRegistry.EVENTS_BUS.dispatchEvent(new CardsSelectionEvent(opt.get()));
      }
      this.selection = null;
      repaint();
    }
  }

  /**
   * Deselects all cards and checks if a link was clicked or double-clicked.
   */
  @Override
  public void mouseClicked(MouseEvent e) {
    if (SwingUtilities.isLeftMouseButton(e)) {
      Optional<long[]> l = this.panel.getHoveredLinkPartners();

      if (l.isPresent()) {
        long[] ids = l.get();
        ApplicationRegistry.EVENTS_BUS.dispatchEvent(new LinkEvent.Clicked(ids[0], ids[1]));
        if (e.getClickCount() == 2)
          ApplicationRegistry.EVENTS_BUS.dispatchEvent(new LinkEvent.DoubleClicked(ids[0], ids[1]));
      }
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    updateMouseLocation(e);
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    Point prevLocation = this.mouseLocation;

    updateMouseLocation(e);
    if (SwingUtilities.isMiddleMouseButton(e)) {
      Point newLocation = this.mouseLocation;

      int xTrans = newLocation.x - prevLocation.x;
      int yTrans = newLocation.y - prevLocation.y;

      this.panel.setHorizontalScroll(this.panel.getHorizontalScroll() - xTrans);
      this.panel.setVerticalScroll(this.panel.getVerticalScroll() - yTrans);
    }
  }

  @SubsribeEvent
  public void onCardDragged(CardDragEvent.Dragging e) {
    this.mouseLocation = e.getMouseLocation();
    resizePanelIfOutside();
    scrollIfOutside();
  }

  private void updateMouseLocation(MouseEvent e) {
    this.mouseLocation = e.getPoint();
    // Selection computation
    if (this.selection != null) {
      int width = this.mouseLocation.x - this.selectionStart.x;
      int height = this.mouseLocation.y - this.selectionStart.y;

      if (width < 0) {
        width = -width;
        this.selection.x = this.selectionStart.x - width;
        this.selection.width = width;
      }
      else {
        this.selection.width = width;
      }
      if (height < 0) {
        height = -height;
        this.selection.y = this.selectionStart.y - height;
        this.selection.height = height;
      }
      else {
        this.selection.height = height;
      }
      scrollIfOutside();
    }
    repaint();
  }

  private static final int INSIDE = 0;
  private static final int LEFT = 1;
  private static final int RIGHT = 2;
  private static final int TOP = 4;
  private static final int BOTTOM = 8;

  private int isOutsideRectangle(Point p, Rectangle r) {
    if (p.getX() < r.x) {
      return LEFT;
    }
    else if (p.getX() > r.x + r.width) {
      return RIGHT;
    }
    if (p.getY() < r.y) {
      return TOP;
    }
    else if (p.getY() > r.y + r.height) {
      return BOTTOM;
    }
    return INSIDE;
  }

  /**
   * Resizes the panel if a component is dragged outside.
   */
  private void resizePanelIfOutside() {
    Rectangle r = this.panel.getBounds();
    r.x = r.y = 0;
    int mouse = isOutsideRectangle(this.mouseLocation, r);
    int vAdd = 0;
    int hAdd = 0;
    final int step = 30;

    switch (mouse) {
      case RIGHT:
        hAdd = step;
        break;
      case BOTTOM:
        vAdd = step;
        break;
    }

    if (vAdd != 0 || hAdd != 0) {
      Dimension d = this.panel.getSize();
      this.panel.setPreferredSize(new Dimension(d.width + hAdd, d.height + vAdd));
      this.panel.revalidate();
      repaint();
    }
  }

  /**
   * Scrolls if the mouse is outside the viewport.
   */
  private void scrollIfOutside() {
    int mouse = isOutsideRectangle(this.mouseLocation, this.panel.getVisibleRect());
    int vTrans = 0;
    int hTrans = 0;
    final int step = 16;

    switch (mouse) {
      case LEFT:
        hTrans = -step;
        break;
      case RIGHT:
        hTrans = step;
        break;
      case TOP:
        vTrans = -step;
        break;
      case BOTTOM:
        vTrans = step;
        break;
    }

    if (vTrans != 0)
      this.panel.setVerticalScroll(this.panel.getVerticalScroll() + vTrans);
    if (hTrans != 0)
      this.panel.setHorizontalScroll(this.panel.getHorizontalScroll() + hTrans);
  }

  private void repaint() {
    this.panel.repaint();
  }
}