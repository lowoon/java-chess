package chess.domain.piece;

import static chess.domain.player.PlayerColor.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import chess.domain.board.BoardFactory;
import chess.domain.board.Position;
import chess.domain.exception.InvalidMovementException;

class GamePieceTest {

    @Test
    @DisplayName("플레이어별 game piece 생성")
    void getGamePieces() {
        assertThat(GamePiece.createGamePieces()).hasSize(12);
    }

    @ParameterizedTest
    @DisplayName("gamepiece가 pawn인지 확인")
    @MethodSource("createPieces")
    void isPawn(GamePiece gamePiece, boolean expected) {
        assertThat(gamePiece.isPawn()).isEqualTo(expected);
    }

    static Stream<Arguments> createPieces() {
        return Stream.of(
                Arguments.of(new Rook(BLACK), false),
                Arguments.of(new Pawn(WHITE), true)
        );
    }

    @ParameterizedTest
    @DisplayName("gamepiece가 king인지 확인")
    @MethodSource("createKingPieces")
    void isKing(GamePiece gamePiece, boolean expected) {
        assertThat(gamePiece.isKing()).isEqualTo(expected);
    }

    static Stream<Arguments> createKingPieces() {
        return Stream.of(
                Arguments.of(new King(BLACK), true),
                Arguments.of(new Pawn(WHITE), false)
        );
    }

    @ParameterizedTest
    @DisplayName("GamePiece별 초기 위치")
    @MethodSource("createPositions")
    void getInitialPositions(GamePiece piece, List<Position> expected) {
        assertThat(piece.getOriginalPositions()).isEqualTo(expected);
    }

    static Stream<Arguments> createPositions() {
        return Stream.of(
                Arguments.of(new Rook(BLACK), Arrays.asList(Position.from("a8"), Position.from("h8"))),
                Arguments.of(new Rook(WHITE), Arrays.asList(Position.from("a1"), Position.from("h1")))
        );
    }

    @ParameterizedTest
    @DisplayName("gamepiece player가 white인지 확인")
    @MethodSource("createWhitePieces")
    void isWhite(GamePiece gamePiece, boolean expected) {
        assertThat(gamePiece.is(WHITE)).isEqualTo(expected);
    }

    static Stream<Arguments> createWhitePieces() {
        return Stream.of(
                Arguments.of(new Rook(BLACK), false),
                Arguments.of(new Rook(WHITE), true)
        );
    }

    @Test
    void samePlayerColor() {
        Position source = Position.from("d5");
        Position target = Position.from("d4");
        Map<Position, GamePiece> board = new TreeMap<>(BoardFactory.createEmptyBoard().getBoard());
        GamePiece gamePiece = new Rook(BLACK);
        board.put(source, gamePiece);
        board.put(target, new Bishop(BLACK));

        assertThatThrownBy(() -> gamePiece.validateMoveTo(board, source, target))
                .isInstanceOf(InvalidMovementException.class)
                .hasMessage("이동할 수 없습니다.\n자신의 말은 잡을 수 없습니다.");
    }
}