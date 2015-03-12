/*
 * Copyright 2015 SIB Visions GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.sibvisions.kitchensink.samples.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.rad.genui.celleditor.UICheckBoxCellEditor;
import javax.rad.genui.celleditor.UIChoiceCellEditor;
import javax.rad.genui.celleditor.UIDateCellEditor;
import javax.rad.genui.celleditor.UILinkedCellEditor;
import javax.rad.genui.celleditor.UITextCellEditor;
import javax.rad.genui.component.UILabel;
import javax.rad.genui.container.UIPanel;
import javax.rad.genui.container.UISplitPanel;
import javax.rad.genui.control.UIEditor;
import javax.rad.genui.control.UITable;
import javax.rad.genui.layout.UIBorderLayout;
import javax.rad.genui.layout.UIFormLayout;
import javax.rad.model.ColumnDefinition;
import javax.rad.model.IDataBook;
import javax.rad.model.IDataRow;
import javax.rad.model.ModelException;
import javax.rad.model.datatype.BigDecimalDataType;
import javax.rad.model.datatype.BooleanDataType;
import javax.rad.model.datatype.StringDataType;
import javax.rad.model.datatype.TimestampDataType;
import javax.rad.model.reference.ReferenceDefinition;
import javax.rad.ui.container.IPanel;

import com.sibvisions.kitchensink.ISample;
import com.sibvisions.kitchensink.samples.AbstractSample;
import com.sibvisions.rad.model.mem.MemDataBook;

/**
 * Demonstrates the data binding capabilities of JVx.
 * 
 * @author Robert Zenz
 */
