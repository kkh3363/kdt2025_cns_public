package mygui.boardapp.controller;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import mygui.boardapp.model.common.DBHelper;
import mygui.boardapp.model.dao.BoardDaoImp;
import mygui.boardapp.model.dto.BoardDto;

public class DbManager {
	private static DbManager dbManager;
	BoardDaoImp boardImp;
	Connection conn;
	
	List<BoardDto> boardList ;
	
	private DbManager() {
		conn = DBHelper.getInstance().open();
		boardImp = new BoardDaoImp(conn);
	}
	
	public static DbManager getInstance() {
		if (dbManager == null) {
			dbManager = new DbManager();
		}
		return dbManager;
	}
	
	/**
	 * 
	 */
	public void loadDataToJTable( JTable jtable) {
		boardList = boardImp.select();
		DefaultTableModel tableModel = (DefaultTableModel)jtable.getModel();
		
		for (BoardDto item: boardList ) {
			Object[] rowData ={item.getNo(), item.getTitle(),item.getWriter(), item.getDate(),item.getHitcount()};
			tableModel.addRow(rowData);
		}
	}
	
	public BoardDto getBoardOne(int nIndex) {
		return boardList.get(nIndex);
	}
}
