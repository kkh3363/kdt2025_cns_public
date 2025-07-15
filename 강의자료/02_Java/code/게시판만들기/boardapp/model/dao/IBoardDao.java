package mygui.boardapp.model.dao;

import java.util.List;


import mygui.boardapp.model.dto.BoardDto;

public interface IBoardDao {
	public int insert(BoardDto params);
	public int delete(int params);
	public int update(BoardDto params);
	public BoardDto selectOne(int params);
	public List<BoardDto> select();
}
