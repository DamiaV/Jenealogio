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
package net.darmo_creations.jenealogio.util;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

public class ImagesTest {
  private BufferedImage img;

  @Before
  public void setUp() throws Exception {
    this.img = ImageIO.read(getClass().getResourceAsStream("/net/darmo_creations/jenealogio/util/lena.bmp"));
  }

  @Test
  public void testDeepCopy() {
    assertEquals(Images.hashCode(this.img), Images.hashCode(Images.deepCopy(this.img)));
  }
}
