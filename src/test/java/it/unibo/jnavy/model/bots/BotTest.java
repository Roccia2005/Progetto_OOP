package it.unibo.jnavy.model.bots;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.grid.GridImpl;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.HitType;
import it.unibo.jnavy.model.utilities.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test class for {@link BotStrategy}
 * This class verifies the behavior of the different bot strategies.
 * It covers BeginnerBot, ProBot and SniperBot logics.
 */
class BotTest {

    private static final int COORD_ZERO = 0;
    private static final int COORD_ONE = 1;
    private static final int COORD_TWO = 2;
    private static final int COORD_THREE = 3;
    private static final int COORD_FOUR = 4;
    private static final int COORD_FIVE = 5;
    private static final int COORD_SIX = 6;
    private static final int COORD_SEVEN = 7;

    private static final int SHIP_SIZE = 3;
    private static final int MAX_ITERATIONS = 100;
    private static final int MIN_HITS = 50;
    private static final int ADJACENT_DISTANCE = 1;

    private Grid grid;

    @BeforeEach
    void setUp() {
        this.grid = new GridImpl();
    }

    /**
     * Test for {@link BeginnerBot}.
     * Verifies that the bot generates random valid positions and never shoots at the same.
     */
    @Test
    void testBeginnerBot() {
        final BotStrategy bot = new BeginnerBot();

        final Position randomTarget = bot.selectTarget(this.grid);
        assertTrue(this.grid.isPositionValid(randomTarget));

        final Optional<Cell> cell = this.grid.getCell(randomTarget);
        assertTrue(cell.isPresent());
        assertFalse(cell.get().isHit());
    }

    /**
     * Test for {@link ProBot} HUNTING phase.
     * Initially, during this phase it should behave like a BeginnerBot.
     */
    @Test
    void testProBotHunting() {
        final BotStrategy bot = new ProBot();

        final Position randomTarget = bot.selectTarget(this.grid);
        assertTrue(this.grid.isPositionValid(randomTarget));
    }

    /**
     * Test for {@link ProBot} SEEKING phase.
     * After a HIT, the bot should target an adjacent cell.
     */
    @Test
    void testProBotSeeking() {
        final BotStrategy bot = new ProBot();

        final Position firstHit = new Position(COORD_FIVE, COORD_FIVE);
        this.grid.receiveShot(firstHit);
        bot.lastShotFeedback(firstHit, HitType.HIT);

        final Position secondHit = bot.selectTarget(this.grid);
        assertTrue(isNear(firstHit, secondHit));
    }

    /**
     * Test for {@link ProBot} DESTROYING phase.
     * After two hits, it should keep the same direction.
     */
    @Test
    void testProBotDestroying() {
        final BotStrategy bot = new ProBot();

        final Position firstHit = new Position(COORD_FIVE, COORD_FIVE);
        this.grid.receiveShot(firstHit);
        bot.lastShotFeedback(firstHit, HitType.HIT);

        final Position secondHit = new Position(COORD_FIVE, COORD_SIX);
        this.grid.receiveShot(secondHit);
        bot.lastShotFeedback(secondHit, HitType.HIT);

        final Position thirdHit = bot.selectTarget(this.grid);
        assertEquals(new Position(COORD_FIVE, COORD_SEVEN), thirdHit);
    }

    /**
     * Test for {@link ProBot} RESET.
     * After sinking a ship, it should return to HUNTING state.
     */
    @Test
    void testProBotReturnToHuntingAfterSunk() {
        final BotStrategy bot = new ProBot();

        bot.lastShotFeedback(new Position(COORD_THREE, COORD_THREE), HitType.HIT);
        bot.lastShotFeedback(new Position(COORD_THREE, COORD_FOUR), HitType.SUNK);

        final Position randomTarget = bot.selectTarget(this.grid);
        assertTrue(this.grid.isPositionValid(randomTarget)); // da capire come valutare lo sparo random
    }

    /**
     * Test for {@link ProBot} reverse direction.
     * Sequence tested: HIT, HIT, MISS -> Should reverse to other side of first hit.
     */
    @Test
    void testProBotReverseDirection() {
        final BotStrategy bot = new ProBot();

        final Position firstHit = new Position(COORD_FIVE, COORD_FIVE);
        this.grid.receiveShot(firstHit);
        bot.lastShotFeedback(firstHit, HitType.HIT);

        final Position secondHit = new Position(COORD_FIVE, COORD_SIX);
        this.grid.receiveShot(secondHit);
        bot.lastShotFeedback(secondHit, HitType.HIT);

        final Position firstMiss = new Position(COORD_FIVE, COORD_SEVEN);
        this.grid.receiveShot(firstMiss);
        bot.lastShotFeedback(firstMiss, HitType.MISS);

        final Position reverseTarget = bot.selectTarget(this.grid);
        assertEquals(new Position(COORD_FIVE, COORD_FOUR), reverseTarget);
    }

    /**
     * Test for {@link SniperBot}.
     * SniperBot has an 18% chance to miss.
     * We place a ship and verify that the bot targets it immediately most of the time.
     */
    @Test
    void testSniperBotCheatingLogic() {
        final Ship ship = new ShipImpl(SHIP_SIZE);
        final Position p = new Position(COORD_ZERO, COORD_ZERO);
        this.grid.placeShip(ship, p, CardinalDirection.DOWN);
        final List<Position> ships = Arrays.asList(
                new Position(COORD_ZERO, COORD_ZERO),
                new Position(COORD_ONE, COORD_ZERO),
                new Position(COORD_TWO, COORD_ZERO)
        );

        int hits = 0;
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            final BotStrategy bot = new SniperBot(ships);
            final Position target = bot.selectTarget(this.grid);
            if (ships.contains(target)) {
                hits++;
            }
        }
        assertTrue(hits > MIN_HITS);
    }

    /**
     * Helper method.
     *
     * @param first position to use for distance calcs.
     * @param second position to use for distance calcs.
     * @return true if the two positions are adjacent.
     */
    private boolean isNear(final Position first, final Position second) {
        final int distanceX = Math.abs(first.x() - second.x());
        final int distanceY = Math.abs(first.y() - second.y());
        return (distanceX + distanceY) == ADJACENT_DISTANCE;
    }
}
