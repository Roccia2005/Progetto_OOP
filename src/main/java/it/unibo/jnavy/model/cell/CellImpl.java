package it.unibo.jnavy.model.cell;

import java.util.Optional;

import it.unibo.jnavy.model.ship.Ship;
import it.unibo.jnavy.model.utilities.HitType;
import it.unibo.jnavy.model.utilities.Position;

/**
 * Concrete implementation of the Cell interface.
 */
public class CellImpl implements Cell{

    private final Position position;
    private Ship ship;
    private HitType status;
    private boolean isVisible;

    public CellImpl(final Position p) {
        this.position = p;
        this.ship = null;
        this.status = null;
        this.isVisible = false;
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
            this.status = HitType.HIT;

            return this.ship.isSunk() ? HitType.SUNK : HitType.HIT;
        }
    }

    @Override
    public void setShip(Ship ship) {
        this.ship = ship;
        if(ship == null) {this.status =  null;}
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
    public void setVisible() {
        this.isVisible = true;
    }

    @Override
    public boolean isHit() {
        return this.status != null;
    }

    @Override
    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public boolean repair() {
        if (this.ship != null && !this.ship.isSunk()) {
            this.status = null;
            return true;
        }
        return false;
    }

}
