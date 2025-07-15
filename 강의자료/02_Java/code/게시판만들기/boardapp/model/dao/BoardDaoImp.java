package mygui.boardapp.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import mygui.boardapp.model.dto.BoardDto;

public class BoardDaoImp implements IBoardDao {
	private final String BoardTable = "tb_board";
	private Connection conn;
	
	public BoardDaoImp(Connection conn) {
		this.conn = conn;
	}
	@Override
	public int insert(BoardDto params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(int params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(BoardDto params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BoardDto selectOne(int params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BoardDto> select() {
		List<BoardDto> result = null;
		String sql ="select * from " + BoardTable;
		PreparedStatement pstmt =null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement(sql );
			rs = pstmt.executeQuery();
			
			result = new ArrayList<BoardDto>();
			while(rs.next()) {
				BoardDto item = new BoardDto();
				item.setNo(rs.getInt("no"));
				item.setTitle(rs.getString("title"));
				item.setWriter(rs.getString("writer"));
                item.setContent(rs.getString("content"));
                item.setDate(rs.getString("date"));
                item.setHitcount(rs.getInt("hitcount"));
                
                result.add(item);
			}
			
		}catch (SQLException e) {
			System.out.println("fail : " + e.getMessage());
		} finally {
			try {
				if ( rs != null)
					rs.close();
			}catch(SQLException e) {}
			
			try {
				if ( pstmt != null)
					pstmt.close();
			}catch(SQLException e) {}
		}
		
		return result;
	}

}
