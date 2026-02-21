package it.unibo.jnavy.view.game;

import it.unibo.jnavy.controller.game.GameController;
import it.unibo.jnavy.controller.utilities.CellCondition;
import it.unibo.jnavy.model.utilities.Position;
import java.util.Locale;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class designed to calculate the target positions for visual animations.
 */
public final class TargetCalculator {

    private TargetCalculator() {
        // Utility classes should not be instantiated.
    }

    /**
     * Scans the opponent's grid and retrieves all positions that have already been revealed.
     * This includes cells where water was hit, a ship was hit, or a ship was completely sunk.
     *
     * @param controller the active {@link GameController} used to query the board state.
     * @return a {@link List} of all {@link Position}s currently revealed on the bot's grid.
     */
    public static List<Position> getAllRevealedPositions(final GameController controller) {
        final List<Position> hits = new ArrayList<>();
        final int size = controller.getGridSize();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                final Position pos = new Position(r, c);
                final CellCondition state = controller.getBotCellState(pos);
                if (state == CellCondition.HIT_SHIP || state == CellCondition.SUNK_SHIP || state == CellCondition.HIT_WATER) {
                    hits.add(pos);
                }
            }
        }
        return hits;
    }

    /**
     * Determines the exact coordinates that should be targeted by the shot animation.
     * It handles the logic for standard single-cell shots as well as area-of-effect 
     * abilities, calculating the best visual anchor point.
     *
     * @param p the primary logical {@link Position} clicked by the player.
     * @param newHits a list of positions that were newly affected by this specific turn.
     * @param isAbility {@code true} if the current attack was triggered by a captain's ability.
     * @param captainName the name of the player's captain, used to identify specific attack patterns.
     * @param gridSize the dimension of the board, used to prevent animations from exceeding the boundaries.
     * @return a {@link List} of {@link Position}s representing the tiles to animate.
     */
    public static List<Position> determineAnimationTargets(final Position p,
                                                           final List<Position> newHits,
                                                           final boolean isAbility,
                                                           final String captainName,
                                                           final int gridSize) {
        final boolean isGunner = captainName.toLowerCase(Locale.ROOT).contains("gunner");
        if (!isAbility || !isGunner) {
            return newHits.isEmpty() ? List.of(p) : List.of(newHits.getFirst());
        }
        if (newHits.isEmpty()) {
            return getAreaPositions(p, gridSize);
        }

        Position bestAnchor = newHits.getFirst();
        int minDistance = Integer.MAX_VALUE;

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                boolean containsAll = true;
                for (final Position n : newHits) {
                    if (n.x() < r || n.x() > r + 1 || n.y() < c || n.y() > c + 1) {
                        containsAll = false;
                        break;
                    }
                }
                if (containsAll) {
                    final int dist = Math.abs(r - p.x()) + Math.abs(c - p.y());
                    if (dist < minDistance) {
                        minDistance = dist;
                        bestAnchor = new Position(r, c);
                    }
                }
            }
        }
        return getAreaPositions(bestAnchor, gridSize);
    }

    private static List<Position> getAreaPositions(final Position p, final int gridSize) {
        final List<Position> area = new ArrayList<>();
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                final Position pos = new Position(p.x() + r, p.y() + c);
                if (pos.x() < gridSize && pos.y() < gridSize) {
                    area.add(pos);
                }
            }
        }
        return area;
    }
}