public class DataBindingSample extends AbstractSample implements ISample
{
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Interface implementation
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCategory()
	{
		return "Model";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPanel getContent() throws Throwable
	{
		BooleanDataType booleanDataType = new BooleanDataType();
		booleanDataType.setCellEditor(new UICheckBoxCellEditor(Boolean.TRUE, Boolean.FALSE));
		
		Object[] allowedValues = new Object[] { "favorite", "important", "readonly", "unreadable" };
		String[] imageNames = new String[] {
				getImagePath("emblem-favorite.png"),
				getImagePath("emblem-important.png"),
				getImagePath("emblem-readonly.png"),
				getImagePath("emblem-unreadable.png")
		};
		
		UIChoiceCellEditor choiceCellEditor = new UIChoiceCellEditor(allowedValues, imageNames, getImagePath("emblem-favorite.png"));
		
		IDataBook typeDataBook = new MemDataBook();
		typeDataBook.getRowDefinition().addColumnDefinition(new ColumnDefinition("ID", new BigDecimalDataType()));
		typeDataBook.getRowDefinition().addColumnDefinition(new ColumnDefinition("NAME", new StringDataType(new UITextCellEditor())));
		typeDataBook.getRowDefinition().addColumnDefinition(new ColumnDefinition("DESCRIPTION", new StringDataType(new UITextCellEditor())));
		typeDataBook.setName("TYPES");
		typeDataBook.open();
		
		typeDataBook.insert(false);
		typeDataBook.setValue("ID", BigDecimal.valueOf(1));
		typeDataBook.setValue("NAME", "Primitive");
		typeDataBook.setValue("DESCRIPTION", "Some primitive type.");
		
		typeDataBook.insert(false);
		typeDataBook.setValue("ID", BigDecimal.valueOf(2));
		typeDataBook.setValue("NAME", "Extended");
		typeDataBook.setValue("DESCRIPTION", "Type extended by unknown people in the interwebs.");
		
		typeDataBook.insert(false);
		typeDataBook.setValue("ID", BigDecimal.valueOf(3));
		typeDataBook.setValue("NAME", "Hyper");
		typeDataBook.setValue("DESCRIPTION", "Basically classified.");
		
		typeDataBook.saveAllRows();
		
		ReferenceDefinition referenceDefinition = new ReferenceDefinition(new String[] { "TYPE_ID", "TYPE_NAME" }, typeDataBook, new String[] { "ID", "NAME" });
		
		IDataBook dataBook = new MemDataBook();
		dataBook.getRowDefinition().addColumnDefinition(new ColumnDefinition("ID", new BigDecimalDataType()));
		dataBook.getRowDefinition().addColumnDefinition(new ColumnDefinition("STRING", new StringDataType(new UITextCellEditor())));
		dataBook.getRowDefinition().addColumnDefinition(new ColumnDefinition("BOOLEAN", booleanDataType));
		dataBook.getRowDefinition().addColumnDefinition(new ColumnDefinition("CHOICE", new StringDataType(choiceCellEditor)));
		dataBook.getRowDefinition().addColumnDefinition(new ColumnDefinition("DATETIME", new TimestampDataType(new UIDateCellEditor("dd.MM.yyyy"))));
		dataBook.getRowDefinition().addColumnDefinition(new ColumnDefinition("TYPE_ID", new BigDecimalDataType()));
		dataBook.getRowDefinition().addColumnDefinition(new ColumnDefinition("TYPE_NAME", new StringDataType(new UILinkedCellEditor(referenceDefinition))));
		dataBook.setName("DATABINDING");
		
		dataBook.open();
		
		dataBook.insert(false);
		dataBook.setValue("ID", BigDecimal.valueOf(1));
		dataBook.setValue("BOOLEAN", Boolean.TRUE);
		dataBook.setValue("STRING", "This is the actual value of the column.");
		dataBook.setValue("CHOICE", null);
		dataBook.setValue("DATETIME", new Date());
		dataBook.setValue("TYPE_ID", BigDecimal.valueOf(1));
		dataBook.setValue("TYPE_NAME", "Primitive");
		
		dataBook.insert(false);
		dataBook.setValue("ID", BigDecimal.valueOf(2));
		dataBook.setValue("BOOLEAN", Boolean.FALSE);
		dataBook.setValue("STRING", "This is the second row.");
		dataBook.setValue("CHOICE", "important");
		dataBook.setValue("DATETIME", null);
		dataBook.setValue("TYPE_ID", BigDecimal.valueOf(3));
		dataBook.setValue("TYPE_NAME", "Hyper");
		
		dataBook.saveAllRows();
		
		dataBook.setSelectedRow(0);
		
		UITable table = new UITable(dataBook);
		
		UIFormLayout editorsPaneLayout = new UIFormLayout();
		editorsPaneLayout.setNewlineCount(2);
		
		UIPanel editorsPane = new UIPanel();
		editorsPane.setLayout(editorsPaneLayout);
		
		addEditor(editorsPane, dataBook, "ID");
		addEditor(editorsPane, dataBook, "BOOLEAN");
		addEditor(editorsPane, dataBook, "STRING");
		addEditor(editorsPane, dataBook, "CHOICE");
		addEditor(editorsPane, dataBook, "DATETIME");
		addEditor(editorsPane, dataBook, "TYPE_ID");
		addEditor(editorsPane, dataBook, "TYPE_NAME");
		
		UISplitPanel splitPanel = new UISplitPanel(UISplitPanel.SPLIT_LEFT_RIGHT);
		splitPanel.setDividerAlignment(UISplitPanel.DIVIDER_BOTTOM_RIGHT);
		splitPanel.setFirstComponent(table);
		splitPanel.setSecondComponent(editorsPane);
		
		UIPanel content = new UIPanel();
		content.setLayout(new UIBorderLayout());
		content.add(splitPanel, UIBorderLayout.CENTER);
		
		return content;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName()
	{
		return "DataBinding";
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// User-defined methods
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * Adds the editor.
	 *
	 * @param pPanel the panel.
	 * @param pDataRow the data row.
	 * @param pColumnName the column name.
	 * @throws ModelException the model exception
	 */
	private void addEditor(UIPanel pPanel, IDataRow pDataRow, String pColumnName) throws ModelException
	{
		pPanel.add(new UILabel(pColumnName));
		pPanel.add(new UIEditor(pDataRow, pColumnName));
	}
	
}	// DataBindingSample
