/*
 * Copyright © 2018 Damien Vergnet
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
package net.darmo_creations.jenealogio.model;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

import net.darmo_creations.jenealogio.gui.components.canvas_view.CardState;

public class CardStateTest {
  @Test
  public void testEquals() {
    assertEquals(new CardState(new Point(), false), new CardState(new Point(), false));
  }

  @Test
  public void testHashcode() {
    assertEquals(new CardState(new Point(), false).hashCode(), new CardState(new Point(), false).hashCode());
  }
}
