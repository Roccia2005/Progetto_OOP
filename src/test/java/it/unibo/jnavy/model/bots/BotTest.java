package it.unibo.jnavy;

import static org.junit.jupiter.api.Assertions.assertTrue;

import it.unibo.jnavy.model.bots.BeginnerBot;
import it.unibo.jnavy.model.bots.BotStrategy;
import it.unibo.jnavy.model.utilities.Position;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import model.*;

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
        Position target = bot.selectTarget(this.grid);
        AssertTrue(validPosition(target));
    }

    @Test
    void testProBotHunting() {

    }

    @Test
    void testProBotSeeking() {

    }

    @Test
    void testProBotDestroying() {

    }

    @Test
    void testProBotReturnToHuntingAfterSunk() {

    }

    @Test
    void testProBotReverseDirection() {

    }

    @Test
    void testSniperBotHalfGridPriority() {

    }

    @Test
    void testSniperBotSwitchOnRight() {

    }

    @Test
    void testSniperBotBorderCrossing() {

    }

    private boolean validPosition(Position p) {
        return p.x() >= 0
        && p.x() < G_SIZE
        && p.y() >= 0
        && p.y() < G_SIZE;
    }
}
