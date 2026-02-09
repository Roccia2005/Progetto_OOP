package it.unibo.jnavy.model.bots;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.HitType;
import it.unibo.jnavy.model.cell.Cell;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.grid.GridImpl;
import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.utilities.CardinalDirection;
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

    private Grid grid;
    private static final int G_SIZE = 10;

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
        BotStrategy bot = new BeginnerBot();

        Position randomTarget = bot.selectTarget(this.grid);
        assertTrue(validPosition(randomTarget));

        Optional<Cell> cell = this.grid.getCell(randomTarget);
        assertTrue(cell.isPresent());
        assertFalse(cell.get().isHit());
        this.grid.receiveShot(randomTarget);
    }

    /**
     * Test for {@link ProBot} HUNTING phase.
     * Initially, during this phase it should behave like a BeginnerBot.
     */
    @Test
    void testProBotHunting() {
        BotStrategy bot = new ProBot();

        Position randomTarget = bot.selectTarget(this.grid);
        assertTrue(validPosition(randomTarget));
    }

    /**
     * Test for {@link ProBot} SEEKING phase.
     * After a HIT, the bot should target an adjacent cell.
     */
    @Test
    void testProBotSeeking() {
        BotStrategy bot = new ProBot();

        Position firstHit = new Position(5, 5);
        this.grid.receiveShot(firstHit);
        bot.lastShotFeedback(firstHit, HitType.HIT);

        Position secondHit = bot.selectTarget(this.grid);
        assertTrue(isNear(firstHit, secondHit));
    }

    /**
     * Test for {@link ProBot} DESTROYING phase.
     * After two hits, it should keep the same direction.
     */
    @Test
    void testProBotDestroying() {
        BotStrategy bot = new ProBot();

        Position firstHit = new Position(5, 5);
        this.grid.receiveShot(firstHit);
        bot.lastShotFeedback(firstHit, HitType.HIT);

        Position secondHit = new Position(5, 6);
        this.grid.receiveShot(secondHit);
        bot.lastShotFeedback(secondHit, HitType.HIT);

        Position thirdHit = bot.selectTarget(this.grid);
        assertEquals(new Position(5, 7), thirdHit);
    }

    /**
     * Test for {@link ProBot} RESET.
     * After sinking a ship, it should return to HUNTING state.
     */
    @Test
    void testProBotReturnToHuntingAfterSunk() {
        BotStrategy bot = new ProBot();

        bot.lastShotFeedback(new Position(3, 3), HitType.HIT);
        bot.lastShotFeedback(new Position(3, 4), HitType.SUNK);

        Position randomTarget = bot.selectTarget(this.grid);
        assertTrue(validPosition(randomTarget)); // da capire come valutare lo sparo random
    }

    /**
     * Test for {@link ProBot} reverse direction.
     * Sequence tested: HIT, HIT, MISS -> Should reverse to other side of first hit.
     */
    @Test
    void testProBotReverseDirection() {
        BotStrategy bot = new ProBot();

        Position firstHit = new Position(5, 5);
        this.grid.receiveShot(firstHit);
        bot.lastShotFeedback(firstHit, HitType.HIT);

        Position secondHit = new Position(5, 6);
        this.grid.receiveShot(secondHit);
        bot.lastShotFeedback(secondHit, HitType.HIT);

        Position firstMiss = new Position(5, 7);
        this.grid.receiveShot(firstMiss);
        bot.lastShotFeedback(firstMiss, HitType.MISS);

        Position reverseTarget = bot.selectTarget(this.grid);
        assertEquals(new Position(5, 4), reverseTarget);
    }

    /**
     * Test for {@link SniperBot}.
     * Verifies it cheats by looking at the left half of the grid first.
     */
    @Test
    void testSniperBotHalfGridPriority() {
        Ship ship = new ShipImpl(3);
        this.grid.placeShip(ship, new Position(0, 0), CardinalDirection.DOWN);

        BotStrategy bot = new SniperBot(this.grid);
        Position knownTarget = bot.selectTarget(this.grid);

        Cell knownTargetCell = this.grid.getCell(knownTarget).get();
        assertTrue(knownTargetCell.isOccupied());
        assertEquals(ship, knownTargetCell.getShip());
    }

    /**
     * Test for {@link SniperBot} right half switch behaviour.
     * When left half is empty, it should switch to ProBot behavior on the right half only.
     */
    @Test
    void testSniperBotSwitchOnRight() {
        BotStrategy bot = new SniperBot(this.grid);

        Position rightHalfTarget = bot.selectTarget(this.grid);
        grid.receiveShot(rightHalfTarget);
        assertTrue(rightHalfTarget.y() >= G_SIZE / 2);
    }

    /**
     * Test for {@link SniperBot} half border crossing.
     * Logic: Hit left -> Ship continues to right -> Bot crosses on the right half.
     */
    @Test
    void testSniperBotBorderCrossing() {
        Ship ship = new ShipImpl(2);
        this.grid.placeShip(ship, new Position(5, 4), CardinalDirection.RIGHT);

        BotStrategy bot = new SniperBot(this.grid);

        Position firstHit = bot.selectTarget(this.grid);
        assertEquals(new Position(5, 4), firstHit);
        this.grid.receiveShot(firstHit);
        bot.lastShotFeedback(firstHit, HitType.HIT);

        Position secondHit = bot.selectTarget(this.grid);
        assertEquals(new Position(5, 5), secondHit);
    }

    /**
     * Helper method.
     * @param p the position to check.
     * @return true if p is a valid position on the grid, false otherwise.
     */
    private boolean validPosition(final Position p) {
        return p.x() >= 0
        && p.x() < G_SIZE
        && p.y() >= 0
        && p.y() < G_SIZE;
    }

    /**
     * Helper method.
     * @param first position to use for distance calcs.
     * @param second position to use for distance calcs.
     * @return true if the two positions are adjacent.
     */
    private boolean isNear(final Position first, final Position second) {
        final int distanceX = Math.abs(first.x() - second.x());
        final int distanceY = Math.abs(first.y() - second.y());
        return (distanceX == 1) && (distanceY == 1);
    }
}
