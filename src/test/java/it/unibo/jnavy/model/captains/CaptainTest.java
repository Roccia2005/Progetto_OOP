package it.unibo.jnavy.model.captains;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.grid.GridImpl;
import it.unibo.jnavy.model.ship.ShipImpl;
import it.unibo.jnavy.model.utilities.CardinalDirection;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

/**
 * Test class for {@link Captain}
 * This class verifies the behavior of the abstract cooldown mechanism
 * and the specific effects of each concrete captain implementation.
 */
class CaptainTest {

    private static final int TEST_COOLDOWN = 2;

    private static final int SHIP_SIZE = 2;
    private static final int COORD_MINUS_ONE = -1;
    private static final int COORD_ZERO = 0;
    private static final int COORD_ONE = 1;
    private static final int COORD_TWO = 2;
    private static final int COORD_FOUR = 4;
    private static final int COORD_FIVE = 5;
    private static final int COORD_SIX = 6;
    private static final int OFFSET_ONE = 1;

    private Grid grid;
    private Captain captain;
    private final Position position = new Position(COORD_ZERO, COORD_ZERO);
    private final Position invalidPosition = new Position(COORD_MINUS_ONE, COORD_MINUS_ONE);

    @BeforeEach
    void setUp() {
        this.grid = new GridImpl();
        this.grid.placeShip(new ShipImpl(SHIP_SIZE), position, CardinalDirection.DOWN);
        WeatherManagerImpl.getInstance().reset();
        this.captain = new Engineer();
    }

    /**
     * Tests the cooldown mechanism provided by the {@link AbstractCaptain} class.
     * At the start the ability should be not charged.
     * After the ability is used the cooldown should reset.
     */
    @Test
    void testRechargeAbility() {
        this.captain = new AbstractCaptain(TEST_COOLDOWN) {

            @Override
            public boolean doesAbilityConsumeTurn() {
                return false;
            }

            @Override
            public boolean targetsEnemyGrid() {
                return false;
            }

            @Override
            protected boolean executeEffect(final Grid targetGrid, final Position p) {
                return true;
            }
        };
        this.assertAbilityIsNotCharged();

        this.chargeAbility(TEST_COOLDOWN);

        assertTrue(this.captain.isAbilityRecharged());
        assertFalse(this.captain.useAbility(this.grid, this.invalidPosition));
        assertTrue(this.captain.useAbility(this.grid, this.position));

        this.assertAbilityIsNotCharged();
    }

    /**
     * Test the {@link Engineer} ability.
     * Verifies that the engineer can repair a damaged ship
     */
    @Test
    void testEngineer() {
        this.captain = new Engineer();
        assertFalse(this.captain.targetsEnemyGrid());
        assertFalse(this.captain.doesAbilityConsumeTurn());
        this.chargeAbility(Engineer.COOLDOWN);

        // The ability should not reset if the position is not valid or has not been hit
        assertFalse(this.captain.useAbility(grid, invalidPosition));
        assertFalse(this.captain.useAbility(grid, position));

        // The ability should not repair a cell hit with water
        final Position waterPosition = new Position(COORD_FIVE, COORD_FIVE);
        this.grid.receiveShot(waterPosition);
        assertFalse(this.captain.useAbility(grid, waterPosition));

        this.grid.receiveShot(position);
        assertTrue(this.captain.useAbility(grid, position));
        assertFalse(grid.getCell(position).get().isHit());

        this.assertAbilityIsNotCharged();

        this.chargeAbility(Engineer.COOLDOWN);

        // The ability should not repair a sunk ship
        this.grid.receiveShot(new Position(COORD_ZERO, COORD_ONE));
        assertFalse(this.captain.useAbility(grid, position));
    }

    /**
     * Test the {@link Gunner} ability.
     * Verifies that the Gunner hits a 2x2 area.
     */
    @Test
    void testGunner() {
        this.captain = new Gunner();
        assertTrue(this.captain.targetsEnemyGrid());
        assertTrue(this.captain.doesAbilityConsumeTurn());
        this.chargeAbility(Gunner.COOLDOWN);

        assertFalse(this.captain.useAbility(grid, invalidPosition));
        assertTrue(this.captain.useAbility(grid, position));

        final List<Position> areaShot = List.of(
                position,
                new Position(position.x(), position.y() + OFFSET_ONE),
                new Position(position.x() + OFFSET_ONE, position.y()),
                new Position(position.x() + OFFSET_ONE, position.y() + OFFSET_ONE)
        );

        // Verify that every cell in the expected area has been hit
        for (final Position p : areaShot) {
            assertTrue(this.grid.getCell(p).get().isHit());
        }

        this.assertAbilityIsNotCharged();
    }

    /**
     * Test the {@link SonarOfficer} ability.
     * Verifies that the Sonar reveals a cell's visibility status.
     */
    @Test
    void testSonarOfficer() {
        this.captain = new SonarOfficer();
        assertTrue(this.captain.targetsEnemyGrid());
        assertFalse(this.captain.doesAbilityConsumeTurn());
        this.chargeAbility(SonarOfficer.COOLDOWN);
        assertFalse(this.captain.useAbility(grid, invalidPosition));
        assertTrue(this.captain.useAbility(grid, position));

        // Verify that the 3x3 area (from 0,0 to 2,2) correctly registered the ship's presence
        for (int x = COORD_ZERO; x <= COORD_TWO; x++) {
            for (int y = COORD_ZERO; y <= COORD_TWO; y++) {
                final Position p = new Position(x, y);
                assertTrue(grid.getCell(p).get().getScanResult().isPresent());
                assertTrue(grid.getCell(p).get().getScanResult().get());
            }
        }

        this.chargeAbility(SonarOfficer.COOLDOWN + OFFSET_ONE);
        // Verify that the scanned 3x3 area (from 4,4 to 6,6) correctly registered no ships
        final Position emptyPosition = new Position(COORD_FIVE, COORD_FIVE);
        assertTrue(this.captain.useAbility(grid, emptyPosition));

        for (int x = COORD_FOUR; x <= COORD_SIX; x++) {
            for (int y = COORD_FOUR; y <= COORD_SIX; y++) {
                final Position p = new Position(x, y);
                assertTrue(grid.getCell(p).get().getScanResult().isPresent());
                assertFalse(grid.getCell(p).get().getScanResult().get());
            }
        }

        this.assertAbilityIsNotCharged();
    }

    /**
     * Helper method to assert that the captain is currently discharged
     * and cannot perform abilities.
     */
    private void assertAbilityIsNotCharged() {
        assertFalse(this.captain.isAbilityRecharged());
        assertFalse(this.captain.useAbility(grid, position));
    }

    /**
     * Helper method to advance turns until the captain is fully charged.
     * Note that the cooldown counter does
     * not increment during the same turn in which the ability is activated.
     *
     * @param cooldown The number of turns to wait.
     */
    private void chargeAbility(final int cooldown) {
        for (int i = 0; i < cooldown + OFFSET_ONE; i++) {
            this.captain.processTurnEnd();
        }
    }
}
