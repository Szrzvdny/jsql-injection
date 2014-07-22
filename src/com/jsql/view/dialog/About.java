/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.jsql.view.GUIMediator;
import com.jsql.view.GUITools;
import com.jsql.view.component.RoundBorder;
import com.jsql.view.component.RoundScroller;
import com.jsql.view.component.popupmenu.JPopupTextComponentMenu;

@SuppressWarnings("serial")
public class About extends JDialog{
	
    public JButton close = null;
    RoundScroller scrollPane;

    public About(){
        super(GUIMediator.gui(), "About jSQL Injection", Dialog.ModalityType.MODELESS);
        
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Define a small and large app icon
        this.setIconImages(GUITools.getIcons());

        // Action for ESCAPE key
        ActionListener escapeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                About.this.dispose();
            }
        };

        this.getRootPane().registerKeyboardAction(escapeListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        JPanel lastLine = new JPanel();
        lastLine.setLayout(new BoxLayout(lastLine, BoxLayout.LINE_AXIS));
        lastLine.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        close = new JButton("Close");
        close.setBorder(new RoundBorder(20, 3, true));
        close.addActionListener(escapeListener);

        this.setLayout(new BorderLayout());
        Container dialogPane = this.getContentPane();
        JButton webpage = new JButton("Webpage");
        webpage.setBorder(new RoundBorder(20, 3, true));
        webpage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    Desktop.getDesktop().browse(new URI("http://code.google.com/p/jsql-injection/"));
                } catch (IOException e) {
                    GUIMediator.model().sendDebugMessage(e);
                } catch (URISyntaxException e) {
                    GUIMediator.model().sendDebugMessage(e);
                }
            }
        });
        lastLine.add(webpage);
        lastLine.add(Box.createGlue());
        lastLine.add(close);

        JLabel iconJSQL = new JLabel(new ImageIcon(GUIMediator.gui().getClass().getResource("/com/jsql/view/images/app-32x32.png")));
        iconJSQL.setBorder(BorderFactory.createEmptyBorder(2, 15, 2, 15));
        dialogPane.add(iconJSQL, BorderLayout.WEST);
        dialogPane.add(lastLine, BorderLayout.SOUTH);

        // Contact info, use HTML text
        final JEditorPane text[] = new JEditorPane[1];
		try {
			text[0] = new JEditorPane(About.class.getResource("about.htm"));
		} catch (IOException e) {
			GUIMediator.model().sendDebugMessage(e);
		}

		text[0].setComponentPopupMenu(new JPopupTextComponentMenu(text[0]));

        text[0].addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                text[0].requestFocusInWindow();
            }
        });
        
        text[0].addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent arg0) {
                text[0].getCaret().setVisible(true);
                text[0].getCaret().setSelectionVisible(true);
            }
        });

        text[0].setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        text[0].setDragEnabled(true);
        text[0].setEditable(false);

        text[0].addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent hle) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                    try {
                        Desktop.getDesktop().browse(hle.getURL().toURI());
                    } catch (IOException e) {
                    	GUIMediator.model().sendErrorMessage(e.getMessage());
                    } catch (URISyntaxException e) {
                        GUIMediator.model().sendDebugMessage(e);
                    }
                }
            }
        });

        scrollPane = new RoundScroller(text[0]);
        dialogPane.add(scrollPane, BorderLayout.CENTER);

        reinit();
    }

    public void reinit(){
        scrollPane.getViewport().setViewPosition(new Point(0,0));
        this.setSize(400, 300);
        this.setLocationRelativeTo(GUIMediator.gui());
        close.requestFocusInWindow();
        this.getRootPane().setDefaultButton(close);
    }
}
