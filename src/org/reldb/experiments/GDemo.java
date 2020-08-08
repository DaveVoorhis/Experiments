package org.reldb.experiments;

import javax.swing.*;
import java.awt.*;
  
@SuppressWarnings("serial")
class GDemo extends JFrame {
    public void paint(Graphics graphics) {
        graphics.drawLine(100, 100, 250, 250);
    }
  
    public static void main(String[] args) {
        var demo = new GDemo();
        demo.setSize(450, 450);
        demo.setVisible(true);
    }
}
