/*
 * Copyright 2015 Thomas Hoffmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kz.evilteamgenius.chessapp.engine.pieces;

import java.util.LinkedList;
import java.util.List;

import kz.evilteamgenius.chessapp.engine.Board;
import kz.evilteamgenius.chessapp.engine.Coordinate;
import kz.evilteamgenius.chessapp.engine.GameEngine;

/**
 * Just like a "normal" pawn, but can only move from top to down
 */
public class DownPawn extends Piece {
    public DownPawn(final Coordinate p, final String o) {
        super(p, o);
    }

    @Override
    public List<Coordinate> getPossiblePositions(Piece[][] board) {
        List<Coordinate> re = new LinkedList<>();
        Coordinate c;
        int x = position.x;
        int y = position.y;
        c = new Coordinate(x, y - 1);
        if (c.isValid() && Board.getPiece(c, board) == null) {
            re.add(c);
        }
        // can move two squares at the beginning
        // (only if no other piece stands 1 before us)
        if (((GameEngine.match.mode == GameEngine.MODE_2_PLAYER_2_SIDES && y == 6) || (y == 10)) &&
                Board.getPiece(c, board) == null) {
            c = new Coordinate(x, y - 2);
            if (c.isValid() && Board.getPiece(c, board) == null) {
                re.add(c);
            }
        }

        // check if we can attack another piece
        c = new Coordinate(x + 1, y - 1);
        if (c.isValid() && Board.getPiece(c, board) != null && sameTeam(c, board)) {
            re.add(c);
        }
        c = new Coordinate(x - 1, y - 1);
        if (c.isValid() && Board.getPiece(c, board) != null && sameTeam(c, board)) {
            re.add(c);
        }
        return re;
    }

    @Override
    public String getString() {
        return "\u265F";
    }
}
