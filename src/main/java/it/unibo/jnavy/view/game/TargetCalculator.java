package it.unibo.jnavy.view.game;

import it.unibo.jnavy.controller.game.GameController;
import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.utilities.Position;

import java.util.ArrayList;
import java.util.List;

public final class TargetCalculator {

    private TargetCalculator() {
    }

    public static List<Position> getAllRevealedPositions(GameController controller) {
        List<Position> hits = new ArrayList<>();
        int size = controller.getGridSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Position pos = new Position(r, c);
                CellCondition state = controller.getBotCellState(pos);
                if (state == CellCondition.HIT_SHIP || state == CellCondition.SUNK_SHIP || state == CellCondition.HIT_WATER) {
                    hits.add(pos);
                }
            }
        }
        return hits;
    }

    public static List<Position> determineAnimationTargets(Position p, List<Position> newHits, boolean isAbility, String captainName, int gridSize) {
        boolean isGunner = captainName.toLowerCase().contains("gunner");
        if (!isAbility || !isGunner) {
            return newHits.isEmpty() ? List.of(p) : List.of(newHits.get(0));
        }
        if (newHits.isEmpty()) {
            return getAreaPositions(p, gridSize);
        }

        Position bestAnchor = newHits.get(0);
        int minDistance = Integer.MAX_VALUE;

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                boolean containsAll = true;
                for (Position n : newHits) {
                    if (n.x() < r || n.x() > r + 1 || n.y() < c || n.y() > c + 1) {
                        containsAll = false;
                        break;
                    }
                }
                if (containsAll) {
                    int dist = Math.abs(r - p.x()) + Math.abs(c - p.y());
                    if (dist < minDistance) {
                        minDistance = dist;
                        bestAnchor = new Position(r, c);
                    }
                }
            }
        }
        return getAreaPositions(bestAnchor, gridSize);
    }

    private static List<Position> getAreaPositions(Position p, int gridSize) {
        List<Position> area = new ArrayList<>();
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                Position pos = new Position(p.x() + r, p.y() + c);
                if (pos.x() < gridSize && pos.y() < gridSize) {
                    area.add(pos);
                }
            }
        }
        return area;
    }
}