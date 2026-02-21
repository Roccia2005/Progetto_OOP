package it.unibo.jnavy.model.cell;

import java.util.Optional;

import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.utilities.HitType;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Concrete implementation of the Cell interface.
 */
public class CellImpl implements Cell {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final Position position;
    private Ship ship;
    private HitType status;
    private Boolean scanResult;

    public CellImpl(final Position p) {
        this.position = p;
        this.ship = null;
        this.status = null;
    }

    @Override
    public HitType receiveShot() {
        if (isHit()) {
            throw new IllegalStateException("Cannot shoot the same cell twice!");
        }

        if (this.ship == null) {
            this.status = HitType.MISS;
            return HitType.MISS;
        } else {
            this.ship.hit();
            return this.status = this.ship.isSunk() ? HitType.SUNK : HitType.HIT;
        }
    }

    @Override
    public void setShip(final Ship ship) {
        this.ship = ship;
    }

    @Override
    public Optional<Ship> getShip() {
        return Optional.ofNullable(this.ship);
    }

    @Override
    public boolean isOccupied() {
        return getShip().isPresent();
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public boolean isHit() {
        return this.status != null;
    }

    @Override
    public boolean repair() {
        if (this.ship != null && !this.ship.isSunk()) {
            if (this.ship.repair()) {
                this.status = null;
                return true;
            }
        }
        return false;
    }

    @Override
    public void setScanResult(final boolean shipFound) {
        this.scanResult = shipFound;
    }

    @Override
    public Optional<Boolean> getScanResult() {
        return Optional.ofNullable(this.scanResult);
    }

    @Override
    public boolean hisDetectable() {
        return getShip().map(ship -> !ship.isSunk() && !isHit()).orElse(false);
    }
}
