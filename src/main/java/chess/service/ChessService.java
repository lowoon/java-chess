package chess.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chess.dao.BoardDAO;
import chess.dao.UserDAO;
import chess.domain.board.Board;
import chess.domain.board.BoardFactory;
import chess.domain.player.User;
import chess.domain.result.ChessResult;
import chess.dto.LineDto;
import chess.dto.RowsDtoConverter;
import chess.util.DBConnector;

public class ChessService {

    private BoardDAO boardDAO;
    private Map<User, Board> boards;
    private DBConnector dbConnector;

    public ChessService() {
        boards = new HashMap<>();
        boards.put(User.EMPTY_BOARD_USER, BoardFactory.createEmptyBoard());
    }

    public Board findByUserName(User blackUser, User whiteUser) throws SQLException {
        dbConnector = new DBConnector();
        boardDAO = new BoardDAO(dbConnector);
        Board board;
        if (!boardDAO.findBoardByUser(blackUser, whiteUser).isPresent()) {
            UserDAO userDAO = new UserDAO(dbConnector);
            userDAO.addUser(blackUser);
            userDAO.addUser(whiteUser);
            boardDAO.addBoard(BoardFactory.createInitialBoard(), blackUser, whiteUser);
        }
        board = boardDAO.findBoardByUser(blackUser, whiteUser)
                .orElse(BoardFactory.createInitialBoard());
        boards.put(blackUser, board);
        return board;
    }

    public Board move(User blackUser, String source, String target) {
        Board board = boards.get(blackUser).move(source, target);
        boards.put(blackUser, board);
        return board;
    }

    public void save(User blackUser, User whiteUser) throws SQLException {
        boardDAO.saveBoardByUserName(boards.get(blackUser), blackUser, whiteUser);
        boards.remove(blackUser);
    }

    public void delete(User blackUser, User whiteUser) throws SQLException {
        boardDAO.deleteBoardByUser(blackUser, whiteUser);
        UserDAO userDAO = new UserDAO(dbConnector);
        userDAO.deleteUserByUserName(blackUser.getName());
        userDAO.deleteUserByUserName(whiteUser.getName());
        boards.remove(blackUser);
        dbConnector.closeConnection();
    }

    public String searchPath(User blackUser, String sourceInput) {
        return boards.get(blackUser).searchPath(sourceInput);
    }

    public List<LineDto> getEmptyRowsDto() {
        return RowsDtoConverter.convertFrom(boards.get(User.EMPTY_BOARD_USER).getBoard());
    }

    public List<LineDto> getRowsDto(User blackUser, User whiteUser) throws SQLException {
        Board board = findByUserName(blackUser, whiteUser);
        return RowsDtoConverter.convertFrom(board.getBoard());
    }

    public int getTurn(User blackUser) {
        return boards.get(blackUser)
                .getTurn();
    }

    public double calculateWhiteScore(User blackUser) {
        return calculateResult(blackUser).getWhiteScore()
                .getScore();
    }

    public double calculateBlackScore(User blackUser) {
        return calculateResult(blackUser).getBlackScore()
                .getScore();
    }

    public ChessResult calculateResult(User blackUser) {
        return boards.get(blackUser)
                .calculateResult();
    }

    public boolean checkGameNotFinished(User blackUser) {
        return boards.get(blackUser)
                .isNotFinished();
    }

    public Board getBoard(User blackUser) {
        return boards.get(blackUser);
    }
}
