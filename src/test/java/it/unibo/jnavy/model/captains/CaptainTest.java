package it.unibo.jnavy.model.captains;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.grid.GridImpl;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.weather.WeatherManagerImpl;

/**
 * Test class for {@link Captain}
 * This class verifies the behavior of the abstract cooldown mechanism
 * and the specific effects of each concrete captain implementation.
 */
class CaptainTest {

    private static final int TEST_COOLDOWN = 2;

    private Grid grid;
    private Captain captain;
    private Position position = new Position(0, 0);
    private Position invalidPosition = new Position(-1, -1);

    @BeforeEach
    void setUp() {
        this.grid = new GridImpl();
        WeatherManagerImpl.getInstance().reset();
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
            public boolean useAbility(Grid grid, Position p) {
                if (this.isAbilityRecharged()) {
                    this.resetCooldown();
                    return true;
                }
                return false;
            }
            
        };
        this.assertAbilityIsNotCharged();

        this.chargeAbility(TEST_COOLDOWN);

        assertTrue(this.captain.isAbilityRecharged());
        assertTrue(this.captain.useAbility(null, null));

        this.assertAbilityIsNotCharged();
    }

    /**
     * Test the {@link Engineer} ability.
     * Verifies that the engineer can repair a damaged ship
     */
    @Test
    void testEngineer() {
        this.captain = new Engineer();
        this.chargeAbility(Engineer.COOLDOWN);
        /**
         * The ability should not reset if the position is 
         * not valid or has not been hit
         */
        assertFalse(this.captain.useAbility(grid, invalidPosition));
        assertFalse(this.captain.useAbility(grid, position));

        this.grid.receiveShot(position);
        assertTrue(this.captain.useAbility(grid, position));
        assertFalse(grid.getCell(position).get().isHit());
        this.assertAbilityIsNotCharged();
    }

    /**
     * Test the {@link Gunner} ability.
     * Verifies that the Gunner hits a 2x2 area.
     */
    @Test
    void testGunner() {
        this.captain = new Gunner();
        this.chargeAbility(Gunner.COOLDOWN);

        assertFalse(this.captain.useAbility(grid, invalidPosition));
        assertTrue(this.captain.useAbility(grid, position));

        List<Position> areaShot = List.of(
            position,
            new Position(position.x(), position.y() + 1),
            new Position(position.x() + 1, position.y()),
            new Position(position.x() + 1, position.y() + 1)
        );

        for (Position p : areaShot) {
            assertTrue(this.grid.getCell(p).map(cell -> cell.isHit()).orElse(false));
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
        this.chargeAbility(SonarOfficer.COOLDOWN);
        
        assertFalse(this.captain.useAbility(grid, invalidPosition));

        assertTrue(this.captain.useAbility(grid, position));
        assertTrue(grid.getCell(position).get().isVisible());
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
     * * @param cooldown The number of turns to wait.
     */
    private void chargeAbility(int cooldown) {
        for(int i = 0; i < cooldown; i++) {
            assertFalse(this.captain.isAbilityRecharged());
            this.captain.processTurnEnd();
        }
    }
}