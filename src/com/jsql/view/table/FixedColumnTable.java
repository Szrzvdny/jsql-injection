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
package com.jsql.view.table;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/*
 *  Prevent the specified number of columns from scrolling horizontally in
 *  the scroll pane. The table must already exist in the scroll pane.
 *
 *  The functionality is accomplished by creating a second JTable (fixed)
 *  that will share the TableModel and SelectionModel of the main table.
 *  This table will be used as the row header of the scroll pane.
 *
 *  The fixed table created can be accessed by using the getFixedTable()
 *  method. will be returned from this method. It will allow you to:
 *
 *  You can change the model of the main table and the change will be
 *  reflected in the fixed model. However, you cannot change the structure
 *  of the model.
 */
public class FixedColumnTable implements ChangeListener, PropertyChangeListener
{
    private JTable main;
    private JTable fixed;
    private JScrollPane scrollPane;

    /*
     *  Specify the number of columns to be fixed and the scroll pane
     *  containing the table.
     */
    @SuppressWarnings("serial")
	public FixedColumnTable(int fixedColumns, JScrollPane scrollPane)
    {
        this.scrollPane = scrollPane;

        main = ((JTable)scrollPane.getViewport().getView());
        main.setAutoCreateColumnsFromModel( false );
        main.addPropertyChangeListener( this );

        //  Use the existing table to create a new table sharing
        //  the DataModel and ListSelectionModel

//        int totalColumns = main.getColumnCount();

        fixed = new JTable(){
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };
        fixed.setAutoCreateColumnsFromModel( false );
        fixed.setRowHeight(20);
        fixed.setModel( main.getModel() );
        fixed.setSelectionModel( main.getSelectionModel() );
        fixed.setFocusable( false );
        fixed.getTableHeader().setReorderingAllowed(false);
        
        fixed.setGridColor(Color.LIGHT_GRAY);
        
        final TableCellRenderer tcrOs = fixed.getTableHeader().getDefaultRenderer();
        fixed.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                JLabel lbl = (JLabel) tcrOs.getTableCellRendererComponent(table,
                        value, isSelected, hasFocus, row, column);
                lbl.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.LIGHT_GRAY));
                if(column == 1)
                    lbl.setBackground(new Color(230,230,230));
                return lbl;
            }
        });
        
        //  Remove the fixed columns from the main table
        //  and add them to the fixed table

        for (int i = 0; i < fixedColumns; i++)
        {
            TableColumnModel columnModel = main.getColumnModel();
            TableColumn column = columnModel.getColumn( 0 );
            columnModel.removeColumn( column );
            fixed.getColumnModel().addColumn( column );
        }

        //  Add the fixed table to the scroll pane

        fixed.setPreferredScrollableViewportSize(fixed.getPreferredSize());
        scrollPane.setRowHeaderView( fixed );
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixed.getTableHeader());

        // Synchronize scrolling of the row header with the main table

        scrollPane.getRowHeader().addChangeListener( this );
    }

    /*
     *  Return the table being used in the row header
     */
    public JTable getFixedTable()
    {
        return fixed;
    }
//
//  Implement the ChangeListener
//
    public void stateChanged(ChangeEvent e)
    {
        //  Sync the scroll pane scrollbar with the row header

        JViewport viewport = (JViewport) e.getSource();
        scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
    }
//
//  Implement the PropertyChangeListener
//
    public void propertyChange(PropertyChangeEvent e)
    {
        //  Keep the fixed table in sync with the main table

        if ("selectionModel".equals(e.getPropertyName()))
        {
            fixed.setSelectionModel( main.getSelectionModel() );
        }

        if ("model".equals(e.getPropertyName()))
        {
            fixed.setModel( main.getModel() );
        }
    }
}
